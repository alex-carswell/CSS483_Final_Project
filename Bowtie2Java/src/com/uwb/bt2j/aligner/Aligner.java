package com.uwb.bt2j.aligner;

import org.omg.CORBA_2_3.portable.OutputStream;

import com.uwb.bt2j.aligner.sink.ALNSink;
import com.uwb.bt2j.util.BitPairReference;
import com.uwb.bt2j.util.file.OutFileBuf;
import com.uwb.bt2j.util.pattern.PatternComposer;
import com.uwb.bt2j.util.pattern.PatternParams;
import com.uwb.bt2j.util.pattern.PatternSourcePerThread;
import com.uwb.bt2j.util.pattern.PatternSourcePerThreadFactory;
import com.uwb.bt2j.util.types.EList;

import javafx.util.Pair;

class Aligner<T> {
	public static int FNAME_SIZE;
	public static int thread_counter;
	public static EList<String> mates1;
	public static EList<String> mates2;
	public static EList<String> mates12;
	public static EList<String> qualities;
	public static EList<String> qualities1;
	public static EList<String> qualities2;
	public static EList<String> queries;
	public static EList<String> presetList;
	public static EList<Pair<Integer, String>> extraOpts;
	
	public static PerfMetrics metrics;
	
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
	public static Boolean saw_M;
	public static Boolean saw_a;
	public static Boolean saw_k;
	
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
	
	public static PatternComposer   multiseed_patsrc;
	public static PatternParams            multiseed_pp;
	public static Ebwt                    multiseed_ebwtFw;
	public static Ebwt                    multiseed_ebwtBw;
	public static Scoring             multiseed_sc;
	public static BitPairReference        multiseed_refs;
	public static AlignmentCache          multiseed_ca; // seed cache
	public static ALNSink                 multiseed_msink;
	public static OutFileBuf              multiseed_metricsOfb;
	
	public int gVerbose;
	public int qQuiet;
	public int gMinInsert;
	public int gMaxInsert;
	public int gGapBarrier;
	public int gDefaultSeedLen;
	
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
				System.out.println( arg0 + " version " + BOWTIE2_VERSION);
				
				System.out.println( "Built on " + BUILD_HOST);
				System.out.println( BUILD_TIME );
				System.out.println( "Compiler: " + COMPILER_VERSION);
				System.out.println( "Options: " + COMPILER_OPTIONS);
			}
			{
				Timer _t(cerr, "Overall time: ", timing);
				if(startVerbose) {
					System.err.println( "Parsing index and read arguments: "); logTime(cerr, true);
				}

				// Get index basename (but only if it wasn't specified via --index)
				if(bt2index.empty()) {
					System.err.println( "No index, query, or output file specified!");
					printUsage(cerr);
				}
		
				if(thread_stealing && thread_stealing_dir.empty()) {
					System.err.println( "When --thread-ceiling is specified, must also specify --thread-piddir");
					printUsage(cerr);
				}

				// Get query filename
				Boolean got_reads = !queries.empty() || !mates1.empty() || !mates12.empty();
				
				if(optind >= args.length) {
					if(!got_reads) {
						printUsage(cerr);
						System.err.println( "***");
						System.err.println(  "Error: Must specify at least one read input with -U/-1/-2");
					}
				} else if(!got_reads) {
					// Tokenize the list of query files
					tokenize(args[optind++], ",", queries);
					if(queries.empty()) {
						System.err.println( "Tokenized query file list was empty!");
						printUsage(cerr);
					}
				}

				// Get output filename
				if(optind < args.length && outfile.empty()) {
					outfile = args[optind++];
					System.err.println( "Warning: Output file '" + outfile.c_str()
					     + "' was specified without -S.  This will not work in "
						 + "future Bowtie 2 versions.  Please use -S instead."
						 );
				}

				// Extra parametesr?
				if(optind < args.length) {
					System.err.println( "Extra parameter(s) specified: ");
					for(int i = optind; i < args.length; i++) {
						System.err.println( "\"" + args[i] + "\"");
						if(i < args.length-1)
							System.err.println( ", ");
					}
					System.err.println( );
					if(mates1.size() > 0) {
						System.err.println( "Note that if <mates> files are specified using -1/-2, a <singles> file cannot"
							 + "also be specified.  Please run bowtie separately for mates and singles." );
					}
				}

				// Optionally summarize
				if(gVerbose) {
					System.out.println( "Input " + gEbwt_ext +" file: \"" << bt2index.c_str() << "\"" );
					System.out.println( "Query inputs (DNA, " << file_format_names[format].c_str() << "):" );
					for(double i = 0; i < queries.size(); i++) {
						System.out.println( "  " << queries[i].c_str() );
					}
					System.out.println( "Quality inputs:" );
					for(double i = 0; i < qualities.size(); i++) {
						System.out.println( "  " << qualities[i].c_str() );
					}
					System.out.println( "Output file: \"" << outfile.c_str() << "\"" );
					System.out.println( "Local endianness: " << (currentlyBigEndian()? "big":"little") );
					System.out.println( "Sanity checking: " << (sanityCheck? "enabled":"disabled") );
				}

				driver<SString<char> >("DNA", bt2index, outfile);
			}
		} catch(Exception e) {
			System.err.println("Error: Encountered exception: '" + e + "'");
			System.err.println("Command: ");
			for(int i = 0; i < args.length; i++)
				System.err.println(args[i] + " ");
		} catch(int e) {
			if(e != 0) {
				System.err.println("Error: Encountered internal Bowtie 2 exception (#" + e + ")");
				System.err.println("Command: ");
				for(int i = 0; i < args.length; i++)
					System.err.println(args[i] + " ");
			}
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
		nCeil.init     (SIMPLE_FUNC_LINEAR, 0.0f, Double.MAX_VALUE, 2.0f, 0.1f);
		msIval.init    (SIMPLE_FUNC_LINEAR, 1.0f, Double.MAX_VALUE, DEFAULT_IVAL_B, DEFAULT_IVAL_A);
		descConsExp     = 2.0;
		descPrioritizeRoots = false;
		descLanding = 20;
		descentTotSz.init(SIMPLE_FUNC_LINEAR, 1024.0, Double.MAX_VALUE, 0.0, 1024.0);
		descentTotFmops.init(SIMPLE_FUNC_LINEAR, 100.0, Double.MAX_VALUE, 0.0, 10.0);
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
		if(startVerbose) { System.err.println( "Parsing options: "); logTime(cerr, true); }
		while(true) {
			next_option = getopt_long(
				argc, const_cast<char>(argv),
				short_options, long_options, &option_index);
			String arg = optarg;
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
		for(double i = 0; i < presetList.size(); i++) {
			polstr += applyPreset(presetList[i], *presets.get());
		}
		for(double i = 0; i < extra_opts.size(); i++) {
			next_option = extra_opts[extra_opts_cur].first;
			const char *arg = extra_opts[extra_opts_cur].second.c_str();
			parseOption(next_option, arg);
		}
		// Remove initial semicolons
		while(!polstr.empty() && polstr[0] == ';') {
			polstr = polstr.substr(1);
		}
		if(gVerbose) {
			System.err.println( "Final policy string: '" + polstr.c_str() + "'" );
		}
		double failStreakTmp = 0;
		SeedAlignmentPolicy.parseString(
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
			System.err.println( "Error: " + mates1.size() + " mate files/sequences were specified with -1, but " + mates2.size() + "\n"
			     + "mate files/sequences were specified with -2.  The same number of mate files/" + "\n"
			     + "sequences must be specified with -1 and -2." );
		}
		if(qualities.size() && format != FASTA) {
			System.err.println( "Error: one or more quality files were specified with -Q but -f was not" + "\n"
			     + "enabled.  -Q works only in combination with -f and -C." );
		}
		if(qualities1.size() && format != FASTA) {
			System.err.println( "Error: one or more quality files were specified with --Q1 but -f was not" + "\n"
			     + "enabled.  --Q1 works only in combination with -f and -C." );
		}
		if(qualities2.size() && format != FASTA) {
			System.err.println( "Error: one or more quality files were specified with --Q2 but -f was not" + "\n"
			     + "enabled.  --Q2 works only in combination with -f and -C." );
		}
		if(qualities1.size() > 0 && mates1.size() != qualities1.size()) {
			System.err.println( "Error: " + mates1.size() + " mate files/sequences were specified with -1, but " + qualities1.size() + "\n"
			     + "quality files were specified with --Q1.  The same number of mate and quality" + "\n"
			     + "files must sequences must be specified with -1 and --Q1." );
		}
		if(qualities2.size() > 0 && mates2.size() != qualities2.size()) {
			System.err.println( "Error: " + mates2.size() + " mate files/sequences were specified with -2, but " + qualities2.size() + "\n"
			     + "quality files were specified with --Q2.  The same number of mate and quality" + "\n"
			     + "files must sequences must be specified with -2 and --Q2." );
		}
		if(!rgs.empty() && rgid.empty()) {
			System.err.println( "Warning: --rg was specified without --rg-id also "
			     + "being specified.  @RG line is not printed unless --rg-id "
				 + "is specified." );
		}
		// Check for duplicate mate input files
		if(format != CMDLINE) {
			for(double i = 0; i < mates1.size(); i++) {
				for(double j = 0; j < mates2.size(); j++) {
					if(mates1[i] == mates2[j] && !gQuiet) {
						System.err.println( "Warning: Same mate file \"" + mates1[i].c_str() + "\" appears as argument to both -1 and -2" );
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
			System.err.println( "Warning: --shmem overrides --mm..." );
			useMm = false;
		}
		if(gGapBarrier < 1) {
			System.err.println( "Warning: --gbar was set less than 1 (=" + gGapBarrier
			     + "); setting to 1 instead" );
			gGapBarrier = 1;
		}
		if(bonusMatch > 0 && !scoreMin.alwaysPositive()) {
			System.err.println( "Error: the match penalty is greater than 0 (" + bonusMatch
			     + ") but the --score-min function can be less than or equal to "
				 + "zero.  Either let the match penalty be 0 or make --score-min "
				 + "always positive." );
		}
		if(multiseedMms >= multiseedLen) {
			System.err.println( "Warning: seed mismatches (" + multiseedMms
			     + ") is less than seed length (" + multiseedLen
				 + "); setting mismatches to " + (multiseedMms-1)
				 + " instead" );
			multiseedMms = multiseedLen-1;
		}
		sam_print_zm = sam_print_zm && bowtie2p5;

		if(!gQuiet) {
			System.err.println( "Warning: Running in debug mode.  Please use debug mode only "
				 + "for diagnosing errors, and not for typical use of Bowtie 2."
				 );
		}
	}
	
	public void printArgDesc(OutputStream out) {
		double i = 0;
		while(long_options[i].name != 0) {
			out.write(long_options[i].name + "\t"
			    + (long_options[i].has_arg == no_argument ? 0 : 1));
			i++;
		}
		double solen = strlen(short_options);
		for(i = 0; i < solen; i++) {
			// Has an option?  Does if next char is :
			if(i == solen-1) {
				System.out.println((char)short_options[i] + "\t" + 0);
			} else {
				if(short_options[i+1] == ':') {
					// Option with argument
					System.out.println((char)short_options[i] + "\t" + 1);
					i++; // skip the ':'
				} else {
					// Option with no argument
					System.out.println((char)short_options[i] + "\t" + 0 );
				}
			}
		}
	}
	
	public static void printUsage(OutputStream out) {
		out.write("Bowtie 2 version " + BOWTIE2_VERSION + " by Ben Langmead (langmea@cs.jhu.edu, www.cs.jhu.edu/~langmea)");
		String tool_name = "bowtie2-align";
		if(wrapper == "basic-0") {
			tool_name = "bowtie2";
		}
		out.write("Usage: " + "\n"
		    + "  " + tool_name + " [options]* -x <bt2-idx> {-1 <m1> -2 <m2> | -U <r> | --interleaved <i>} [-S <sam>]" + "\n"
		    + "\n"
			+     "  <bt2-idx>  Index filename prefix (minus trailing .X." + gEbwt_ext + ")." + "\n"
			+     "             NOTE: Bowtie 1 and Bowtie 2 indexes are not compatible." + "\n"
		    +     "  <m1>       Files with #1 mates, paired with files in <m2>." + "\n");
		if(wrapper == "basic-0") {
			out.write("             Could be gzip'ed (extension: .gz) or bzip2'ed (extension: .bz2)." + "\n");
		}
		out.write("  <m2>       Files with #2 mates, paired with files in <m1>." + "\n");
		if(wrapper == "basic-0") {
			out.write("             Could be gzip'ed (extension: .gz) or bzip2'ed (extension: .bz2)." + "\n");
		}
		out.write("  <r>        Files with unpaired reads." + "\n");
		if(wrapper == "basic-0") {
			out.write("             Could be gzip'ed (extension: .gz) or bzip2'ed (extension: .bz2)." + "\n");
		}
		out.write("  <i>        Files with interleaved paired-end FASTQ reads" + "\n");
		if(wrapper == "basic-0") {
			out.write("             Could be gzip'ed (extension: .gz) or bzip2'ed (extension: .bz2)." + "\n");
		}
		out.write("  <sam>      File for SAM output (default: stdout)" + "\n"
		    + "\n"
		    + "  <m1>, <m2>, <r> can be comma-separated lists (no whitespace) and can be" + "\n"
			+ "  specified many times.  E.g. '-U file1.fq,file2.fq -U file3.fq'." + "\n"
			// Wrapper script should write <bam> line next
			+ "\n"
		    + "Options (defaults in parentheses):" + "\n"
			+ "\n"
		    + " Input:" + "\n"
		    + "  -q                 query input files are FASTQ .fq/.fastq (default)" + "\n"
			+ "  --tab5             query input files are TAB5 .tab5" + "\n"
			+ "  --tab6             query input files are TAB6 .tab6" + "\n"
		    + "  --qseq             query input files are in Illumina's qseq format" + "\n"
		    + "  -f                 query input files are (multi-)FASTA .fa/.mfa" + "\n"
		    + "  -r                 query input files are raw one-sequence-per-line"+ "\n"
		    + "  -F k:<int>,i:<int> query input files are continuous FASTA where reads" + "\n"
		    + "                     are substrings (k-mers) extracted from a FASTA file <s>" + "\n"
		    + "                     and aligned at offsets 1, 1+i, 1+2i ... end of reference" + "\n"
		    + "  -c                 <m1>, <m2>, <r> are sequences themselves, not files" + "\n"
		    + "  -s/--skip <int>    skip the first <int> reads/pairs in the input (none)" + "\n"
		    + "  -u/--upto <int>    stop after first <int> reads/pairs (no limit)" + "\n"
		    + "  -5/--trim5 <int>   trim <int> bases from 5'/left end of reads (0)" + "\n"
		    + "  -3/--trim3 <int>   trim <int> bases from 3'/right end of reads (0)" + "\n"
		    + "  --phred33          qualities are Phred+33 (default)" + "\n"
		    + "  --phred64          qualities are Phred+64" + "\n"
		    + "  --int-quals        qualities encoded as space-delimited integers"+ "\n"
		    + "\n"
		    + " Presets:                 Same as:" + "\n"
			+ "  For --end-to-end:" + "\n"
			+ "   --very-fast            -D 5 -R 1 -N 0 -L 22 -i S,0,2.50" + "\n"
			+ "   --fast                 -D 10 -R 2 -N 0 -L 22 -i S,0,2.50" + "\n"
			+ "   --sensitive            -D 15 -R 2 -N 0 -L 22 -i S,1,1.15 (default)" + "\n"
			+ "   --very-sensitive       -D 20 -R 3 -N 0 -L 20 -i S,1,0.50" + "\n"
			+ "\n"
			+ "  For --local:" + "\n"
			+ "   --very-fast-local      -D 5 -R 1 -N 0 -L 25 -i S,1,2.00" + "\n"
			+ "   --fast-local           -D 10 -R 2 -N 0 -L 22 -i S,1,1.75" + "\n"
			+ "   --sensitive-local      -D 15 -R 2 -N 0 -L 20 -i S,1,0.75 (default)" + "\n"
			+ "   --very-sensitive-local -D 20 -R 3 -N 0 -L 20 -i S,1,0.50" + "\n"
			+ "\n"
		    + " Alignment:" + "\n"
			+ "  -N <int>           max # mismatches in seed alignment; can be 0 or 1 (0)"+ "\n"
			+ "  -L <int>           length of seed substrings; must be >3, <32 (22)" + "\n"
			+ "  -i <func>          interval between seed substrings w/r/t read len (S,1,1.15)" + "\n"
			+ "  --n-ceil <func>    func for max # non-A/C/G/Ts permitted in aln (L,0,0.15)" + "\n"
			+ "  --dpad <int>       include <int> extra ref chars on sides of DP table (15)" + "\n"
			+ "  --gbar <int>       disallow gaps within <int> nucs of read extremes (4)" + "\n"
			+ "  --ignore-quals     treat all quality values as 30 on Phred scale (off)" + "\n"
		    + "  --nofw             do not align forward (original) version of read (off)" + "\n"
		    + "  --norc             do not align reverse-complement version of read (off)" + "\n"
		    + "  --no-1mm-upfront   do not allow 1 mismatch alignments before attempting to" + "\n"
		    + "                     scan for the optimal seeded alignments"
		    + "\n"
			+ "  --end-to-end       entire read must align; no clipping (on)" + "\n"
			+ "   OR" + "\n"
			+ "  --local            local alignment; ends might be soft clipped (off)" + "\n"
			+ "\n"
		    + " Scoring:" + "\n"
			+ "  --ma <int>         match bonus (0 for --end-to-end, 2 for --local) " + "\n"
			+ "  --mp <int>         max penalty for mismatch; lower qual = lower penalty (6)" + "\n"
			+ "  --np <int>         penalty for non-A/C/G/Ts in read/ref (1)" + "\n"
			+ "  --rdg <int>,<int>  read gap open, extend penalties (5,3)" + "\n"
			+ "  --rfg <int>,<int>  reference gap open, extend penalties (5,3)" + "\n"
			+ "  --score-min <func> min acceptable alignment score w/r/t read length" + "\n"
			+ "                     (G,20,8 for local, L,-0.6,-0.6 for end-to-end)" + "\n"
			+ "\n"
		    + " Reporting:" + "\n"
		    + "  (default)          look for multiple alignments, report best, with MAPQ" + "\n"
			+ "   OR" + "\n"
		    + "  -k <int>           report up to <int> alns per read; MAPQ not meaningful" + "\n"
			+ "   OR" + "\n"
		    + "  -a/--all           report all alignments; very slow, MAPQ not meaningful" + "\n"
		    + "\n"
		    + " Effort:" + "\n"
		    + "  -D <int>           give up extending after <int> failed extends in a row (15)" + "\n"
		    + "  -R <int>           for reads w/ repetitive seeds, try <int> sets of seeds (2)" + "\n"
		    + "\n"
			+ " Paired-end:" + "\n"
		    + "  -I/--minins <int>  minimum fragment length (0)" + "\n"
		    + "  -X/--maxins <int>  maximum fragment length (500)" + "\n"
		    + "  --fr/--rf/--ff     -1, -2 mates align fw/rev, rev/fw, fw/fw (--fr)"+ "\n"
			+ "  --no-mixed         suppress unpaired alignments for paired reads" + "\n"
			+ "  --no-discordant    suppress discordant alignments for paired reads" + "\n"
			+ "  --dovetail         concordant when mates extend past each other" + "\n"
			+ "  --no-contain       not concordant when one mate alignment contains other" + "\n"
			+ "  --no-overlap       not concordant when mates overlap at all" + "\n"
			+ "\n"
		    + " Output:");

		out.write("  -t/--time          print wall-clock time taken by search phases" + "\n");
		if(wrapper == "basic-0") {
		out.write("  --un <path>        write unpaired reads that didn't align to <path>" + "\n"
		    + "  --al <path>        write unpaired reads that aligned at least once to <path>" + "\n"
		    + "  --un-conc <path>   write pairs that didn't align concordantly to <path>" + "\n"
		    + "  --al-conc <path>   write pairs that aligned concordantly at least once to <path>" + "\n"
		    + "    (Note: for --un, --al, --un-conc, or --al-conc, add '-gz' to the option name, e.g." + "\n"
		    + "    --un-gz <path>, to gzip compress output, or add '-bz2' to bzip2 compress output.)" + "\n");
		}
		out.write("  --quiet            print nothing to stderr except serious errors" + "\n"
			+ "  --met-file <path>  send metrics to file at <path> (off)" + "\n"
			+ "  --met-stderr       send metrics to stderr (off)" + "\n"
			+ "  --met <int>        report internal counters & metrics every <int> secs (1)" + "\n"
		// Following is supported in the wrapper instead
		    + "  --no-unal          suppress SAM records for unaligned reads" + "\n"
		    + "  --no-head          suppress header lines, i.e. lines starting with @" + "\n"
		    + "  --no-sq            suppress @SQ header lines" + "\n"
		    + "  --rg-id <text>     set read group id, reflected in @RG line and RG:Z: opt field" + "\n"
		    + "  --rg <text>        add <text> (\"lab:value\") to @RG line of SAM header." + "\n"
		    + "                     Note: @RG line only printed when --rg-id is set." + "\n"
		    + "  --omit-sec-seq     put '*' in SEQ and QUAL fields for secondary alignments." + "\n"
		    + "  --sam-no-qname-trunc Suppress standard behavior of truncating readname at first whitespace " + "\n"
		    + "                      at the expense of generating non-standard SAM." + "\n"
		    + "  --xeq              Use '='/'X', instead of 'M,' to specify matches/mismatches in SAM record." + "\n"
		    + "  --soft-clipped-unmapped-tlen Exclude soft-clipped bases when reporting TLEN" + "\n"
		    + "\n"
		    + " Performance:" + "\n"
		    + "  -p/--threads <int> number of alignment threads to launch (1)" + "\n"
		    + "  --reorder          force SAM output order to match order of input reads"+ "\n"
		    + "\n"
		    + " Other:" + "\n"
			+ "  --qc-filter        filter out reads that are bad according to QSEQ filter" + "\n"
		    + "  --seed <int>       seed for random number generator (0)" + "\n"
		    + "  --non-deterministic seed rand. gen. arbitrarily instead of using read attributes" + "\n"
		    + "  --version          print version information and quit" + "\n"
		    + "  -h/--help          print this usage message" + "\n");
		if(wrapper.empty()) {
			System.err.println("*** Warning ***" + "\n" + "'bowtie2-align' was run directly.  It is recommended that you run the wrapper script 'bowtie2' instead.");
		}
	}
	
	public static int parseInt(int lower, int upper, String errmsg, String arg) {
		long l = Long.parseLong(arg);
		
			if (l < lower || l > upper) {
				System.err.println(errmsg);
				printUsage(cerr);
			}
			return (int)l;
		System.err.println(errmsg);
		printUsage(cerr);
		return -1;
	}
	
	public static int parseInt(int lower, String errmsg, String arg) {
		return parseInt(lower, Integer.MAX_VALUE, errmsg, arg);
	}
	
	public T parse(String s) {
		T tmp;
		
	}
	
	public Pair<T, T> parsePair(String s, char delim) {
		EList<String> ss = tokenize(s, delim);
		Pair<T, T> ret;
		ret.first = parse<T>(ss[0]);
		ret.second = parse<T>(ss[1]);
		return ret;
	}
	
	public void parseTuple(String s, char delim, EList<T> ret) {
		EList<String> ss = tokenize(s, delim);
		for(double i = 0; i < ss.size(); i++) {
			ret.push_back(parse<T>(ss[i]));
		}
	}
	
	public static String applyPreset(String sorig, Presets presets) {
		String s = sorig;
		int found = s.indexOf("%LOCAL%");
		if(found != -1) {
			s.replace("%LOCAL%", localAlign ? "-local" : "");
		}
		if(gVerbose) {
			System.err.println("Applying preset: '" + s + "' using preset menu '"
				 + presets.name() + "'" + "\n");
		}
		String pol;
		presets.apply(s, pol, extra_opts);
		return pol;
	}
	
	public static PatternSourcePerThreadFactory createPatsrcFactory(
			PatternComposer patcomp, PatternParams pp, int tid) {
		PatternSourcePerThreadFactory patsrcFact;
		patsrcFact = new PatternSourcePerThreadFactory(patcomp, pp);
		return patsrcFact;
	}
	
 	public static void parseOption(int next_option, String arg) {
	switch (next_option) {
		case ARG_TEST_25: bowtie2p5 = true; break;
		case ARG_DESC_KB: descentTotSz = SimpleFunc.parse(arg, 0.0, 1024.0, 1024.0, Double.MAX_VALUE); break;
		case ARG_DESC_FMOPS: descentTotFmops = SimpleFunc.parse(arg, 0.0, 10.0, 100.0, Double.MAX_VALUE); break;
		case ARG_LOG_DP: logDps = arg; break;
		case ARG_LOG_DP_OPP: logDpsOpp = arg; break;
		case ARG_DESC_LANDING: {
			descLanding = parse<Integer>(arg);
			if(descLanding < 1) {
				System.err.println( "Error: --desc-landing must be greater than or equal to 1");
			}
			break;
		}
		case ARG_DESC_EXP: {
			descConsExp = parse<double>(arg);
			if(descConsExp < 0.0) {
				System.err.println( "Error: --desc-exp must be greater than or equal to 0");
			}
			break;
		}
		case ARG_DESC_PRIORITIZE: descPrioritizeRoots = true; break;
		case '1': tokenize(arg, ",", mates1); break;
		case '2': tokenize(arg, ",", mates2); break;
		case ARG_ONETWO: tokenize(arg, ",", mates12); format = TAB_MATE5; break;
		case ARG_TAB5:   tokenize(arg, ",", mates12); format = TAB_MATE5; break;
		case ARG_TAB6:   tokenize(arg, ",", mates12); format = TAB_MATE6; break;
		case ARG_INTERLEAVED_FASTQ: tokenize(arg, ",", mates12); format = INTERLEAVED; break;
		case 'f': format = FASTA; break;
		case 'F': {
			format = FASTA_CONT;
			Pair<double, double> p = parsePair<double>(arg, ',');
			fastaContLen = p.first;
			fastaContFreq = p.second;
			break;
		}
		case ARG_BWA_SW_LIKE: {
			bwaSwLikeC = 5.5f;
			bwaSwLikeT = 30;
			bwaSwLike = true;
			localAlign = true;
			// -a INT   Score of a match [1]
			// -b INT   Mismatch penalty [3]
			// -q INT   Gap open penalty [5]
			// -r INT   Gap extension penalty. The penalty for a contiguous
			//          gap of size k is q+k*r. [2] 
			polstr += ";MA=1;MMP=C3;RDG=5,2;RFG=5,2";
			break;
		}
		case 'q': format = FASTQ; break;
		case 'r': format = RAW; break;
		case 'c': format = CMDLINE; break;
		case ARG_QSEQ: format = QSEQ; break;
		case 'I':
			gMinInsert = parseInt(0, "-I arg must be positive", arg);
			break;
		case 'X':
			gMaxInsert = parseInt(1, "-X arg must be at least 1", arg);
			break;
		case ARG_NO_DISCORDANT: gReportDiscordant = false; break;
		case ARG_NO_MIXED: gReportMixed = false; break;
		case 's':
			skipReads = (double)parseInt(0, "-s arg must be positive", arg);
			break;
		case ARG_FF: gMate1fw = true;  gMate2fw = true;  break;
		case ARG_RF: gMate1fw = false; gMate2fw = true;  break;
		case ARG_FR: gMate1fw = true;  gMate2fw = false; break;
		case ARG_SHMEM: useShmem = true; break;
		case ARG_SEED_SUMM: seedSumm = true; break;
		case ARG_SC_UNMAPPED: scUnMapped = true; break;
		case ARG_XEQ: xeq = true; break;
		case ARG_MM: {
			System.err.println( "Memory-mapped I/O mode is disabled because bowtie was not compiled with" + "\n"
				 + "BOWTIE_MM defined.  Memory-mapped I/O is not supported under Windows.  If you" + "\n"
				 + "would like to use memory-mapped I/O on a platform that supports it, please" + "\n"
				 + "refrain from specifying BOWTIE_MM=0 when compiling Bowtie.");
		}
		case ARG_MMSWEEP: mmSweep = true; break;
		case ARG_HADOOPOUT: hadoopOut = true; break;
		case ARG_SOLEXA_QUALS: solexaQuals = true; break;
		case ARG_INTEGER_QUALS: integerQuals = true; break;
		case ARG_PHRED64: phred64Quals = true; break;
		case ARG_PHRED33: solexaQuals = false; phred64Quals = false; break;
		case ARG_OVERHANG: gReportOverhangs = true; break;
		case ARG_NO_CACHE: msNoCache = true; break;
		case ARG_USE_CACHE: msNoCache = false; break;
		case ARG_LOCAL_SEED_CACHE_SZ:
			seedCacheLocalMB = (double)parseInt(1, "--local-seed-cache-sz arg must be at least 1", arg);
			break;
		case ARG_CURRENT_SEED_CACHE_SZ:
			seedCacheCurrentMB = (double)parseInt(1, "--seed-cache-sz arg must be at least 1", arg);
			break;
		case ARG_REFIDX: noRefNames = true; break;
		case ARG_FULLREF: fullRef = true; break;
		case ARG_GAP_BAR:
			gGapBarrier = parseInt(1, "--gbar must be no less than 1", arg);
			break;
		case ARG_SEED:
			seed = parseInt(0, "--seed arg must be at least 0", arg);
			break;
		case ARG_NON_DETERMINISTIC:
			arbitraryRandom = true;
			break;
		case 'u':
			qUpto = (double)parseInt(1, "-u/--qupto arg must be at least 1", arg);
			break;
		case 'Q':
			tokenize(arg, ",", qualities);
			integerQuals = true;
			break;
		case ARG_QUALS1:
			tokenize(arg, ",", qualities1);
			integerQuals = true;
			break;
		case ARG_QUALS2:
			tokenize(arg, ",", qualities2);
			integerQuals = true;
			break;
		case ARG_CACHE_LIM:
			cacheLimit = (double)parseInt(1, "--cachelim arg must be at least 1", arg);
			break;
		case ARG_CACHE_SZ:
			cacheSize = (double)parseInt(1, "--cachesz arg must be at least 1", arg);
			cacheSize *= (1024 * 1024); // convert from MB to B
			break;
		case ARG_WRAPPER: wrapper = arg; break;
		case 'p':
			nthreads = parseInt(1, "-p/--threads arg must be at least 1", arg);
			break;
		case ARG_THREAD_CEILING:
			thread_ceiling = parseInt(0, "--thread-ceiling must be at least 0", arg);
			break;
		case ARG_THREAD_PIDDIR:
			thread_stealing_dir = arg;
			break;
		case ARG_FILEPAR:
			fileParallel = true;
			break;
		case '3': gTrim3 = parseInt(0, "-3/--trim3 arg must be at least 0", arg); break;
		case '5': gTrim5 = parseInt(0, "-5/--trim5 arg must be at least 0", arg); break;
		case 'h': printUsage(cout); throw 0; break;
		case ARG_USAGE: printUsage(cout); throw 0; break;
		//
		// NOTE that unlike in Bowtie 1, -M, -a and -k are mutually
		// exclusive here.
		//
		case 'M': {
			msample = true;
			mhits = parse<double>(arg);
			if(saw_a || saw_k) {
				System.err.println( "Warning: -M, -k and -a are mutually exclusive. "
					 + "-M will override");
				khits = 1;
			}
			saw_M = true;
			System.err.println( "Warning: -M is deprecated.  Use -D and -R to adjust " +
			        "effort instead.");
			break;
		}
		case ARG_EXTEND_ITERS: {
			maxIters = parse<double>(arg);
			break;
		}
		case ARG_NO_EXTEND: {
			doExtend = false;
			break;
		}
		case 'R': { polstr += ";ROUNDS="; polstr += arg; break; }
		case 'D': { polstr += ";DPS=";    polstr += arg; break; }
		case ARG_DP_MATE_STREAK_THRESH: {
			maxMateStreak = parse<double>(arg);
			break;
		}
		case ARG_DP_FAIL_STREAK_THRESH: {
			maxDpStreak = parse<double>(arg);
			break;
		}
		case ARG_EE_FAIL_STREAK_THRESH: {
			maxEeStreak = parse<double>(arg);
			break;
		}
		case ARG_UG_FAIL_STREAK_THRESH: {
			maxUgStreak = parse<double>(arg);
			break;
		}
		case ARG_DP_FAIL_THRESH: {
			maxDp = parse<double>(arg);
			break;
		}
		case ARG_UG_FAIL_THRESH: {
			maxUg = parse<double>(arg);
			break;
		}
		case ARG_SEED_BOOST_THRESH: {
			seedBoostThresh = parse<int>(arg);
			break;
		}
		case 'a': {
			msample = false;
			allHits = true;
			mhits = 0; // disable -M
			if(saw_M || saw_k) {
				System.err.println( "Warning: -M, -k and -a are mutually exclusive. "
					 + "-a will override");
			}
			saw_a = true;
			break;
		}
		case 'k': {
			msample = false;
			khits = (double)parseInt(1, "-k arg must be at least 1", arg);
			mhits = 0; // disable -M
			if(saw_M || saw_a) {
				System.err.println( "Warning: -M, -k and -a are mutually exclusive. "
					 + "-k will override");
			}
			saw_k = true;
			break;
		}
		case ARG_VERBOSE: gVerbose = 1; break;
		case ARG_STARTVERBOSE: startVerbose = true; break;
		case ARG_QUIET: gQuiet = true; break;
		case ARG_SANITY: sanityCheck = true; break;
		case 't': timing = true; break;
		case ARG_METRIC_IVAL: {
			metricsIval = parseInt(1, "--metrics arg must be at least 1", arg);
			break;
		}
		case ARG_METRIC_FILE: metricsFile = arg; break;
		case ARG_METRIC_STDERR: metricsStderr = true; break;
		case ARG_METRIC_PER_READ: metricsPerRead = true; break;
		case ARG_NO_FW: gNofw = true; break;
		case ARG_NO_RC: gNorc = true; break;
		case ARG_SAM_NO_QNAME_TRUNC: samTruncQname = false; break;
		case ARG_SAM_OMIT_SEC_SEQ: samOmitSecSeqQual = true; break;
		case ARG_SAM_NO_UNAL: samNoUnal = true; break;
		case ARG_SAM_NOHEAD: samNoHead = true; break;
		case ARG_SAM_NOSQ: samNoSQ = true; break;
		case ARG_SAM_PRINT_YI: sam_print_yi = true; break;
		case ARG_REORDER: reorder = true; break;
		case ARG_MAPQ_EX: {
			sam_print_zt = true;
			break;
		}
		case ARG_SHOW_RAND_SEED: {
			sam_print_zs = true;
			break;
		}
		case ARG_SAMPLE:
			sampleFrac = parse<float>(arg);
			break;
		case ARG_CP_MIN:
			cminlen = parse<double>(arg);
			break;
		case ARG_CP_IVAL:
			cpow2 = parse<double>(arg);
			break;
		case ARG_TRI:
			doTri = true;
			break;
		case ARG_READ_PASSTHRU: {
			sam_print_xr = true;
			break;
		}
		case ARG_READ_TIMES: {
			sam_print_xt = true;
			sam_print_xd = true;
			sam_print_xu = true;
			sam_print_yl = true;
			sam_print_ye = true;
			sam_print_yu = true;
			sam_print_yr = true;
			sam_print_zb = true;
			sam_print_zr = true;
			sam_print_zf = true;
			sam_print_zm = true;
			sam_print_zi = true;
			break;
		}
		case ARG_SAM_RG: {
			String argstr = arg;
			if(argstr.substr(0, 3) == "ID:") {
				rgid = "\t";
				rgid += argstr;
				rgs_optflag = "RG:Z:" + argstr.substr(3);
			} else {
				rgs += '\t';
				rgs += argstr;
			}
			break;
		}
		case ARG_SAM_RGID: {
			String argstr = arg;
			rgid = "\t";
			rgid = "\tID:" + argstr;
			rgs_optflag = "RG:Z:" + argstr;
			break;
		}
		case ARG_PARTITION: partitionSz = parse<int>(arg); break;
		case ARG_READS_PER_BATCH:
			readsPerBatch = parseInt(1, "--reads-per-batch arg must be at least 1", arg);
			break;
		case ARG_DPAD:
			maxhalf = parseInt(0, "--dpad must be no less than 0", arg);
			break;
		case ARG_ORIG:
			if(arg == null || arg.length() == 0) {
				System.err.println( "--orig arg must be followed by a string");
				printUsage(cerr);
			}
			origString = arg;
			break;
		case ARG_LOCAL: {
			localAlign = true;
			gDefaultSeedLen = DEFAULT_LOCAL_SEEDLEN;
			break;
		}
		case ARG_END_TO_END: localAlign = false; break;
		case ARG_SSE8: enable8 = true; break;
		case ARG_SSE8_NO: enable8 = false; break;
		case ARG_UNGAPPED: doUngapped = true; break;
		case ARG_UNGAPPED_NO: doUngapped = false; break;
		case ARG_NO_DOVETAIL: gDovetailMatesOK = false; break;
		case ARG_NO_CONTAIN:  gContainMatesOK  = false; break;
		case ARG_NO_OVERLAP:  gOlapMatesOK     = false; break;
		case ARG_DOVETAIL:    gDovetailMatesOK = true;  break;
		case ARG_CONTAIN:     gContainMatesOK  = true;  break;
		case ARG_OVERLAP:     gOlapMatesOK     = true;  break;
		case ARG_QC_FILTER: qcFilter = true; break;
		case ARG_IGNORE_QUALS: ignoreQuals = true; break;
		case ARG_MAPQ_V: mapqv = parse<int>(arg); break;
		case ARG_TIGHTEN: tighten = parse<int>(arg); break;
		case ARG_EXACT_UPFRONT:    doExactUpFront = true; break;
		case ARG_1MM_UPFRONT:      do1mmUpFront   = true; break;
		case ARG_EXACT_UPFRONT_NO: doExactUpFront = false; break;
		case ARG_1MM_UPFRONT_NO:   do1mmUpFront   = false; break;
		case ARG_1MM_MINLEN:       do1mmMinLen = parse<double>(arg); break;
		case ARG_NOISY_HPOLY: noisyHpolymer = true; break;
		case 'x': bt2index = arg; break;
		case ARG_PRESET_VERY_FAST_LOCAL: localAlign = true;
		case ARG_PRESET_VERY_FAST: {
			presetList.push_back("very-fast%LOCAL%"); break;
		}
		case ARG_PRESET_FAST_LOCAL: localAlign = true;
		case ARG_PRESET_FAST: {
			presetList.push_back("fast%LOCAL%"); break;
		}
		case ARG_PRESET_SENSITIVE_LOCAL: localAlign = true;
		case ARG_PRESET_SENSITIVE: {
			presetList.push_back("sensitive%LOCAL%"); break;
		}
		case ARG_PRESET_VERY_SENSITIVE_LOCAL: localAlign = true;
		case ARG_PRESET_VERY_SENSITIVE: {
			presetList.push_back("very-sensitive%LOCAL%"); break;
		}
		case 'P': { presetList.push_back(arg); break; }
		case ARG_ALIGN_POLICY: {
			if(arg.length() > 0) {
				polstr += ";"; polstr += arg;
			}
			break;
		}
		case 'N': {
			long len = parse<double>(arg);
			if (len < 0 || len > 1) {
				System.err.println( "Error: -N argument must be within the interval [0,1]; was " + arg );
			}
			polstr += ";SEED=";
			polstr += arg;
			break;
		}
		case 'L': {
			long len = parse<double>(arg);
			if(len < 1 || len > 32) {
				System.err.println( "Error: -L argument must be within the interval [1,32]; was " + arg);
			}
			polstr += ";SEEDLEN=";
			polstr += arg;
			break;
		}
		case 'O':
			multiseedOff = parse<double>(arg);
			break;
		case 'i': {
			EList<String> args;
			tokenize(arg, ",", args);
			if(args.size() > 3 || args.size() == 0) {
				System.err.println( "Error: expected 3 or fewer comma-separated "
					 + "arguments to -i option, got "
					 + args.size());
			}
			// Interval-settings arguments
			polstr += (";IVAL=" + args[0]); // Function type
			if(args.size() > 1) {
				polstr += ("," + args[1]);  // Constant term
			}
			if(args.size() > 2) {
				polstr += ("," + args[2]);  // Coefficient
			}
			break;
		}
		case ARG_MULTISEED_IVAL: {
			polstr += ";";
			// Split argument by comma
			EList<String> args;
			tokenize(arg, ",", args);
			if(args.size() > 5 || args.size() == 0) {
				System.err.println( "Error: expected 5 or fewer comma-separated "
					 + "arguments to --multiseed option, got "
					 + args.size());
			}
			// Seed mm and length arguments
			polstr += "SEED=";
			polstr += (args[0]); // # mismatches
			if(args.size() >  1) polstr += (";SEEDLEN=" + args[1]); // length
			if(args.size() >  2) polstr += (";IVAL=" + args[2]); // Func type
			if(args.size() >  3) polstr += ("," + args[ 3]); // Constant term
			if(args.size() >  4) polstr += ("," + args[ 4]); // Coefficient
			break;
		}
		case ARG_N_CEIL: {
			// Split argument by comma
			EList<String> args;
			tokenize(arg, ",", args);
			if(args.size() > 3) {
				System.err.println( "Error: expected 3 or fewer comma-separated "
					 + "arguments to --n-ceil option, got "
					 + args.size());
			}
			if(args.size() == 0) {
				System.err.println( "Error: expected at least one argument to --n-ceil option");
			}
			polstr += ";NCEIL=";
			if(args.size() == 3) {
				polstr += (args[0] + "," + args[1] + "," + args[2]);
			} else {
				polstr += ("L," + args[0]);
				if(args.size() > 1) {
					polstr += ("," + (args[1]));
				}
			}
			break;
		}
		case ARG_SCORE_MA:  polstr += ";MA=";    polstr += arg; break;
		case ARG_SCORE_MMP: {
			EList<String> args;
			tokenize(arg, ",", args);
			if(args.size() > 2 || args.size() == 0) {
				System.err.println( "Error: expected 1 or 2 comma-separated "
					 + "arguments to --mmp option, got " + args.size());
			}
			if(args.size() >= 1) {
				polstr += ";MMP=Q,";
				polstr += args[0];
				if(args.size() >= 2) {
					polstr += ",";
					polstr += args[1];
				}
			}
			break;
		}
		case ARG_SCORE_NP:  polstr += ";NP=C";   polstr += arg; break;
		case ARG_SCORE_RDG: polstr += ";RDG=";   polstr += arg; break;
		case ARG_SCORE_RFG: polstr += ";RFG=";   polstr += arg; break;
		case ARG_SCORE_MIN: {
			polstr += ";";
			EList<String> args;
			tokenize(arg, ",", args);
			if(args.size() > 3 || args.size() == 0) {
				System.err.println( "Error: expected 3 or fewer comma-separated "
					 + "arguments to --n-ceil option, got "
					 + args.size() );
			}
			polstr += ("MIN=" + args[0]);
			if(args.size() > 1) {
				polstr += ("," + args[1]);
			}
			if(args.size() > 2) {
				polstr += ("," + args[2]);
			}
			break;
		}
		case ARG_DESC: printArgDesc(cout);
		case 'S': outfile = arg; break;
		case 'U': {
			EList<String> args;
			tokenize(arg, ",", args);
			for(double i = 0; i < args.size(); i++) {
				queries.push_back(args[i]);
			}
			break;
		}
		case ARG_VERSION: showVersion = 1; break;
		default:
			printUsage(cerr);
			throw 1;
	}
	if (!localAlign && scUnMapped) {
		scUnMapped = false;
		System.err.println( "WARNING: --soft-clipped-unmapped-tlen can only be set for "
		     + "local alignment... ignoring");
	}
	}
	
	public static void driver(String type, String bt2indexBase, String outfile) {
		if(gVerbose || startVerbose)  {
			System.err.println("Entered driver(): "); logTime(System.err, true);
		}
		// Vector of the reference sequences; used for sanity-checking
		EList<SString<Character> > names, os;
		EList<Integer> nameLens, seqLens;
		// Read reference sequences from the command-line or from a FASTA file
		if(!origString.empty()) {
			// Read fasta file(s)
			EList<String> origFiles;
			tokenize(origString, ",", origFiles);
			parseFastas(origFiles, names, nameLens, os, seqLens);
		}
		PatternParams pp = new PatternParams(
			format,        // file format
			fileParallel,  // true -> wrap files with separate PairedPatternSources
			seed,          // pseudo-random seed
			readsPerBatch, // # reads in a light parsing batch
			solexaQuals,   // true -> qualities are on solexa64 scale
			phred64Quals,  // true -> qualities are on phred64 scale
			integerQuals,  // true -> qualities are space-separated numbers
			gTrim5,        // amt to hard clip from 5' end
			gTrim3,        // amt to hard clip from 3' end
			fastaContLen,  // length of sampled reads for FastaContinuous...
			fastaContFreq, // frequency of sampled reads for FastaContinuous...
			skipReads,     // skip the first 'skip' patterns
			nthreads,      //number of threads for locking
			outType != OUTPUT_SAM // whether to fix mate names
		);
		if(gVerbose || startVerbose) {
			System.err.println("Creating PatternSource: "); logTime(cerr, true);
		}
		PatternComposer patsrc = PatternComposer.setupPatternComposer(
			queries,     // singles, from argv
			mates1,      // mate1's, from -1 arg
			mates2,      // mate2's, from -2 arg
			mates12,     // both mates on each line, from --12 arg
			qualities,   // qualities associated with singles
			qualities1,  // qualities associated with m1
			qualities2,  // qualities associated with m2
			pp,          // read read-in parameters
			gVerbose || startVerbose); // be talkative
		// Open hit output file
		if(gVerbose || startVerbose) {
			System.err("Opening hit output file: "); logTime(cerr, true);
		}
		OutFileBuf fout;
		if(!outfile.empty()) {
			fout = new OutFileBuf(outfile, false);
		} else {
			fout = new OutFileBuf();
		}
		// Initialize Ebwt object and read in header
		if(gVerbose || startVerbose) {
			System.err.println("About to initialize fw Ebwt: "); logTime(cerr, true);
		}
		adjIdxBase = adjustEbwtBase(argv0, bt2indexBase, gVerbose);
		Ebwt ebwt = new Ebwt(
			adjIdxBase,
		    0,        // index is colorspace
			-1,       // fw index
		    true,     // index is for the forward direction
		    /* overriding: */ offRate,
			0, // amount to add to index offrate or <= 0 to do nothing
		    useMm,    // whether to use memory-mapped files
		    useShmem, // whether to use shared memory
		    mmSweep,  // sweep memory-mapped files
		    !noRefNames, // load names?
			true,        // load SA sample?
			true,        // load ftab?
			true,        // load rstarts?
		    gVerbose, // whether to be talkative
		    startVerbose, // talkative during initialization
		    false /*passMemExc*/,
		    sanityCheck);
		Ebwt ebwtBw = null;
		// We need the mirror index if mismatches are allowed
		if(multiseedMms > 0 || do1mmUpFront) {
			if(gVerbose || startVerbose) {
				System.err.println( "About to initialize rev Ebwt: "); logTime(cerr, true);
			}
			ebwtBw = new Ebwt(
				adjIdxBase + ".rev",
				0,       // index is colorspace
				1,       // TODO: maybe not
			    false, // index is for the reverse direction
			    /* overriding: */ offRate,
				0, // amount to add to index offrate or <= 0 to do nothing
			    useMm,    // whether to use memory-mapped files
			    useShmem, // whether to use shared memory
			    mmSweep,  // sweep memory-mapped files
			    !noRefNames, // load names?
				true,        // load SA sample?
				true,        // load ftab?
				true,        // load rstarts?
			    gVerbose,    // whether to be talkative
			    startVerbose, // talkative during initialization
			    false /*passMemExc*/,
			    sanityCheck);
		}
		if(sanityCheck && !os.empty()) {
			// Sanity check number of patterns and pattern lengths in Ebwt
			// against original strings
			for(int i = 0; i < os.size(); i++) {
				assert_eq(os[i].length(), ebwt.plen()[i]);
			}
		}
		// Sanity-check the restored version of the Ebwt
		if(sanityCheck && !os.empty()) {
			ebwt.loadIntoMemory(
				0,
				-1, // fw index
				true, // load SA sample
				true, // load ftab
				true, // load rstarts
				!noRefNames,
				startVerbose);
			ebwt.checkOrigs(os, false, false);
			ebwt.evictFromMemory();
		}
		OutputQueue oq = new OutputQueue(
			fout,                           // out file buffer
			reorder && (nthreads > 1 || thread_stealing), // whether to reorder
			nthreads,                        // # threads
			nthreads > 1 || thread_stealing, // whether to be thread-safe
			readsPerBatch,                   // size of output buffer of reads 
			skipReads);                      // first read will have this rdid
		{
			Timer _t(cerr, "Time searching: ", timing);
			// Set up penalities
			if(bonusMatch > 0 && !localAlign) {
				System.err.println( "Warning: Match bonus always = 0 in --end-to-end mode; ignoring user setting" );
				bonusMatch = 0;
			}
			Scoring sc = new Scoring(
				bonusMatch,     // constant reward for match
				penMmcType,     // how to penalize mismatches
				penMmcMax,      // max mm pelanty
				penMmcMin,      // min mm pelanty
				scoreMin,       // min score as function of read len
				nCeil,          // max # Ns as function of read len
				penNType,       // how to penalize Ns in the read
				penN,           // constant if N pelanty is a constant
				penNCatPair,    // whether to concat mates before N filtering
				penRdGapConst,  // constant coeff for read gap cost
				penRfGapConst,  // constant coeff for ref gap cost
				penRdGapLinear, // linear coeff for read gap cost
				penRfGapLinear, // linear coeff for ref gap cost
				gGapBarrier);   // # rows at top/bot only entered diagonally
			EList<Integer> reflens;
			for(int i = 0; i < ebwt.nPat(); i++) {
				reflens.push_back(ebwt.plen()[i]);
			}
			EList<String> refnames;
			readEbwtRefnames(adjIdxBase, refnames);
			SamConfig samc = new SamConfig(
				refnames,               // reference sequence names
				reflens,                // reference sequence lengths
				samTruncQname,          // whether to truncate QNAME to 255 chars
				samOmitSecSeqQual,      // omit SEQ/QUAL for 2ndary alignments?
				samNoUnal,              // omit unaligned-read records?
				"bowtie2",      // program id
				"bowtie2",      // program name
				BOWTIE2_VERSION, // program version
				argstr,                 // command-line
				rgs_optflag,            // read-group string
				sam_print_as,
				sam_print_xs,
				sam_print_xss,
				sam_print_yn,
				sam_print_xn,
				sam_print_x0,
				sam_print_x1,
				sam_print_xm,
				sam_print_xo,
				sam_print_xg,
				sam_print_nm,
				sam_print_md,
				sam_print_yf,
				sam_print_yi,
				sam_print_ym,
				sam_print_yp,
				sam_print_yt,
				sam_print_ys,
				sam_print_zs,
				sam_print_xr,
				sam_print_xt,
				sam_print_xd,
				sam_print_xu,
				sam_print_yl,
				sam_print_ye,
				sam_print_yu,
				sam_print_xp,
				sam_print_yr,
				sam_print_zb,
				sam_print_zr,
				sam_print_zf,
				sam_print_zm,
				sam_print_zi,
				sam_print_zp,
				sam_print_zu,
				sam_print_zt);
			// Set up hit sink; if sanityCheck && !os.empty() is true,
			// then instruct the sink to "retain" hits in a vector in
			// memory so that we can easily sanity check them later on
			AlnSink mssink = null;
			switch(outType) {
				case OUTPUT_SAM: {
					mssink = new AlnSinkSam(
						oq,           // output queue
						samc,         // settings & routines for SAM output
						refnames,     // reference names
						gQuiet);      // don't print alignment summary at end
					if(!samNoHead) {
						boolean printHd = true, printSq = true;
						BTString buf;
						samc.printHeader(buf, rgid, rgs, printHd, !samNoSQ, printSq);
						fout.writeString(buf);
					}
					break;
				}
				default:
					System.err.println( "Invalid output type: " + outType);
			}
			if(gVerbose || startVerbose) {
				System.err.println( "Dispatching to search driver: "); logTime(cerr, true);
			}
			// Set up global constraint
			OutFileBuf metricsOfb = null;
			if(!metricsFile.empty() && metricsIval > 0) {
				metricsOfb = new OutFileBuf(metricsFile);
			}
			// Do the search for all input reads
			multiseedSearch(
				sc,      // scoring scheme
				pp,      // pattern params
				patsrc, // pattern source
				mssink, // hit sink
				ebwt,    // BWT
				ebwtBw, // BWT'
				metricsOfb);
			// Evict any loaded indexes from memory
			if(ebwt.isInMemory()) {
				ebwt.evictFromMemory();
			}
			if(!gQuiet && !seedSumm) {
				int repThresh = mhits;
				if(repThresh == 0) {
					repThresh = Integer.MAX_VALUE;
				}
				mssink.finish(
					repThresh,
					gReportDiscordant,
					gReportMixed,
					hadoopOut);
			}
			oq.flush(true);
		}
	}
	
	public static void printMmsSkipMsg(PatternSourcePerThread ps, boolean paired, boolean mate1, int seedmms) {
		OutputStream os;
		if(paired) {
			os.write(("Warning: skipping mate #" + (mate1 ? '1' : '2')
			   + " of read '" + (mate1 ? ps.read_a().name : ps.read_b().name)
			   + "' because length (" + (mate1 ? ps.read_a().patFw.length() : ps.read_b().patFw.length())
			   + ") <= # seed mismatches (" + seedmms + ")" + "\n").getBytes());
		} else {
			os.write(("Warning: skipping read '" + (mate1 ? ps.read_a().name : ps.read_b().name)
			   + "' because length (" + (mate1 ? ps.read_a().patFw.length() : ps.read_b().patFw.length())
			   + ") <= # seed mismatches (" + seedmms + ")" + "\n").getBytes());
		}
		System.err.println(os);
	}
	public static void printLenSkipMsg(PatternSourcePerThread ps, boolean paired, boolean mate1, int seedmms) {
		OutputStream os;
		if(paired) {
			os.write(("Warning: skipping mate #" + (mate1 ? '1' : '2')
			   + " of read '" + (mate1 ? ps.read_a().name : ps.read_b().name)
			   + "' because it was < 2 characters long" + "\n").getBytes());
		} else {
			os.write(("Warning: skipping read '" + (mate1 ? ps.read_a().name : ps.read_b().name)
			   + "' because it was < 2 characters long" + "\n").getBytes());
		}
		System.err.println(os);
	}
	public static void printLocalScorepMsg(PatternSourcePerThread ps, boolean paired, boolean mate1, int seedmms) {
		OutputStream os;
		if(paired) {
			os.write(("Warning: minimum score function gave negative number in "
			   + "--local mode for mate #" + (mate1 ? '1' : '2')
			   + "of read '" + (mate1 ? ps.read_a().name : ps.read_b().name)
			   + "; setting to 0 instead" + "\n").getBytes());
		} else {
			os.write(("Warning: minimum score function gave negative number in "
			   + "--local mode for read '" + (mate1 ? ps.read_a().name : ps.read_b().name)
			   + "; setting to 0 instead" + "\n").getBytes());
		}
		System.err.println(os);
	}
	public static void printEEScorepMsg(PatternSourcePerThread ps, boolean paired, boolean mate1, int seedmms) {
		OutputStream os;
		if(paired) {
			os.write(("Warning: minimum score function gave positive number in "
			   + "--end-to-end mode for mate #" + (mate1 ? '1' : '2')
			   + "of read '" + (mate1 ? ps.read_a().name : ps.read_b().name)
			   + "; setting to 0 instead" + "\n").getBytes());
		} else {
			os.write(("Warning: minimum score function gave positive number in "
			   + "--end-to-end mode for read '" + (mate1 ? ps.read_a().name : ps.read_b().name)
			   + "; setting to 0 instead" + "\n").getBytes());
		}
		System.err.println(os);
	}
	
	public static void setupMinScores(
			PatternSourcePerThread ps,
			boolean paired,
			boolean localAlign,
			Scoring sc,
			int rdlens,
			long minsc,
			long maxpen
			) {
		if(bwaSwLike) {
			// From BWA-SW manual: "Given an l-long query, the
			// threshold for a hit to be retained is
			// a*max{T,c*log(l)}."  We try to recreate that here.
			float a = (float)sc.match(30);
			float T = bwaSwLikeT, c = bwaSwLikeC;
			minsc[0] = (long)Float.max(a*T, a*c*Math.log(rdlens[0]));
			if(paired) {
				minsc[1] = (long)Float.max(a*T, a*c*Math.log(rdlens[1]));
			}
		} else {
			minsc[0] = scoreMin.f<Long>(rdlens[0]);
			if(paired) minsc[1] = scoreMin.f<Long>(rdlens[1]);
			if(localAlign) {
				if(minsc[0] < 0) {
					if(!gQuiet) printLocalScoreMsg(ps, paired, true);
					minsc[0] = 0;
				}
				if(paired && minsc[1] < 0) {
					if(!gQuiet) printLocalScoreMsg(ps, paired, false);
					minsc[1] = 0;
				}
			} else {
				if(minsc[0] > 0) {
					if(!gQuiet) printEEScoreMsg(ps, paired, true);
					minsc[0] = 0;
				}
				if(paired && minsc[1] > 0) {
					if(!gQuiet) printEEScoreMsg(ps, paired, false);
					minsc[1] = 0;
				}
			}
		}
		// Given minsc, calculate maxpen
		if(localAlign) {
			long perfect0 = sc.perfectScore(rdlens[0]);
			maxpen[0] = perfect0 - minsc[0];
			if(paired) {
				long perfect1 = sc.perfectScore(rdlens[1]);
				maxpen[1] = perfect1 - minsc[1];
			} else {
				maxpen[1] = Long.MIN_VALUE;
			}
		} else {
			maxpen[0] = -minsc[0];
			if(paired) {
				maxpen[1] = -minsc[1];
			} else {
				maxpen[1] = Long.MIN_VALUE;
			}
		}
	}
	
	public static void multiseedSearchWorker() {
		int tid = *((int*)vp);
		#endif
			assert(multiseed_ebwtFw != NULL);
			assert(multiseedMms == 0 || multiseed_ebwtBw != NULL);
			PatternComposer&        patsrc   = *multiseed_patsrc;
			PatternParams           pp       = multiseed_pp;
			const Ebwt&             ebwtFw   = *multiseed_ebwtFw;
			const Ebwt&             ebwtBw   = *multiseed_ebwtBw;
			const Scoring&          sc       = *multiseed_sc;
			const BitPairReference& ref      = *multiseed_refs;
			AlnSink&                msink    = *multiseed_msink;
			OutFileBuf*             metricsOfb = multiseed_metricsOfb;

			{
		#ifdef PER_THREAD_TIMING
				uint64_t ncpu_changeovers = 0;
				uint64_t nnuma_changeovers = 0;
				
				int current_cpu = 0, current_node = 0;
				get_cpu_and_node(current_cpu, current_node);
				
				std::stringstream ss;
				std::string msg;
				ss << "thread: " << tid << " time: ";
				msg = ss.str();
				Timer timer(std::cout, msg.c_str());
		#endif

				// Sinks: these are so that we can print tables encoding counts for
				// events of interest on a per-read, per-seed, per-join, or per-SW
				// level.  These in turn can be used to diagnose performance
				// problems, or generally characterize performance.
				
				//const BitPairReference& refs   = *multiseed_refs;
				auto_ptr<PatternSourcePerThreadFactory> patsrcFact(createPatsrcFactory(patsrc, pp, tid));
				auto_ptr<PatternSourcePerThread> ps(patsrcFact->create());
				
				// Thread-local cache for seed alignments
				PtrWrap<AlignmentCache> scLocal;
				if(!msNoCache) {
					scLocal.init(new AlignmentCache(seedCacheLocalMB * 1024 * 1024, false));
				}
				AlignmentCache scCurrent(seedCacheCurrentMB * 1024 * 1024, false);
				// Thread-local cache for current seed alignments
				
				// Interfaces for alignment and seed caches
				AlignmentCacheIface ca(
					&scCurrent,
					scLocal.get(),
					msNoCache ? NULL : multiseed_ca);
				
				// Instantiate an object for holding reporting-related parameters.
				ReportingParams rp(
					(allHits ? std::numeric_limits<THitInt>::max() : khits), // -k
					mhits,             // -m/-M
					0,                 // penalty gap (not used now)
					msample,           // true -> -M was specified, otherwise assume -m
					gReportDiscordant, // report discordang paired-end alignments?
					gReportMixed);     // report unpaired alignments for paired reads?

				// Instantiate a mapping quality calculator
				auto_ptr<Mapq> bmapq(new_mapq(mapqv, scoreMin, sc));
				
				// Make a per-thread wrapper for the global MHitSink object.
				AlnSinkWrap msinkwrap(
					msink,         // global sink
					rp,            // reporting parameters
					*bmapq,        // MAPQ calculator
					(size_t)tid);  // thread id
				
				// Write dynamic-programming problem descriptions here
				ofstream *dpLog = NULL, *dpLogOpp = NULL;
				if(!logDps.empty()) {
					dpLog = new ofstream(logDps.c_str(), ofstream::out);
					dpLog->sync_with_stdio(false);
				}
				if(!logDpsOpp.empty()) {
					dpLogOpp = new ofstream(logDpsOpp.c_str(), ofstream::out);
					dpLogOpp->sync_with_stdio(false);
				}
				
				SeedAligner al;
				SwDriver sd(exactCacheCurrentMB * 1024 * 1024);
				SwAligner sw(dpLog), osw(dpLogOpp);
				SeedResults shs[2];
				OuterLoopMetrics olm;
				SeedSearchMetrics sdm;
				WalkMetrics wlm;
				SwMetrics swmSeed, swmMate;
				ReportingMetrics rpm;
				RandomSource rnd, rndArb;
				SSEMetrics sseU8ExtendMet;
				SSEMetrics sseU8MateMet;
				SSEMetrics sseI16ExtendMet;
				SSEMetrics sseI16MateMet;
				uint64_t nbtfiltst = 0; // TODO: find a new home for these
				uint64_t nbtfiltsc = 0; // TODO: find a new home for these
				uint64_t nbtfiltdo = 0; // TODO: find a new home for these

				ASSERT_ONLY(BTDnaString tmp);

				int pepolFlag;
				if(gMate1fw && gMate2fw) {
					pepolFlag = PE_POLICY_FF;
				} else if(gMate1fw && !gMate2fw) {
					pepolFlag = PE_POLICY_FR;
				} else if(!gMate1fw && gMate2fw) {
					pepolFlag = PE_POLICY_RF;
				} else {
					pepolFlag = PE_POLICY_RR;
				}
				assert_geq(gMaxInsert, gMinInsert);
				assert_geq(gMinInsert, 0);
				PairedEndPolicy pepol(
					pepolFlag,
					gMaxInsert,
					gMinInsert,
					localAlign,
					gFlippedMatesOK,
					gDovetailMatesOK,
					gContainMatesOK,
					gOlapMatesOK,
					gExpandToFrag);
				
				PerfMetrics metricsPt; // per-thread metrics object; for read-level metrics
				BTString nametmp;
				EList<Seed> seeds1, seeds2;
				EList<Seed> *seeds[2] = { &seeds1, &seeds2 };
				
				PerReadMetrics prm;

				// Used by thread with threadid == 1 to measure time elapsed
				time_t iTime = time(0);

				// Keep track of whether last search was exhaustive for mates 1 and 2
				bool exhaustive[2] = { false, false };
				// Keep track of whether mates 1/2 were filtered out last time through
				bool filt[2]    = { true, true };
				// Keep track of whether mates 1/2 were filtered out due Ns last time
				bool nfilt[2]   = { true, true };
				// Keep track of whether mates 1/2 were filtered out due to not having
				// enough characters to rise about the score threshold.
				bool scfilt[2]  = { true, true };
				// Keep track of whether mates 1/2 were filtered out due to not having
				// more characters than the number of mismatches permitted in a seed.
				bool lenfilt[2] = { true, true };
				// Keep track of whether mates 1/2 were filtered out by upstream qc
				bool qcfilt[2]  = { true, true };

				rndArb.init((uint32_t)time(0));
				int mergei = 0;
				int mergeival = 16;
				bool done = false;
				while(!done) {
					pair<bool, bool> ret = ps->nextReadPair();
					bool success = ret.first;
					done = ret.second;
					if(!success && done) {
						break;
					} else if(!success) {
						continue;
					}
					TReadId rdid = ps->read_a().rdid;
					bool sample = true;
					if(arbitraryRandom) {
						ps->read_a().seed = rndArb.nextU32();
						ps->read_b().seed = rndArb.nextU32();
					}
					if(sampleFrac < 1.0f) {
						rnd.init(ROTL(ps->read_a().seed, 2));
						sample = rnd.nextFloat() < sampleFrac;
					}
					if(rdid >= skipReads && rdid < qUpto && sample) {
						// Align this read/pair
						bool retry = true;
						//
						// Check if there is metrics reporting for us to do.
						//
						if(metricsIval > 0 &&
						   (metricsOfb != NULL || metricsStderr) &&
						   !metricsPerRead &&
						   ++mergei == mergeival)
						{
							// Do a periodic merge.  Update global metrics, in a
							// synchronized manner if needed.
							MERGE_METRICS(metrics);
							mergei = 0;
							// Check if a progress message should be printed
							if(tid == 0) {
								// Only thread 1 prints progress messages
								time_t curTime = time(0);
								if(curTime - iTime >= metricsIval) {
									metrics.reportInterval(metricsOfb, metricsStderr, false, NULL);
									iTime = curTime;
								}
							}
						}
						prm.reset(); // per-read metrics
						prm.doFmString = false;
						if(sam_print_xt) {
							gettimeofday(&prm.tv_beg, &prm.tz_beg);
						}
		#ifdef PER_THREAD_TIMING
						int cpu = 0, node = 0;
						get_cpu_and_node(cpu, node);
						if(cpu != current_cpu) {
							ncpu_changeovers++;
							current_cpu = cpu;
						}
						if(node != current_node) {
							nnuma_changeovers++;
							current_node = node;
						}
		#endif
						// Try to align this read
						while(retry) {
							retry = false;
							ca.nextRead(); // clear the cache
							olm.reads++;
							assert(!ca.aligning());
							bool paired = !ps->read_b().empty();
							const size_t rdlen1 = ps->read_a().length();
							const size_t rdlen2 = paired ? ps->read_b().length() : 0;
							olm.bases += (rdlen1 + rdlen2);
							msinkwrap.nextRead(
								&ps->read_a(),
								paired ? &ps->read_b() : NULL,
								rdid,
								sc.qualitiesMatter());
							assert(msinkwrap.inited());
							size_t rdlens[2] = { rdlen1, rdlen2 };
							size_t rdrows[2] = { rdlen1, rdlen2 };
							// Calculate the minimum valid score threshold for the read
							TAlScore minsc[2];
							minsc[0] = minsc[1] = std::numeric_limits<TAlScore>::max();
							if(bwaSwLike) {
								// From BWA-SW manual: "Given an l-long query, the
								// threshold for a hit to be retained is
								// a*max{T,c*log(l)}."  We try to recreate that here.
								float a = (float)sc.match(30);
								float T = bwaSwLikeT, c = bwaSwLikeC;
								minsc[0] = (TAlScore)max<float>(a*T, a*c*log(rdlens[0]));
								if(paired) {
									minsc[1] = (TAlScore)max<float>(a*T, a*c*log(rdlens[1]));
								}
							} else {
								minsc[0] = scoreMin.f<TAlScore>(rdlens[0]);
								if(paired) minsc[1] = scoreMin.f<TAlScore>(rdlens[1]);
								if(localAlign) {
									if(minsc[0] < 0) {
										if(!gQuiet) printLocalScoreMsg(*ps, paired, true);
										minsc[0] = 0;
									}
									if(paired && minsc[1] < 0) {
										if(!gQuiet) printLocalScoreMsg(*ps, paired, false);
										minsc[1] = 0;
									}
								} else {
									if(minsc[0] > 0) {
										if(!gQuiet) printEEScoreMsg(*ps, paired, true);
										minsc[0] = 0;
									}
									if(paired && minsc[1] > 0) {
										if(!gQuiet) printEEScoreMsg(*ps, paired, false);
										minsc[1] = 0;
									}
								}
							}
							// N filter; does the read have too many Ns?
							size_t readns[2] = {0, 0};
							sc.nFilterPair(
								&ps->read_a().patFw,
								paired ? &ps->read_b().patFw : NULL,
								readns[0],
								readns[1],
								nfilt[0],
								nfilt[1]);
							// Score filter; does the read enough character to rise above
							// the score threshold?
							scfilt[0] = sc.scoreFilter(minsc[0], rdlens[0]);
							scfilt[1] = sc.scoreFilter(minsc[1], rdlens[1]);
							lenfilt[0] = lenfilt[1] = true;
							if(rdlens[0] <= (size_t)multiseedMms || rdlens[0] < 2) {
								if(!gQuiet) printMmsSkipMsg(*ps, paired, true, multiseedMms);
								lenfilt[0] = false;
							}
							if((rdlens[1] <= (size_t)multiseedMms || rdlens[1] < 2) && paired) {
								if(!gQuiet) printMmsSkipMsg(*ps, paired, false, multiseedMms);
								lenfilt[1] = false;
							}
							if(rdlens[0] < 2) {
								if(!gQuiet) printLenSkipMsg(*ps, paired, true);
								lenfilt[0] = false;
							}
							if(rdlens[1] < 2 && paired) {
								if(!gQuiet) printLenSkipMsg(*ps, paired, false);
								lenfilt[1] = false;
							}
							qcfilt[0] = qcfilt[1] = true;
							if(qcFilter) {
								qcfilt[0] = (ps->read_a().filter != '0');
								qcfilt[1] = (ps->read_b().filter != '0');
							}
							filt[0] = (nfilt[0] && scfilt[0] && lenfilt[0] && qcfilt[0]);
							filt[1] = (nfilt[1] && scfilt[1] && lenfilt[1] && qcfilt[1]);
							prm.nFilt += (filt[0] ? 0 : 1) + (filt[1] ? 0 : 1);
							Read* rds[2] = { &ps->read_a(), &ps->read_b() };
							// For each mate...
							assert(msinkwrap.empty());
							sd.nextRead(paired, rdrows[0], rdrows[1]); // SwDriver
							size_t minedfw[2] = { 0, 0 };
							size_t minedrc[2] = { 0, 0 };
							// Calcualte nofw / no rc
							bool nofw[2] = { false, false };
							bool norc[2] = { false, false };
							nofw[0] = paired ? (gMate1fw ? gNofw : gNorc) : gNofw;
							norc[0] = paired ? (gMate1fw ? gNorc : gNofw) : gNorc;
							nofw[1] = paired ? (gMate2fw ? gNofw : gNorc) : gNofw;
							norc[1] = paired ? (gMate2fw ? gNorc : gNofw) : gNorc;
							// Calculate nceil
							int nceil[2] = { 0, 0 };
							nceil[0] = nCeil.f<int>((double)rdlens[0]);
							nceil[0] = min(nceil[0], (int)rdlens[0]);
							if(paired) {
								nceil[1] = nCeil.f<int>((double)rdlens[1]);
								nceil[1] = min(nceil[1], (int)rdlens[1]);
							}
							exhaustive[0] = exhaustive[1] = false;
							size_t matemap[2] = { 0, 1 };
							bool pairPostFilt = filt[0] && filt[1];
							if(pairPostFilt) {
								rnd.init(ps->read_a().seed ^ ps->read_b().seed);
							} else {
								rnd.init(ps->read_a().seed);
							}
							// Calculate interval length for both mates
							int interval[2] = { 0, 0 };
							for(size_t mate = 0; mate < (paired ? 2:1); mate++) {
								interval[mate] = msIval.f<int>((double)rdlens[mate]);
								if(filt[0] && filt[1]) {
									// Boost interval length by 20% for paired-end reads
									interval[mate] = (int)(interval[mate] * 1.2 + 0.5);
								}
								interval[mate] = max(interval[mate], 1);
							}
							// Calculate streak length
							size_t streak[2]    = { maxDpStreak,   maxDpStreak };
							size_t mtStreak[2]  = { maxMateStreak, maxMateStreak };
							size_t mxDp[2]      = { maxDp,         maxDp       };
							size_t mxUg[2]      = { maxUg,         maxUg       };
							size_t mxIter[2]    = { maxIters,      maxIters    };
							if(allHits) {
								streak[0]   = streak[1]   = std::numeric_limits<size_t>::max();
								mtStreak[0] = mtStreak[1] = std::numeric_limits<size_t>::max();
								mxDp[0]     = mxDp[1]     = std::numeric_limits<size_t>::max();
								mxUg[0]     = mxUg[1]     = std::numeric_limits<size_t>::max();
								mxIter[0]   = mxIter[1]   = std::numeric_limits<size_t>::max();
							} else if(khits > 1) {
								for(size_t mate = 0; mate < 2; mate++) {
									streak[mate]   += (khits-1) * maxStreakIncr;
									mtStreak[mate] += (khits-1) * maxStreakIncr;
									mxDp[mate]     += (khits-1) * maxItersIncr;
									mxUg[mate]     += (khits-1) * maxItersIncr;
									mxIter[mate]   += (khits-1) * maxItersIncr;
								}
							}
							if(filt[0] && filt[1]) {
								streak[0] = (size_t)ceil((double)streak[0] / 2.0);
								streak[1] = (size_t)ceil((double)streak[1] / 2.0);
								assert_gt(streak[1], 0);
							}
							assert_gt(streak[0], 0);
							// Calculate # seed rounds for each mate
							size_t nrounds[2] = { nSeedRounds, nSeedRounds };
							if(filt[0] && filt[1]) {
								nrounds[0] = (size_t)ceil((double)nrounds[0] / 2.0);
								nrounds[1] = (size_t)ceil((double)nrounds[1] / 2.0);
								assert_gt(nrounds[1], 0);
							}
							assert_gt(nrounds[0], 0);
							// Increment counters according to what got filtered
							for(size_t mate = 0; mate < (paired ? 2:1); mate++) {
								if(!filt[mate]) {
									// Mate was rejected by N filter
									olm.freads++;               // reads filtered out
									olm.fbases += rdlens[mate]; // bases filtered out
								} else {
									shs[mate].clear();
									shs[mate].nextRead(mate == 0 ? ps->read_a() : ps->read_b());
									assert(shs[mate].empty());
									olm.ureads++;               // reads passing filter
									olm.ubases += rdlens[mate]; // bases passing filter
								}
							}
							size_t eePeEeltLimit = std::numeric_limits<size_t>::max();
							// Whether we're done with mate1 / mate2
							bool done[2] = { !filt[0], !filt[1] };
							size_t nelt[2] = {0, 0};
												
								// Find end-to-end exact alignments for each read
								if(doExactUpFront) {
									for(size_t matei = 0; matei < (paired ? 2:1); matei++) {
										size_t mate = matemap[matei];
										if(!filt[mate] || done[mate] || msinkwrap.state().doneWithMate(mate == 0)) {
											continue;
										}
										swmSeed.exatts++;
										nelt[mate] = al.exactSweep(
											ebwtFw,        // index
											*rds[mate],    // read
											sc,            // scoring scheme
											nofw[mate],    // nofw?
											norc[mate],    // norc?
											2,             // max # edits we care about
											minedfw[mate], // minimum # edits for fw mate
											minedrc[mate], // minimum # edits for rc mate
											true,          // report 0mm hits
											shs[mate],     // put end-to-end results here
											sdm);          // metrics
										size_t bestmin = min(minedfw[mate], minedrc[mate]);
										if(bestmin == 0) {
											sdm.bestmin0++;
										} else if(bestmin == 1) {
											sdm.bestmin1++;
										} else {
											assert_eq(2, bestmin);
											sdm.bestmin2++;
										}
									}
									matemap[0] = 0; matemap[1] = 1;
									if(nelt[0] > 0 && nelt[1] > 0 && nelt[0] > nelt[1]) {
										// Do the mate with fewer exact hits first
										// TODO: Consider mates & orientations separately?
										matemap[0] = 1; matemap[1] = 0;
									}
									for(size_t matei = 0; matei < (seedSumm ? 0:2); matei++) {
										size_t mate = matemap[matei];
										if(nelt[mate] == 0 || nelt[mate] > eePeEeltLimit) {
											shs[mate].clearExactE2eHits();
											continue;
										}
										if(msinkwrap.state().doneWithMate(mate == 0)) {
											shs[mate].clearExactE2eHits();
											done[mate] = true;
											continue;
										}
										assert(filt[mate]);
										assert(matei == 0 || paired);
										assert(!msinkwrap.maxed());
										assert(msinkwrap.repOk());
										int ret = 0;
										if(paired) {
											// Paired-end dynamic programming driver
											ret = sd.extendSeedsPaired(
												*rds[mate],     // mate to align as anchor
												*rds[mate ^ 1], // mate to align as opp.
												mate == 0,      // anchor is mate 1?
												!filt[mate ^ 1],// opposite mate filtered out?
												shs[mate],      // seed hits for anchor
												ebwtFw,         // bowtie index
												&ebwtBw,        // rev bowtie index
												ref,            // packed reference strings
												sw,             // dyn prog aligner, anchor
												osw,            // dyn prog aligner, opposite
												sc,             // scoring scheme
												pepol,          // paired-end policy
												-1,             // # mms allowed in a seed
												0,              // length of a seed
												0,              // interval between seeds
												minsc[mate],    // min score for anchor
												minsc[mate^1],  // min score for opp.
												nceil[mate],    // N ceil for anchor
												nceil[mate^1],  // N ceil for opp.
												nofw[mate],     // don't align forward read
												norc[mate],     // don't align revcomp read
												maxhalf,        // max width on one DP side
												doUngapped,     // do ungapped alignment
												mxIter[mate],   // max extend loop iters
												mxUg[mate],     // max # ungapped extends
												mxDp[mate],     // max # DPs
												streak[mate],   // stop after streak of this many end-to-end fails
												streak[mate],   // stop after streak of this many ungap fails
												streak[mate],   // stop after streak of this many dp fails
												mtStreak[mate], // max mate fails per seed range
												doExtend,       // extend seed hits
												enable8,        // use 8-bit SSE where possible
												cminlen,        // checkpoint if read is longer
												cpow2,          // checkpointer interval, log2
												doTri,          // triangular mini-fills?
												tighten,        // -M score tightening mode
												ca,             // seed alignment cache
												rnd,            // pseudo-random source
												wlm,            // group walk left metrics
												swmSeed,        // DP metrics, seed extend
												swmMate,        // DP metrics, mate finding
												prm,            // per-read metrics
												&msinkwrap,     // for organizing hits
												true,           // seek mate immediately
												true,           // report hits once found
												gReportDiscordant,// look for discordant alns?
												gReportMixed,   // look for unpaired alns?
												exhaustive[mate]);
											// Might be done, but just with this mate
										} else {
											// Unpaired dynamic programming driver
											ret = sd.extendSeeds(
												*rds[mate],     // read
												mate == 0,      // mate #1?
												shs[mate],      // seed hits
												ebwtFw,         // bowtie index
												&ebwtBw,        // rev bowtie index
												ref,            // packed reference strings
												sw,             // dynamic prog aligner
												sc,             // scoring scheme
												-1,             // # mms allowed in a seed
												0,              // length of a seed
												0,              // interval between seeds
												minsc[mate],    // minimum score for valid
												nceil[mate],    // N ceil for anchor
												maxhalf,        // max width on one DP side
												doUngapped,     // do ungapped alignment
												mxIter[mate],   // max extend loop iters
												mxUg[mate],     // max # ungapped extends
												mxDp[mate],     // max # DPs
												streak[mate],   // stop after streak of this many end-to-end fails
												streak[mate],   // stop after streak of this many ungap fails
												doExtend,       // extend seed hits
												enable8,        // use 8-bit SSE where possible
												cminlen,        // checkpoint if read is longer
												cpow2,          // checkpointer interval, log2
												doTri,          // triangular mini-fills
												tighten,        // -M score tightening mode
												ca,             // seed alignment cache
												rnd,            // pseudo-random source
												wlm,            // group walk left metrics
												swmSeed,        // DP metrics, seed extend
												prm,            // per-read metrics
												&msinkwrap,     // for organizing hits
												true,           // report hits once found
												exhaustive[mate]);
										}
										assert_gt(ret, 0);
										MERGE_SW(sw);
										MERGE_SW(osw);
										// Clear out the exact hits so that we don't try to
										// extend them again later!
										shs[mate].clearExactE2eHits();
										if(ret == EXTEND_EXHAUSTED_CANDIDATES) {
											// Not done yet
										} else if(ret == EXTEND_POLICY_FULFILLED) {
											// Policy is satisfied for this mate at least
											if(msinkwrap.state().doneWithMate(mate == 0)) {
												done[mate] = true;
											}
											if(msinkwrap.state().doneWithMate(mate == 1)) {
												done[mate^1] = true;
											}
										} else if(ret == EXTEND_PERFECT_SCORE) {
											// We exhausted this mode at least
											done[mate] = true;
										} else if(ret == EXTEND_EXCEEDED_HARD_LIMIT) {
											// We exceeded a per-read limit
											done[mate] = true;
										} else if(ret == EXTEND_EXCEEDED_SOFT_LIMIT) {
											// Not done yet
										} else {
											//
											cerr << "Bad return value: " << ret << endl;
											throw 1;
										}
										if(!done[mate]) {
											TAlScore perfectScore = sc.perfectScore(rdlens[mate]);
											if(!done[mate] && minsc[mate] == perfectScore) {
												done[mate] = true;
											}
										}
									}
								}

								// 1-mismatch
								if(do1mmUpFront && !seedSumm) {
									for(size_t matei = 0; matei < (paired ? 2:1); matei++) {
										size_t mate = matemap[matei];
										if(!filt[mate] || done[mate] || nelt[mate] > eePeEeltLimit) {
											// Done with this mate
											shs[mate].clear1mmE2eHits();
											nelt[mate] = 0;
											continue;
										}
										nelt[mate] = 0;
										assert(!msinkwrap.maxed());
										assert(msinkwrap.repOk());
										//rnd.init(ROTL(rds[mate]->seed, 10));
										assert(shs[mate].empty());
										assert(shs[mate].repOk(&ca.current()));
										bool yfw = minedfw[mate] <= 1 && !nofw[mate];
										bool yrc = minedrc[mate] <= 1 && !norc[mate];
										if(yfw || yrc) {
											// Clear out the exact hits
											swmSeed.mm1atts++;
											al.oneMmSearch(
												&ebwtFw,        // BWT index
												&ebwtBw,        // BWT' index
												*rds[mate],     // read
												sc,             // scoring scheme
												minsc[mate],    // minimum score
												!yfw,           // don't align forward read
												!yrc,           // don't align revcomp read
												localAlign,     // must be legal local alns?
												false,          // do exact match
												true,           // do 1mm
												shs[mate],      // seed hits (hits installed here)
												sdm);           // metrics
											nelt[mate] = shs[mate].num1mmE2eHits();
										}
									}
									// Possibly reorder the mates
									matemap[0] = 0; matemap[1] = 1;
									if(nelt[0] > 0 && nelt[1] > 0 && nelt[0] > nelt[1]) {
										// Do the mate with fewer exact hits first
										// TODO: Consider mates & orientations separately?
										matemap[0] = 1; matemap[1] = 0;
									}
									for(size_t matei = 0; matei < (seedSumm ? 0:2); matei++) {
										size_t mate = matemap[matei];
										if(nelt[mate] == 0 || nelt[mate] > eePeEeltLimit) {
											continue;
										}
										if(msinkwrap.state().doneWithMate(mate == 0)) {
											done[mate] = true;
											continue;
										}
										int ret = 0;
										if(paired) {
											// Paired-end dynamic programming driver
											ret = sd.extendSeedsPaired(
												*rds[mate],     // mate to align as anchor
												*rds[mate ^ 1], // mate to align as opp.
												mate == 0,      // anchor is mate 1?
												!filt[mate ^ 1],// opposite mate filtered out?
												shs[mate],      // seed hits for anchor
												ebwtFw,         // bowtie index
												&ebwtBw,        // rev bowtie index
												ref,            // packed reference strings
												sw,             // dyn prog aligner, anchor
												osw,            // dyn prog aligner, opposite
												sc,             // scoring scheme
												pepol,          // paired-end policy
												-1,             // # mms allowed in a seed
												0,              // length of a seed
												0,              // interval between seeds
												minsc[mate],    // min score for anchor
												minsc[mate^1],  // min score for opp.
												nceil[mate],    // N ceil for anchor
												nceil[mate^1],  // N ceil for opp.
												nofw[mate],     // don't align forward read
												norc[mate],     // don't align revcomp read
												maxhalf,        // max width on one DP side
												doUngapped,     // do ungapped alignment
												mxIter[mate],   // max extend loop iters
												mxUg[mate],     // max # ungapped extends
												mxDp[mate],     // max # DPs
												streak[mate],   // stop after streak of this many end-to-end fails
												streak[mate],   // stop after streak of this many ungap fails
												streak[mate],   // stop after streak of this many dp fails
												mtStreak[mate], // max mate fails per seed range
												doExtend,       // extend seed hits
												enable8,        // use 8-bit SSE where possible
												cminlen,        // checkpoint if read is longer
												cpow2,          // checkpointer interval, log2
												doTri,          // triangular mini-fills?
												tighten,        // -M score tightening mode
												ca,             // seed alignment cache
												rnd,            // pseudo-random source
												wlm,            // group walk left metrics
												swmSeed,        // DP metrics, seed extend
												swmMate,        // DP metrics, mate finding
												prm,            // per-read metrics
												&msinkwrap,     // for organizing hits
												true,           // seek mate immediately
												true,           // report hits once found
												gReportDiscordant,// look for discordant alns?
												gReportMixed,   // look for unpaired alns?
												exhaustive[mate]);
											// Might be done, but just with this mate
										} else {
											// Unpaired dynamic programming driver
											ret = sd.extendSeeds(
												*rds[mate],     // read
												mate == 0,      // mate #1?
												shs[mate],      // seed hits
												ebwtFw,         // bowtie index
												&ebwtBw,        // rev bowtie index
												ref,            // packed reference strings
												sw,             // dynamic prog aligner
												sc,             // scoring scheme
												-1,             // # mms allowed in a seed
												0,              // length of a seed
												0,              // interval between seeds
												minsc[mate],    // minimum score for valid
												nceil[mate],    // N ceil for anchor
												maxhalf,        // max width on one DP side
												doUngapped,     // do ungapped alignment
												mxIter[mate],   // max extend loop iters
												mxUg[mate],     // max # ungapped extends
												mxDp[mate],     // max # DPs
												streak[mate],   // stop after streak of this many end-to-end fails
												streak[mate],   // stop after streak of this many ungap fails
												doExtend,       // extend seed hits
												enable8,        // use 8-bit SSE where possible
												cminlen,        // checkpoint if read is longer
												cpow2,          // checkpointer interval, log2
												doTri,          // triangular mini-fills?
												tighten,        // -M score tightening mode
												ca,             // seed alignment cache
												rnd,            // pseudo-random source
												wlm,            // group walk left metrics
												swmSeed,        // DP metrics, seed extend
												prm,            // per-read metrics
												&msinkwrap,     // for organizing hits
												true,           // report hits once found
												exhaustive[mate]);
										}
										assert_gt(ret, 0);
										MERGE_SW(sw);
										MERGE_SW(osw);
										// Clear out the 1mm hits so that we don't try to
										// extend them again later!
										shs[mate].clear1mmE2eHits();
										if(ret == EXTEND_EXHAUSTED_CANDIDATES) {
											// Not done yet
										} else if(ret == EXTEND_POLICY_FULFILLED) {
											// Policy is satisfied for this mate at least
											if(msinkwrap.state().doneWithMate(mate == 0)) {
												done[mate] = true;
											}
											if(msinkwrap.state().doneWithMate(mate == 1)) {
												done[mate^1] = true;
											}
										} else if(ret == EXTEND_PERFECT_SCORE) {
											// We exhausted this mode at least
											done[mate] = true;
										} else if(ret == EXTEND_EXCEEDED_HARD_LIMIT) {
											// We exceeded a per-read limit
											done[mate] = true;
										} else if(ret == EXTEND_EXCEEDED_SOFT_LIMIT) {
											// Not done yet
										} else {
											//
											cerr << "Bad return value: " << ret << endl;
											throw 1;
										}
										if(!done[mate]) {
											TAlScore perfectScore = sc.perfectScore(rdlens[mate]);
											if(!done[mate] && minsc[mate] == perfectScore) {
												done[mate] = true;
											}
										}
									}
								}
								int seedlens[2] = { multiseedLen, multiseedLen };
								nrounds[0] = min<size_t>(nrounds[0], interval[0]);
								nrounds[1] = min<size_t>(nrounds[1], interval[1]);
								Constraint gc = Constraint::penaltyFuncBased(scoreMin);
								size_t seedsTried = 0;
							size_t seedsTriedMS[] = {0, 0, 0, 0};
								size_t nUniqueSeeds = 0, nRepeatSeeds = 0, seedHitTot = 0;
							size_t nUniqueSeedsMS[] = {0, 0, 0, 0};
							size_t nRepeatSeedsMS[] = {0, 0, 0, 0};
							size_t seedHitTotMS[] = {0, 0, 0, 0};
								for(size_t roundi = 0; roundi < nSeedRounds; roundi++) {
									ca.nextRead(); // Clear cache in preparation for new search
									shs[0].clearSeeds();
									shs[1].clearSeeds();
									assert(shs[0].empty());
									assert(shs[1].empty());
									assert(shs[0].repOk(&ca.current()));
									assert(shs[1].repOk(&ca.current()));
									//if(roundi > 0) {
									//	if(seedlens[0] > 8) seedlens[0]--;
									//	if(seedlens[1] > 8) seedlens[1]--;
									//}
									for(size_t matei = 0; matei < (paired ? 2:1); matei++) {
										size_t mate = matemap[matei];
										if(done[mate] || msinkwrap.state().doneWithMate(mate == 0)) {
											// Done with this mate
											done[mate] = true;
											continue;
										}
										if(roundi >= nrounds[mate]) {
											// Not doing this round for this mate
											continue;
										}
										// Figure out the seed offset
										if(interval[mate] <= (int)roundi) {
											// Can't do this round, seeds already packed as
											// tight as possible
											continue; 
										}
										size_t offset = (interval[mate] * roundi) / nrounds[mate];
										assert(roundi == 0 || offset > 0);
										assert(!msinkwrap.maxed());
										assert(msinkwrap.repOk());
										//rnd.init(ROTL(rds[mate]->seed, 10));
										assert(shs[mate].repOk(&ca.current()));
										swmSeed.sdatts++;
										// Set up seeds
										seeds[mate]->clear();
										Seed::mmSeeds(
											multiseedMms,    // max # mms per seed
											seedlens[mate],  // length of a multiseed seed
											*seeds[mate],    // seeds
											gc);             // global constraint
										// Check whether the offset would drive the first seed
										// off the end
										if(offset > 0 && (*seeds[mate])[0].len + offset > rds[mate]->length()) {
											continue;
										}
										// Instantiate the seeds
									std::pair<int, int> instFw, instRc;
										std::pair<int, int> inst = al.instantiateSeeds(
											*seeds[mate],   // search seeds
											offset,         // offset to begin extracting
											interval[mate], // interval between seeds
											*rds[mate],     // read to align
											sc,             // scoring scheme
											nofw[mate],     // don't align forward read
											norc[mate],     // don't align revcomp read
											ca,             // holds some seed hits from previous reads
											shs[mate],      // holds all the seed hits
										sdm,            // metrics
										instFw,
										instRc);
										assert(shs[mate].repOk(&ca.current()));
										if(inst.first + inst.second == 0) {
											// No seed hits!  Done with this mate.
											assert(shs[mate].empty());
											done[mate] = true;
											break;
										}
										seedsTried += (inst.first + inst.second);
									seedsTriedMS[mate * 2 + 0] = instFw.first + instFw.second;
									seedsTriedMS[mate * 2 + 1] = instRc.first + instRc.second;
										// Align seeds
										al.searchAllSeeds(
											*seeds[mate],     // search seeds
											&ebwtFw,          // BWT index
											&ebwtBw,          // BWT' index
											*rds[mate],       // read
											sc,               // scoring scheme
											ca,               // alignment cache
											shs[mate],        // store seed hits here
											sdm,              // metrics
											prm);             // per-read metrics
										assert(shs[mate].repOk(&ca.current()));
										if(shs[mate].empty()) {
											// No seed alignments!  Done with this mate.
											done[mate] = true;
											break;
										}
									}
									// shs contain what we need to know to update our seed
									// summaries for this seeding
									for(size_t mate = 0; mate < 2; mate++) {
										if(!shs[mate].empty()) {
											nUniqueSeeds += shs[mate].numUniqueSeeds();
										nUniqueSeedsMS[mate * 2 + 0] += shs[mate].numUniqueSeedsStrand(true);
										nUniqueSeedsMS[mate * 2 + 1] += shs[mate].numUniqueSeedsStrand(false);
											nRepeatSeeds += shs[mate].numRepeatSeeds();
										nRepeatSeedsMS[mate * 2 + 0] += shs[mate].numRepeatSeedsStrand(true);
										nRepeatSeedsMS[mate * 2 + 1] += shs[mate].numRepeatSeedsStrand(false);
											seedHitTot += shs[mate].numElts();
										seedHitTotMS[mate * 2 + 0] += shs[mate].numEltsFw();
										seedHitTotMS[mate * 2 + 1] += shs[mate].numEltsRc();
										}
									}
									double uniqFactor[2] = { 0.0f, 0.0f };
									for(size_t i = 0; i < 2; i++) {
										if(!shs[i].empty()) {
											swmSeed.sdsucc++;
											uniqFactor[i] = shs[i].uniquenessFactor();
										}
									}
									// Possibly reorder the mates
									matemap[0] = 0; matemap[1] = 1;
									if(!shs[0].empty() && !shs[1].empty() && uniqFactor[1] > uniqFactor[0]) {
										// Do the mate with fewer exact hits first
										// TODO: Consider mates & orientations separately?
										matemap[0] = 1; matemap[1] = 0;
									}
									for(size_t matei = 0; matei < (paired ? 2:1); matei++) {
										size_t mate = matemap[matei];
										if(done[mate] || msinkwrap.state().doneWithMate(mate == 0)) {
											// Done with this mate
											done[mate] = true;
											continue;
										}
										assert(!msinkwrap.maxed());
										assert(msinkwrap.repOk());
										//rnd.init(ROTL(rds[mate]->seed, 10));
										assert(shs[mate].repOk(&ca.current()));
										if(!seedSumm) {
											// If there aren't any seed hits...
											if(shs[mate].empty()) {
												continue; // on to the next mate
											}
											// Sort seed hits into ranks
											shs[mate].rankSeedHits(rnd, msinkwrap.allHits());
											int ret = 0;
											if(paired) {
												// Paired-end dynamic programming driver
												ret = sd.extendSeedsPaired(
													*rds[mate],     // mate to align as anchor
													*rds[mate ^ 1], // mate to align as opp.
													mate == 0,      // anchor is mate 1?
													!filt[mate ^ 1],// opposite mate filtered out?
													shs[mate],      // seed hits for anchor
													ebwtFw,         // bowtie index
													&ebwtBw,        // rev bowtie index
													ref,            // packed reference strings
													sw,             // dyn prog aligner, anchor
													osw,            // dyn prog aligner, opposite
													sc,             // scoring scheme
													pepol,          // paired-end policy
													multiseedMms,   // # mms allowed in a seed
													seedlens[mate], // length of a seed
													interval[mate], // interval between seeds
													minsc[mate],    // min score for anchor
													minsc[mate^1],  // min score for opp.
													nceil[mate],    // N ceil for anchor
													nceil[mate^1],  // N ceil for opp.
													nofw[mate],     // don't align forward read
													norc[mate],     // don't align revcomp read
													maxhalf,        // max width on one DP side
													doUngapped,     // do ungapped alignment
													mxIter[mate],   // max extend loop iters
													mxUg[mate],     // max # ungapped extends
													mxDp[mate],     // max # DPs
													streak[mate],   // stop after streak of this many end-to-end fails
													streak[mate],   // stop after streak of this many ungap fails
													streak[mate],   // stop after streak of this many dp fails
													mtStreak[mate], // max mate fails per seed range
													doExtend,       // extend seed hits
													enable8,        // use 8-bit SSE where possible
													cminlen,        // checkpoint if read is longer
													cpow2,          // checkpointer interval, log2
													doTri,          // triangular mini-fills?
													tighten,        // -M score tightening mode
													ca,             // seed alignment cache
													rnd,            // pseudo-random source
													wlm,            // group walk left metrics
													swmSeed,        // DP metrics, seed extend
													swmMate,        // DP metrics, mate finding
													prm,            // per-read metrics
													&msinkwrap,     // for organizing hits
													true,           // seek mate immediately
													true,           // report hits once found
													gReportDiscordant,// look for discordant alns?
													gReportMixed,   // look for unpaired alns?
													exhaustive[mate]);
												// Might be done, but just with this mate
											} else {
												// Unpaired dynamic programming driver
												ret = sd.extendSeeds(
													*rds[mate],     // read
													mate == 0,      // mate #1?
													shs[mate],      // seed hits
													ebwtFw,         // bowtie index
													&ebwtBw,        // rev bowtie index
													ref,            // packed reference strings
													sw,             // dynamic prog aligner
													sc,             // scoring scheme
													multiseedMms,   // # mms allowed in a seed
													seedlens[mate], // length of a seed
													interval[mate], // interval between seeds
													minsc[mate],    // minimum score for valid
													nceil[mate],    // N ceil for anchor
													maxhalf,        // max width on one DP side
													doUngapped,     // do ungapped alignment
													mxIter[mate],   // max extend loop iters
													mxUg[mate],     // max # ungapped extends
													mxDp[mate],     // max # DPs
													streak[mate],   // stop after streak of this many end-to-end fails
													streak[mate],   // stop after streak of this many ungap fails
													doExtend,       // extend seed hits
													enable8,        // use 8-bit SSE where possible
													cminlen,        // checkpoint if read is longer
													cpow2,          // checkpointer interval, log2
													doTri,          // triangular mini-fills?
													tighten,        // -M score tightening mode
													ca,             // seed alignment cache
													rnd,            // pseudo-random source
													wlm,            // group walk left metrics
													swmSeed,        // DP metrics, seed extend
													prm,            // per-read metrics
													&msinkwrap,     // for organizing hits
													true,           // report hits once found
													exhaustive[mate]);
											}
											assert_gt(ret, 0);
											MERGE_SW(sw);
											MERGE_SW(osw);
											if(ret == EXTEND_EXHAUSTED_CANDIDATES) {
												// Not done yet
											} else if(ret == EXTEND_POLICY_FULFILLED) {
												// Policy is satisfied for this mate at least
												if(msinkwrap.state().doneWithMate(mate == 0)) {
													done[mate] = true;
												}
												if(msinkwrap.state().doneWithMate(mate == 1)) {
													done[mate^1] = true;
												}
											} else if(ret == EXTEND_PERFECT_SCORE) {
												// We exhausted this made at least
												done[mate] = true;
											} else if(ret == EXTEND_EXCEEDED_HARD_LIMIT) {
												// We exceeded a per-read limit
												done[mate] = true;
											} else if(ret == EXTEND_EXCEEDED_SOFT_LIMIT) {
												// Not done yet
											} else {
												//
												cerr << "Bad return value: " << ret << endl;
												throw 1;
											}
										} // if(!seedSumm)
									} // for(size_t matei = 0; matei < 2; matei++)
									
									// We don't necessarily have to continue investigating both
									// mates.  We continue on a mate only if its average
									// interval length is high (> 1000)
									for(size_t mate = 0; mate < 2; mate++) {
										if(!done[mate] && shs[mate].averageHitsPerSeed() < seedBoostThresh) {
											done[mate] = true;
										}
									}
								} // end loop over reseeding rounds
							if(seedsTried > 0) {
									prm.seedPctUnique = (float)nUniqueSeeds / seedsTried;
									prm.seedPctRep = (float)nRepeatSeeds / seedsTried;
									prm.seedHitAvg = (float)seedHitTot / seedsTried;
							} else {
								prm.seedPctUnique = -1.0f;
								prm.seedPctRep = -1.0f;
								prm.seedHitAvg = -1.0f;
							}
							for(int i = 0; i < 4; i++) {
								if(seedsTriedMS[i] > 0) {
									prm.seedPctUniqueMS[i] = (float)nUniqueSeedsMS[i] / seedsTriedMS[i];
									prm.seedPctRepMS[i] = (float)nRepeatSeedsMS[i] / seedsTriedMS[i];
									prm.seedHitAvgMS[i] = (float)seedHitTotMS[i] / seedsTriedMS[i];
								} else {
									prm.seedPctUniqueMS[i] = -1.0f;
									prm.seedPctRepMS[i] = -1.0f;
									prm.seedHitAvgMS[i] = -1.0f;
								}
								}
								size_t totnucs = 0;
								for(size_t mate = 0; mate < (paired ? 2:1); mate++) {
									if(filt[mate]) {
										size_t len = rdlens[mate];
										if(!nofw[mate] && !norc[mate]) {
											len *= 2;
										}
										totnucs += len;
									}
								}
							prm.seedsPerNuc = totnucs > 0 ? ((float)seedsTried / totnucs) : -1;
							for(int i = 0; i < 4; i++) {
								prm.seedsPerNucMS[i] = totnucs > 0 ? ((float)seedsTriedMS[i] / totnucs) : -1;
							}
								for(size_t i = 0; i < 2; i++) {
									assert_leq(prm.nExIters, mxIter[i]);
									assert_leq(prm.nExDps,   mxDp[i]);
									assert_leq(prm.nMateDps, mxDp[i]);
									assert_leq(prm.nExUgs,   mxUg[i]);
									assert_leq(prm.nMateUgs, mxUg[i]);
									assert_leq(prm.nDpFail,  streak[i]);
									assert_leq(prm.nUgFail,  streak[i]);
									assert_leq(prm.nEeFail,  streak[i]);
								}

						// Commit and report paired-end/unpaired alignments
						//uint32_t sd = rds[0]->seed ^ rds[1]->seed;
						//rnd.init(ROTL(sd, 20));
						msinkwrap.finishRead(
							&shs[0],              // seed results for mate 1
							&shs[1],              // seed results for mate 2
							exhaustive[0],        // exhausted seed hits for mate 1?
							exhaustive[1],        // exhausted seed hits for mate 2?
							nfilt[0],
							nfilt[1],
							scfilt[0],
							scfilt[1],
							lenfilt[0],
							lenfilt[1],
							qcfilt[0],
							qcfilt[1],
							rnd,                  // pseudo-random generator
							rpm,                  // reporting metrics
							prm,                  // per-read metrics
							sc,                   // scoring scheme
							!seedSumm,            // suppress seed summaries?
							seedSumm,             // suppress alignments?
							scUnMapped,           // Consider soft-clipped bases unmapped when calculating TLEN
							xeq);
						assert(!retry || msinkwrap.empty());
					} // while(retry)
				} // if(rdid >= skipReads && rdid < qUpto)
				else if(rdid >= qUpto) {
					break;
				}
				if(metricsPerRead) {
					MERGE_METRICS(metricsPt);
					nametmp = ps->read_a().name;
					metricsPt.reportInterval(
						metricsOfb, metricsStderr, true, &nametmp);
					metricsPt.reset();
				}
			} // while(true)
			
			// One last metrics merge
			MERGE_METRICS(metrics);
			
			if(dpLog    != NULL) dpLog->close();
			if(dpLogOpp != NULL) dpLogOpp->close();

		#ifdef PER_THREAD_TIMING
				ss.str("");
				ss.clear();
				ss << "thread: " << tid << " cpu_changeovers: " << ncpu_changeovers << std::endl
				   << "thread: " << tid << " node_changeovers: " << nnuma_changeovers << std::endl;
				std::cout << ss.str();
		#endif
			}
		#ifdef WITH_TBB
			p->done->fetch_and_add(1);
		#endif

			return;
	}
	public static void multiseedSearchWorker_2p5() {
		int tid = *((int*)vp);
		#endif
			PatternComposer&        patsrc   = *multiseed_patsrc;
			PatternParams           pp       = multiseed_pp;
			const Ebwt&             ebwtFw   = *multiseed_ebwtFw;
			const Ebwt&             ebwtBw   = *multiseed_ebwtBw;
			const Scoring&          sc       = *multiseed_sc;
			const BitPairReference& ref      = *multiseed_refs;
			AlnSink&                msink    = *multiseed_msink;
			OutFileBuf*             metricsOfb = multiseed_metricsOfb;

			// Sinks: these are so that we can print tables encoding counts for
			// events of interest on a per-read, per-seed, per-join, or per-SW
			// level.  These in turn can be used to diagnose performance
			// problems, or generally characterize performance.
			
			ThreadCounter tc;
			auto_ptr<PatternSourcePerThreadFactory> patsrcFact(createPatsrcFactory(patsrc, pp, tid));
			auto_ptr<PatternSourcePerThread> ps(patsrcFact->create());
			
			// Instantiate an object for holding reporting-related parameters.
			ReportingParams rp(
				(allHits ? std::numeric_limits<THitInt>::max() : khits), // -k
				mhits,             // -m/-M
				0,                 // penalty gap (not used now)
				msample,           // true -> -M was specified, otherwise assume -m
				gReportDiscordant, // report discordang paired-end alignments?
				gReportMixed);     // report unpaired alignments for paired reads?

			// Instantiate a mapping quality calculator
			auto_ptr<Mapq> bmapq(new_mapq(mapqv, scoreMin, sc));
			
			// Make a per-thread wrapper for the global MHitSink object.
			AlnSinkWrap msinkwrap(
				msink,         // global sink
				rp,            // reporting parameters
				*bmapq,        // MAPQ calculator
				(size_t)tid);  // thread id

			OuterLoopMetrics olm;
			SeedSearchMetrics sdm;
			WalkMetrics wlm;
			SwMetrics swmSeed, swmMate;
			DescentMetrics descm;
			ReportingMetrics rpm;
			RandomSource rnd, rndArb;
			SSEMetrics sseU8ExtendMet;
			SSEMetrics sseU8MateMet;
			SSEMetrics sseI16ExtendMet;
			SSEMetrics sseI16MateMet;
			uint64_t nbtfiltst = 0; // TODO: find a new home for these
			uint64_t nbtfiltsc = 0; // TODO: find a new home for these
			uint64_t nbtfiltdo = 0; // TODO: find a new home for these

			ASSERT_ONLY(BTDnaString tmp);

			int pepolFlag;
			if(gMate1fw && gMate2fw) {
				pepolFlag = PE_POLICY_FF;
			} else if(gMate1fw && !gMate2fw) {
				pepolFlag = PE_POLICY_FR;
			} else if(!gMate1fw && gMate2fw) {
				pepolFlag = PE_POLICY_RF;
			} else {
				pepolFlag = PE_POLICY_RR;
			}
			assert_geq(gMaxInsert, gMinInsert);
			assert_geq(gMinInsert, 0);
			PairedEndPolicy pepol(
				pepolFlag,
				gMaxInsert,
				gMinInsert,
				localAlign,
				gFlippedMatesOK,
				gDovetailMatesOK,
				gContainMatesOK,
				gOlapMatesOK,
				gExpandToFrag);
			
			AlignerDriver ald(
				descConsExp,         // exponent for interpolating maximum penalty
				descPrioritizeRoots, // whether to select roots with scores and weights
				msIval,              // interval length, as function of read length
				descLanding,         // landing length
				gVerbose,            // verbose?
				descentTotSz,        // limit on total bytes of best-first search data
				descentTotFmops);    // limit on total number of FM index ops in BFS
			
			PerfMetrics metricsPt; // per-thread metrics object; for read-level metrics
			BTString nametmp;
			
			PerReadMetrics prm;

			// Used by thread with threadid == 1 to measure time elapsed
			time_t iTime = time(0);

			// Keep track of whether last search was exhaustive for mates 1 and 2
			bool exhaustive[2] = { false, false };
			// Keep track of whether mates 1/2 were filtered out last time through
			bool filt[2]    = { true, true };
			// Keep track of whether mates 1/2 were filtered out due Ns last time
			bool nfilt[2]   = { true, true };
			// Keep track of whether mates 1/2 were filtered out due to not having
			// enough characters to rise about the score threshold.
			bool scfilt[2]  = { true, true };
			// Keep track of whether mates 1/2 were filtered out due to not having
			// more characters than the number of mismatches permitted in a seed.
			bool lenfilt[2] = { true, true };
			// Keep track of whether mates 1/2 were filtered out by upstream qc
			bool qcfilt[2]  = { true, true };

			rndArb.init((uint32_t)time(0));
			int mergei = 0;
			int mergeival = 16;
			while(true) {
				pair<bool, bool> ret = ps->nextReadPair();
				bool success = ret.first;
				bool done = ret.second;
				if(!success && done) {
					break;
				} else if(!success) {
					continue;
				}
				TReadId rdid = ps->read_a().rdid;
				bool sample = true;
				if(arbitraryRandom) {
					ps->read_a().seed = rndArb.nextU32();
					ps->read_b().seed = rndArb.nextU32();
				}
				if(sampleFrac < 1.0f) {
					rnd.init(ROTL(ps->read_a().seed, 2));
					sample = rnd.nextFloat() < sampleFrac;
				}
				if(rdid >= skipReads && rdid < qUpto && sample) {
					//
					// Check if there is metrics reporting for us to do.
					//
					if(metricsIval > 0 &&
					   (metricsOfb != NULL || metricsStderr) &&
					   !metricsPerRead &&
					   ++mergei == mergeival)
					{
						// Do a periodic merge.  Update global metrics, in a
						// synchronized manner if needed.
						MERGE_METRICS(metrics);
						mergei = 0;
						// Check if a progress message should be printed
						if(tid == 0) {
							// Only thread 1 prints progress messages
							time_t curTime = time(0);
							if(curTime - iTime >= metricsIval) {
								metrics.reportInterval(metricsOfb, metricsStderr, false, NULL);
								iTime = curTime;
							}
						}
					}
					prm.reset(); // per-read metrics
					prm.doFmString = sam_print_zm;
					// If we're reporting how long each read takes, get the initial time
					// measurement here
					if(sam_print_xt) {
						gettimeofday(&prm.tv_beg, &prm.tz_beg);
					}
					// Try to align this read
					olm.reads++;
					bool paired = !ps->read_b().empty();
					const size_t rdlen1 = ps->read_a().length();
					const size_t rdlen2 = paired ? ps->read_b().length() : 0;
					olm.bases += (rdlen1 + rdlen2);
					// Check if read is identical to previous read
					rnd.init(ROTL(ps->read_a().seed, 5));
					msinkwrap.nextRead(
						&ps->read_a(),
						paired ? &ps->read_b() : NULL,
						rdid,
						sc.qualitiesMatter());
					assert(msinkwrap.inited());
					size_t rdlens[2] = { rdlen1, rdlen2 };
					// Calculate the minimum valid score threshold for the read
					TAlScore minsc[2], maxpen[2];
					minsc[0] = minsc[1] = std::numeric_limits<TAlScore>::max();
					setupMinScores(*ps, paired, localAlign, sc, rdlens, minsc, maxpen);
					// N filter; does the read have too many Ns?
					size_t readns[2] = {0, 0};
					sc.nFilterPair(
						&ps->read_a().patFw,
						paired ? &ps->read_b().patFw : NULL,
						readns[0],
						readns[1],
						nfilt[0],
						nfilt[1]);
					// Score filter; does the read enough character to rise above
					// the score threshold?
					scfilt[0] = sc.scoreFilter(minsc[0], rdlens[0]);
					scfilt[1] = sc.scoreFilter(minsc[1], rdlens[1]);
					lenfilt[0] = lenfilt[1] = true;
					if(rdlens[0] <= (size_t)multiseedMms || rdlens[0] < 2) {
						if(!gQuiet) printMmsSkipMsg(*ps, paired, true, multiseedMms);
						lenfilt[0] = false;
					}
					if((rdlens[1] <= (size_t)multiseedMms || rdlens[1] < 2) && paired) {
						if(!gQuiet) printMmsSkipMsg(*ps, paired, false, multiseedMms);
						lenfilt[1] = false;
					}
					if(rdlens[0] < 2) {
						if(!gQuiet) printLenSkipMsg(*ps, paired, true);
						lenfilt[0] = false;
					}
					if(rdlens[1] < 2 && paired) {
						if(!gQuiet) printLenSkipMsg(*ps, paired, false);
						lenfilt[1] = false;
					}
					qcfilt[0] = qcfilt[1] = true;
					if(qcFilter) {
						qcfilt[0] = (ps->read_a().filter != '0');
						qcfilt[1] = (ps->read_b().filter != '0');
					}
					filt[0] = (nfilt[0] && scfilt[0] && lenfilt[0] && qcfilt[0]);
					filt[1] = (nfilt[1] && scfilt[1] && lenfilt[1] && qcfilt[1]);
					prm.nFilt += (filt[0] ? 0 : 1) + (filt[1] ? 0 : 1);
					Read* rds[2] = { &ps->read_a(), &ps->read_b() };
					assert(msinkwrap.empty());
					// Calcualte nofw / no rc
					bool nofw[2] = { false, false };
					bool norc[2] = { false, false };
					nofw[0] = paired ? (gMate1fw ? gNofw : gNorc) : gNofw;
					norc[0] = paired ? (gMate1fw ? gNorc : gNofw) : gNorc;
					nofw[1] = paired ? (gMate2fw ? gNofw : gNorc) : gNofw;
					norc[1] = paired ? (gMate2fw ? gNorc : gNofw) : gNorc;
					// Calculate nceil
					int nceil[2] = { 0, 0 };
					nceil[0] = nCeil.f<int>((double)rdlens[0]);
					nceil[0] = min(nceil[0], (int)rdlens[0]);
					if(paired) {
						nceil[1] = nCeil.f<int>((double)rdlens[1]);
						nceil[1] = min(nceil[1], (int)rdlens[1]);
					}
					exhaustive[0] = exhaustive[1] = false;
					bool pairPostFilt = filt[0] && filt[1];
					if(pairPostFilt) {
						rnd.init(ROTL((rds[0]->seed ^ rds[1]->seed), 10));
					}
					// Calculate streak length
					size_t streak[2]    = { maxDpStreak,   maxDpStreak };
					size_t mtStreak[2]  = { maxMateStreak, maxMateStreak };
					size_t mxDp[2]      = { maxDp,         maxDp       };
					size_t mxUg[2]      = { maxUg,         maxUg       };
					size_t mxIter[2]    = { maxIters,      maxIters    };
					if(allHits) {
						streak[0]   = streak[1]   = std::numeric_limits<size_t>::max();
						mtStreak[0] = mtStreak[1] = std::numeric_limits<size_t>::max();
						mxDp[0]     = mxDp[1]     = std::numeric_limits<size_t>::max();
						mxUg[0]     = mxUg[1]     = std::numeric_limits<size_t>::max();
						mxIter[0]   = mxIter[1]   = std::numeric_limits<size_t>::max();
					} else if(khits > 1) {
						for(size_t mate = 0; mate < 2; mate++) {
							streak[mate]   += (khits-1) * maxStreakIncr;
							mtStreak[mate] += (khits-1) * maxStreakIncr;
							mxDp[mate]     += (khits-1) * maxItersIncr;
							mxUg[mate]     += (khits-1) * maxItersIncr;
							mxIter[mate]   += (khits-1) * maxItersIncr;
						}
					}
					// If paired-end and neither mate filtered...
					if(filt[0] && filt[1]) {
						// Reduce streaks for either mate
						streak[0] = (size_t)ceil((double)streak[0] / 2.0);
						streak[1] = (size_t)ceil((double)streak[1] / 2.0);
						assert_gt(streak[1], 0);
					}
					assert_gt(streak[0], 0);
					// Increment counters according to what got filtered
					for(size_t mate = 0; mate < (paired ? 2:1); mate++) {
						if(!filt[mate]) {
							// Mate was rejected by N filter
							olm.freads++;               // reads filtered out
							olm.fbases += rdlens[mate]; // bases filtered out
						} else {
							olm.ureads++;               // reads passing filter
							olm.ubases += rdlens[mate]; // bases passing filter
						}
					}
					if(filt[0]) {
						ald.initRead(ps->read_a(), nofw[0], norc[0], minsc[0], maxpen[0], filt[1] ? &ps->read_b() : NULL);
					} else if(filt[1]) {
						ald.initRead(ps->read_b(), nofw[1], norc[1], minsc[1], maxpen[1], NULL);
					}
					if(filt[0] || filt[1]) {
						ald.go(sc, ebwtFw, ebwtBw, ref, descm, wlm, prm, rnd, msinkwrap);
					}
					// Commit and report paired-end/unpaired alignments
					uint32_t sd = rds[0]->seed ^ rds[1]->seed;
					rnd.init(ROTL(sd, 20));
					msinkwrap.finishRead(
						NULL,                 // seed results for mate 1
						NULL,                 // seed results for mate 2
						exhaustive[0],        // exhausted seed results for 1?
						exhaustive[1],        // exhausted seed results for 2?
						nfilt[0],
						nfilt[1],
						scfilt[0],
						scfilt[1],
						lenfilt[0],
						lenfilt[1],
						qcfilt[0],
						qcfilt[1],
						rnd,                  // pseudo-random generator
						rpm,                  // reporting metrics
						prm,                  // per-read metrics
						sc,                   // scoring scheme
						!seedSumm,            // suppress seed summaries?
						seedSumm,             // suppress alignments?
						scUnMapped,           // Consider soft-clipped bases unmapped when calculating TLEN
						xeq);
				} // if(rdid >= skipReads && rdid < qUpto)
				else if(rdid >= qUpto) {
					break;
				}
				if(metricsPerRead) {
					MERGE_METRICS(metricsPt);
					nametmp = ps->read_a().name;
					metricsPt.reportInterval(
						metricsOfb, metricsStderr, true, &nametmp);
					metricsPt.reset();
				}
			} // while(true)
			
			// One last metrics merge
			MERGE_METRICS(metrics);
		#ifdef WITH_TBB
			p->done->fetch_and_add(1);
		#endif

			return;
	}
	
	public static void multiseedSearch(
			Scoring sc,
			PatternParams pp,
			PatternComposer patsrc,      // pattern source
			ALNSink msink,               // hit sink
			Ebwt ebwtFw,                 // index of original text
			Ebwt ebwtBw,                 // index of mirror text
			OutFileBuf metricsOfb) {
		multiseed_patsrc = patsrc;
		multiseed_pp = pp;
		multiseed_msink  = msink;
		multiseed_ebwtFw = ebwtFw;
		multiseed_ebwtBw = ebwtBw;
		multiseed_sc     = sc;
		multiseed_metricsOfb      = metricsOfb;
		Timer _t = new Timer(cerr, "Time loading reference: ", timing);
		BitPairReference refs =	new BitPairReference(
				adjIdxBase,
				false,
				sanityCheck,
				null,
				null,
				false,
				useMm,
				useShmem,
				mmSweep,
				gVerbose,
				startVerbose);
		multiseed_refs = refs.get();
		EList<Integer> tids;
		EList<tthread::thread*> threads;
		threads.reserveExact(Math.max(nthreads, thread_ceiling));
		tids.reserveExact(Math.max(nthreads, thread_ceiling));
		{
			// Load the other half of the index into memory
			Timer _t(cerr, "Time loading forward index: ", timing);
			ebwtFw.loadIntoMemory(
				0,  // colorspace?
				-1, // not the reverse index
				true,         // load SA samp? (yes, need forward index's SA samp)
				true,         // load ftab (in forward index)
				true,         // load rstarts (in forward index)
				!noRefNames,  // load names?
				startVerbose);
		}
		if(multiseedMms > 0 || do1mmUpFront) {
			// Load the other half of the index into memory
			Timer _t(cerr, "Time loading mirror index: ", timing);
			ebwtBw.loadIntoMemory(
				0, // colorspace?
				// It's bidirectional search, so we need the reverse to be
				// constructed as the reverse of the concatenated strings.
				1,
				false,        // don't load SA samp in reverse index
				true,         // yes, need ftab in reverse index
				false,        // don't load rstarts in reverse index
				!noRefNames,  // load names?
				startVerbose);
		}
		
		Thread metThread = new Thread(this,"Metrics");
		metThread.start();
		
		if(!metricsPerRead && (metricsOfb != null || metricsStderr)) {
			metrics.reportInterval(metricsOfb, metricsStderr, true, null);
		}
	}
}