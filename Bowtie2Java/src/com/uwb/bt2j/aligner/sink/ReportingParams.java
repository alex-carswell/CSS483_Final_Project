package com.uwb.bt2j.aligner.sink;

public class ReportingParams {
	private long khits, mhits, pengap;
	private Boolean msample, discord, mixed;
	
	public ReportingParams(long khits_,
			long mhits_,
			long pengap_,
			Boolean msample_,
			Boolean discord_,
			Boolean mixed_) {
		init(khits_, mhits_, pengap_, msample_, discord_, mixed_);
	}
	
	public void init(
			long khits_,
			long mhits_,
			long pengap_,
			Boolean msample_,
			Boolean discord_,
			Boolean mixed_)
	{
		khits   = khits_;     // -k (or high if -a)
		mhits   = ((mhits_ == 0) ? Long.MAX_VALUE : mhits_);
		pengap  = pengap_;
		msample = msample_;
		discord = discord_;
		mixed   = mixed_;
	}
	
	public final Boolean mhitsSet() {
		return mhits < Long.MAX_VALUE;
	}
	
	public final long mult() {
		if(mhitsSet())
			return mhits + 1;
		return khits;
	}
	
	public void boostThreshold(SimpleFunc func) {
		long mul = mult();
		if(mul == Long.MAX_VALUE)
			func.setMin(Double.MAX_VALUE);
		else if(mul > 1)
			func.mult(mul);
	}
	
	public final Boolean allHits() {
		return khits == Long.MAX_VALUE;
	}
}