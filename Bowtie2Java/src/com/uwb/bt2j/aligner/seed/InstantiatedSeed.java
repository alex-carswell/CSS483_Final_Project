package com.uwb.bt2j.aligner.seed;

import com.uwb.bt2j.util.strings.BTDnaString;
import com.uwb.bt2j.util.strings.BTString;
import com.uwb.bt2j.util.types.EList;

import javafx.util.Pair;

public class InstantiatedSeed {
	public EList<Integer> steps;
	public EList<Pair<Integer,Integer>> zones;
	public BTDnaString seq;
	public BTString qual;
	public Constraint cons[];
	public Constraint overall;
	public int maxjump;
	public int seedoff;
	public int seedoffidx;
	public int seedtypeidx;
	public boolean fw;
	public boolean nfiltered;
	public Seed s;
	
	public InstantiatedSeed() {
		steps = new EList(5);
		zones = new EList(5);
	}
}
