package com.uwb.bt2j.inspector;
import com.uwb.bt2j.util.*;

class Bowtie2Inspector {
	public static Boolean showVersion = false;
	public int verbose = 0;
	public static int names_only = 0;
	public static int summarize_only = 0;
	public static int across = 60;
	public static Boolean refFromEbwt = false;
	public static String wrapper;
	public static final String short_options = "vhnsea:";
	
	enum ARGS {
		ARG_VERSION(256),
		ARG_WRAPPER(257),
		ARG_USAGE(258);
		
		private int x;
		ARGS(int y){this.x = y;}
	};
	
	public static void printUsage() {
		
	}
	
	public static int parseInt() {
		return 0;
	}
	
	public static void parseOptions() {

	}
	
	public static void printFastaRecord() {
		
	}
	
	public static void printRefSequence() {
		
	}
	
	public static void printRefSequences() {
		
	}
	
	public static void printIndexSequences() {
		
	}
	
	public static void printIndexSequencesNames() {
		
	}
	
	public static void printIndexSummary() {
		
	}
	
	public static void driver() {
		
	}
	
	public static void main(String[] args){
		
	}
}