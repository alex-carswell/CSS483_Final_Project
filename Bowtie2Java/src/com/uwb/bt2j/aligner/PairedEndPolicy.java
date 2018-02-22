package com.uwb.bt2j.aligner;

public class PairedEndPolicy {
	protected Boolean local_;
	protected int pol_;
	protected Boolean flippingOk_;
	protected Boolean dovetailOk_;
	protected Boolean containOk_;
	protected Boolean olapOk_;
	protected Boolean expandToFit_;
	protected double maxfrag_;
	protected double minfrag_;
	
	public enum PEPolicy {
		PE_POLICY_FF(1), PE_POLICY_RR(2), PE_POLICY_FR(3), PE_POLICY_RF(4);
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
	
	public void reset() {
		
	}
	
	public void init() {
		
	}
	
	public final Boolean otherMate() {
		
	}
	
	public int classifyPair() {
		
	}
	
	public final int policy() {
		return pol_;
	}
	
	public final double maxFragLen() {
		return maxfrag_;
	}
	
	public final double minFragLen() {
		return minfrag_;
	}
}
