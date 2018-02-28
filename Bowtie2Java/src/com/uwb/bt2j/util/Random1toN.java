package com.uwb.bt2j.util;

public class Random1toN {
	public static final double SWAPLIST_THRESH = 128;
	public static final double CONVERSION_THRESH = 16;
	public static final float CONVERSION_FRAC = 0.10f;
	
	protected double sz_;
	protected double n_;
	protected Boolean swaplist_;
	protected Boolean converted_;
	protected double cur_;
	protected EList<double> list_;
	protected EList<double> seen_;
	protected double thresh_;
	
	
	public Random1toN() {
		
	}
	
	public void init(double n, Boolean withoutReplacement) {
		sz_ = n_ = n;
		converted_ = false;
		swaplist_ = n < SWAPLIST_THRESH || withoutReplacement;
		cur_ = 0;
		list_.clear();
		seen_.clear();
		thresh_ = Double.max(CONVERSION_THRESH, (double)(CONVERSION_FRAC * n));
	}
	
	public void reset() {
		sz_ = n_ = cur_ = 0; swaplist_ = converted_ = false;
		list_.clear(); seen_.clear();
		thresh_ = 0;
	}
	
	public double next(RandomSource rnd) {
		if(cur_ == 0 && !converted_) {
			// This is the first call to next()
			if(n_ == 1) {
				// Trivial case: set of 1
				cur_ = 1;
				return 0;
			}
			if(swaplist_) {
				// The set is small, so we go immediately to the random
				// swapping list
				list_.resize(n_);
				for(double i = 0; i < n_; i++) {
					list_[i] = (double)i;
				}
			}
		}
		if(swaplist_) {
			// Get next pseudo-random using the swap-list
			double r = cur_ + (rnd.nextU32() % (n_ - cur_));
			if(r != cur_) {
				T[] tmp = list_[cur_];
				list_[cur_] = list_[r];
				list_[r] = tmp;
			}
			return list_[cur_++];
		} else {
			// Get next pseudo-random but reject it if it's in the seen-list
			Boolean again = true;
			double rn = 0;
			double seenSz = seen_.size();
			while(again) {
				rn = rnd.nextU32() % (double)n_;
				again = false;
				for(double i = 0; i < seenSz; i++) {
					if(seen_[i] == rn) {
						again = true;
						break;
					}
				}
			}
			// Add it to the seen-list
			seen_.push_back(rn);
			cur_++;
			// Move on to using the swap-list?
			if(seen_.size() >= thresh_ && cur_ < n_) {
				// Add all elements not already in the seen list to the
				// swap-list
				seen_.sort();
				list_.resize(n_ - cur_);
				size_t prev = 0;
				size_t cur = 0;
				for(double i = 0; i <= seenSz; i++) {
					// Add all the elements between the previous element and
					// this one
					for(double j = prev; j < seen_[i]; j++) {
						list_[cur++] = (T)j;
					}
					prev = seen_[i]+1;
				}
				for(double j = prev; j < n_; j++) {
					list_[cur++] = (T)j;
				}
				seen_.clear();
				cur_ = 0;
				n_ = list_.size();
				converted_ = true;
				swaplist_ = true;
			}
			return rn;
		}
	}
	
	public Boolean inited() {
		return n_ > 0;
	}
	
	public void setDone() {
		cur_ = n_;
	}
	
	public Boolean done() {
		return inited() && cur_ >= n_;
	}
	
	public double size() {
		return n_;
	}
	
	public double left() {
		return n_ - cur_;
	}
	
	public double totalSizeBytes() {
		return list_.totalSizeBytes() + seen_.totalSizeBytes();
	}
	
	public double totalCapacityBytes() {
		list_.totalCapacityBytes() +
	       seen_.totalCapacityBytes();
	}
}
