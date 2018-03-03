package com.uwb.bt2j.aligner;

public class PerReadMetrics {
	public long nExIters;      // iterations of seed hit extend loop

	public long nExDps;        // # extend DPs run on this read
	public long nExDpSuccs;    // # extend DPs run on this read
	public long nExDpFails;    // # extend DPs run on this read
	
	public long nExUgs;        // # extend ungapped alignments run on this read
	public long nExUgSuccs;    // # extend ungapped alignments run on this read
	public long nExUgFails;    // # extend ungapped alignments run on this read

	public long nExEes;        // # extend ungapped alignments run on this read
	public long nExEeSuccs;    // # extend ungapped alignments run on this read
	public long nExEeFails;    // # extend ungapped alignments run on this read

	public long nMateDps;      // # mate DPs run on this read
	public long nMateDpSuccs;  // # mate DPs run on this read
	public long nMateDpFails;  // # mate DPs run on this read
	
	public long nMateUgs;      // # mate ungapped alignments run on this read
	public long nMateUgSuccs;  // # mate ungapped alignments run on this read
	public long nMateUgFails;  // # mate ungapped alignments run on this read

	public long nRedundants;   // # redundant seed hits
	
	public double nSeedRanges;   // # BW ranges found for seeds
	public double nSeedElts;     // # BW elements found for seeds

	public double nSeedRangesFw; // # BW ranges found for seeds from fw read
	public double nSeedEltsFw;   // # BW elements found for seeds from fw read

	public double nSeedRangesRc; // # BW ranges found for seeds from fw read
	public double nSeedEltsRc;   // # BW elements found for seeds from fw read
	
	public double seedMedian;    // median seed hit count
	public float seedMean;      // rounded mean seed hit count
	
	public long nEeFmops;      // FM Index ops for end-to-end alignment
	public long nSdFmops;      // FM Index ops used to align seeds
	public long nExFmops;      // FM Index ops used to resolve offsets
	
	public long nFtabs;        // # ftab lookups
	public long nRedSkip;      // # times redundant path was detected and aborted
	public long nRedFail;      // # times a path was deemed non-redundant
	public long nRedIns;       // # times a path was added to redundancy list
	
	public long nDpFail;       // number of dp failures in a row up until now
	public long nDpFailStreak; // longest streak of dp failures
	public long nDpLastSucc;   // index of last dp attempt that succeeded
	
	public long nUgFail;       // number of ungap failures in a row up until now
	public long nUgFailStreak; // longest streak of ungap failures
	public long nUgLastSucc;   // index of last ungap attempt that succeeded

	public long nEeFail;       // number of ungap failures in a row up until now
	public long nEeFailStreak; // longest streak of ungap failures
	public long nEeLastSucc;   // index of last ungap attempt that succeeded
	
	public long nFilt;         // # mates filtered
	
	public long bestLtMinscMate1; // best invalid score observed for mate 1
	public long bestLtMinscMate2; // best invalid score observed for mate 2
	
	public float seedPctUnique;      // % of read covered by unique seed hits
	public float seedPctUniqueMS[]; // % of read covered by unique seed hits by mate and strand
	public float seedPctRep;         // % of read covered by repetitive seed hits
	public float seedPctRepMS[];    // % of read covered by repetitive seed hits by mate and strand
	public float seedHitAvg;         // avg # seed hits per hitting seed
	public float seedHitAvgMS[];    // avg # seed hits per hitting seed by mate and strand
	public float seedsPerNuc;        // # seeds tried / # alignable nucleotides
	public float seedsPerNucMS[];   // # seeds tried / # alignable nucleotides by mate and strand
	
	// For collecting information to go into an FM string
	public boolean doFmString;
	public FmString fmString;
	
	public PerReadMetrics() {
		reset();
	}
	
	public void reset() {
		nExIters =
				nExDps   = nExDpSuccs   = nExDpFails   =
				nMateDps = nMateDpSuccs = nMateDpFails =
				nExUgs   = nExUgSuccs   = nExUgFails   =
				nMateUgs = nMateUgSuccs = nMateUgFails =
				nExEes   = nExEeSuccs   = nExEeFails   =
				nRedundants =
				nEeFmops = nSdFmops = nExFmops =
				nDpFail = nDpFailStreak = nDpLastSucc =
				nUgFail = nUgFailStreak = nUgLastSucc =
				nEeFail = nEeFailStreak = nEeLastSucc =
				nFilt = 0;
				nFtabs = 0;
				nRedSkip = 0;
				nRedFail = 0;
				nRedIns = 0;
				doFmString = false;
				nSeedRanges = nSeedElts = 0;
				nSeedRangesFw = nSeedEltsFw = 0;
				nSeedRangesRc = nSeedEltsRc = 0;
				seedMedian = seedMean = 0;
				bestLtMinscMate1 =
				bestLtMinscMate2 = Long.MIN_VALUE;
				seedPctUnique = seedPctRep = seedsPerNuc = seedHitAvg = 0.0f;
				fmString.reset();
	}
}
