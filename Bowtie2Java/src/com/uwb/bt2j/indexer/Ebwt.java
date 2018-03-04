package com.uwb.bt2j.indexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class Ebwt <TStr>{
	public static final String gEbwt_ext = "bt2";
	public boolean       _toBigEndian;
	public int    _overrideOffRate;
	public boolean       _verbose;
	public boolean       _passMemExc;
	public boolean       _sanity;
	public boolean       fw_;     // true iff this is a forward index
	public File       _in1;    // input fd for primary index file
	public File       _in2;    // input fd for secondary index file
	public String     _in1Str; // filename for primary index file
	public String     _in2Str; // filename for secondary index file
	public String     _inSaStr;  // filename for suffix-array file
	public String     _inBwtStr; // filename for BWT file
	public long  _zOff;
	public long  _zEbwtByteOff;
	public long   _zEbwtBpOff; 
	public long  _nPat;  /// number of reference texts
	public long  _nFrag; /// number of fragments
	
	APtrWrap<long> _plen;
	APtrWrap<long> _rstarts; // starting offset of fragments / text indexes
	// _fchr, _ftab and _eftab are expected to be relatively small
	// (usually < 1MB, perhaps a few MB if _fchr is particularly large
	// - like, say, 11).  For this reason, we don't bother with writing
	// them to disk through separate output streams; we
	APtrWrap<long> _fchr;
	APtrWrap<long> _ftab;
	APtrWrap<long> _eftab; // "extended" entries for _ftab
	// _offs may be extremely large.  E.g. for DNA w/ offRate=4 (one
	// offset every 16 rows), the total size of _offs is the same as
	// the total size of the input sequence
	APtrWrap<long> _offs;
	// _ebwt is the Extended Burrows-Wheeler Transform itself, and thus
	// is at least as large as the input sequence.
	APtrWrap<uint8_t> _ebwt;
	
	public boolean       _useMm;        /// use memory-mapped files to hold the index
	public boolean       useShmem_;     /// use shared memory to hold large parts of the index
	public EList<String> _refnames; /// names of the reference sequences
	public String mmFile1_;
	public String mmFile2_;
	public EbwtParams _eh;
	public boolean packed_;

	public static final long default_bmax = IndexTypes.IndexTypes.OFF_MASK;
	public static final long default_bmaxMultSqrt = IndexTypes.IndexTypes.OFF_MASK;
	public static final long default_bmaxDivN = 4;
	public static final int      default_dcv = 1024;
	public static final boolean     default_noDc = false;
	public static final boolean     default_useBlockwise = true;
	public static final int default_seed = 0;
	public static final int      default_lineRate = 6;
	public static final int      default_offRate = 5;
	public static final int      default_offRatePlus = 0;
	public static final int      default_ftabChars = 10;
	public static final boolean     default_bigEndian = false;
	
	public enum EbwtFlags {
		EBWT_COLOR(2),
		EBWT_ENTIRE_REV(4);
		private int x;
		EbwtFlags(int y){x = y;}
	}
	
	public void Ebwt_INITS() {
		
	}
	
	public Ebwt(
			String in,
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
		Ebwt_INITS();
		packed_ = false;
		_useMm = useMm;
		useShmem_ = useShmem;
		_in1Str = in + ".1." + gEbwt_ext;
		_in2Str = in + ".2." + gEbwt_ext;
		readIntoMemory(
			color,       // expect index to be colorspace?
			fw ? -1 : needEntireReverse, // need ReadDir.REF_READ_REVERSE
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
		Ebwt_INITS();
		_eh(
				joinedLen(szs),
				lineRate,
				offRate,
				ftabChars,
				color,
				refparams.reverse == ReadDir.REF_READ_REVERSE);
		_in1Str = file + ".1." + gEbwt_ext;
		_in2Str = file + ".2." + gEbwt_ext;
		packed_ = packed;
		// Open output files
		FileOutputStream fout1 = new FileOutputStream(_in1Str);

		FileOutputStream fout2 = new FileOutputStream(_in2Str);

		_inSaStr = file + ".sa";
		_inBwtStr = file + ".bwt";
		FileOutputStream saOut = null, bwtOut = null;
		if(doSaFile) {
			saOut = new FileOutputStream(_inSaStr);
		}
		if(doBwtFile) {
			bwtOut = new FileOutputStream(_inBwtStr);
		}
		// Build SA(T) and BWT(T) block by block
		initFromVector<TStr>(
			is,
		    szs,
		    sztot,
		    refparams,
		    fout1,
		    fout2,
            file,
			saOut,
			bwtOut,
            nthreads,
		    useBlockwise,
		    bmax,
		    bmaxSqrtMult,
		    bmaxDivN,
		    dcv,
		    seed,
		    verbose);
		
		// Close output files
		fout1.flush();
		
		long tellpSz1 = (long)fout1.getChannel().position();
		if(this.verbose()) {
			String tmp = "Wrote " + fout1.getChannel().position() + " bytes to primary EBWT file: " + _in1Str + '\n';
			this.verbose(tmp);
		}
		
		fout1.close();
		boolean err = false;
		if(tellpSz1 > Indexer.fileSize(_in1Str)) {
			err = true;
			System.err.println( "Index is corrupt: File size for " + _in1Str + " should have been " + tellpSz1
			     + " but is actually " + Indexer.Indexer.fileSize(_in1Str) + "." + "\n");
		}
		fout2.flush();
		
		long tellpSz2 = (long)fout2.getChannel().position();
		if(this.verbose()) {
			String tmp = "Wrote " + fout2.getChannel().position() + " bytes to secondary EBWT file: " + _in2Str;
			this.verbose(tmp);
		}
		fout2.close();
		if(tellpSz2 > Indexer.fileSize(_in2Str)) {
			err = true;
			System.err.println( "Index is corrupt: File size for " + _in2Str + " should have been " + tellpSz2
			     + " but is actually " + Indexer.fileSize(_in2Str) + "." + "\n");
		}
		
		if(saOut != null) {
			// Check on suffix array output file size
			long tellpSzSa = (long)saOut.getChannel().position();
			if(this.verbose()) {
				String tmp = "Wrote " + tellpSzSa + " bytes to suffix-array file: " + _inSaStr;
				this.verbose(tmp);
			}
			saOut.close();
			if(tellpSzSa > Indexer.fileSize(_inSaStr)) {
				err = true;
				System.err.println( "Index is corrupt: File size for " + _inSaStr + " should have been " + tellpSzSa
					 + " but is actually " + Indexer.fileSize(_inSaStr) + "." + "\n");
			}
		}

		if(bwtOut != null) {
			// Check on suffix array output file size
			long tellpSzBwt = (long)bwtOut.getChannel().position();
			if(this.verbose()) {
				String tmp = "Wrote " + tellpSzBwt + " bytes to BWT file: " + _inBwtStr;
				this.verbose(tmp);
			}
			bwtOut.close();
			if(tellpSzBwt > Indexer.fileSize(_inBwtStr)) {
				err = true;
				System.err.println( "Index is corrupt: File size for " + _inBwtStr + " should have been " + tellpSzBwt
					 + " but is actually " + Indexer.fileSize(_inBwtStr) + "." + "\n");
			}
		}
		
		if(err) {
			System.err.println( "Please check if there is a problem with the disk or if disk is full." + "\n");
		}
		
		// Reopen as input streams
		if(this.verbose()) {
			String tmp = "Re-opening _in1 and _in2 as input streams";
			this.verbose(tmp);
		}
		
		if(_sanity) {
			if(this.verbose()) {
				String tmp = "Sanity-checking Bt2";
				this.verbose(tmp);
			}
			readIntoMemory(
				color,                       // colorspace?
				fw ? -1 : needEntireReverse, // 1 . need the reverse to be reverse-of-concat
				true,                        // load SA sample (_offs[])?
				true,                        // load ftab (_ftab[] & _eftab[])?
				true,                        // load r-starts (_rstarts[])?
				false,                       // just load header?
				null,                        // Params object to fill
				false,                       // mm sweep?
				true,                        // load names?
				false);                      // verbose startup?
			sanityCheckAll(refparams.reverse);
			evictFromMemory();
		}
		if(this.verbose()) {
			String tmp = "Returning from Ebwt constructor";
			this.verbose(tmp);
		}
	}
	
	public static Pair<Ebwt, Ebwt> fromString(
			String str,
			boolean packed,
			int color,
			int reverse,
			boolean bigEndian,
			int lineRate,
			int offRate,
			int ftabChars,
			String file,
			boolean useBlockwise,
			long bmax,
			long bmaxSqrtMult,
			long bmaxDivN,
			int dcv,
			int seed,
			boolean verbose,
			boolean autoMem,
			boolean sanity){
		EList<String> strs = new EList(1);
		strs.push_back(str);
		return fromStrings<TStr>(
			strs,
			packed,
			color,
			reverse,
			bigEndian,
			lineRate,
			offRate,
			ftabChars,
			file,
			useBlockwise,
			bmax,
			bmaxSqrtMult,
			bmaxDivN,
			dcv,
			seed,
			verbose,
			autoMem,
			sanity);
	}
	public static Pair<Ebwt, Ebwt> fromStrings(
			String strs,
			boolean packed,
			int color,
			int reverse,
			boolean bigEndian,
			int lineRate,
			int offRate,
			int ftabChars,
			String file,
			boolean useBlockwise,
			long bmax,
			long bmaxSqrtMult,
			long bmaxDivN,
			int dcv,
			int seed,
			boolean verbose,
			boolean autoMem,
			boolean sanity){
		EList<FileBuf> is = new EList(1);
		RefReadInParams refparams = new RefReadInParams(color, ReadDir.REF_READ_FORWARD, false, false);
		String ss = "";
		for(int i = 0; i < strs.size(); i++) {
			ss += ">" + i + "\n" + strs[i] + "\n";
		}
		FileBuf fb = new FileBuf(ss.get());
		is.push_back(fb.get());
		
		// Vector for the ordered list of "records" comprising the input
		// sequences.  A record represents a stretch of unambiguous
		// characters in one of the input sequences.
		EList<RefRecord> szs = new EList(1);
		Pair<Long, Long> sztot;
		sztot = BitPairReference.szsFromFasta(is, file, bigEndian, refparams, szs, sanity);
		
		// Construct Ebwt from input strings and parameters
		Ebwt ebwtFw = new Ebwt(
			TStr,
			packed,
			refparams.color ? 1 : 0,
			-1,           // fw
			lineRate,
			offRate,      // suffix-array sampling rate
			ftabChars,    // number of chars in initial arrow-pair calc
			file,         // basename for .?.ebwt files
			true,         // fw?
			useBlockwise, // useBlockwise
			bmax,         // block size for blockwise SA builder
			bmaxSqrtMult, // block size as multiplier of sqrt(len)
			bmaxDivN,     // block size as divisor of len
			dcv,          // difference-cover period
			is,           // list of input streams
			szs,          // list of reference sizes
			sztot.first,  // total size of all unambiguous ref chars
			refparams,    // reference read-in parameters
			seed,         // pseudo-random number generator seed
			-1,           // override offRate
			verbose,      // be talkative
			autoMem,      // pass exceptions up to the toplevel so that we can adjust memory settings automatically
			sanity);      // verify results and internal consistency
		refparams.reverse = reverse;
		szs.clear();
		sztot = BitPairReference.szsFromFasta(is, file, bigEndian, refparams, szs, sanity);
		// Construct Ebwt from input strings and parameters
		Ebwt ebwtBw = new Ebwt(
			TStr(),
			packed,
			refparams.color ? 1 : 0,
			reverse == ReadDir.REF_READ_REVERSE,
			lineRate,
			offRate,      // suffix-array sampling rate
			ftabChars,    // number of chars in initial arrow-pair calc
			file + ".rev",// basename for .?.ebwt files
			false,        // fw?
			useBlockwise, // useBlockwise
			bmax,         // block size for blockwise SA builder
			bmaxSqrtMult, // block size as multiplier of sqrt(len)
			bmaxDivN,     // block size as divisor of len
			dcv,          // difference-cover period
			is,           // list of input streams
			szs,          // list of reference sizes
			sztot.first,  // total size of all unambiguous ref chars
			refparams,    // reference read-in parameters
			seed,         // pseudo-random number generator seed
			-1,           // override offRate
			verbose,      // be talkative
			autoMem,      // pass exceptions up to the toplevel so that we can adjust memory settings automatically
			sanity);      // verify results and internal consistency
		return new Pair(ebwtFw, ebwtBw);
	}
	
	public boolean isPacked() {
		return packed_;
	}
	
	public void szsToDisk(EList<RefRecord> szs, OutputStream os, int reverse) {
		
	}
	
	public void initFromVector(
			EList<FileBuf> is,
            EList<RefRecord> szs,
            long sztot,
            RefReadInParams refparams,
            FileOutputStream out1,
            FileOutputStream out2,
            String outfile,
            FileOutputStream saOut,
            FileOutputStream bwtOut,
            int nthreads,
            boolean useBlockwise,
            long bmax,
            long bmaxSqrtMult,
            long bmaxDivN,
            int dcv,
            int seed,
            boolean verbose){
		// Compose text strings into single string
			if(this.verbose()) {
				String tmp = "Calculating joined length" + '\n';
				this.verbose(tmp);
			}
				TStr s; // holds the entire joined reference after call to joinToDisk
				long jlen;
				jlen = joinedLen(szs);
				VMSG_NL("Writing header");
				writeFromMemory(true, out1, out2);
				try {
					if(this.verbose()) {
						String tmp = "Reserving space for joined string" + '\n';
						this.verbose(tmp);
					}
					s.resize(jlen);
					if(this.verbose()) {
						String tmp = "Joining reference sequences" + '\n';
						this.verbose(tmp);
					}
					if(refparams.reverse == ReadDir.REF_READ_REVERSE) {
						{
							joinToDisk(is, szs, sztot, refparams, s, out1, out2);
						} {
							EList<RefRecord> tmp = new EList(1);
							s.reverse();
							reverseRefRecords(szs, tmp, false, verbose);
							szsToDisk(tmp, out1, refparams.reverse);
						}
					} else {
						joinToDisk(is, szs, sztot, refparams, s, out1, out2);
						szsToDisk(szs, out1, refparams.reverse);
					}
					// Joined reference sequence now in 's'
				} catch(Exception e) {
					// If we throw an allocation exception in the try block,
					// that means that the joined version of the reference
					// string itself is too larger to fit in memory.  The only
					// alternatives are to tell the user to give us more memory
					// or to try again with a packed representation of the
					// reference (if we haven't tried that already).
					System.err.println( "Could not allocate space for a joined string of " + jlen + " elements." + "\n");
					if(!isPacked() && _passMemExc) {
						// Pass the exception up so that we can retry using a
						// packed string representation
						throw e;
					}
					// There's no point passing this exception on.  The fact
					// that we couldn't allocate the joined string means that
					// --bmax is irrelevant - the user should re-run with
					// ebwt-build-packed
					if(isPacked()) {
						System.err.println( "Please try running bowtie-build on a computer with more memory." + "\n");
					} else {
						System.err.println( "Please try running bowtie-build in packed mode (-p/--packed) or in automatic" + "\n"
						     + "mode (-a/--auto), or try again on a computer with more memory." + "\n");
					}
				}
				// Succesfully obtained joined reference string
				if(bmax != IndexTypes.OFF_MASK) {
					if(this.verbose()) {
						String tmp = "bmax according to bmax setting: " + bmax + '\n';
						this.verbose(tmp);
					}
				}
				else if(bmaxSqrtMult != IndexTypes.OFF_MASK) {
					bmax *= bmaxSqrtMult;
					if(this.verbose()) {
						String tmp = "bmax according to bmaxSqrtMult setting: " + bmax + '\n';
						this.verbose(tmp);
					}
				}
				else if(bmaxDivN != IndexTypes.OFF_MASK) {
					bmax = Long.max(jlen / bmaxDivN, 1);
					if(this.verbose()) {
						String tmp = "bmax according to bmaxDivN setting: " + bmax + '\n';
						this.verbose(tmp);
					}
				}
				else {
					bmax = (long)Math.sqrt(s.length());
					if(this.verbose()) {
						String tmp = "bmax defaulted to: " + bmax + '\n';
						this.verbose(tmp);
					}
				}
				int iter = 0;
				boolean first = true;
				long out1pos = out1.getChannel().position();
				long out2pos = out2.getChannel().position();
				// Look for bmax/dcv parameters that work.
				while(true) {
					if(!first && bmax < 40 && _passMemExc) {
						System.err.println( "Could not find approrpiate bmax/dcv settings for building this index." + "\n");;
						System.err.println( "Already tried a packed string representation." + "\n");;
						System.err.println( "Please try indexing this reference on a computer with more memory." + "\n");;
					}
					if(!first) {
						out1.getChannel().position(out1pos);
						out2.getChannel().position(out2pos);
					}
					if(dcv > 4096) dcv = 4096;
					if((iter % 6) == 5 && dcv < 4096 && dcv != 0) {
						dcv <<= 1; // double difference-cover period
					} else {
						bmax -= (bmax >> 2); // reduce by 25%
					}
					if(this.verbose()) {
						String tmp = "Using parameters --bmax " + bmax + '\n';
						this.verbose(tmp);
					}
					if(dcv == 0) {
						if(this.verbose()) {
							String tmp = " and *no difference cover*" + '\n';
							this.verbose(tmp);
						}
					} else {
						if(this.verbose()) {
							String tmp = " --dcv " << dcv + '\n';
							this.verbose(tmp);
						}
					}
					iter++;
					try {
						{
							if(this.verbose()) {
								String tmp = "  Doing ahead-of-time memory usage test" + '\n';
								this.verbose(tmp);
							}
							// Make a quick-and-dirty attempt to force a bad_alloc iff
							// we would have thrown one eventually as part of
							// constructing the DifferenceCoverSample
							dcv <<= 1;
							long sz = (long)DifferenceCoverSample<TStr>.simulateAllocs(s, dcv >> 1);
		                    if(nthreads > 1) sz *= (nthreads + 1);
							char[] tmp;
							dcv >>= 1;
							// Likewise with the KarkkainenBlockwiseSA
							sz = (long)KarkkainenBlockwiseSA<TStr>.simulateAllocs(s, bmax);
							char[] tmp2;
							// Now throw in the 'ftab' and 'isaSample' structures
							// that we'll eventually allocate in buildToDisk
							long[] ftab;
							char[] side;
							// Grab another 20 MB out of caution
							int[] extra;
							// If we made it here without throwing bad_alloc, then we
							// passed the memory-usage stress test
							if(this.verbose()) {
								String tmp = "  Passed!  Constructing with these parameters: --bmax " + bmax + " --dcv " + dcv + '\n';
								this.verbose(tmp);
							}
							if(isPacked()) {
								if(this.verbose()) {
									String tmp = " --packed" + '\n';
									this.verbose(tmp);
								}
							}
							if(this.verbose()) {
								String tmp = "" + '\n';
								this.verbose(tmp);
							}
						}
						if(this.verbose()) {
							String tmp = "Constructing suffix-array element generator" + '\n';
							this.verbose(tmp);
						}
						KarkkainenBlockwiseSA<TStr> bsa = new KarkkainenBlockwiseSA(s, bmax, nthreads, dcv, seed, _sanity, _passMemExc, _verbose, outfile);
						if(this.verbose()) {
							String tmp = "Converting suffix-array elements to index image" + '\n';
							this.verbose(tmp);
						}
						buildToDisk(bsa, s, out1, out2, saOut, bwtOut);
						out1.flush(); out2.flush();
						boolean failed = out1.fail() || out2.fail();
						if(saOut != null) {
							saOut.flush();
							failed = failed || saOut.fail();
						}
						if(bwtOut != null) {
							bwtOut.flush();
							failed = failed || bwtOut.fail();
						}
						if(failed) {
							System.err.println( "An error occurred writing the index to disk.  Please check if the disk is full." + "\n");;
						}
						break;
					} catch(Exception e) {
						if(_passMemExc) {
							if(this.verbose()) {
								String tmp = "  Ran out of memory; automatically trying more memory-economical parameters." + '\n';
								this.verbose(tmp);
							}
						} else {
							System.err.println( "Out of memory while constructing suffix array.  Please try using a smaller" + "\n"
								 + "number of blocks by specifying a smaller --bmax or a larger --bmaxdivn" + "\n");
						}
					}
					first = false;
				}
				for(int i = 0; i < this._refnames.size(); i++) {
					out1.write(this._refnames.get(i) + "\n");;
				}
				out1 .write('\0');
				out1.flush(); out2.flush();
				if(out1.fail() || out2.fail()) {
					System.err.println( "An error occurred writing the index to disk.  Please check if the disk is full." + "\n");;
				}
				if(this.verbose()) {
					String tmp = "Returning from initFromVector" + '\n';
					this.verbose(tmp);
				}
	}
	
	public long joinedLen(EList<RefRecord> szs) {
		long ret = 0;
		for(int i = 0; i < szs.size();i++) {
			ret += szs.get(i).len;
		}
		return ret;
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
	
	public boolean contains(String str, long top, long bot) {
		return contains(new BTDnaString(str, true), top, bot);
	}
	
	public boolean isInMemory() {
		if(ebwt() != null) {
			return true;
		}
		return false;
	}
	
	public boolean isEvicted() {
		return !isInMemory();
	}
	
	public void loadIntoMemory(
			int color,
			int needEntireReverse,
			boolean loadSASamp,
			boolean loadFtab,
			boolean loadRstarts,
			boolean loadNames,
			boolean verbose){
		readIntoMemory(
				color,       // expect index to be colorspace?
				needEntireReverse, // require reverse index to be concatenated reference reversed
				loadSASamp,  // load the SA sample portion?
				loadFtab,    // load the ftab (_ftab[] and _eftab[])?
				loadRstarts, // load the r-starts (_rstarts[])?
				false,       // stop after loading the header portion?
				null,        // params
				false,       // mmSweep
				loadNames,   // loadNames
				verbose);    // startVerbose
	}
	
	public void evictFromMemory() {
		_zEbwtByteOff = IndexTypes.IndexTypes.OFF_MASK;
		_zEbwtBpOff = -1;
	}
	
	public EbwtParams eh() {
		return _eh;
	}
	
	public long ftabSeqToInt(BTDnaString seq, int off, boolean rev) {
		int fc = _eh._ftabChars;
		int lo = off, hi = lo + fc;
		long ftabOff = 0;
		for(int i = 0; i < fc; i++) {
			boolean fwex = fw();
			if(rev) fwex = !fwex;
			// We add characters to the ftabOff in the order they would
			// have been consumed in a normal search.  For BWT, this
			// means right-to-left order; for BWT' it's left-to-right.
			int c = (fwex ? seq[lo + i] : seq[hi - i - 1]);
			if(c > 3) {
				return Long.MAX_VALUE;
			}
			ftabOff <<= 2;
			ftabOff |= c;
		}
		return ftabOff;
	}
	
	public long ftabHi(BTDnaString seq, int off) {
		return ftabHi(ftabSeqToInt(seq, off, false));
	}
	
	public boolean ftabLoHi(
			BTDnaString seq, // sequence to extract from
			int off,             // offset into seq to begin extracting
			boolean rev,               // reverse while extracting
			long top,
			long bot)
			) {
		long fi = ftabSeqToInt(seq, off, rev);
		if(fi == Long.MAX_VALUE) {
			return false;
		}
		top = ftabHi(fi);
		bot = ftabLo(fi+1);
		return true;
	}
	
	public long ftabHi(long i) {
		return Ebwt.ftabHi(
				ftab(),
				eftab(),
				_eh._len,
				_eh._ftabLen,
				_eh._eftabLen,
				i);
	}
	
	public static long ftabHi(
			long ftab[],
			long eftab[],
			long len,
			long ftabLen,
			long eftabLen,
			long i) {
		if(ftab[i] <= len) {
			return ftab[i];
		} else {
			long efIdx = ftab[i] ^ IndexTypes.IndexTypes.OFF_MASK;
			return eftab[efIdx*2+1];
		}
	}
	
	public static long ftabLo(
			long ftab[],
			long eftab[],
			long len,
			long ftabLen,
			long eftabLen,
			long i) {
		if(ftab[i] <= len) {
			return ftab[i];
		} else {
			long efIdx = ftab[i] ^ IndexTypes.OFF_MASK;
			return eftab[efIdx*2];
		}
	}
	
	public long ftabLo(long i) {
		return Ebwt.ftabLo(
				ftab(),
				eftab(),
				_eh._len,
				_eh._ftabLen,
				_eh._eftabLen,
				i);
	}
	
	public long ftabLo(BTDnaString seq, int off) {
		return ftabLo(ftabSeqToInt(seq, off, false));
	}
	
	public long tryOffset(long elt) {
		if(elt == _zOff) return 0;
		if((elt & _eh._offMask) == elt) {
			long eltOff = elt >> _eh._offRate;
			long off = offs()[eltOff];
			return off;
		} else {
			// Try looking at zoff
			return IndexTypes.IndexTypes.OFF_MASK;
		}
	}
	
	public long tryOffset(long elt, boolean fw, long hitlen) {
		long off = tryOffset(elt);
		if(off != IndexTypes.IndexTypes.OFF_MASK && !fw) {
			off = _eh._len - off - 1;
			off -= (hitlen-1);
		}
		return off;
	}
	
	public long walkLeft(long row, long steps) {
		SideLocus l;
		if(steps > 0) l.initFromRow(row, _eh, ebwt());
		while(steps > 0) {
			if(row == _zOff) return IndexTypes.IndexTypes.OFF_MASK;
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
	
	public void postReadInit(EbwtParams eh) {
		long sideNum     = _zOff / eh._sideBwtLen;
		long sideCharOff = _zOff % eh._sideBwtLen;
		long sideByteOff = sideNum * eh._sideSz;
		_zEbwtByteOff = sideCharOff >> 2;
		_zEbwtBpOff = sideCharOff & 3;
		_zEbwtByteOff += sideByteOff;
	}
	
	public static int readFlags(String instr) {
		
	}
	
	public void print(OutputStream out) {
		print(out, _eh);
	}
	
	public void print(OutputStream out, EbwtParams eh) {
		eh.print(out); // print params
		out.write(("Ebwt (" + (isInMemory()? "memory" : "disk") + "):" + "\n"
		    + "    zOff: "         + _zOff + "\n"
		    + "    zEbwtByteOff: " + _zEbwtByteOff + "\n"
		    + "    zEbwtBpOff: "   + _zEbwtBpOff + "\n"
		    + "    nPat: "  + _nPat + "\n"
		    + "    plen: ").getBytes());
		if(plen() == null) {
			out.write( "null" + "\n");
		} else {
			out.write( "non-null, [0] = " + plen()[0] + "\n");
		}
		out.write( "    rstarts: ";
		if(rstarts() == null) {
			out.write( "null" + "\n");
		} else {
			out.write( "non-null, [0] = " + rstarts()[0] + "\n");
		}
		out.write( "    ebwt: ";
		if(ebwt() == null) {
			out.write( "null" + "\n");
		} else {
			out.write( "non-null, [0] = " + ebwt()[0] + "\n");
		}
		out.write( "    fchr: ";
		if(fchr() == null) {
			out.write( "null" + "\n");
		} else {
			out.write( "non-null, [0] = " + fchr()[0] + "\n");
		}
		out.write( "    ftab: ";
		if(ftab() == null) {
			out.write( "null" + "\n");
		} else {
			out.write( "non-null, [0] = " + ftab()[0] + "\n");
		}
		out.write( "    eftab: ";
		if(eftab() == null) {
			out.write( "null" + "\n");
		} else {
			out.write( "non-null, [0] = " + eftab()[0] + "\n");
		}
		out.write( "    offs: ";
		if(offs() == null) {
			out.write( "null" + "\n");
		} else {
			out.write( "non-null, [0] = " + offs()[0] + "\n");
		}
	}
	
	public TStr join(EList<TStr> l, int seed) {
		RandomSource rand; // reproducible given same seed
		rand.init(seed);
		TStr ret;
		long guessLen = 0;
		for(long i = 0; i < l.size(); i++) {
			guessLen += length(l[i]);
		}
		ret.resize(guessLen);
		long off = 0;
		for(int i = 0; i < l.size(); i++) {
			TStr s = l[i];
			for(int j = 0; j < s.size(); j++) {
				ret.set(s.get(j), off++);
			}
		}
		return ret;
	}
	
	public TStr join(
			EList<FileBuf> l,
            EList<RefRecord> szs,
            long sztot,
            RefReadInParams refparams,
            int seed) {
		RandomSource rand; // reproducible given same seed
		rand.init(seed);
		RefReadInParams rpcp = refparams;
		TStr ret;
		long guessLen = sztot;
		ret.resize(guessLen);
		long dstoff = 0;
		for(int i = 0; i < l.size(); i++) {
			// For each sequence we can pull out of istream l[i]...
			boolean first = true;
			while(!l.get(i).eof()) {
				RefRecord rec = fastaRefReadAppend(l.get(i), first, ret, dstoff, rpcp);
				first = false;
				long bases = rec.len;
				if(bases == 0) continue;
			}
		}
		return ret;
	}
	
	public void joinToDisk(
			EList<FileBuf> l,
			EList<RefRecord> szs,
			long sztot,
			RefReadInParams refparams,
			TStr ret,
			OutputStream out1,
			OutputStream out2
			) {
		RefReadInParams rpcp = refparams;
		// Not every fragment represents a distinct sequence - many
		// fragments may correspond to a single sequence.  Count the
		// number of sequences here by counting the number of "first"
		// fragments.
		this._nPat = 0;
		this._nFrag = 0;
		for(int i = 0; i < szs.size(); i++) {
			if(szs.get(i).len > 0) this._nFrag++;
			if(szs.get(i).first && szs.get(i).len > 0) this._nPat++;
		}
		_rstarts.reset();
		writeU<Long>(out1, this._nPat, this.toBe());
		// Allocate plen[]
		try {
			this._plen.init(new long[this._nPat], this._nPat);
		} catch(bad_alloc& e) {
			System.err.println( "Out of memory allocating plen[] in Ebwt::join()"
			     << " at " << __FILE__ << ":" << __LINE__ + "\n");;
			throw e;
		}
		// For each pattern, set plen
		long npat = -1;
		for(long i = 0; i < szs.size(); i++) {
			if(szs.get(i).first && szs.get(i).len > 0) {
				if(npat >= 0) {
					writeU<Long>(out1, this.plen()[npat], this.toBe());
				}
				this.plen()[++npat] = (szs.get(i).len + szs.get(i).off);
			} else {
				// edge case, but we could get here with npat == -1
				// e.g. when building from a reference of all Ns
				if (npat < 0) npat = 0;
				this.plen()[npat] += (szs.get(i).len + szs.get(i).off);
			}
		}
		writeU<long>(out1, this.plen()[npat], this.toBe());
		// Write the number of fragments
		writeU<long>(out1, this._nFrag, this.toBe());
		long seqsRead = 0;
		long dstoff = 0;
		// For each filebuf
		for(int i = 0; i < l.size(); i++) {
			boolean first = true;
			long patoff = 0;
			// For each *fragment* (not necessary an entire sequence) we
			// can pull out of istream l[i]...
			while(!l.get(i).eof()) {
				String name;
				// Push a new name onto our vector
				_refnames.push_back("");
				RefRecord rec = fastaRefReadAppend(
					l.get(i), first, ret, dstoff, rpcp, _refnames.back());
				first = false;
				long bases = rec.len;
				if(rec.first && rec.len > 0) {
					if(_refnames.back().length() == 0) {
						// If name was empty, replace with an index
						OutputStream stm;
						stm.write(seqsRead);
						_refnames.back() = stm.str();
					}
				} else {
					// This record didn't actually start a new sequence so
					// no need to add a name
					//assert_eq(0, _refnames.back().length());
					_refnames.pop_back();
				}
				// Increment seqsRead if this is the first fragment
				if(rec.first && rec.len > 0) seqsRead++;
				if(bases == 0) continue;
				assert_leq(bases, this.plen()[seqsRead-1]);
				// Reset the patoff if this is the first fragment
				if(rec.first) patoff = 0;
				patoff += rec.off; // add fragment's offset from end of last frag.
				// Adjust rpcps
				//uint32_t seq = seqsRead-1;
				// This is where rstarts elements are written to the output stream
				//writeU32(out1, oldRetLen, this.toBe()); // offset from beginning of joined string
				//writeU32(out1, seq,       this.toBe()); // sequence id
				//writeU32(out1, patoff,    this.toBe()); // offset into sequence
				patoff += bases;
			}
			l.get(i).reset();
		}
	}
	
	public void buildToDisk(
			InorderBlockwiseSA<TStr> sa,
			TStr s,
			OutputStream out1,
			OutputStream out2,
			OutputStream saOut,
			OutputStream bwtOut
			) {
		EbwtParams& eh = this._eh;

		assert(eh.repOk());
		assert_eq(s.length()+1, sa.size());
		assert_eq(s.length(), eh._len);
		assert_gt(eh._lineRate, 3);
		assert(sa.suffixItrIsReset());

		long len = eh._len;
		long ftabLen = eh._ftabLen;
		long sideSz = eh._sideSz;
		long ebwtTotSz = eh._ebwtTotSz;
		long fchr[] = {0, 0, 0, 0, 0};
		EList<long> ftab(EBWT_CAT);
		long zOff = IndexTypes.OFF_MASK;

		// Save # of occurrences of each character as we walk along the bwt
		long occ[4] = {0, 0, 0, 0};
		long occSave[4] = {0, 0, 0, 0};

		// Record rows that should "absorb" adjacent rows in the ftab.
		// The absorbed rows represent suffixes shorter than the ftabChars
		// cutoff.
		uint8_t absorbCnt = 0;
		EList<uint8_t> absorbFtab(EBWT_CAT);
		try {
			VMSG_NL("Allocating ftab, absorbFtab");
			ftab.resize(ftabLen);
			ftab.fillZero();
			absorbFtab.resize(ftabLen);
			absorbFtab.fillZero();
		} catch(bad_alloc &e) {
			System.err.println( "Out of memory allocating ftab[] or absorbFtab[] "
			     << "in Ebwt::buildToDisk() at " << __FILE__ << ":"
			     << __LINE__ + "\n");;
			throw e;
		}

		// Allocate the side buffer; holds a single side as its being
		// constructed and then written to disk.  Reused across all sides.
	#ifdef SIXTY4_FORMAT
		EList<ulong> ebwtSide(EBWT_CAT);
	#else
		EList<uint8_t> ebwtSide(EBWT_CAT);
	#endif
		try {
	#ifdef SIXTY4_FORMAT
			ebwtSide.resize(sideSz >> 3);
	#else
			ebwtSide.resize(sideSz);
	#endif
		} catch(bad_alloc &e) {
			System.err.println( "Out of memory allocating ebwtSide[] in "
			     << "Ebwt::buildToDisk() at " << __FILE__ << ":"
			     << __LINE__ + "\n");;
			throw e;
		}

		// Points to the base offset within ebwt for the side currently
		// being written
		long side = 0;

		// Whether we're assembling a forward or a reverse bucket
		boolean fw;
		long sideCur = 0;
		fw = true;

		// Have we skipped the '$' in the last column yet?
		ASSERT_ONLY(boolean dollarSkipped = false);

		long si = 0;   // string offset (chars)
		ASSERT_ONLY(long lastSufInt = 0);
		ASSERT_ONLY(boolean inSA = true); // true iff saI still points inside suffix
		                               // array (as opposed to the padding at the
		                               // end)
		// Iterate over packed bwt bytes
		VMSG_NL("Entering Ebwt loop");
		ASSERT_ONLY(long beforeEbwtOff = (long)out1.getChannel().position()); // @double-check - pos_type, std::streampos 
		
		// First integer in the suffix-array output file is the length of the
		// array, including $
		if(saOut != null) {
			// Write length word
			writeU<long>(*saOut, len+1, this.toBe());
		}
		
		// First integer in the BWT output file is the length of BWT(T), including $
		if(bwtOut != null) {
			// Write length word
			writeU<long>(*bwtOut, len+1, this.toBe());
		}
		
		while(side < ebwtTotSz) {
			// Sanity-check our cursor into the side buffer
			assert_geq(sideCur, 0);
			assert_lt(sideCur, (int)eh._sideBwtSz);
			assert_eq(0, side % sideSz); // 'side' must be on side boundary
			ebwtSide[sideCur] = 0; // clear
			assert_lt(side + sideCur, ebwtTotSz);
			// Iterate over bit-pairs in the si'th character of the BWT
	#ifdef SIXTY4_FORMAT
			for(int bpi = 0; bpi < 32; bpi++, si++)
	#else
			for(int bpi = 0; bpi < 4; bpi++, si++)
	#endif
			{
				int bwtChar;
				boolean count = true;
				if(si <= len) {
					// Still in the SA; extract the bwtChar
					long saElt = sa.nextSuffix();
					// Write it to the optional suffix-array output file
					if(saOut != null) {
						writeU<long>(*saOut, saElt, this.toBe());
					}
					// TODO: what exactly to write to the BWT output file?  How to
					// represent $?  How to pack nucleotides into bytes/words?
					
					// (that might have triggered sa to calc next suf block)
					if(saElt == 0) {
						// Don't add the '$' in the last column to the BWT
						// transform; we can't encode a $ (only A C T or G)
						// and counting it as, say, an A, will mess up the
						// LR mapping
						bwtChar = 0; count = false;
						ASSERT_ONLY(dollarSkipped = true);
						zOff = si; // remember the SA row that
						           // corresponds to the 0th suffix
					} else {
						bwtChar = (int)(s[saElt-1]);
						assert_lt(bwtChar, 4);
						// Update the fchr
						fchr[bwtChar]++;
					}
					// Update ftab
					if((len-saElt) >= (long)eh._ftabChars) {
						// Turn the first ftabChars characters of the
						// suffix into an integer index into ftab.  The
						// leftmost (lowest index) character of the suffix
						// goes in the most significant bit pair if the
						// integer.
						long sufInt = 0;
						for(int i = 0; i < eh._ftabChars; i++) {
							sufInt <<= 2;
							assert_lt((long)i, len-saElt);
							sufInt |= (unsigned char)(s[saElt+i]);
						}
						// Assert that this prefix-of-suffix is greater
						// than or equal to the last one (true b/c the
						// suffix array is sorted)
						#ifndef NDEBUG
						if(lastSufInt > 0) assert_geq(sufInt, lastSufInt);
						lastSufInt = sufInt;
						#endif
						// Update ftab
						assert_lt(sufInt+1, ftabLen);
						ftab[sufInt+1]++;
						if(absorbCnt > 0) {
							// Absorb all short suffixes since the last
							// transition into this transition
							absorbFtab[sufInt] = absorbCnt;
							absorbCnt = 0;
						}
					} else {
						// Otherwise if suffix is fewer than ftabChars
						// characters long, then add it to the 'absorbCnt';
						// it will be absorbed into the next transition
						assert_lt(absorbCnt, 255);
						absorbCnt++;
					}
					// Suffix array offset boundary? - update offset array
					if((si & eh._offMask) == si) {
						assert_lt((si >> eh._offRate), eh._offsLen);
						// Write offsets directly to the secondary output
						// stream, thereby avoiding keeping them in memory
						writeU<long>(out2, saElt, this.toBe());
					}
				} else {
					// Strayed off the end of the SA, now we're just
					// padding out a bucket
					#ifndef NDEBUG
					if(inSA) {
						// Assert that we wrote all the characters in the
						// string before now
						assert_eq(si, len+1);
						inSA = false;
					}
					#endif
					// 'A' used for padding; important that padding be
					// counted in the occ[] array
					bwtChar = 0;
				}
				if(count) occ[bwtChar]++;
				// Append BWT char to bwt section of current side
				if(fw) {
					// Forward bucket: fill from least to most
	#ifdef SIXTY4_FORMAT
					ebwtSide[sideCur] |= ((ulong)bwtChar << (bpi << 1));
					if(bwtChar > 0) assert_gt(ebwtSide[sideCur], 0);
	#else
					pack_2b_in_8b(bwtChar, ebwtSide[sideCur], bpi);
					assert_eq((ebwtSide[sideCur] >> (bpi*2)) & 3, bwtChar);
	#endif
				} else {
					// Backward bucket: fill from most to least
	#ifdef SIXTY4_FORMAT
					ebwtSide[sideCur] |= ((ulong)bwtChar << ((31 - bpi) << 1));
					if(bwtChar > 0) assert_gt(ebwtSide[sideCur], 0);
	#else
					pack_2b_in_8b(bwtChar, ebwtSide[sideCur], 3-bpi);
					assert_eq((ebwtSide[sideCur] >> ((3-bpi)*2)) & 3, bwtChar);
	#endif
				}
			} // end loop over bit-pairs
			assert_eq(dollarSkipped ? 3 : 0, (occ[0] + occ[1] + occ[2] + occ[3]) & 3);
	#ifdef SIXTY4_FORMAT
			assert_eq(0, si & 31);
	#else
			assert_eq(0, si & 3);
	#endif

			sideCur++;
			if(sideCur == (int)eh._sideBwtSz) {
				sideCur = 0;
				long *cpptr = reinterpret_cast<long*>(ebwtSide.ptr());
				// Write 'A', 'C', 'G' and 'T' tallies
				side += sideSz;
				assert_leq(side, eh._ebwtTotSz);
	#ifdef BOWTIE_64BIT_INDEX
				cpptr[(sideSz >> 3)-4] = endianizeU<long>(occSave[0], this.toBe());
				cpptr[(sideSz >> 3)-3] = endianizeU<long>(occSave[1], this.toBe());
				cpptr[(sideSz >> 3)-2] = endianizeU<long>(occSave[2], this.toBe());
				cpptr[(sideSz >> 3)-1] = endianizeU<long>(occSave[3], this.toBe());
	#else
				cpptr[(sideSz >> 2)-4] = endianizeU<long>(occSave[0], this.toBe());
				cpptr[(sideSz >> 2)-3] = endianizeU<long>(occSave[1], this.toBe());
				cpptr[(sideSz >> 2)-2] = endianizeU<long>(occSave[2], this.toBe());
				cpptr[(sideSz >> 2)-1] = endianizeU<long>(occSave[3], this.toBe());
	#endif
				occSave[0] = occ[0];
				occSave[1] = occ[1];
				occSave[2] = occ[2];
				occSave[3] = occ[3];
				// Write backward side to primary file
				out1.write((char *)ebwtSide.ptr(), sideSz);
			}
		}
		VMSG_NL("Exited Ebwt loop");
		assert_neq(zOff, IndexTypes.OFF_MASK);
		if(absorbCnt > 0) {
			// Absorb any trailing, as-yet-unabsorbed short suffixes into
			// the last element of ftab
			absorbFtab[ftabLen-1] = absorbCnt;
		}
		// Assert that our loop counter got incremented right to the end
		assert_eq(side, eh._ebwtTotSz);
		// Assert that we wrote the expected amount to out1
		assert_eq(((long)out1.getChannel().position() - beforeEbwtOff), eh._ebwtTotSz); // @double-check - pos_type
		// assert that the last thing we did was write a forward bucket

		//
		// Write zOff to primary stream
		//
		writeU<long>(out1, zOff, this.toBe());

		//
		// Finish building fchr
		//
		// Exclusive prefix sum on fchr
		for(int i = 1; i < 4; i++) {
			fchr[i] += fchr[i-1];
		}
		assert_eq(fchr[3], len);
		// Shift everybody up by one
		for(int i = 4; i >= 1; i--) {
			fchr[i] = fchr[i-1];
		}
		fchr[0] = 0;
		if(_verbose) {
			for(int i = 0; i < 5; i++)
				cout.write( "fchr[" << "ACGT$"[i] << "]: " << fchr[i] + "\n");;
		}
		// Write fchr to primary file
		for(int i = 0; i < 5; i++) {
			writeU<long>(out1, fchr[i], this.toBe());
		}

		//
		// Finish building ftab and build eftab
		//
		// Prefix sum on ftable
		long eftabLen = 0;
		assert_eq(0, absorbFtab[0]);
		for(long i = 1; i < ftabLen; i++) {
			if(absorbFtab[i] > 0) eftabLen += 2;
		}
		assert_leq(eftabLen, (long)eh._ftabChars*2);
		eftabLen = eh._ftabChars*2;
		EList<long> eftab(EBWT_CAT);
		try {
			eftab.resize(eftabLen);
			eftab.fillZero();
		} catch(bad_alloc &e) {
			System.err.println( "Out of memory allocating eftab[] "
			     << "in Ebwt::buildToDisk() at " << __FILE__ << ":"
			     << __LINE__ + "\n");;
			throw e;
		}
		long eftabCur = 0;
		for(long i = 1; i < ftabLen; i++) {
			long lo = ftab[i] + Ebwt::ftabHi(ftab.ptr(), eftab.ptr(), len, ftabLen, eftabLen, i-1);
			if(absorbFtab[i] > 0) {
				// Skip a number of short pattern indicated by absorbFtab[i]
				long hi = lo + absorbFtab[i];
				assert_lt(eftabCur*2+1, eftabLen);
				eftab[eftabCur*2] = lo;
				eftab[eftabCur*2+1] = hi;
				ftab[i] = (eftabCur++) ^ IndexTypes.OFF_MASK; // insert pointer into eftab
				assert_eq(lo, Ebwt::ftabLo(ftab.ptr(), eftab.ptr(), len, ftabLen, eftabLen, i));
				assert_eq(hi, Ebwt::ftabHi(ftab.ptr(), eftab.ptr(), len, ftabLen, eftabLen, i));
			} else {
				ftab[i] = lo;
			}
		}
		assert_eq(Ebwt::ftabHi(ftab.ptr(), eftab.ptr(), len, ftabLen, eftabLen, ftabLen-1), len+1);
		// Write ftab to primary file
		for(long i = 0; i < ftabLen; i++) {
			writeU<long>(out1, ftab[i], this.toBe());
		}
		// Write eftab to primary file
		for(long i = 0; i < eftabLen; i++) {
			writeU<long>(out1, eftab[i], this.toBe());
		}

		// Note: if you'd like to sanity-check the Ebwt, you'll have to
		// read it back into memory first!
		assert(!isInMemory());
		VMSG_NL("Exiting Ebwt::buildToDisk()");
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
		long elt = IndexTypes.IndexTypes.OFF_MASK;
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
							tidx = IndexTypes.IndexTypes.OFF_MASK;
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
	
	public long countBt2Side(SideLocus l, int c) {
		byte[] side = l.side(this.ebwt());
		long cCnt = countUpTo(l, c);
		if(c == 0 && l._sideByteOff <= _zEbwtByteOff && l._sideByteOff + l._by >= _zEbwtByteOff) {
			// Adjust for the fact that we represented $ with an 'A', but
			// shouldn't count it as an 'A' here
			if((l._sideByteOff + l._by > _zEbwtByteOff) ||
			   (l._sideByteOff + l._by == _zEbwtByteOff && l._bp > _zEbwtBpOff))
			{
				cCnt--; // Adjust for '$' looking like an 'A'
			}
		}
		long ret;
		// Now factor in the occ[] count at the side break
		byte[] acgt8 = side + _eh._sideBwtSz;
		long[] acgt = reinterpret_cast<Long>(acgt8);
		ret = acgt[c] + cCnt + this.fchr()[c];
		return ret;
	}
	
	public long    zOff()         { return _zOff; }
	public long    zEbwtByteOff() { return _zEbwtByteOff; }
	public long    zEbwtBpOff()   { return _zEbwtBpOff; } 
	public long    nPat()         { return _nPat; }
	public long    nFrag()        { return _nFrag; }
	public long   ftab()              { return _ftab.get(); }
	public long   eftab()             { return _eftab.get(); }
	public long   offs()              { return _offs.get(); }
	public long   plen()              { return _plen.get(); }
	public long   rstarts()           { return _rstarts.get(); }
	public boolean        toBe()         { return _toBigEndian; }
	public boolean        sanityCheck()  { return _sanity; }
	public EList<String> refnames()        { return _refnames; }
	public boolean        fw()           { return fw_; }
	
	public void countBt2SideRange(SideLocus l, long num, long[] cntsUpto, long[] cntsIn, EList<boolean> masks) {
		countUpToEx(l, cntsUpto);
		WITHIN_FCHR_DOLLARA(cntsUpto);
		WITHIN_BWT_LEN(cntsUpto);
		byte[] side = l.side(this.ebwt());
		if(l._sideByteOff <= _zEbwtByteOff && l._sideByteOff + l._by >= _zEbwtByteOff) {
			// Adjust for the fact that we represented $ with an 'A', but
			// shouldn't count it as an 'A' here
			if((l._sideByteOff + l._by > _zEbwtByteOff) ||
			   (l._sideByteOff + l._by == _zEbwtByteOff && l._bp > _zEbwtBpOff))
			{
				cntsUpto[0]--; // Adjust for '$' looking like an 'A'
			}
		}
		// Now factor in the occ[] count at the side break
		long acgt = reinterpret_cast<Long>(side + _eh._sideBwtSz);
		cntsUpto[0] += (acgt[0] + this.fchr()[0]);
		cntsUpto[1] += (acgt[1] + this.fchr()[1]);
		cntsUpto[2] += (acgt[2] + this.fchr()[2]);
		cntsUpto[3] += (acgt[3] + this.fchr()[3]);
		masks[0].resize(num);
		masks[1].resize(num);
		masks[2].resize(num);
		masks[3].resize(num);
		WITHIN_FCHR_DOLLARA(cntsUpto);
		WITHIN_FCHR_DOLLARA(cntsIn);
		// 'cntsUpto' is complete now.
		// Walk forward until we've tallied the entire 'In' range
		long nm = 0;
		// Rest of this side
		nm += countBt2SideRange2(l, true, num - nm, cntsIn, masks, nm);
		SideLocus lcopy = l;
		while(nm < num) {
			// Subsequent sides, if necessary
			lcopy.nextSide(this._eh);
			nm += countBt2SideRange2(lcopy, false, num - nm, cntsIn, masks, nm);
			WITHIN_FCHR_DOLLARA(cntsIn);
		}
		WITHIN_FCHR_DOLLARA(cntsIn);
	}
	
	public void countBt2SideEx(SideLocus l, long[] arrs) {
		countUpToEx(l, arrs);
		if(l._sideByteOff <= _zEbwtByteOff && l._sideByteOff + l._by >= _zEbwtByteOff) {
			// Adjust for the fact that we represented $ with an 'A', but
			// shouldn't count it as an 'A' here
			if((l._sideByteOff + l._by > _zEbwtByteOff) ||
			   (l._sideByteOff + l._by == _zEbwtByteOff && l._bp > _zEbwtBpOff))
			{
				arrs[0]--; // Adjust for '$' looking like an 'A'
			}
		}
		WITHIN_FCHR(arrs);
		WITHIN_BWT_LEN(arrs);
		// Now factor in the occ[] count at the side break
		byte[] side = l.side(this.ebwt());
		byte[] acgt16 = side + this._eh._sideSz - OFF_SIZE*4;
		long[] acgt = reinterpret_cast<Long>(acgt16);
		arrs[0] += (acgt[0] + this.fchr()[0]);
		arrs[1] += (acgt[1] + this.fchr()[1]);
		arrs[2] += (acgt[2] + this.fchr()[2]);
		arrs[3] += (acgt[3] + this.fchr()[3]);
		WITHIN_FCHR(arrs);
	}
	
	public long countUpTo(SideLocus l, int c) {
		// Count occurrences of c in each 64-bit (using bit trickery);
				// Someday countInU64() and pop() functions should be
				// vectorized/SSE-ized in case that helps.
				long cCnt = 0;
				byte[] side = l.side(this.ebwt());
				int i = 0;
		        for(; i + 7 < l._by; i += 8) {
		            cCnt += countInU64(c, (long)side[i]);
		        }	
				// Count occurences of c in the rest of the side (using LUT)
				for(; i < l._by; i++) {
					cCnt += cCntLUT_4[0][c][side[i]];
				}
				// Count occurences of c in the rest of the byte
				if(l._bp > 0) {
					cCnt += cCntLUT_4[(int)l._bp][c][side[i]];
				}
				return cCnt;
	}
	
	public void countUpToEx(SideLocus l, long[] arrs) {
		int i = 0;
		// Count occurrences of each nucleotide in each 64-bit word using
		// bit trickery; note: this seems does not seem to lend a
		// significant boost to performance in practice.  If you comment
		// out this whole loop (which won't affect correctness - it will
		// just cause the following loop to take up the slack) then runtime
		// does not change noticeably. Someday the countInU64() and pop()
		// functions should be vectorized/SSE-ized in case that helps.
		byte[] side = l.side(this.ebwt());

		for(; i+7 < l._by; i += 8) {
			countInU64Ex((long)side[i], arrs);
		}
		// Count occurences of nucleotides in the rest of the side (using LUT)
		// Many cache misses on following lines (~20K)
		for(; i < l._by; i++) {
			arrs[0] += cCntLUT_4[0][0][side[i]];
			arrs[1] += cCntLUT_4[0][1][side[i]];
			arrs[2] += cCntLUT_4[0][2][side[i]];
			arrs[3] += cCntLUT_4[0][3][side[i]];
		}
		// Count occurences of c in the rest of the byte
		if(l._bp > 0) {
			arrs[0] += cCntLUT_4[(int)l._bp][0][side[i]];
			arrs[1] += cCntLUT_4[(int)l._bp][1][side[i]];
			arrs[2] += cCntLUT_4[(int)l._bp][2][side[i]];
			arrs[3] += cCntLUT_4[(int)l._bp][3][side[i]];
		}
	}
	
	public void mapLFEx(SideLocus l, long[] arrs) {
		countBt2SideEx(l, arrs);
	}
	
	public void mapLFEx(long top, long bot, long[] tops, long[] bots) {
		SideLocus ltop, lbot;
		SideLocus.initFromTopBot(top, bot, _eh, ebwt(), ltop, lbot);
		mapLFEx(ltop, lbot, tops, bots);
	}
	
	public void mapLFEx(SideLocus ltop, SIdeLocus lbot, long[] tops, long[] bots) {
		countBt2SideEx(ltop, tops);
		countBt2SideEx(lbot, bots);
	}
	
	public long countBt2SideRange2(
			SideLocus l,
			boolean startAtLocus,
			long num,
			long[] arrs,
			EList<boolean> masks,
			long maskOff){
		long nm = 0; // number of nucleotides tallied so far
		int iby = 0;      // initial byte offset
		int ibp = 0;      // initial base-pair offset
		if(startAtLocus) {
			iby = l._by;
			ibp = l._bp;
		} else {
			// Start at beginning
		}
		int by = iby, bp = ibp;
		byte[] side = l.side(this.ebwt());
		while(nm < num) {
			int c = (side[by] >> (bp * 2)) & 3;
			masks[0][maskOff + nm] = masks[1][maskOff + nm] =
			masks[2][maskOff + nm] = masks[3][maskOff + nm] = false;
			// Note: we tally $ just like an A
			arrs[c]++; // tally it
			masks[c][maskOff + nm] = true; // not dead
			nm++;
			if(++bp == 4) {
				bp = 0;
				by++;
				if(by == (int)this._eh._sideBwtSz) {
					// Fell off the end of the side
					break;
				}
			}
		}
		WITHIN_FCHR_DOLLARA(arrs);
		return nm;
	}
	
	public int rowL(SideLocus l) {
		// Extract and return appropriate bit-pair
		return unpack_2b_from_8b(l.side(this.ebwt())[l._by], l._bp);
	}
	
	public int rowL(long i) {
		// Extract and return appropriate bit-pair
		SideLocus l;
		l.initFromRow(i, _eh, ebwt());
		return rowL(l);
	}
	
	public void mapLFRange(SideLocus ltop, SIdeLocus lbot, long num, long[] cntsUpto, long[] cntsIn, EList<boolean> masks) {
		countBt2SideRange(ltop, num, cntsUpto, cntsIn, masks);
		if(_sanity && !overrideSanity) {
			// Make sure results match up with individual calls to mapLF;
			// be sure to override sanity-checking in the callee, or we'll
			// have infinite recursion
			TIndexOffU tops[4] = {0, 0, 0, 0};
			TIndexOffU bots[4] = {0, 0, 0, 0};

			mapLFEx(ltop, lbot, tops, bots, false);
		}
	}
	
	public long mapLF(SideLocus l) {
		long ret;
		int c = rowL(l);
		ret = countBt2Side(l, c);
		if(_sanity && !overrideSanity) {
			// Make sure results match up with results from mapLFEx;
			// be sure to override sanity-checking in the callee, or we'll
			// have infinite recursion
			TIndexOffU arrs[] = { 0, 0, 0, 0 };
			mapLFEx(l, arrs, true);
		}
		return ret;
	}
	
	public long mapLF(SideLocus l, int c) {
		long ret;
		ret = countBt2Side(l, c);
		if(_sanity && !overrideSanity) {
			// Make sure results match up with results from mapLFEx;
			// be sure to override sanity-checking in the callee, or we'll
			// have infinite recursion
			TIndexOffU arrs[] = { 0, 0, 0, 0 };
			mapLFEx(l, arrs, true);
		}
		return ret;
	}
	
	public void mapBiLFEx(SideLocus ltop, SIdeLocus lbot, long[] tops, long[] bots, long[] topsP, long[] botsP) {
		countBt2SideEx(ltop, tops);
		countBt2SideEx(lbot, bots);
		
		// bots[0..3] - tops[0..3] = # of ways to extend the suffix with an
		// A, C, G, T
		botsP[0] = topsP[0] + (bots[0] - tops[0]);
		topsP[1] = botsP[0];
		botsP[1] = topsP[1] + (bots[1] - tops[1]);
		topsP[2] = botsP[1];
		botsP[2] = topsP[2] + (bots[2] - tops[2]);
		topsP[3] = botsP[2];
		botsP[3] = topsP[3] + (bots[3] - tops[3]);
	}
	
	public long mapLF1(long row, SideLocus l, int c) {
		if(rowL(l) != c || row == _zOff) return IndexTypes.IndexTypes.OFF_MASK;
		long ret = countBt2Side(l, c);
		if(_sanity && !overrideSanity) {
			// Make sure results match up with results from mapLFEx;
			// be sure to override sanity-checking in the callee, or we'll
			// have infinite recursion
			TIndexOffU arrs[] = { 0, 0, 0, 0 };
			mapLFEx(l, arrs, true);
		}
		return ret;
	}
	
	public int mapLF1(long row, SideLocus l) {
		if(row == _zOff) return -1;
		int c = rowL(l);
		row = countBt2Side(l, c);
		if(_sanity && !overrideSanity) {
			// Make sure results match up with results from mapLFEx;
			// be sure to override sanity-checking in the callee, or we'll
			// have infinite recursion
			TIndexOffU arrs[] = { 0, 0, 0, 0 };
			mapLFEx(l, arrs, true);
		}
		return c;
	}
	
	public boolean is_read_err(int fdesc, int ret, int count) {
		if (ret < 0) {
			gLastIOErrMsg = "ERRNO: " + errno + " ERR Msg:" + strerror(errno) + "\n";
			return true;
		}
		return false;
	}
	
	public boolean is_fread_err(File file_hd, int ret, int count) {
		if(file_hd.canRead()) {
			gLastIOErrMsg = "Error Reading File!";
			return true;
		}
		return false;
	}
			
	public long[] fchr() {
		return _fchr.get();
	}
	
	public byte ebwt() {
		return _ebwt.get();
	}
	
	private PrintStream log() {
		return System.out;
	}
	
	public boolean verbose() {
		return _verbose;
	}
	
	private void verbose(String s) {
		if(verbose()) {
			log().println(s);
			log().flush();
		}
	}
}