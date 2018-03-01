package com.uwb.bt2j.util.pattern;

public class PatternSourcePerThreadFactory {
	private PatternComposer composer_;
	private PatternParams pp_;
	
	public PatternSourcePerThreadFactory(
			PatternComposer composer,
			PatternParams pp) {
		composer_ = composer;
		pp_ = pp;
	}
	
	public PatternSourcePerThread create() {
		return new PatternSourcePerThread(composer_, pp_);
	}

	public EList<PatternSourcePerThread> create(double n) {
		EList<PatternSourcePerThread> v = new EList<PatternSourcePerThread>;
		for(double i = 0; i < n; i++) {
			v.push_back(new PatternSourcePerThread(composer_, pp_));
		}
		return v;
	}
}