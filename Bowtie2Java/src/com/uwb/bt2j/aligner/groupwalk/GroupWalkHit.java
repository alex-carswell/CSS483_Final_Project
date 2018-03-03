package com.uwb.bt2j.aligner.groupwalk;

import com.uwb.bt2j.util.IndexTypes;
import com.uwb.bt2j.util.types.EList;

import javafx.util.Pair;

public class GroupWalkHit <T>{
	public EList<Pair<Long,Long>> fmap;
	public long offidx;
	public boolean fw;
	public long range;
	public long len;
	
	protected EList<Boolean> reported_;
	protected int nrep_;
	
	public GroupWalkHit() {
		fmap = new EList(0, 4);
		offidx = IndexTypes.OFF_MASK;
		fw = false;
		range = IndexTypes.OFF_MASK;
		len = IndexTypes.OFF_MASK;
		reported_ = new EList(0,4);
		nrep_ = 0;
	}
	
	public void init(
			SARangeWithOffs<T> sa,
			long oi,
			boolean f,
			long r){
		nrep_ = 0;
		offidx = oi;
		fw = f;
		range = r;
		len = (long)sa.len;
		reported_.resize(sa.offs.size());
		reported_.fill(false);
		fmap.resize(sa.offs.size());
		fmap.fill(new Pair(IndexTypes.OFF_MASK, IndexTypes.OFF_MASK));
	}
	
	public void reset() {
		reported_.clear();
		fmap.clear();
		nrep_ = 0;
		offidx = IndexTypes.OFF_MASK;
		fw = false;
		range = IndexTypes.OFF_MASK;
		len = IndexTypes.OFF_MASK;
	}
	
	public void setReport(int i) {
		reported_[i] = true;
		nrep_++;
	}
	
	public boolean reported(int i) {
		return reported_[i];
	}
	
	public boolean done() {
		return nrep_ == reported_.size();
	}
}