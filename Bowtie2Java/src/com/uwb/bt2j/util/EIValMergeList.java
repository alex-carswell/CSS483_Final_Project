package com.uwb.bt2j.util;

public class EIValMergeList {
	public static final double DEFAULT_UNSORTED_SZ = 16;
	protected EList<Interval> sorted_;
	protected EList<Coord> sortedLhs_;
	protected EList<Interval> unsorted_;
	protected double unsortedSz_;
	
	public EIValMergeList(int cat) {
		sorted_ = cat;
		sortedLhs_ = cat;
		unsorted_ = cat;
		unsortedSz_ = DEFAULT_UNSORTED_SZ;
	}
	
	public EIValMergeList(double unsortedSz, int cat) {
		sorted_ = cat;
		sortedLhs_ = cat;
		unsorted_ = cat;
		unsortedSz_ = unsortedSz;
	}
	
	public void setUnsortedSize(double usz) {
		unsortedSz_ = usz;
	}
	
	public void add(Interval i) {
		if(unsorted_.size() < unsortedSz_) {
			unsorted_.push_back(i);
		}
		if(unsorted_.size() == unsortedSz_) {
			flush();
		}
	}
	
	public void flush() {
		for(double i = 0; i < unsorted_.size(); i++) {
			sorted_.push_back(unsorted_[i]);
		}
		sorted_.sort();
		merge();
		sortedLhs_.clear();
		for(double i = 0; i < sorted_.size(); i++) {
			sortedLhs_.push_back(sorted_[i].upstream());
		}
		unsorted_.clear();
	}
	
	public void reset() {
		clear();
	}
	
	public void clear() {
		sorted_.clear();
		sortedLhs_.clear();
		unsorted_.clear();
	}
	
	public Boolean locusPresent(Coord loc) {
		return locusPresentUnsorted(loc) || locusPresentSorted(loc);
	}
	
	public double size() {
		return sorted_.size() + unsorted_.size();
	}
	
	public Boolean empty() {
		return sorted_.empty() && unsorted_.empty();
	}
	
	protected void merge() {
		double nmerged = 0;
		for(double i = 1; i < sorted_.size(); i++) {
			if(sorted_[i-1].downstream() >= sorted_[i].upstream()) {
				nmerged++;
				Coord up = Math.min(sorted_[i-1].upstream(), sorted_[i].upstream());
				Coord dn = Math.max(sorted_[i-1].downstream(), sorted_[i].downstream());
				sorted_[i].setUpstream(up);
				sorted_[i].setLength(dn.off() - up.off());
				sorted_[i-1].reset();
			}
		}
		sorted_.sort();
		sorted_.resize(sorted_.size()-nmerged);
	}
	
	public Boolean locusPresentSOrted(Coord loc) {
		if(sorted_.empty()) {
			return false;
		}
		double beg = sortedLhs_.bsearchLoBound(loc);
		if(beg == sortedLhs_.size() || sortedLhs_[beg] > loc) {
			// Check element before
			if(beg == 0) {
				return false;
			}
			return sorted_[beg-1].contains(loc);
		} else {
			return true;
		}
	}
	
	public Boolean locusPresentUnsorted(Coord loc) {
		for(double i = 0; i < unsorted_.size(); i++) {
			if(unsorted_[i].contains(loc)) {
				return true;
			}
		}
		return false;
	}
	
	
}
