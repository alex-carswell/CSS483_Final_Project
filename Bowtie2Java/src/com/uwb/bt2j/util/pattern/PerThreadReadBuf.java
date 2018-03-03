package com.uwb.bt2j.util.pattern;

import com.uwb.bt2j.aligner.Read;
import com.uwb.bt2j.util.types.EList;

public class PerThreadReadBuf {
	double max_buf_; // max # reads to read into buffer at once
	EList<Read> bufa_;	   // Read buffer for mate as
	EList<Read> bufb_;	   // Read buffer for mate bs
	int cur_buf_;	   // Read buffer currently active
	long rdid_;		   // index of read at offset 0 of bufa_/bufb_
	public PerThreadReadBuf(double max_buf) {
		max_buf_ = max_buf;
		bufa_ = max_buf;
		bufb_ = max_buf;
		
		bufa_.resize(max_buf);
		bufb_.resize(max_buf);
		reset();
	}
	
	public Read read_a() {
		return bufa_[cur_buf_];
	}
	
	public Read read_b() {
		return bufb_[cur_buf_];
	}
	
	public long rdid() {
		return rdid_ + cur_buf_;
	}
	
	public void reset() {
		cur_buf_ = bufa_.size();
		for(int i = 0; i < max_buf_; i++) {
			bufa_[i].reset();
			bufb_[i].reset();
		}
		rdid_ = Long.MAX_VALUE;
	}
	
	public void next() {
		cur_buf_++;
	}
	
	public boolean exhausted() {
		return cur_buf_ >= bufa_.size()-1 || bufa_[cur_buf_+1].readOrigBuf.empty();
	}
	
	public void init() {
		cur_buf_ = 0;
	}
	
	public void setReadId(long rdid) {
		rdid_ = rdid;
	}
}