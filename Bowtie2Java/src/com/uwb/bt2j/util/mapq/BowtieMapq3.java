package com.uwb.bt2j.util.mapq;

import com.uwb.bt2j.aligner.AlignerFlags;
import com.uwb.bt2j.aligner.AlignerResult;
import com.uwb.bt2j.aligner.AlignmentSetSumm;
import com.uwb.bt2j.aligner.Scoring;
import com.uwb.bt2j.aligner.SimpleFunc;

public class BowtieMapq3 extends Mapq {

	public BowtieMapq3(SimpleFunc scoreMin, Scoring sc) {
		super(scoreMin, sc);
	}

	public long mapq(AlignmentSetSumm s, AlignerFlags flags, boolean mate1, long rdlen, long ordlen, String inps) {
		if(s.paired()) {
			return pair_nosec_perf;
		} else {
			boolean hasSecbest = AlignerResult.VALID_AL_SCORE(s.bestUnchosenScore(mate1));
			if(!flags.canMax() && !s.exhausted(mate1) && !hasSecbest) {
				return 255;
			}
			long scMax = (long)sc_.perfectScore(rdlen);
			long scMin = scoreMin_.f<Long>((float)rdlen);
			long best  = scMax - s.bestScore(mate1).score(); // best score (lower=better)
			double best_bin = (double)((double)best * (10.0 / (double)(scMax - scMin)) + 0.5);
			if(hasSecbest) {
				double diff = s.bestScore(mate1).score() - s.bestUnchosenScore(mate1).score();
				double diff_bin = (double)((double)diff * (10.0 / (double)(scMax - scMin)) + 0.5);
				// A valid second-best alignment was found
				if(best == scMax) {
					// Best alignment has perfect score
					return unp_sec_perf[best_bin];
				} else {
					// Best alignment has less than perfect score
					return unp_sec[diff_bin][best_bin];
				}
			} else {
				// No valid second-best alignment was found
				if(best == scMax) {
					// Best alignment has perfect score
					return unp_nosec_perf;
				} else {
					// Best alignment has less than perfect score
					return unp_nosec[best_bin];
				}
			}
		}
	}

}