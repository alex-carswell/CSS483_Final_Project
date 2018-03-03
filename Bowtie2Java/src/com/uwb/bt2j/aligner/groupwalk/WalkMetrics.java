package com.uwb.bt2j.aligner.groupwalk;

public class WalkMetrics {
	public long bwops;       // Burrows-Wheeler operations
	public long branches;    // BW range branch-offs
	public long resolves;    // # offs resolved with BW walk-left
	public long refresolves; // # resolutions caused by reference scanning
	public long reports;     // # offs reported (1 can be reported many times)
	public WalkMetrics() {
		reset();
	}
	
	public void merge(WalkMetrics m, boolean getLock) {
			mergeImpl(m);
	}
	
	public void reset() {
		bwops = branches = resolves = refresolves = reports = 0;
	}
	
	private void mergeImpl(WalkMetrics m) {
		bwops += m.bwops;
		branches += m.branches;
		resolves += m.resolves;
		refresolves += m.refresolves;
		reports += m.reports;
	}
}
