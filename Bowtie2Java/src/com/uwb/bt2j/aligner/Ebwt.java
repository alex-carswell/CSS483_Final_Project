package com.uwb.bt2j.aligner;

import java.io.File;
import java.io.FileInputStream;

import com.uwb.bt2j.indexer.SideLocus;
import com.uwb.bt2j.util.IndexTypes;
import com.uwb.bt2j.util.strings.BTDnaString;

public class Ebwt {
	public static final String gEbwt_ext = "bt2";
	public String gLastIOErrMsg;
	
	public enum EbwtFlags {
		EBWT_COLOR(2),
		EBWT_ENTIRE_REV(4);
		private int x;
		EbwtFlags(int y){x = y;}
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
}
