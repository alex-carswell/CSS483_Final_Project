package com.uwb.bt2j.indexer;

public class StringUtils<S, T> {
	
	public static int hash_string(String s) {
		int ret = 0;
		int a = 63689;
		int b = 378551;
		for(int i = 0; i < s.length(); i++) {
			ret = (ret * a) + (int)s.charAt(i);
			if(a == 0) {
				a += b;
			} else {
				a *= b;
			}
			if(a == 0) {
				a += b;
			}
		}
		return ret;
	}
}
