package com.uwb.bt2j.aligner;

public class PairedEndPolicy {
	protected Boolean local_;
	protected PEPolicy pol_;
	protected Boolean flippingOk_;
	protected Boolean dovetailOk_;
	protected Boolean containOk_;
	protected Boolean olapOk_;
	protected Boolean expandToFit_;
	protected double maxfrag_;
	protected double minfrag_;
	
	public enum PEPolicy {
		PE_POLICY_NONE(-1), PE_POLICY_FF(1), PE_POLICY_RR(2), PE_POLICY_FR(3), PE_POLICY_RF(4);
		private int x;
		PEPolicy(int y){x = y;}
	}
	
	public enum PEALS {
		PE_ALS_NORMAL(1), PE_ALS_OVERLAP(2), PE_ALS_CONTAIN(3), PE_ALS_DOVETAIL(4), PE_ALS_DISCORD(5);
		private int x;
		PEALS(int y){x = y;}
	}
	
	public static Boolean pairedEndPolicyCompat(PEPolicy policy, Boolean oneLeft, Boolean oneWat, Boolean twoWat) {
		switch(policy) {
			case PE_POLICY_FF:
				return oneWat == twoWat && oneWat == oneLeft;
			case PE_POLICY_RR:
				return oneWat == twoWat && oneWat != oneLeft;
			case PE_POLICY_FR:
				return oneWat != twoWat && oneWat == oneLeft;
			case PE_POLICY_RF:
				return oneWat != twoWat && oneWat != oneLeft;
			default: {
				System.err.println("Bad PE_POLICY: " + policy);
			}
		}
		return false;
	}
	
	public static void pairedEndPolicyMateDir(PEPolicy policy, Boolean is1, Boolean fw, Boolean left, Boolean mfw) {
		switch(policy) {
		case PE_POLICY_FF: {
			left = (is1 != fw);
			mfw = fw;
			break;
		}
		case PE_POLICY_RR: {
			left = (is1 == fw);
			mfw = fw;
			break;
		}
		case PE_POLICY_FR: {
			left = !fw;
			mfw = !fw;
			break;
		}
		case PE_POLICY_RF: {
			left = fw;
			mfw = !fw;
			break;
		}
		default: {
			System.err.println("Error: No such PE_POLICY: " + policy);
		}
	}
	}
	
	public PairedEndPolicy() {
		reset();
	}
	
	public PairedEndPolicy(PEPolicy pol, double maxfrag, double minfrag, Boolean local, Boolean flippingOk, Boolean dovetailOk, Boolean containOk, Boolean olapOk, Boolean expandToFit) {
		init(pol,maxfrag,minfrag,local,flippingOk,dovetailOk,containOk,olapOk,expandToFit);
	}
	
	public void reset() {
		init(PEPolicy.PE_POLICY_NONE, 0xffffffff, 0xffffffff, false, false, false, false, false, false);
	}
	
	public void init(PEPolicy pol, double maxfrag, double minfrag, Boolean local, Boolean flippingOk, Boolean dovetailOk, Boolean containOk, Boolean olapOk, Boolean expandToFit) {
		pol_         = pol;
		maxfrag_     = maxfrag;
		minfrag_     = minfrag;
		local_       = local;
		flippingOk_  = flippingOk;
		dovetailOk_  = dovetailOk;
		containOk_   = containOk;
		olapOk_      = olapOk;
		expandToFit_ = expandToFit;
	}
	
	public final Boolean otherMate(Boolean is1, Boolean fw, long off, long maxalcols, double reflen, double len1, double len2, Boolean oleft, long oll, long olr,long orl,long orr,Boolean ofw) {
		// Calculate whether opposite mate should align to left or to right
		// of given mate, and what strand it should align to
		pairedEndPolicyMateDir(pol_, is1, fw, oleft, ofw);
		
		long alen = (long) (is1 ? len1 : len2); // length of opposite mate
		
		// Expand the maximum fragment length if necessary to accomodate
		// the longer mate
		double maxfrag = maxfrag_;
		double minfrag = minfrag_;
		if(minfrag < 1) {
			minfrag = 1;
		}
		if(len1 > maxfrag && expandToFit_) maxfrag = len1;
		if(len2 > maxfrag && expandToFit_) maxfrag = len2;
		if(!expandToFit_ && (len1 > maxfrag || len2 > maxfrag)) {
			// Not possible to find a concordant alignment; one of the
			// mates is too long
			return false;
		}
		
		// Now calculate bounds within which a dynamic programming
		// algorithm should search for an alignment for the opposite mate
		if(oleft) {
			//    -----------FRAG MAX----------------
			//                 -------FRAG MIN-------
			//                               |-alen-|
			//                             Anchor mate
			//                               ^off
			//                  |------|
			//       Not concordant: LHS not outside min
			//                 |------|
			//                Concordant
			//      |------|
			//     Concordant
			//  |------|
			// Not concordant: LHS outside max
			
			//    -----------FRAG MAX----------------
			//                 -------FRAG MIN-------
			//                               |-alen-|
			//                             Anchor mate
			//                               ^off
			//    |------------|
			// LHS can't be outside this range
			//                               -----------FRAG MAX----------------
			//    |------------------------------------------------------------|
			// LHS can't be outside this range, assuming no restrictions on
			// flipping, dovetailing, containment, overlap, etc.
			//                                      |-------|
			//                                      maxalcols
			//    |-----------------------------------------|
			// LHS can't be outside this range, assuming no flipping
			//    |---------------------------------|
			// LHS can't be outside this range, assuming no dovetailing
			//    |-------------------------|
			// LHS can't be outside this range, assuming no overlap

			oll = (long) (off + alen - maxfrag);
			olr = (long)(off + alen - minfrag);
			
			orl = oll;
			orr = (long) (off + maxfrag - 1);

			// What if overlapping alignments are not allowed?
			if(!olapOk_) {
				// RHS can't be flush with or to the right of off
				orr = Long.min(orr, off-1);
				if(orr < olr) olr = orr;
			}
			// What if dovetail alignments are not allowed?
			else if(!dovetailOk_) {
				// RHS can't be past off+alen-1
				orr = Long.min(orr, off + alen - 1);
			}
			// What if flipped alignments are not allowed?
			else if(!flippingOk_ && maxalcols != -1) {
				// RHS can't be right of ???
				orr = Long.min(orr, off + alen - 1 + (maxalcols-1));
			}
		} else {
			//                             -----------FRAG MAX----------------
			//                             -------FRAG MIN-------
			//  -----------FRAG MAX----------------
			//                             |-alen-|
			//                           Anchor mate
			//                             ^off
	 		//                                          |------|
			//                            Not concordant: RHS not outside min
			//                                           |------|
			//                                          Concordant
			//                                                      |------|
			//                                                     Concordant
			//                                                          |------|
			//                                      Not concordant: RHS outside max
			//

			//                             -----------FRAG MAX----------------
			//                             -------FRAG MIN-------
			//  -----------FRAG MAX----------------
			//                             |-alen-|
			//                           Anchor mate
			//                             ^off
			//                                                  |------------|
			//                                      RHS can't be outside this range
			//  |------------------------------------------------------------|
			// LHS can't be outside this range, assuming no restrictions on
			// dovetailing, containment, overlap, etc.
			//                     |-------|
			//                     maxalcols
			//                     |-----------------------------------------|
			//             LHS can't be outside this range, assuming no flipping
			//                             |---------------------------------|
			//          LHS can't be outside this range, assuming no dovetailing
			//                                     |-------------------------|
			//              LHS can't be outside this range, assuming no overlap
			
			orr = (long) (off + (maxfrag - 1));
			orl  = (long) (off + (minfrag - 1));
			
			oll = (long) (off + alen - maxfrag);
			olr = orr;
			
			// What if overlapping alignments are not allowed?
			if(!olapOk_) {
				// LHS can't be left of off+alen
				oll = Long.max(oll, off+alen);
				if(oll > orl) orl = oll;
			}
			// What if dovetail alignments are not allowed?
			else if(!dovetailOk_) {
				// LHS can't be left of off
				oll = Long.max(oll, off);
			}
			// What if flipped alignments are not allowed?
			else if(!flippingOk_ && maxalcols != -1) {
				// LHS can't be left of off - maxalcols + 1
				oll = Long.max(oll, off - maxalcols + 1);
			}
		}

		// Boundaries and orientation determined
		return true;
	}
	
	public PEALS classifyPair(long off1,long len1,Boolean fw1,long off2,long len2,Boolean fw2) {
		// Expand the maximum fragment length if necessary to accomodate
		// the longer mate
		double maxfrag = maxfrag_;
		if(len1 > maxfrag && expandToFit_) maxfrag = len1;
		if(len2 > maxfrag && expandToFit_) maxfrag = len2;
		double minfrag = minfrag_;
		if(minfrag < 1) {
			minfrag = 1;
		}
		Boolean oneLeft = false;
		if(pol_ == PEPolicy.PE_POLICY_FF) {
			if(fw1 != fw2) {
				// Bad combination of orientations
				return PEALS.PE_ALS_DISCORD;
			}
			oneLeft = fw1;
		} else if(pol_ == PEPolicy.PE_POLICY_RR) {
			if(fw1 != fw2) {
				// Bad combination of orientations
				return PEALS.PE_ALS_DISCORD;
			}
			oneLeft = !fw1;
		} else if(pol_ == PEPolicy.PE_POLICY_FR) {
			if(fw1 == fw2) {
				// Bad combination of orientations
				return PEALS.PE_ALS_DISCORD;
			}
			oneLeft = fw1;
		} else if(pol_ == PEPolicy.PE_POLICY_RF) {
			if(fw1 == fw2) {
				// Bad combination of orientations
				return PEALS.PE_ALS_DISCORD;
			}
			oneLeft = !fw1;
		}
		// Calc implied fragment size
		long fraglo = Long.min(off1, off2);
		long fraghi = Long.max(off1+len1, off2+len2);
		double frag = (double)(fraghi - fraglo);
		if(frag > maxfrag || frag < minfrag) {
			// Pair is discordant by virtue of the extents
			return PEALS.PE_ALS_DISCORD;
		}
		long lo1 = off1;
		long hi1 = (long) (off1 + len1 - 1);
		long lo2 = off2;
		long hi2 = (long) (off2 + len2 - 1);
		Boolean containment = false;
		// Check whether one mate entirely contains the other
		if((lo1 >= lo2 && hi1 <= hi2) ||
		   (lo2 >= lo1 && hi2 <= hi1))
		{
			containment = true;
		}
		PEALS type = PEALS.PE_ALS_NORMAL;
		// Check whether one mate overlaps the other
		Boolean olap = false;
		if((lo1 <= lo2 && hi1 >= lo2) ||
		   (lo1 <= hi2 && hi1 >= hi2) ||
		   containment)
		{
			// The mates overlap
			olap = true;
			if(!olapOk_) return PEALS.PE_ALS_DISCORD;
			type = PEALS.PE_ALS_OVERLAP;
		}
		// Check if the mates are in the wrong relative orientation,
		// without any overlap
		if(!olap) {
			if((oneLeft && lo2 < lo1) || (!oneLeft && lo1 < lo2)) {
				return PEALS.PE_ALS_DISCORD;
			}
		}
		// If one mate contained the other, report that
		if(containment) {
			if(!containOk_) return PEALS.PE_ALS_DISCORD;
			type = PEALS.PE_ALS_CONTAIN;
		}
		// Check whether there's dovetailing; i.e. does the left mate
		// extend past the right end of the right mate, or vice versa
		if(( oneLeft && (hi1 > hi2 || lo2 < lo1)) ||
		   (!oneLeft && (hi2 > hi1 || lo1 < lo2)))
		{
			if(!dovetailOk_) return PEALS.PE_ALS_DISCORD;
			type = PEALS.PE_ALS_DOVETAIL;
		}
		return type;
	}
	
	public final PEPolicy policy() {
		return pol_;
	}
	
	public final double maxFragLen() {
		return maxfrag_;
	}
	
	public final double minFragLen() {
		return minfrag_;
	}
}
