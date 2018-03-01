package com.uwb.bt2j.aligner;

public class Coord {
	protected double ref_;
	protected double off_;
	protected int orient_;
	
	public Coord() {
		reset();
	}
	
	public Coord(Coord c) {
		init(c);
	}
	
	public Coord(double rf, double of, int fw) {
		init(rf,of,fw);
	}
	
	public void init(double rf, double of, int fw) {
		ref_ = rf;
		off_ = of;
		orient_ = fw;
	}
	
	public void init(Coord c) {
		ref_ = c.ref_;
		off_ = c.off_;
		orient_ = c.orient_;
	}
	
	public void reset() {
		ref_ = Double.MAX_VALUE;
		off_ = Double.MAX_VALUE;
		orient_ = -1;
	}
	
	public Boolean inited() {
		if(ref_ != Double.MAX_VALUE &&
				   off_ != Double.MAX_VALUE)
				{
					return true;
				}
				return false;
	}
	
	public Boolean fw() {
		return orient_ == 1;
	}
	
	public Boolean within(long len, long inbegin, long inend) {
		return off_ >= inbegin && off_ + len <= inend;
	}
	
	public double ref() {
		return ref_;
	}
	
	public double off() {
		return off_;
	}
	
	public int orient() {
		return orient_;
	}
	
	public void setRef(double id) {
		ref_ = id;
	}
	
	public void setOff(double off) {
		off_ = off;
	}
	
	public void adjustOff(double off) {
		off_ += off;
	}
}