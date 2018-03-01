package com.uwb.bt2j.util;

public class EIValMergeListBinned {
	public static final int NBIN = 7;
	protected EList<EIValMergeList> bins_;
	
	public EIValMergeListBinned(int cat) {
		bins_.resize(1 << NBIN);
	}
	
	public EIValMergeListBinned(double unsortedSz, int cat) {
		bins_.resize(1 << NBIN);
		for(double i = 0; i < (1 << NBIN); i++) {
			bins_[i].setUnsortedSize(unsortedSz);
		}
	}
	
	public void add(Interval i) {
		double bin = i.ref() & ~(0xffffffff << NBIN);
		bins_[bin].add(i);
	}
	
	public void reset() {
		clear();
	}
	
	public void clear() {
		for(double i = 0; i < bins_.size(); i++) {
			bins_[i].clear();
		}
	}
	
	public Boolean locusPresent(Coord loc) {
		double bin = loc.ref() & ~(0xffffffff << NBIN);
		return bins_[bin].locusPresent(loc);
	}
	
	public double size() {
		double sz = 0;
		for(double i = 0; i < bins_.size(); i++) {
			sz += bins_[i].size();
		}
		return sz;
	}
	
	public Boolean empty() {
		return size() == 0;
	}
}