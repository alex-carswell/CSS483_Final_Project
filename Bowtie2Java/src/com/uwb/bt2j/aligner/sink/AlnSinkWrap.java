package com.uwb.bt2j.aligner.sink;

public class AlnSinkWrap {
	protected AlnSink g_;
	protected ReportingParams rp_;
	protected double threadid_;
	protected Mapq mapq_;
	protected Boolean init_;
	protected Boolean maxed1_;
	protected Boolean maxed2_;
	protected Boolean maxedOverall_;
	protected long bestPair_;
	protected long best2Pair_;
	protected long bestUnp1;
	protected long best2Unp1;
	protected long bestUnp2;
	protected long best2Unp2;
	protected final Read rd1_;
	protected final Read rd2_;
	protected double rdid_;
	protected EList<AlnRes> rs1_;
	protected EList<AlnRes> rs2_;
	protected EList<AlnRes> rs1u_;
	protected EList<AlnRes> rs2u_;
	protected EList<double> select1_;
	protected EList<double> select2_;
	protected ReportingState st_;
	protected EList<Pair<AlnScore, double>> selectBuf_;
	protected BString obuf_;
	protected StackedAln staln_;

	public int nextRead() {
		
	}
	
	public void finishRead() {
		
	}
	
	public Boolean report() {
		
	}
	
	public final Boolean empty() {
		return rs1_.empty() && rs1u_.empty() && rs2u_.empty();
	}
	
	public final Boolean maxed() {
		return maxedOverall_;
	}
	
	public final Boolean readIsPair() {
		return rd1_ != null && rd2_ != null;
	}
	
	public final Boolean inited() {
		return init_;
	}
	
	public final ReportingState state() {
		return st_;
	}
	
	public final Boolean Mmode() {
		return rp_.mhitsSet();
	}
	
	public final Boolean allHits() {
		return rp_.allHits();
	}
	
	public final Boolean hasSecondBestUnp1() {
		return best2Unp1_ != Long.MIN_VALUE;
	}
	
	public final Boolean hasSecondBestUnp2() {
		return best2Unp2_ != Long.MIN_VALUE;
	}
	
	public final Boolean hasSecondBestPair() {
		return best2Pair_ != Long.MIN_VALUE;
	}
	
	public final long bestUnp1() {
		return bestUnp1_;
	}
	
	public final long secondBestUnp1() {
		return best2Unp1;
	}
	
	public final long bestUnp2() {
		return bestUnp2_;
	}
	
	public final long secondBestUnp2() {
		return best2Unp2;
	}
	
	public final long bestPair() {
		return bestPair_;
	}
	
	public final long secondBestPar() {
		return best2Pair;
	}
	
	protected Boolean sameRead() {
		
	}
	
	protected Boolean prepareDiscordants() {
		
	}
	
	protected final double selectAlnsToReport(final EList<AlnRes> rs, long num, EList<double> select, RandomSource rnd) {
		
	}
	
	protected final double selectByScore() {
		
	}
	
	
}