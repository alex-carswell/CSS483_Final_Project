package com.uwb.bt2j.indexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import com.uwb.bt2j.indexer.types.EList;
import com.uwb.bt2j.indexer.types.SideLocus;

public class EBWT <TStr>{
	public static final String gEbwt_ext = "bt2";
	public String gLastIOErrMsg;
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
	
	long _plen;
	long _rstarts; // starting offset of fragments / text indexes
	// _fchr, _ftab and _eftab are expected to be relatively small
	// (usually < 1MB, perhaps a few MB if _fchr is particularly large
	// - like, say, 11).  For this reason, we don't bother with writing
	// them to disk through separate output streams; we
	long _fchr;
	long _ftab;
	long _eftab; // "extended" entries for _ftab
	// _offs may be extremely large.  E.g. for DNA w/ offRate=4 (one
	// offset every 16 rows), the total size of _offs is the same as
	// the total size of the input sequence
	long _offs;
	// _ebwt is the Extended Burrows-Wheeler Transform itself, and thus
	// is at least as large as the input sequence.
	byte _ebwt;
	
	public boolean       _useMm;        /// use memory-mapped files to hold the index
	public boolean       useShmem_;     /// use shared memory to hold large parts of the index
	public EList<String> _refnames; /// names of the reference sequences
	public String mmFile1_;
	public String mmFile2_;
	public EbwtParams _eh;
	public boolean packed_;
	
	public static final long default_bmax = IndexTypes.OFF_MASK;
	public static final long default_bmaxMultSqrt = IndexTypes.OFF_MASK;
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
	
	public EBWT(String in,
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
		 _overrideOffRate = overrideOffRate;
		    _verbose = verbose;
		    _passMemExc = passMemExc;
		    _sanity = sanityCheck;
		    fw_ = fw;
		    _in1 = null;
		    _in2 = null;
		    _zOff = IndexTypes.OFF_MASK;
		    _zEbwtByteOff = IndexTypes.OFF_MASK;
		    _zEbwtBpOff = -1;
		    _nPat = 0;
		    _nFrag = 0;
		    _plen = 1;
		    _rstarts = 1;
		    _fchr = 1;
		    _ftab = 1;
		    _eftab = 1;
		    _offs = 1;
		    _ebwt = 1;
		    _useMm = false;
		    useShmem_ = false;
		    _refnames = new EList(1);
		    mmFile1_ = null;
		    mmFile2_ = null;
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
	
	public EBWT(TStr exampleStr,
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
				refparams.reverse == ReadDir.REF_READ_REVERSE);
		 _overrideOffRate = overrideOffRate;
		    _verbose = verbose;
		    _passMemExc = passMemExc;
		    _sanity = sanityCheck;
		    fw_ = fw;
		    _in1 = null;
		    _in2 = null;
		    _zOff = IndexTypes.OFF_MASK;
		    _zEbwtByteOff = IndexTypes.OFF_MASK;
		    _zEbwtBpOff = -1;
		    _nPat = 0;
		    _nFrag = 0;
		    _plen = 1;
		    _rstarts = 1;
		    _fchr = 1;
		    _ftab = 1;
		    _eftab = 1;
		    _offs = 1;
		    _ebwt = 1;
		    _useMm = false;
		    useShmem_ = false;
		    _refnames = 1;
		    mmFile1_ = null;
		    mmFile2_ = null;
		_in1Str = file + ".1." + gEbwt_ext;
		_in2Str = file + ".2." + gEbwt_ext;
		packed_ = packed;
		// Open output files
		//ofstream fout1(_in1Str.c_str(), ios::binary);
		if(!fout1.good()) {
			System.err.println("Could not open index file for writing: \"" + _in1Str.c_str() + "\n" +
					+ "Please make sure the directory exists and that permissions allow writing by" +
					+ "Bowtie.");
			throw 1;
		}
		//ofstream fout2(_in2Str.c_str(), ios::binary);
		if(!fout2.good()) {
			System.err.println("Could not open index file for writing: \"" + _in2Str.c_str() + "\"" + "\n" +
					+ "Please make sure the directory exists and that permissions allow writing by" + "\n"
					+ "Bowtie." + "\n");
			throw 1;
		}
		_inSaStr = file + ".sa";
		_inBwtStr = file + ".bwt";
		FileInputStream saOut = null, bwtOut = null;
		if(doSaFile) {
			saOut = new FileInputStream(_inSaStr);
			if(saOut.available() <= 0) {
				System.err.println("Could not open suffix-array file for writing: \"" + _inSaStr + "Please make sure the directory exists and that permissions allow writing by Bowtie.");
				throw 1;
			}
		}
		if(doBwtFile) {
			bwtOut = new ofstream(_inBwtStr.c_str(), ios::binary);
			if(bwtOut.available() <= 0) {
				System.err.println("Could not open suffix-array file for writing: \"" + _inBwtStr.c_str() + "\"" + "\n" +
						+ "Please make sure the directory exists and that permissions allow writing by" + "\n" +
						+ "Bowtie.");
				throw 1;
			}
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
		
		long tellpSz1 = (long)fout1.tellp();
		VMSG_NL("Wrote " + fout1.tellp() + " bytes to primary EBWT file: " + _in1Str.c_str());
		fout1.close();
		boolean err = false;
		if(tellpSz1 > fileSize(_in1Str.c_str())) {
			err = true;
			cerr + "Index is corrupt: File size for " + _in1Str.c_str() + " should have been " + tellpSz1
					+ " but is actually " + fileSize(_in1Str.c_str()) + "." + "\n");;
		}
		fout2.flush();
		
		long tellpSz2 = (long)fout2.tellp();
		VMSG_NL("Wrote " + fout2.tellp() + " bytes to secondary EBWT file: " + _in2Str.c_str());
		fout2.close();
		if(tellpSz2 > fileSize(_in2Str.c_str())) {
			err = true;
			cerr + "Index is corrupt: File size for " + _in2Str.c_str() + " should have been " + tellpSz2
					+ " but is actually " + fileSize(_in2Str.c_str()) + "." + "\n");;
		}
		
		if(saOut != null) {
			// Check on suffix array output file size
			long tellpSzSa = (long)saOut.tellp();
			VMSG_NL("Wrote " + tellpSzSa + " bytes to suffix-array file: " + _inSaStr.c_str());
			saOut.close();
			if(tellpSzSa > fileSize(_inSaStr.c_str())) {
				err = true;
				cerr + "Index is corrupt: File size for " + _inSaStr.c_str() + " should have been " + tellpSzSa
						+ " but is actually " + fileSize(_inSaStr.c_str()) + "." + "\n");;
			}
		}
		
		if(bwtOut != null) {
			// Check on suffix array output file size
			long tellpSzBwt = (long)bwtOut.tellp();
			VMSG_NL("Wrote " + tellpSzBwt + " bytes to BWT file: " + _inBwtStr.c_str());
			bwtOut.close();
			if(tellpSzBwt > fileSize(_inBwtStr.c_str())) {
				err = true;
				cerr + "Index is corrupt: File size for " + _inBwtStr.c_str() + " should have been " + tellpSzBwt
						+ " but is actually " + fileSize(_inBwtStr.c_str()) + "." + "\n");;
			}
		}
		
		if(err) {
			cerr + "Please check if there is a problem with the disk or if disk is full." + "\n");;
			throw 1;
		}
		
		// Reopen as input streams
		VMSG_NL("Re-opening _in1 and _in2 as input streams");
		if(_sanity) {
			VMSG_NL("Sanity-checking Bt2");
			assert(!isInMemory());
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
			assert(!isInMemory());
		}
		VMSG_NL("Returning from Ebwt constructor");
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
	
	public long joinedLen(EList<RefRecord> szs) {
		long ret = 0;
		for(int i = 0; i < szs.size();i++) {
			ret += szs.get(i).len;
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
		writeU<long>(out1, this._nPat, this.toBe());
		// Allocate plen[]
		try {
			this._plen.init(new long[this._nPat], this._nPat);
		} catch(bad_alloc& e) {
			cerr + "Out of memory allocating plen[] in Ebwt::join()"
					+ " at " + __FILE__ + ":" + __LINE__ + "\n");;
			throw e;
		}
		// For each pattern, set plen
		long npat = -1;
		for(long i = 0; i < szs.size(); i++) {
			if(szs.get(i).first && szs.get(i).len > 0) {
				if(npat >= 0) {
					writeU<long>(out1, this.plen()[npat], this.toBe());
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
					////assert_eq(0, _refnames.back().length());
					_refnames.pop_back();
				}
				// Increment seqsRead if this is the first fragment
				if(rec.first && rec.len > 0) seqsRead++;
				if(bases == 0) continue;
				//assert_leq(bases, this.plen()[seqsRead-1]);
				// Reset the patoff if this is the first fragment
				if(rec.first) patoff = 0;
				patoff += rec.off; // add fragment's offset from end of last frag.
				// Adjust rpcps
				//uint32_t seq = seqsRead-1;
				// This is where rstarts elements are written to the output stream
				//writeU32(out1, oldRetLen, this.toBe()); // offset from beginning of joined String
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
		//assert_eq(s.length()+1, sa.size());
		//assert_eq(s.length(), eh._len);
		//assert_gt(eh._lineRate, 3);
		assert(sa.suffixItrIsReset());
		
		long len = eh._len;
		long ftabLen = eh._ftabLen;
		long sideSz = eh._sideSz;
		long ebwtTotSz = eh._ebwtTotSz;
		long fchr[] = {0, 0, 0, 0, 0};
		EList<long> ftab(1);
		long zOff = IndexTypes.OFF_MASK;
		
		// Save # of occurrences of each character as we walk along the bwt
		long occ[4] = {0, 0, 0, 0};
		long occSave[4] = {0, 0, 0, 0};
		
		// Record rows that should "absorb" adjacent rows in the ftab.
		// The absorbed rows represent suffixes shorter than the ftabChars
		// cutoff.
		byte absorbCnt = 0;
		EList<byte> absorbFtab(1);
		try {
			VMSG_NL("Allocating ftab, absorbFtab");
			ftab.resize(ftabLen);
			ftab.fillZero();
			absorbFtab.resize(ftabLen);
			absorbFtab.fillZero();
		} catch(bad_alloc &e) {
			cerr + "Out of memory allocating ftab[] or absorbFtab[] "
					+ "in Ebwt::buildToDisk() at " + __FILE__ + ":"
					+ __LINE__ + "\n");;
			throw e;
		}
		
		// Allocate the side buffer; holds a single side as its being
		// constructed and then written to disk.  Reused across all sides.
	#ifdef SIXTY4_FORMAT
		EList<long> ebwtSide(1);
	#else
		EList<byte> ebwtSide(1);
	#endif
		try {
	#ifdef SIXTY4_FORMAT
			ebwtSide.resize(sideSz >> 3);
	#else
			ebwtSide.resize(sideSz);
	#endif
		} catch(bad_alloc &e) {
			cerr + "Out of memory allocating ebwtSide[] in "
					+ "Ebwt::buildToDisk() at " + __FILE__ + ":"
					+ __LINE__ + "\n");;
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
		//assert_ONLY(boolean dollarSkipped = false);
		
		long si = 0;   // String offset (chars)
		//assert_ONLY(long lastSufInt = 0);
		//assert_ONLY(boolean inSA = true); // true iff saI still points inside suffix
		// array (as opposed to the padding at the
		// end)
		// Iterate over packed bwt bytes
		VMSG_NL("Entering Ebwt loop");
		//assert_ONLY(long beforeEbwtOff = (long)out1.tellp()); // @double-check - pos_type, std::streampos
		
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
			//assert_geq(sideCur, 0);
			//assert_lt(sideCur, (int)eh._sideBwtSz);
			//assert_eq(0, side % sideSz); // 'side' must be on side boundary
			ebwtSide[sideCur] = 0; // clear
			//assert_lt(side + sideCur, ebwtTotSz);
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
						//assert_ONLY(dollarSkipped = true);
						zOff = si; // remember the SA row that
						// corresponds to the 0th suffix
					} else {
						bwtChar = (int)(s[saElt-1]);
						//assert_lt(bwtChar, 4);
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
							sufInt += 2;
							//assert_lt((long)i, len-saElt);
							sufInt |= (unsigned char)(s[saElt+i]);
						}
						// Assert that this prefix-of-suffix is greater
						// than or equal to the last one (true b/c the
						// suffix array is sorted)
						#ifndef NDEBUG
						if(lastSufInt > 0) //assert_geq(sufInt, lastSufInt);
							lastSufInt = sufInt;
						#endif
						// Update ftab
						//assert_lt(sufInt+1, ftabLen);
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
						//assert_lt(absorbCnt, 255);
						absorbCnt++;
					}
					// Suffix array offset boundary? - update offset array
					if((si & eh._offMask) == si) {
						//assert_lt((si >> eh._offRate), eh._offsLen);
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
						// String before now
						//assert_eq(si, len+1);
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
					ebwtSide[sideCur] |= ((long)bwtChar + (bpi + 1));
					if(bwtChar > 0) //assert_gt(ebwtSide[sideCur], 0);
	#else
					pack_2b_in_8b(bwtChar, ebwtSide[sideCur], bpi);
					//assert_eq((ebwtSide[sideCur] >> (bpi*2)) & 3, bwtChar);
	#endif
				} else {
					// Backward bucket: fill from most to least
	#ifdef SIXTY4_FORMAT
					ebwtSide[sideCur] |= ((long)bwtChar + ((31 - bpi) + 1));
					if(bwtChar > 0) //assert_gt(ebwtSide[sideCur], 0);
	#else
					pack_2b_in_8b(bwtChar, ebwtSide[sideCur], 3-bpi);
					//assert_eq((ebwtSide[sideCur] >> ((3-bpi)*2)) & 3, bwtChar);
	#endif
				}
			} // end loop over bit-pairs
			//assert_eq(dollarSkipped ? 3 : 0, (occ[0] + occ[1] + occ[2] + occ[3]) & 3);
	#ifdef SIXTY4_FORMAT
			//assert_eq(0, si & 31);
	#else
			//assert_eq(0, si & 3);
	#endif
			
			sideCur++;
			if(sideCur == (int)eh._sideBwtSz) {
				sideCur = 0;
				long *cpptr = reinterpret_cast<long*>(ebwtSide.ptr());
				// Write 'A', 'C', 'G' and 'T' tallies
				side += sideSz;
				//assert_leq(side, eh._ebwtTotSz);
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
		//assert_neq(zOff, IndexTypes.OFF_MASK);
		if(absorbCnt > 0) {
			// Absorb any trailing, as-yet-unabsorbed short suffixes into
			// the last element of ftab
			absorbFtab[ftabLen-1] = absorbCnt;
		}
		// Assert that our loop counter got incremented right to the end
		//assert_eq(side, eh._ebwtTotSz);
		// Assert that we wrote the expected amount to out1
		//assert_eq(((long)out1.tellp() - beforeEbwtOff), eh._ebwtTotSz); // @double-check - pos_type
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
		//assert_eq(fchr[3], len);
		// Shift everybody up by one
		for(int i = 4; i >= 1; i--) {
			fchr[i] = fchr[i-1];
		}
		fchr[0] = 0;
		if(_verbose) {
			for(int i = 0; i < 5; i++)
				cout.write( "fchr[" + "ACGT$"[i] + "]: " + fchr[i] + "\n");;
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
		//assert_eq(0, absorbFtab[0]);
		for(long i = 1; i < ftabLen; i++) {
			if(absorbFtab[i] > 0) eftabLen += 2;
		}
		//assert_leq(eftabLen, (long)eh._ftabChars*2);
		eftabLen = eh._ftabChars*2;
		EList<long> eftab(1);
		try {
			eftab.resize(eftabLen);
			eftab.fillZero();
		} catch(bad_alloc &e) {
			cerr + "Out of memory allocating eftab[] "
					+ "in Ebwt::buildToDisk() at " + __FILE__ + ":"
					+ __LINE__ + "\n");;
			throw e;
		}
		long eftabCur = 0;
		for(long i = 1; i < ftabLen; i++) {
			long lo = ftab[i] + Ebwt::ftabHi(ftab.ptr(), eftab.ptr(), len, ftabLen, eftabLen, i-1);
			if(absorbFtab[i] > 0) {
				// Skip a number of short pattern indicated by absorbFtab[i]
				long hi = lo + absorbFtab[i];
				//assert_lt(eftabCur*2+1, eftabLen);
				eftab[eftabCur*2] = lo;
				eftab[eftabCur*2+1] = hi;
				ftab[i] = (eftabCur++) ^ IndexTypes.OFF_MASK; // insert pointer into eftab
				//assert_eq(lo, Ebwt::ftabLo(ftab.ptr(), eftab.ptr(), len, ftabLen, eftabLen, i));
				//assert_eq(hi, Ebwt::ftabHi(ftab.ptr(), eftab.ptr(), len, ftabLen, eftabLen, i));
			} else {
				ftab[i] = lo;
			}
		}
		////assert_eq(Ebwt::ftabHi(ftab.ptr(), eftab.ptr(), len, ftabLen, eftabLen, ftabLen-1), len+1);
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
	
	public static int readFlags(String instr) {
	
	}
	
	public void postReadInit(EbwtParams eh) {
		long sideNum     = _zOff / eh._sideBwtLen;
		long sideCharOff = _zOff % eh._sideBwtLen;
		long sideByteOff = sideNum * eh._sideSz;
		_zEbwtByteOff = sideCharOff >> 2;
		_zEbwtBpOff = sideCharOff & 3;
		_zEbwtByteOff += sideByteOff;
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
		long[] acgt = reinterpret_cast<long>(acgt8);
		ret = acgt[c] + cCnt + this.fchr()[c];
		return ret;
	}
	
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
		long acgt = reinterpret_cast<long>(side + _eh._sideBwtSz);
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
		long[] acgt = reinterpret_cast<long>(acgt16);
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
		_fchr.free();
		_ftab.free();
		_eftab.free();
		_rstarts.free();
		_offs.free(); // might not be under control of APtrWrap
		_ebwt.free(); // might not be under control of APtrWrap
		// Keep plen; it's small and the client may want to seq it
		// even when the others are evicted.
		//_plen  = null;
		_zEbwtByteOff = IndexTypes.IndexTypes.OFF_MASK;
		_zEbwtBpOff = -1;
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
				return long.MAX_VALUE;
			}
			ftabOff += 2;
			ftabOff |= c;
		}
		return ftabOff;
	}
	
	public long ftabHi(BTDnaString seq, int off) {
		return ftabHi(ftabSeqToInt(seq, off, false));
	}
	
	public long ftabHi(long i) {
		return EBWT.ftabHi(
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
		return EBWT.ftabLo(
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
	
	public boolean ftabLoHi(
			BTDnaString seq, // sequence to extract from
			int off,             // offset into seq to begin extracting
			boolean rev,               // reverse while extracting
			long top,
			long bot)
	{
		long fi = ftabSeqToInt(seq, off, rev);
		if(fi == long.MAX_VALUE) {
			return false;
		}
		top = ftabHi(fi);
		bot = ftabLo(fi+1);
		return true;
	}
	
	public static Pair<EBWT, EBWT> fromString(
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
	public static Pair<EBWT, EBWT> fromStrings(
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
		RefReadInParams refparams = new RefReadInParams(color, REF_READ_FORWARD, false, false);
		String ss = "";
		for(long i = 0; i < strs.size(); i++) {
			ss += ">" + i + "\n" + strs[i] + "\n";
		}
		auto_ptr<FileBuf> fb = new FileBuf(ss.get());
		is.push_back(fb.get());
		// Vector for the ordered list of "records" comprising the input
		// sequences.  A record represents a stretch of unambiguous
		// characters in one of the input sequences.
		EList<RefRecord> szs = new EList(1);
		Pair<long, long> sztot;
		sztot = BitPairReference::szsFromFasta(is, file, bigEndian, refparams, szs, sanity);
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
				reverse == REF_READ_REVERSE,
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
	
	public boolean isPacked1() {
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
		// Compose text strings into single String
		VMSG_NL("Calculating joined length");
		TStr s; // holds the entire joined reference after call to joinToDisk
		long jlen;
		jlen = joinedLen(szs);
		////assert_geq(jlen, sztot);
		VMSG_NL("Writing header");
		writeFromMemory(true, out1, out2);
		try {
			VMSG_NL("Reserving space for joined String");
			s.resize(jlen);
			VMSG_NL("Joining reference sequences");
			if(refparams.reverse == REF_READ_REVERSE) {
				//Timer timer(cout, "  Time to join reference sequences: ", _verbose);
				joinToDisk(is, szs, sztot, refparams, s, out1, out2);
				//Timer timer(cout, "  Time to reverse reference sequence: ", _verbose);
				//EList<RefRecord> tmp(1);
				s.reverse();
				reverseRefRecords(szs, tmp, false, verbose);
				szsToDisk(tmp, out1, refparams.reverse);
			} else {
				//Timer timer(cout, "  Time to join reference sequences: ", _verbose);
				joinToDisk(is, szs, sztot, refparams, s, out1, out2);
				szsToDisk(szs, out1, refparams.reverse);
			}
			// Joined reference sequence now in 's'
		} catch(Exception e) {
			// If we throw an allocation exception in the try block,
			// that means that the joined version of the reference
			// String itself is too larger to fit in memory.  The only
			// alternatives are to tell the user to give us more memory
			// or to try again with a packed representation of the
			// reference (if we haven't tried that already).
			System.err.println("Could not allocate space for a joined String of " + jlen + " elements.");;
			if(!isPacked() && _passMemExc) {
				// Pass the exception up so that we can retry using a
				// packed String representation
				throw e;
			}
			// There's no point passing this exception on.  The fact
			// that we couldn't allocate the joined String means that
			// --bmax is irrelevant - the user should re-run with
			// ebwt-build-packed
			if(isPacked1()) {
				System.err.println("Please try running bowtie-build on a computer with more memory.");
			} else {
				System.err.println("Please try running bowtie-build in packed mode (-p/--packed) or in automatic" + "\n"
						+ "mode (-a/--auto), or try again on a computer with more memory.");
			}
			if(sizeof(null) == 4) {
				System.err.println("If this computer has more than 4 GB of memory, try using a 64-bit executable;" + "\n"
						+ "this executable is 32-bit." + "\n");
			}
			throw 1;
		}
		// Succesfully obtained joined reference String
		if(bmax != IndexTypes.OFF_MASK) {
			VMSG_NL("bmax according to bmax setting: " + bmax);
		}
		else if(bmaxSqrtMult != IndexTypes.OFF_MASK) {
			bmax *= bmaxSqrtMult;
			VMSG_NL("bmax according to bmaxSqrtMult setting: " + bmax);
		}
		else if(bmaxDivN != IndexTypes.OFF_MASK) {
			bmax = max<long>(jlen / bmaxDivN, 1);
			VMSG_NL("bmax according to bmaxDivN setting: " + bmax);
		}
		else {
			bmax = (long)sqrt(s.length());
			VMSG_NL("bmax defaulted to: " + bmax);
		}
		int iter = 0;
		boolean first = true;
		streampos out1pos = out1.tellp();
		streampos out2pos = out2.tellp();
		// Look for bmax/dcv parameters that work.
		while(true) {
			if(!first && bmax < 40 && _passMemExc) {
				System.err.println("Could not find approrpiate bmax/dcv settings for building this index.");
				if(!isPacked1()) {
					// Throw an exception exception so that we can
					// retry using a packed String representation
					throw bad_alloc();
				} else {
					System.err.println("Already tried a packed String representation.");
				}
				System.err.println("Please try indexing this reference on a computer with more memory.");
				if(sizeof(void*) == 4) {
					System.err.println("If this computer has more than 4 GB of memory, try using a 64-bit executable;" + "\n"
							+ "this executable is 32-bit.");
				}
				throw 1;
			}
			if(!first) {
				out1.seekp(out1pos);
				out2.seekp(out2pos);
			}
			if(dcv > 4096) dcv = 4096;
			if((iter % 6) == 5 && dcv < 4096 && dcv != 0) {
				dcv += 1; // double difference-cover period
			} else {
				bmax -= (bmax >> 2); // reduce by 25%
			}
			VMSG("Using parameters --bmax " + bmax);
			if(dcv == 0) {
				VMSG_NL(" and *no difference cover*");
			} else {
				VMSG_NL(" --dcv " + dcv);
			}
			iter++;
			try {
				{
					VMSG_NL("  Doing ahead-of-time memory usage test");
					// Make a quick-and-dirty attempt to force a bad_alloc iff
					// we would have thrown one eventually as part of
					// constructing the DifferenceCoverSample
					dcv += 1;
					long sz = (long)DifferenceCoverSample<TStr>::simulateAllocs(s, dcv >> 1);
					if(nthreads > 1) sz *= (nthreads + 1);
					AutoArray<byte> tmp(sz, 1);
					dcv >>= 1;
					// Likewise with the KarkkainenBlockwiseSA
					sz = (long)KarkkainenBlockwiseSA<TStr>::simulateAllocs(s, bmax);
					AutoArray<byte> tmp2(sz, 1);
					// Now throw in the 'ftab' and 'isaSample' structures
					// that we'll eventually allocate in buildToDisk
					AutoArray<long> ftab(_eh._ftabLen * 2, 1);
					AutoArray<byte> side(_eh._sideSz, 1);
					// Grab another 20 MB out of caution
					AutoArray<int> extra(20*1024*1024, 1);
					// If we made it here without throwing bad_alloc, then we
					// passed the memory-usage stress test
					VMSG("  Passed!  Constructing with these parameters: --bmax " + bmax + " --dcv " + dcv);
					if(isPacked()) {
						VMSG(" --packed");
					}
					VMSG_NL("");
				}
				VMSG_NL("Constructing suffix-array element generator");
				KarkkainenBlockwiseSA<TStr> bsa(s, bmax, nthreads, dcv, seed, _sanity, _passMemExc, _verbose, outfile);
				assert(bsa.suffixItrIsReset());
				////assert_eq(bsa.size(), s.length()+1);
				VMSG_NL("Converting suffix-array elements to index image");
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
					System.err.println("An error occurred writing the index to disk.  Please check if the disk is full." + "\n");
					throw 1;
				}
				break;
			} catch(Exception e) {
				if(_passMemExc) {
					VMSG_NL("  Ran out of memory; automatically trying more memory-economical parameters.");
				} else {
					System.err.println("Out of memory while constructing suffix array.  Please try using a smaller" + "\n"
							+ "number of blocks by specifying a smaller --bmax or a larger --bmaxdivn");
					throw 1;
				}
			}
			first = false;
		}
		assert(repOk());
		// Now write reference sequence names on the end
		////assert_eq(this._refnames.size(), this._nPat);
		for(long i = 0; i < this._refnames.size(); i++) {
			out1 + this._refnames[i].c_str() + "\n");;
		}
		out1 + '\0';
		out1.flush(); out2.flush();
		if(out1.fail() || out2.fail()) {
			cerr + "An error occurred writing the index to disk.  Please check if the disk is full." + "\n");;
			throw 1;
		}
		VMSG_NL("Returning from initFromVector");
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
	
	public long fchr() {
		return _fchr.get();
	}
	
	public byte ebwt() {
		return _ebwt.get();
	}
	
	public long mapLF(SideLocus l) {
		long ret;
		int c = rowL(l);
		ret = countBt2Side(l, c);
		return ret;
	}
	
	public long mapLF1(long row, SideLocus l, int c) {
		if(rowL(l) != c || row == _zOff) return IndexTypes.IndexTypes.OFF_MASK;
		long ret = countBt2Side(l, c);
		return ret;
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
	
	
	
	
	
	
	
	
	
	
	
	void joinedToTextOff(
			long qlen,
			long off,
			long tidx,
			long textoff,
			long tlen,
			Boolean rejectStraddle,
			Boolean straddled)
	{
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
					if(!this->fw_) {
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
		tlen = this->plen()[tidx];
	}
	
	/**
	 * Walk 'steps' steps to the left and return the row arrived at.  If we
	 * walk through the dollar sign, return max value.
	 */
	long walkLeft(long row, long steps) {
		SideLocus l;
		if(steps > 0) l.initFromRow(row, _eh, ebwt());
		while(steps > 0) {
			if(row == _zOff) return IndexTypes.OFF_MASK;
			long newrow = this->mapLF(l ASSERT_ONLY(, false));
			row = newrow;
			steps--;
			if(steps > 0) l.initFromRow(row, _eh, ebwt());
		}
		return row;
	}
	
	/**
	 * Resolve the reference offset of the BW element 'elt'.
	 */
	long getOffset(long row) {
		if(row == _zOff) return 0;
		if((row & _eh._offMask) == row) return this->offs()[row >> _eh._offRate];
		long jumps = 0;
		SideLocus l;
		l.initFromRow(row, _eh, ebwt());
		while(true) {
			long newrow = this->mapLF(l ASSERT_ONLY(, false));
			jumps++;
			row = newrow;
			if(row == _zOff) {
				return jumps;
			} else if((row & _eh._offMask) == row) {
				return jumps + this->offs()[row >> _eh._offRate];
			}
			l.initFromRow(row, _eh, ebwt());
		}
	}
	
	/**
	 * Resolve the reference offset of the BW element 'elt' such that
	 * the offset returned is at the right-hand side of the forward
	 * reference substring involved in the hit.
	 */
	long getOffset(
			long elt,
			Boolean fw,
			long hitlen)
	{
		long off = getOffset(elt);
		if(!fw) {
			off = _eh._len - off - 1;
			off -= (hitlen-1);
		}
		return off;
	}
	
	/**
	 * Returns true iff the index contains the given String (exactly).  The given
	 * String must contain only unambiguous characters.  TODO: support ambiguous
	 * characters in 'str'.
	 */
	Boolean contains(
			BTDnaString str,
			long otop,
			long obot)
	{
		assert(isInMemory());
		SideLocus tloc, bloc;
		if(str.empty()) {
			if(otop != null && obot != null) *otop = *obot = 0;
			return true;
		}
		int c = str[str.length()-1];
		long top = 0, bot = 0;
		if(c < 4) {
			top = fchr()[c];
			bot = fchr()[c+1];
		} else {
			Boolean set = false;
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
				int c1 = mapLF1(top, tloc, c);
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
	
	/**
	 * Try to find the Bowtie index specified by the user.  First try the
	 * exact path given by the user.  Then try the user-provided String
	 * appended onto the path of the "indexes" subdirectory below this
	 * executable, then try the provided String appended onto
	 * "$BOWTIE2_INDEXES/".
	 */
	String adjustEbwtBase(String cmdline, String ebwtFileBase, Boolean verbose)
	{
		String str = ebwtFileBase;
		Scanner in = new Scanner(System.in);
		if(verbose) System.out.println("Trying " + str);
		in.next(str);
		if(!in.hasNext()) {
			if(verbose) System.out.println("  didn't work");
			in.close();
			if(System.getenv("BOWTIE2_INDEXES") != null) {
				str = (String)System.getenv("BOWTIE2_INDEXES") + "/" + ebwtFileBase;
				if(verbose) System.out.println("Trying " + str);
				in.next((str + ".1." + gEbwt_ext));
				if(!in.hasNext()) {
					if(verbose) System.out.println(   "didn't work");
					in.close();
				} else {
					if(verbose) System.out.println("   worked");
				}
			}
		}
		if(!in.hasNext()) {
			System.out.println("Could not locate a Bowtie index corresponding to basename " + ebwtFileBase);
		}
		return str;
	}	
}







