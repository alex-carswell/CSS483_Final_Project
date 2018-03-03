package com.uwb.bt2j.aligner;

import java.io.File;
import java.io.FileInputStream;

import com.uwb.bt2j.indexer.SideLocus;
import com.uwb.bt2j.util.IndexTypes;
import com.uwb.bt2j.util.RefRecord;
import com.uwb.bt2j.util.file.FileBuf;
import com.uwb.bt2j.util.strings.BTDnaString;
import com.uwb.bt2j.util.types.EList;

public class Ebwt <TStr>{
	public static final String gEbwt_ext = "bt2";
	public String gLastIOErrMsg;
	
	public enum EbwtFlags {
		EBWT_COLOR(2),
		EBWT_ENTIRE_REV(4);
		private int x;
		EbwtFlags(int y){x = y;}
	}
	
	public Ebwt(
			 int color,
			 int needEntireReverse,
		     boolean fw,
		     int overrideOffRate, // = -1,
		     int offRatePlus, // = -1,
		     boolean useMm, // = false,
		     boolean useShmem, // = false,
		     boolean mmSweep, // = false,
		     boolean loadNames, // = false,
			 boolean loadSASamp, // = true,
			 boolean loadFtab, // = true,
			 boolean loadRstarts, // = true,
		     boolean verbose, // = false,
		     boolean startVerbose, // = false,
		     boolean passMemExc, // = false,
		     boolean sanityCheck) {
		packed_ = false;
		_useMm = useMm;
		useShmem_ = useShmem;
		_in1Str = in + ".1." + gEbwt_ext;
		_in2Str = in + ".2." + gEbwt_ext;
		readIntoMemory(
			color,       // expect index to be colorspace?
			fw ? -1 : needEntireReverse, // need REF_READ_REVERSE
			loadSASamp,  // load the SA sample portion?
			loadFtab,    // load the ftab & eftab?
			loadRstarts, // load the rstarts array?
			true,        // stop after loading the header portion?
			_eh,        // params
			mmSweep,     // mmSweep
			loadNames,   // loadNames
			startVerbose); // startVerbose
		// If the offRate has been overridden, reflect that in the
		// _eh._offRate field
		if(offRatePlus > 0 && _overrideOffRate == -1) {
			_overrideOffRate = _eh._offRate + offRatePlus;
		}
		if(_overrideOffRate > _eh._offRate) {
			_eh.setOffRate(_overrideOffRate);
		}
	}
	
	public Ebwt(TStr exampleStr,
			boolean packed,
			int color,
			int needEntireReverse,
			int lineRate,
			int offRate,
			int ftabChars,
	        int nthreads,
			String file,   // base filename for EBWT files
			boolean fw,
			boolean useBlockwise,
			long bmax,
			long bmaxSqrtMult,
			long bmaxDivN,
			int dcv,
			EList<FileBuf> is,
			EList<RefRecord> szs,
			long sztot,
			RefReadInParams refparams,
			int seed,
			int overrideOffRate,
			boolean doSaFile,
			boolean doBwtFile,
			boolean verbose,
			boolean passMemExc,
			boolean sanityCheck) {
		_eh(
				joinedLen(szs),
				lineRate,
				offRate,
				ftabChars,
				color,
				refparams.reverse == REF_READ_REVERSE)
	}
  
	public String adjustEbwtBase(String cmdline, String ebwtFileBase, boolean verbose) {
		String str = ebwtFileBase;
		File in = new File((str + ".1." + gEbwt_ext));
		
		if(verbose) System.out.println( "Trying " + str);
		if(!in.exists())
			if(verbose) System.out.println( "  didn't work" );
			if(System.getenv("BOWTIE2_INDEXES") != null) {
				str = System.getenv("BOWTIE2_INDEXES") + "/" + ebwtFileBase;
				if(verbose) System.out.println( "Trying " + str);
				in=new File((str + ".1." + gEbwt_ext));
			}
		if(!in.exists()) {
			System.err.println("Could not locate a Bowtie index corresponding to basename \"" + ebwtFileBase + "\"" );
		}
		return str;
	}
	
	public void joinedToTextOff(
			long qlen, 
			long off,
			long tidx,
			long textoff,
			long tlen,
			boolean rejectStraddle,
			boolean straddled){
		long top = 0;
		long bot = _nFrag; // 1 greater than largest addressable element
		long elt = IndexTypes.OFF_MASK;
		// Begin binary search
		while(true) {
			elt = top + ((bot - top) >> 1);
			long lower = rstarts()[elt*3];
			long upper;
			if(elt == _nFrag-1) {
				upper = _eh._len;
			} else {
				upper = rstarts()[((elt+1)*3)];
			}
			long fraglen = upper - lower;
			if(lower <= off) {
				if(upper > off) { // not last element, but it's within
					// off is in this range; check if it falls off
					if(off + qlen > upper) {
						straddled = true;
						if(rejectStraddle) {
							// it falls off; signal no-go and return
							tidx = IndexTypes.OFF_MASK;
							return;
						}
					}
					// This is the correct text idx whether the index is
					// forward or reverse
					tidx = rstarts()[(elt*3)+1];
					// it doesn't fall off; now calculate textoff.
					// Initially it's the number of characters that precede
					// the alignment in the fragment
					long fragoff = off - rstarts()[(elt*3)];
					if(!this.fw_) {
						fragoff = fraglen - fragoff - 1;
						fragoff -= (qlen-1);
					}
					// Add the alignment's offset into the fragment
					// ('fragoff') to the fragment's offset within the text
					textoff = fragoff + rstarts()[(elt*3)+2];
					break; // done with binary search
				} else {
					// 'off' belongs somewhere in the region between elt
					// and bot
					top = elt;
				}
			} else {
				// 'off' belongs somewhere in the region between top and
				// elt
				bot = elt;
			}
			// continue with binary search
		}
		tlen = this.plen()[tidx];
	}
	
	public long walkLeft(long row, long steps) {
		SideLocus l;
		if(steps > 0) l.initFromRow(row, _eh, ebwt());
		while(steps > 0) {
			if(row == _zOff) return IndexTypes.OFF_MASK;
			long newrow = this.mapLF(l);
			row = newrow;
			steps--;
			if(steps > 0) l.initFromRow(row, _eh, ebwt());
		}
		return row;
	}
	
	public long getOffset(long row) {
		if(row == _zOff) return 0;
		if((row & _eh._offMask) == row) return this.offs()[row >> _eh._offRate];
		long jumps = 0;
		SideLocus l;
		l.initFromRow(row, _eh, ebwt());
		while(true) {
			long newrow = this.mapLF(l);
			jumps++;
			row = newrow;
			if(row == _zOff) {
				return jumps;
			} else if((row & _eh._offMask) == row) {
				return jumps + this.offs()[row >> _eh._offRate];
			}
			l.initFromRow(row, _eh, ebwt());
		}
	}
	
	public long getOffset(long elt, boolean fw, long hitlen) {
		long off = getOffset(elt);
		if(!fw) {
			off = _eh._len - off - 1;
			off -= (hitlen-1);
		}
		return off;
	}
	
	public boolean contains(BTDnaString str, long otop, long obot) {
		SideLocus tloc, bloc;
		if(str.empty()) {
			if(otop != null && obot != null) otop = obot = 0;
			return true;
		}
		int c = str[str.length()-1];
		long top = 0, bot = 0;
		if(c < 4) {
			top = fchr()[c];
			bot = fchr()[c+1];
		} else {
			boolean set = false;
			for(int i = 0; i < 4; i++) {
				if(fchr()[c] < fchr()[c+1]) {
					if(set) {
						return false;
					} else {
						set = true;
						top = fchr()[c];
						bot = fchr()[c+1];
					}
				}
			}
		}
		tloc.initFromRow(top, eh(), ebwt());
		bloc.initFromRow(bot, eh(), ebwt());
		for(long i = (long)str.length()-2; i >= 0; i--) {
			c = str[i];
			if(c <= 3) {
				top = mapLF(tloc, c);
				bot = mapLF(bloc, c);
			} else {
				long sz = bot - top;
				int c1 = mapLF1(top, tloc);
				bot = mapLF(bloc, c1);
				if(bot - top < sz) {
					// Encountered an N and could not proceed through it because
					// there was more than one possible nucleotide we could replace
					// it with
					return false;
				}
			}
			if(i > 0) {
				tloc.initFromRow(top, eh(), ebwt());
				bloc.initFromRow(bot, eh(), ebwt());
			}
		}
		if(otop != null && obot != null) {
			otop = top; obot = bot;
		}
		return bot > top;
	}
	
	public static long fileSize(String name) {
		File f = new File(name);
		return f.length();
	}
	
	public static int pop32(long x) {
		// Lots of cache misses on following lines (>10K)
		x = x - ((x >> 1) & 0x55555555);
		x = (x & 0x33333333) + ((x >> 2) & 0x33333333);
		x = (x + (x >> 4)) & 0x0F0F0F0F;
		x = x + (x >> 8);
		x = x + (x >> 16);
		x = x + (x >> 32);
		return (int)(x & 0x3F);
	}
	
	public static int countInU64(int c, long dw) {
		long c_table[] = {
				0xffffffff,
				0xaaaaaaaa,
				0x55555555,
				0x00000000
		};
		long c0 = c_table[c];
		long x0 = dw ^ c0;
		long x1 = (x0 >> 1);
		long x2 = x1 & (0x55555555);
		long x3 = x0 & x2;
	    long tmp = pop32(x3);
		return (int) tmp;
	}
}
