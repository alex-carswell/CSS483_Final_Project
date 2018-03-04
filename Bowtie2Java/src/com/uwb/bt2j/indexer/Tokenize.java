package com.uwb.bt2j.indexer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.StringReader;

public class Tokenize<T> {
	public static <T> void tokenize(String s, String delims, T ss, int max) {
		//string::size_type lastPos = s.find_first_not_of(delims, 0);
		int lastPos = 0;
		String[] tokens = s.substring(lastPos).split(delims);
		int pos = tokens.length > 1 ? tokens[0].length() : -1;
		
		while (pos != -1 || lastPos != -1) {
			ss.push_back(s.substring(lastPos, pos - lastPos));
			String[] t = s.substring(pos).split(delims);
			lastPos = t.length > 1 ? s.indexOf(t[0]) : -1;
			pos = s.indexOf(delims, lastPos);
			if(ss.size() == (max - 1)) {
				pos = -1;
			}
		}
	}
	
	public static void tokenize(String s, String delim, T ss) {
		String[] tokens = s.split(delim);
		for (String u : tokens) {
			ss.push_back(u);
		}
	}
}