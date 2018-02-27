package com.uwb.bt2j.aligner;

class Aligner {
	public static EList<String> mates1;
	public static EList<String> mates2;
	public static EList<String> mates12;
	public static EList<String> qualities;
	public static EList<String> qualities1;
	public static EList<String> qualities2;
	public static EList<String> queries;
	public static EList<Pair<int, String>> extraOpts;
	
	public static String short_options = "fF:qbzhcu:rv:s:aP:t3:5:w:p:k:M:1:2:I:X:CQ:N:i:L:U:x:S:g:O:D:R:";
	public static String argstr;
	public static String arg0;
	public static String adjIdxBase;
	public static String origString;
	public static String metricsFile;
	public static String threadStealingDir;
	public static String rgid;
	public static String rgs;
	public static String rgs_optflag;
	public static String polstr;
	public static String defaultPreset;
	public static String wrapper;
	public static String outfile;
	public static String logDps;
	public static String logDpsOpp;
	public static String bt2index;
	
	public static double qUpTo;
	public static double khits;
	public static double mhits;
	public static double cacheLimit;
	public static double cacheSize;
	public static double skipReads;
	public static double fastaContLen;
	public static double fastaContFreq;
	public static double descLanding;
	public static double multiseedOff;
	public static double seedCacheLocalMB;
	public static double seedCacheCurrentMB;
	public static double exactCacheCurrentMB;
	public static double maxHalf;
	public static double maxIters;
	public static double maxUg;
	public static double maxDp;
	public static double maxItersIncr;
	public static double maxEeStreak;
	public static double maxUgStreak;
	public static double maxDpStreak;
	public static double maxStreakIncr;
	public static double maxMateStreak;
	public static double cminlen;
	public static double cpow2;
	public static double do1mmMinLen;
	public static double nSeedRounds;
	public static double extraOptsCur;
	
	public static float bwaSwLikeC;
	public static float bwaSwLikeT;
	
	public static Boolean startVerbose;
	public static Boolean showVersion;
	public static Boolean metricsStderr;
	public static Boolean metricsPerRead;
	public static Boolean allHits;
	public static Boolean solexaQuals;
	public static Boolean phred64Quals;
	public static Boolean integerQuals;
	public static Boolean threadStealing;
	public static Boolean noRefNames;
	public static Boolean fileParallel;
	public static Boolean useShmem;
	public static Boolean useMm;
	public static Boolean mmSweep;
	public static Boolean hadoopOut;
	public static Boolean fullRef;
	public static Boolean samTruncQname;
	public static Boolean samOmitSecSeqQual;
	public static Boolean samNoUnal;
	public static Boolean samNoHead;
	public static Boolean samNoSQ;
	public static Boolean sam_print_as;
	public static Boolean sam_print_xs;
	public static Boolean sam_print_xss;
	public static Boolean sam_print_yn;
	public static Boolean sam_print_xn;
	public static Boolean sam_print_x0;
	public static Boolean sam_print_x1;
	public static Boolean sam_print_xm;
	public static Boolean sam_print_xo;
	public static Boolean sam_print_xg;
	public static Boolean sam_print_nm;
	public static Boolean sam_print_md;
	public static Boolean sam_print_yf;
	public static Boolean sam_print_yi;
	public static Boolean sam_print_ym;
	public static Boolean sam_print_yp;
	public static Boolean sam_print_yt;
	public static Boolean sam_print_ys;
	public static Boolean sam_print_zs;
	public static Boolean sam_print_xr;
	public static Boolean sam_print_xt;
	public static Boolean sam_print_xd;
	public static Boolean sam_print_xu;
	public static Boolean sam_print_yl;
	public static Boolean sam_print_ye;
	public static Boolean sam_print_yu;
	public static Boolean sam_print_xp;
	public static Boolean sam_print_yr;
	public static Boolean sam_print_zb;
	public static Boolean sam_print_zr;
	public static Boolean sam_print_zf;
	public static Boolean sam_print_zm;
	public static Boolean sam_print_zi;
	public static Boolean sam_print_zp;
	public static Boolean sam_print_zu;
	public static Boolean sam_print_zt;
	public static Boolean bwaSwLike;
	public static Boolean gSeedLenIsSet;
	public static Boolean qcFilter;
	public static Boolean msample;
	public static Boolean msNoCache;
	public static Boolean penNCatPair;
	public static Boolean localAlign;
	public static Boolean noisyHpolymer;
	public static Boolean descPrioritizeRoots;
	public static Boolean seedSumm;
	public static Boolean scUnMapped;
	public static Boolean doUngapped;
	public static Boolean xeq;
	public static Boolean doExtend;
	public static Boolean enable8;
	public static Boolean doTri;
	public static Boolean ignoreQuals;
	public static Boolean doExactUpFront;
	public static Boolean do1mmUpFront;
	public static Boolean reorder;
	public static Boolean arbitraryRandom;
	public static Boolean bowtie2p5;
	
	public Boolean gMate1fw;
	public Boolean gMate2fw;
	public Boolean gFlippedMatesOK;
	public Boolean gDovetailMatesOK;
	public Boolean gContainsMatesOK;
	public Boolean gOlapMatesOK;
	public Boolean gExpandToFrag;
	public Boolean gReportDiscordant;
	public Boolean gReportMixed;
	public Boolean gNoFw;
	public Boolean gNorc;
	public Boolean gReportOverhangs;
	
	public static void main(String[] args) {
		try {
			
			// Reset all global state, including getopt state
			int opterr, optind = 1;
			resetOptions();
			
			for(int i = 0; i < args.length; i++) {
				argstr += args[i];
				if(i < args.length-1) argstr += " ";
			}
			
			if(startVerbose) {
				System.err.println("Entered main(): ");
				//logTime(cerr, true);
			}
			
			parseOptions(args);
			arg0 = args[0];
			if(showVersion) {
				cout << argv0 << " version " << BOWTIE2_VERSION << endl;
				if(sizeof(void*) == 4) {
					cout << "32-bit" << endl;
				} else if(sizeof(void*) == 8) {
					cout << "64-bit" << endl;
				} else {
					cout << "Neither 32- nor 64-bit: sizeof(void*) = " << sizeof(void*) << endl;
				}
				
				cout << "Built on " << BUILD_HOST << endl;
				cout << BUILD_TIME << endl;
				cout << "Compiler: " << COMPILER_VERSION << endl;
				cout << "Options: " << COMPILER_OPTIONS << endl;
				cout << "Sizeof {int, long, long long, void*, size_t, off_t}: {"
						 << sizeof(int)
						 << ", " << sizeof(long) << ", " << sizeof(long long)
						 << ", " << sizeof(void *) << ", " << sizeof(size_t)
						 << ", " << sizeof(off_t) << "}" << endl;
				return 0;
			}
			{
				Timer _t(cerr, "Overall time: ", timing);
				if(startVerbose) {
					cerr << "Parsing index and read arguments: "; logTime(cerr, true);
				}

				// Get index basename (but only if it wasn't specified via --index)
				if(bt2index.empty()) {
					cerr << "No index, query, or output file specified!" << endl;
					printUsage(cerr);
					return 1;
				}
		
				if(thread_stealing && thread_stealing_dir.empty()) {
					cerr << "When --thread-ceiling is specified, must also specify --thread-piddir" << endl;
					printUsage(cerr);
					return 1;
				}

				// Get query filename
				bool got_reads = !queries.empty() || !mates1.empty() || !mates12.empty();
				
				if(optind >= argc) {
					if(!got_reads) {
						printUsage(cerr);
						cerr << "***" << endl
						     << "Error: Must specify at least one read input with -U/-1/-2" << endl;
						return 1;
					}
				} else if(!got_reads) {
					// Tokenize the list of query files
					tokenize(argv[optind++], ",", queries);
					if(queries.empty()) {
						cerr << "Tokenized query file list was empty!" << endl;
						printUsage(cerr);
						return 1;
					}
				}

				// Get output filename
				if(optind < argc && outfile.empty()) {
					outfile = argv[optind++];
					cerr << "Warning: Output file '" << outfile.c_str()
					     << "' was specified without -S.  This will not work in "
						 << "future Bowtie 2 versions.  Please use -S instead."
						 << endl;
				}

				// Extra parametesr?
				if(optind < argc) {
					cerr << "Extra parameter(s) specified: ";
					for(int i = optind; i < argc; i++) {
						cerr << "\"" << argv[i] << "\"";
						if(i < argc-1) cerr << ", ";
					}
					cerr << endl;
					if(mates1.size() > 0) {
						cerr << "Note that if <mates> files are specified using -1/-2, a <singles> file cannot" << endl
							 << "also be specified.  Please run bowtie separately for mates and singles." << endl;
					}
					throw 1;
				}

				// Optionally summarize
				if(gVerbose) {
					cout << "Input " + gEbwt_ext +" file: \"" << bt2index.c_str() << "\"" << endl;
					cout << "Query inputs (DNA, " << file_format_names[format].c_str() << "):" << endl;
					for(size_t i = 0; i < queries.size(); i++) {
						cout << "  " << queries[i].c_str() << endl;
					}
					cout << "Quality inputs:" << endl;
					for(size_t i = 0; i < qualities.size(); i++) {
						cout << "  " << qualities[i].c_str() << endl;
					}
					cout << "Output file: \"" << outfile.c_str() << "\"" << endl;
					cout << "Local endianness: " << (currentlyBigEndian()? "big":"little") << endl;
					cout << "Sanity checking: " << (sanityCheck? "enabled":"disabled") << endl;
				#ifdef NDEBUG
					cout << "Assertions: disabled" << endl;
				#else
					cout << "Assertions: enabled" << endl;
				#endif
				}
				if(ipause) {
					cout << "Press key to continue..." << endl;
					getchar();
				}
				driver<SString<char> >("DNA", bt2index, outfile);
			}
			return 0;
		} catch(Exception e) {
			System.err.println("Error: Encountered exception: '" + e + "'");
			System.err.println("Command: ");
			for(int i = 0; i < argc; i++)
				System.err.println(argv[i] + " ");
			return 1;
		} catch(int e) {
			if(e != 0) {
				System.err.println("Error: Encountered internal Bowtie 2 exception (#" + e + ")");
				System.err.println("Command: ");
				for(int i = 0; i < argc; i++)
					System.err.println(argv[i] + " ");
			}
			return e;
	}
	}
	public static void resetOptions() {
		mates1.clear();
		mates2.clear();
		mates12.clear();
		adjIdxBase	            = "";
		gVerbose                = 0;
		startVerbose			= 0;
		gQuiet					= false;
		sanityCheck				= 0;  // enable expensive sanity checks
		format					= FASTQ; // default read format is FASTQ
		origString				= ""; // reference text, or filename(s)
		seed					= 0; // srandom() seed
		timing					= 0; // whether to report basic timing data
		metricsIval				= 1; // interval between alignment metrics messages (0 = no messages)
		metricsFile             = ""; // output file to put alignment metrics in
		metricsStderr           = false; // print metrics to stderr (in addition to --metrics-file if it's specified
		metricsPerRead          = false; // report a metrics tuple for every read?
		allHits					= false; // for multihits, report just one
		showVersion				= false; // just print version and quit?
		ipause					= 0; // pause before maching?
		qUpto					= 0xffffffff; // max # of queries to read
		gTrim5					= 0; // amount to trim from 5' end
		gTrim3					= 0; // amount to trim from 3' end
		offRate					= -1; // keep default offRate
		solexaQuals				= false; // quality strings are solexa quals, not phred, and subtract 64 (not 33)
		phred64Quals			= false; // quality chars are phred, but must subtract 64 (not 33)
		integerQuals			= false; // quality strings are space-separated strings of integers, not ASCII
		nthreads				= 1;     // number of pthreads operating concurrently
		thread_ceiling			= 0;     // max # threads user asked for
		thread_stealing_dir		= ""; // keep track of pids in this directory
		thread_stealing			= false; // true iff thread stealing is in use
		FNAME_SIZE				= 4096;
		outType					= OUTPUT_SAM;  // style of output
		noRefNames				= false; // true -> print reference indexes; not names
		khits					= 1;     // number of hits per read; >1 is much slower
		mhits					= 50;    // stop after finding this many alignments+1
		partitionSz				= 0;     // output a partitioning key in first field
		readsPerBatch			= 16;    // # reads to read from input file at once
		fileParallel			= false; // separate threads read separate input files in parallel
		useShmem				= false; // use shared memory to hold the index
		useMm					= false; // use memory-mapped files to hold the index
		mmSweep					= false; // sweep through memory-mapped files immediately after mapping
		gMinInsert				= 0;     // minimum insert size
		gMaxInsert				= 500;   // maximum insert size
		gMate1fw				= true;  // -1 mate aligns in fw orientation on fw strand
		gMate2fw				= false; // -2 mate aligns in rc orientation on fw strand
		gFlippedMatesOK         = false; // allow mates to be in wrong order
		gDovetailMatesOK        = false; // allow one mate to extend off the end of the other
		gContainMatesOK         = true;  // allow one mate to contain the other in PE alignment
		gOlapMatesOK            = true;  // allow mates to overlap in PE alignment
		gExpandToFrag           = true;  // incr max frag length to =larger mate len if necessary
		gReportDiscordant       = true;  // find and report discordant paired-end alignments
		gReportMixed            = true;  // find and report unpaired alignments for paired reads

		cacheLimit				= 5;     // ranges w/ size > limit will be cached
		cacheSize				= 0;     // # words per range cache
		skipReads				= 0;     // # reads/read pairs to skip
		gNofw					= false; // don't align fw orientation of read
		gNorc					= false; // don't align rc orientation of read
		fastaContLen			= 0;
		fastaContFreq			= 0;
		hadoopOut				= false; // print Hadoop status and summary messages
		fullRef					= false; // print entire reference name instead of just up to 1st space
		samTruncQname           = true;  // whether to truncate QNAME to 255 chars
		samOmitSecSeqQual       = false; // omit SEQ/QUAL for 2ndary alignments?
		samNoUnal               = false; // omit SAM records for unaligned reads
		samNoHead				= false; // don't print any header lines in SAM output
		samNoSQ					= false; // don't print @SQ header lines
		sam_print_as            = true;
		sam_print_xs            = true;
		sam_print_xss           = false; // Xs:i and Ys:i
		sam_print_yn            = false; // YN:i and Yn:i
		sam_print_xn            = true;
		sam_print_x0            = true;
		sam_print_x1            = true;
		sam_print_xm            = true;
		sam_print_xo            = true;
		sam_print_xg            = true;
		sam_print_nm            = true;
		sam_print_md            = true;
		sam_print_yf            = true;
		sam_print_yi            = false;
		sam_print_ym            = false;
		sam_print_yp            = false;
		sam_print_yt            = true;
		sam_print_ys            = true;
		sam_print_zs            = false;
		sam_print_xr            = false;
		sam_print_xt            = false;
		sam_print_xd            = false;
		sam_print_xu            = false;
		sam_print_yl            = false;
		sam_print_ye            = false;
		sam_print_yu            = false;
		sam_print_xp            = false;
		sam_print_yr            = false;
		sam_print_zb            = false;
		sam_print_zr            = false;
		sam_print_zf            = false;
		sam_print_zm            = false;
		sam_print_zi            = false;
		sam_print_zp            = false;
		sam_print_zu            = false;
		sam_print_zt            = false;
		bwaSwLike               = false;
		gSeedLenIsSet			= false;
		bwaSwLikeC              = 5.5f;
		bwaSwLikeT              = 20.0f;
		gDefaultSeedLen			= DEFAULT_SEEDLEN;
		qcFilter                = false; // don't believe upstream qc by default
		rgid					= "";    // SAM outputs for @RG header line
		rgs						= "";    // SAM outputs for @RG header line
		rgs_optflag				= "";    // SAM optional flag to add corresponding to @RG ID
		msample				    = true;
		gGapBarrier				= 4;     // disallow gaps within this many chars of either end of alignment
		qualities.clear();
		qualities1.clear();
		qualities2.clear();
		polstr.clear();
		msNoCache       = true; // true -> disable local cache
		bonusMatchType  = DEFAULT_MATCH_BONUS_TYPE;
		bonusMatch      = DEFAULT_MATCH_BONUS;
		penMmcType      = DEFAULT_MM_PENALTY_TYPE;
		penMmcMax       = DEFAULT_MM_PENALTY_MAX;
		penMmcMin       = DEFAULT_MM_PENALTY_MIN;
		penNType        = DEFAULT_N_PENALTY_TYPE;
		penN            = DEFAULT_N_PENALTY;
		penNCatPair     = DEFAULT_N_CAT_PAIR; // concatenate mates before N filtering?
		localAlign      = false;     // do local alignment in DP steps
		noisyHpolymer   = false;
		penRdGapConst   = DEFAULT_READ_GAP_CONST;
		penRfGapConst   = DEFAULT_REF_GAP_CONST;
		penRdGapLinear  = DEFAULT_READ_GAP_LINEAR;
		penRfGapLinear  = DEFAULT_REF_GAP_LINEAR;
		scoreMin.init  (SIMPLE_FUNC_LINEAR, DEFAULT_MIN_CONST,   DEFAULT_MIN_LINEAR);
		nCeil.init     (SIMPLE_FUNC_LINEAR, 0.0f, DMAX, 2.0f, 0.1f);
		msIval.init    (SIMPLE_FUNC_LINEAR, 1.0f, DMAX, DEFAULT_IVAL_B, DEFAULT_IVAL_A);
		descConsExp     = 2.0;
		descPrioritizeRoots = false;
		descLanding = 20;
		descentTotSz.init(SIMPLE_FUNC_LINEAR, 1024.0, DMAX, 0.0, 1024.0);
		descentTotFmops.init(SIMPLE_FUNC_LINEAR, 100.0, DMAX, 0.0, 10.0);
		multiseedMms    = DEFAULT_SEEDMMS;
		multiseedLen    = gDefaultSeedLen;
		multiseedOff    = 0;
		seedCacheLocalMB   = 32; // # MB to use for non-shared seed alignment cacheing
		seedCacheCurrentMB = 20; // # MB to use for current-read seed hit cacheing
		exactCacheCurrentMB = 20; // # MB to use for current-read seed hit cacheing
		maxhalf            = 15; // max width on one side of DP table
		seedSumm           = false; // print summary information about seed hits, not alignments
		scUnMapped         = false; // consider soft clipped bases unmapped when calculating TLEN
		xeq                = false; // use =/X instead of M in CIGAR string
		doUngapped         = true;  // do ungapped alignment
		maxIters           = 400;   // max iterations of extend loop
		maxUg              = 300;   // stop after this many ungap extends
		maxDp              = 300;   // stop after this many dp extends
		maxItersIncr       = 20;    // amt to add to maxIters for each -k > 1
		maxEeStreak        = 15;    // stop after this many end-to-end fails in a row
		maxUgStreak        = 15;    // stop after this many ungap fails in a row
		maxDpStreak        = 15;    // stop after this many dp fails in a row
		maxStreakIncr      = 10;    // amt to add to streak for each -k > 1
		maxMateStreak      = 10;    // in PE: abort seed range after N mate-find fails
		doExtend           = true;  // do seed extensions
		enable8            = true;  // use 8-bit SSE where possible?
		cminlen            = 2000;  // longer reads use checkpointing
		cpow2              = 4;     // checkpoint interval log2
		doTri              = false; // do triangular mini-fills?
		defaultPreset      = "sensitive%LOCAL%"; // default preset; applied immediately
		extra_opts.clear();
		extra_opts_cur = 0;
		bt2index.clear();        // read Bowtie 2 index from files with this prefix
		ignoreQuals = false;     // all mms incur same penalty, regardless of qual
		wrapper.clear();         // type of wrapper script, so we can print correct usage
		queries.clear();         // list of query files
		outfile.clear();         // write SAM output to this file
		mapqv = 2;               // MAPQ calculation version
		tighten = 3;             // -M tightening mode
		doExactUpFront = true;   // do exact search up front if seeds seem good enough
		do1mmUpFront = true;    // do 1mm search up front if seeds seem good enough
		seedBoostThresh = 300;   // if average non-zero position has more than this many elements
		nSeedRounds = 2;         // # rounds of seed searches to do for repetitive reads
		do1mmMinLen = 60;        // length below which we disable 1mm search
		reorder = false;         // reorder SAM records with -p > 1
		sampleFrac = 1.1f;       // align all reads
		arbitraryRandom = false; // let pseudo-random seeds be a function of read properties
		bowtie2p5 = false;
		logDps.clear();          // log seed-extend dynamic programming problems
		logDpsOpp.clear();       // log mate-search dynamic programming problems
	}
	public static void parseOptions(String[] args) {
		int option_index = 0;
		int next_option;
		saw_M = false;
		saw_a = false;
		saw_k = false;
		presetList.clear();
		if(startVerbose) { cerr << "Parsing options: "; logTime(cerr, true); }
		while(true) {
			next_option = getopt_long(
				argc, const_cast<char**>(argv),
				short_options, long_options, &option_index);
			const char * arg = optarg;
			if(next_option == EOF) {
				if(extra_opts_cur < extra_opts.size()) {
					next_option = extra_opts[extra_opts_cur].first;
					arg = extra_opts[extra_opts_cur].second.c_str();
					extra_opts_cur++;
				} else {
					break;
				}
			}
			parseOption(next_option, arg);
		}
		// Now parse all the presets.  Might want to pick which presets version to
		// use according to other parameters.
		auto_ptr<Presets> presets(new PresetsV0());
		// Apply default preset
		if(!defaultPreset.empty()) {
			polstr = applyPreset(defaultPreset, *presets.get()) + polstr;
		}
		// Apply specified presets
		for(size_t i = 0; i < presetList.size(); i++) {
			polstr += applyPreset(presetList[i], *presets.get());
		}
		for(size_t i = 0; i < extra_opts.size(); i++) {
			next_option = extra_opts[extra_opts_cur].first;
			const char *arg = extra_opts[extra_opts_cur].second.c_str();
			parseOption(next_option, arg);
		}
		// Remove initial semicolons
		while(!polstr.empty() && polstr[0] == ';') {
			polstr = polstr.substr(1);
		}
		if(gVerbose) {
			cerr << "Final policy string: '" << polstr.c_str() << "'" << endl;
		}
		size_t failStreakTmp = 0;
		SeedAlignmentPolicy::parseString(
			polstr,
			localAlign,
			noisyHpolymer,
			ignoreQuals,
			bonusMatchType,
			bonusMatch,
			penMmcType,
			penMmcMax,
			penMmcMin,
			penNType,
			penN,
			penRdGapConst,
			penRfGapConst,
			penRdGapLinear,
			penRfGapLinear,
			scoreMin,
			nCeil,
			penNCatPair,
			multiseedMms,
			multiseedLen,
			msIval,
			failStreakTmp,
			nSeedRounds);
		if(failStreakTmp > 0) {
			maxEeStreak = failStreakTmp;
			maxUgStreak = failStreakTmp;
			maxDpStreak = failStreakTmp;
		}
		if(saw_a || saw_k) {
			msample = false;
			mhits = 0;
		} else {
			assert_gt(mhits, 0);
			msample = true;
		}
		if(mates1.size() != mates2.size()) {
			cerr << "Error: " << mates1.size() << " mate files/sequences were specified with -1, but " << mates2.size() << endl
			     << "mate files/sequences were specified with -2.  The same number of mate files/" << endl
			     << "sequences must be specified with -1 and -2." << endl;
			throw 1;
		}
		if(qualities.size() && format != FASTA) {
			cerr << "Error: one or more quality files were specified with -Q but -f was not" << endl
			     << "enabled.  -Q works only in combination with -f and -C." << endl;
			throw 1;
		}
		if(qualities1.size() && format != FASTA) {
			cerr << "Error: one or more quality files were specified with --Q1 but -f was not" << endl
			     << "enabled.  --Q1 works only in combination with -f and -C." << endl;
			throw 1;
		}
		if(qualities2.size() && format != FASTA) {
			cerr << "Error: one or more quality files were specified with --Q2 but -f was not" << endl
			     << "enabled.  --Q2 works only in combination with -f and -C." << endl;
			throw 1;
		}
		if(qualities1.size() > 0 && mates1.size() != qualities1.size()) {
			cerr << "Error: " << mates1.size() << " mate files/sequences were specified with -1, but " << qualities1.size() << endl
			     << "quality files were specified with --Q1.  The same number of mate and quality" << endl
			     << "files must sequences must be specified with -1 and --Q1." << endl;
			throw 1;
		}
		if(qualities2.size() > 0 && mates2.size() != qualities2.size()) {
			cerr << "Error: " << mates2.size() << " mate files/sequences were specified with -2, but " << qualities2.size() << endl
			     << "quality files were specified with --Q2.  The same number of mate and quality" << endl
			     << "files must sequences must be specified with -2 and --Q2." << endl;
			throw 1;
		}
		if(!rgs.empty() && rgid.empty()) {
			cerr << "Warning: --rg was specified without --rg-id also "
			     << "being specified.  @RG line is not printed unless --rg-id "
				 << "is specified." << endl;
		}
		// Check for duplicate mate input files
		if(format != CMDLINE) {
			for(size_t i = 0; i < mates1.size(); i++) {
				for(size_t j = 0; j < mates2.size(); j++) {
					if(mates1[i] == mates2[j] && !gQuiet) {
						cerr << "Warning: Same mate file \"" << mates1[i].c_str() << "\" appears as argument to both -1 and -2" << endl;
					}
				}
			}
		}
		// If both -s and -u are used, we need to adjust qUpto accordingly
		// since it uses rdid to know if we've reached the -u limit (and
		// rdids are all shifted up by skipReads characters)
		if(qUpto + skipReads > qUpto) {
			qUpto += skipReads;
		}
		if(useShmem && useMm && !gQuiet) {
			cerr << "Warning: --shmem overrides --mm..." << endl;
			useMm = false;
		}
		if(gGapBarrier < 1) {
			cerr << "Warning: --gbar was set less than 1 (=" << gGapBarrier
			     << "); setting to 1 instead" << endl;
			gGapBarrier = 1;
		}
		if(bonusMatch > 0 && !scoreMin.alwaysPositive()) {
			cerr << "Error: the match penalty is greater than 0 (" << bonusMatch
			     << ") but the --score-min function can be less than or equal to "
				 << "zero.  Either let the match penalty be 0 or make --score-min "
				 << "always positive." << endl;
			throw 1;
		}
		if(multiseedMms >= multiseedLen) {
			assert_gt(multiseedLen, 0);
			cerr << "Warning: seed mismatches (" << multiseedMms
			     << ") is less than seed length (" << multiseedLen
				 << "); setting mismatches to " << (multiseedMms-1)
				 << " instead" << endl;
			multiseedMms = multiseedLen-1;
		}
		sam_print_zm = sam_print_zm && bowtie2p5;
	#ifndef NDEBUG
		if(!gQuiet) {
			cerr << "Warning: Running in debug mode.  Please use debug mode only "
				 << "for diagnosing errors, and not for typical use of Bowtie 2."
				 << endl;
		}
	}

}