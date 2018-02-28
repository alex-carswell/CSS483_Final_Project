package com.uwb.bt2j.aligner;

public class Interval {
	protected Coord upstream_;
	protected double len_;
	
	public Interval() {
		reset();
	}
	
	public Interval(Coord upstream, double len) {
		init(upstream, len);
	}
	
	public Interval(double rf, double of, int fw, double len) {
		init(rf,of,fw,len);
	}
	
	public void init(Coord upstream, double len) {
		upstream_ = upstream;
		len_ = len;
	}
	
	public void init(double rf, double of, int fw, double len) {
		upstream_.init(rf,of,fw);
		len_ = len;
	}
	
	public void setOff(double of) {
		upstream_.setOff(of);
	}
	
	public void setLen(double len) {
		len_ = len;
	}
	
	public void reset() {
		upstream_.reset();
		len_ = 0;
	}
	
	public Boolean inited() {
		if(upstream_.inited()) {
			return true;
		} else {
			return false;
		}
	}
	
	public void setUpstream(Coord c) {
		upstream_ = c;
	}
	
	public void setLength(double l) {
		len_ = l;
	}
	
	public double ref() {
		return upstream_.ref();
	}
	
	public double off() {
		return upstream_.off();
	}
	
	public double dnoff() {
		return upstream_.off() + len_;
	}
	
	public int orient() {
		return upstream_.orient();
	}
	
	public Coord upstream() {
		return upstream_;
	}
	
	public double len() {
		return len_;
	}
	
	public void adjustOff(double off) {
		upstream_.adjustOff(off);
	}
	
	public Coord downstream() {
		return new Coord(
				upstream_.ref(),
				upstream_.off() + len_,
				upstream_.orient());
	}
	
	public Boolean contains(Coord c) {
		return
				c.ref()    == ref() &&
				c.orient() == orient() &&
				c.off()    >= off() &&
				c.off()    <  dnoff();
	}
	
	public Boolean containsIgnoreOrient(Coord c) {
		return
				c.ref()    == ref() &&
				c.off()    >= off() &&
				c.off()    <  dnoff();
	}
	
	public Boolean contains(Interval c) {
		return
				c.ref()    == ref() &&
				c.orient() == orient() &&
				c.off()    >= off() &&
				c.dnoff()  <= dnoff();
	}
	
	public Boolean containsIgnoreOrient(Interval c) {
		return
				c.ref()    == ref() &&
				c.off()    >= off() &&
				c.dnoff()  <= dnoff();
	}
	
	public Boolean overlaps(Interval c) {
		return
				c.ref()    == upstream_.ref() &&
				c.orient() == upstream_.orient() &&
				((off() <= c.off()   && dnoff() > c.off())   ||
				 (off() <= c.dnoff() && dnoff() > c.dnoff()) ||
				 (c.off() <= off()   && c.dnoff() > off())   ||
				 (c.off() <= dnoff() && c.dnoff() > dnoff()));
	}
	
	public Boolean overlapsIgnoreOrient(Interval c) {
		return
				c.ref()    == upstream_.ref() &&
				((off() <= c.off()   && dnoff() > c.off())   ||
				 (off() <= c.dnoff() && dnoff() > c.dnoff()) ||
				 (c.off() <= off()   && c.dnoff() > off())   ||
				 (c.off() <= dnoff() && c.dnoff() > dnoff()));
	}
}
