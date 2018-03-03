package com.uwb.bt2j.aligner.seed;

import com.uwb.bt2j.aligner.Scoring;
import com.uwb.bt2j.aligner.SimpleFunc;

public class Constraint {
	public int edits;      // # edits permitted
	public int mms;        // # mismatches permitted
	public int ins;        // # insertions permitted
	public int dels;       // # deletions permitted
	public int penalty;    // penalty total permitted
	public int editsCeil;  // <= this many edits can be left at the end
	public int mmsCeil;    // <= this many mismatches can be left at the end
	public int insCeil;    // <= this many inserts can be left at the end
	public int delsCeil;   // <= this many deletions can be left at the end
	public int penaltyCeil;// <= this much leftover penalty can be left at the end
	public SimpleFunc penFunc;// penalty function; function of read len
	public boolean instantiated; // whether constraint is instantiated w/r/t read len
	
	public Constraint() {
		init();
	}
	
	public void init() {
		edits = mms = ins = dels = penalty = editsCeil = mmsCeil =
				insCeil = delsCeil = penaltyCeil = Integer.MAX_VALUE;
				penFunc.reset();
				instantiated = false;
	}
	
	public boolean mustMatch() {
		return (mms == 0 && edits == 0) ||
		        penalty == 0 ||
		       (mms == 0 && dels == 0 && ins == 0);
	}
	
	public boolean canMismatch(int q, Scoring cm) {
		return (mms > 0 || edits > 0) &&
			       penalty >= cm.mm(q);
	}
	
	public boolean canN(int q, Scoring cm) {
		return (mms > 0 || edits > 0) &&
			       penalty >= cm.n(q);
	}
	
	public boolean canMismatch() {
		return (mms > 0 || edits > 0) && penalty > 0;
	}
	
	public boolean canN() {
		return (mms > 0 || edits > 0);
	}
	
	public boolean canDelete(int ex, Scoring cm) {
		return (dels > 0 && edits > 0) &&
			       penalty >= cm.del(ex);
	}
	
	public boolean canDelete() {
		return (dels > 0 || edits > 0) &&
			       penalty > 0;
	}
	
	public boolean canInsert(int ex, Scoring cm) {
		return (ins > 0 || edits > 0) &&
			       penalty >= cm.ins(ex);
	}
	
	public boolean canInsert() {
		return (ins > 0 || edits > 0) &&
			       penalty > 0;
	}
	
	public boolean canGap() {
		return ((ins > 0 || dels > 0) || edits > 0) && penalty > 0;
	}
	
	public void chargeMismatch(int q, Scoring cm) {
		if(mms == 0) { edits--; }
		else mms--;
		penalty -= cm.mm(q);
	}
	
	public void chargeN(int q, Scoring cm) {
		if(mms == 0) { edits--; }
		else mms--;
		penalty -= cm.n(q);
	}
	
	public void chargeDelete(int ex, Scoring cm) {
		dels--;
		edits--;
		penalty -= cm.del(ex);
	}
	
	public void chargeInsert(int ex, Scoring cm) {
		ins--;
		edits--;
		penalty -= cm.ins(ex);
	}
	
	public boolean acceptable() {
		return edits   <= editsCeil &&
			       mms     <= mmsCeil   &&
			       ins     <= insCeil   &&
			       dels    <= delsCeil  &&
			       penalty <= penaltyCeil;
	}
	
	public static int instantiate(double rdlen, SimpleFunc func) {
		return func.f<Integer>((double)rdlen);
	}
	
	public void instantiate(double rdlen) {
		if(penFunc.initialized()) {
			penalty = Constraint.instantiate(rdlen, penFunc);
		}
		instantiated = true;
	}
	
	public static Constraint exact() {
		Constraint c = new Constraint();
		c.edits = c.mms = c.ins = c.dels = c.penalty = 0;
		return c;
	}
	
	public Constraint penaltyBased(int pen) {
		Constraint c = new Constraint();
		c.penalty = pen;
		return c;
	}
	
	public Constraint penaltyFuncBased(SimpleFunc f) {
		Constraint c = new Constraint();
		c.penFunc = f;
		return c;
	}
	
	public static Constraint mmBased(int mms) {
		Constraint c = new Constraint();
		c.mms = mms;
		c.edits = c.dels = c.ins = 0;
		return c;
	}
	
	public Constraint editBased(int edits) {
		Constraint c = new Constraint();
		c.edits = edits;
		c.dels = c.ins = c.mms = 0;
		return c;
	}
}
