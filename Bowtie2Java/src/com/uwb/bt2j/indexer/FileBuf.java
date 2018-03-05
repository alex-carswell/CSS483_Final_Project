package com.uwb.bt2j.indexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class FileBuf <TNameStr, TSeqStr> {
	private static final double BUF_SZ = 1024 * 256;
	private File _in;
	private FileInputStream _inf;
	private InputStream _ins;
	private double _cur;
	private double _buf_sz;
	private boolean _done;
	private byte _buf;
	private double _lastn_cur;
	private char _lastn_buf[];
	
	public static boolean isnewline(int c){
		return c == '\r' || c == '\n';
	}
	
	public static boolean isspace_notnl(int c) {
		return c != ' ' && !isnewline(c);
	}
	
	public FileBuf() {
		init();
	}
	
	public FileBuf(File in) {
		init();
		_in = in;
	}
	
	public FileBuf(FileInputStream inf) {
		init();
		_inf = inf;
	}
	
	public FileBuf(InputStream ins) {
		init();
		_ins = ins;
	}
	
	public boolean isOpen() {
		return _in != null || _inf != null || _ins != null;
	}
	
	public void close() {
		if(_in != null && _ins != System.in) {
			fclose(_in);
		} else if(_inf != null) {
			_inf.close();
		} else if(_zIn != null) {
			gzclose(_zIn);
		}
	}
	
	public int get() {
		int c = peek();
		if(c != -1) {
			_cur++;
			if(_lastn_cur < LASTN_BUF_SZ) _lastn_buf[_lastn_cur++] = c;
		}
		return c;
	}
	
	public boolean eof() {
		return (_cur == _buf_sz) && _done;
	}
	
	public void newFile(File in) {
		_in = in;
		_zIn = null;
		_inf = null;
		_ins = null;
		_cur = BUF_SZ;
		_buf_sz = BUF_SZ;
		_done = false;
	}
	
	public void newFile(gzFile in) {
		_in = null;
		_zIn = in;
		_inf = null;
		_ins = null;
		_cur = BUF_SZ;
		_buf_sz = BUF_SZ;
		_done = false;
	}
	
	public void newFile(FileInputStream __inf) {
		_in = null;
		_zIn = null;
		_inf = __inf;
		_ins = null;
		_cur = BUF_SZ;
		_buf_sz = BUF_SZ;
		_done = false;
	}
	
	public void newFile(InputStream __ins) {
		_in = null;
		_zIn = null;
		_inf = null;
		_ins = __ins;
		_cur = BUF_SZ;
		_buf_sz = BUF_SZ;
		_done = false;
	}
	
	public void reset() {
		if(_inf != null) {
			_inf.clear();
			_inf.seekg(0, std::ios::beg);
		} else if(_ins != null) {
			_ins.clear();
			_ins.seekg(0, std::ios::beg);
		} else if (_zIn != null) {
			gzrewind(_zIn);
		} else {
			rewind(_in);
		}
		_cur = BUF_SZ;
		_buf_sz = BUF_SZ;
		_done = false;
	}
	
	public int peek() {
		if(_cur == _buf_sz) {
			if(_done) {
				// We already exhausted the input stream
				return -1;
			}
			// Read a new buffer's worth of data
			else {
				// Get the next chunk
				if(_inf != null) {
					_inf.read((String)_buf, BUF_SZ);
					_buf_sz = _inf.gcount();
				} else if(_zIn != null) {
					_buf_sz = gzread(_zIn, _buf, BUF_SZ);
				} else if(_ins != null) {
					_ins.read((String)_buf, BUF_SZ);
					_buf_sz = _ins.gcount();
				} else {
					// TODO: consider an _unlocked function
					_buf_sz = fread(_buf, 1, BUF_SZ, _in);
				}
				_cur = 0;
				if(_buf_sz == 0) {
					// Exhausted, and we have nothing to return to the
					// caller
					_done = true;
					return -1;
				} else if(_buf_sz < BUF_SZ) {
					// Exhausted
					_done = true;
				}
			}
		}
		return (int)_buf[_cur];
	}
	
	public double gets(String buf, double len) {
		double stored = 0;
		while(true) {
			int c = get();
			if(c == -1) {
				// End-of-file
				buf[stored] = '\0';
				return stored;
			}
			if(stored == len-1 || isnewline(c)) {
				// End of string
				buf[stored] = '\0';
				// Skip over all end-of-line characters
				int pc = peek();
				while(isnewline(pc)) {
					get(); // discard
					pc = peek();
				}
				// Next get() will be after all newline characters
				return stored;
			}
			buf[stored++] = (char)c;
		}
	}
	
	public double get(String buf, double len) {
		double stored = 0;
		for(double i = 0; i < len; i++) {
			int c = get();
			if(c == -1) return i;
			buf[stored++] = (char)c;
		}
		return len;
	}
	
	public static final double LASTN_BUF_SZ = 1024 * 8;
	
	public int getPastWhitespace() {
		int c;
		while(isspace(c = get()) && c != -1);
		return c;
	}
	
	public int getPastNewline() {
		int c = get();
		while(!isnewline(c) && c != -1) c = get();
		while(isnewline(c)) c = get();
		return c;
	}
	
	public int peekPastNewline() {
		int c = peek();
		while(!isnewline(c) && c != -1) c = get();
		while(isnewline(c)) c = get();
		return c;
	}
	
	public int peekUptoNewline() {
		int c = peek();
		while(!isnewline(c) && c != -1) {
			get(); c = peek();
		}
		while(isnewline(c)) {
			get();
			c = peek();
		}
		return c;
	}
	
	public void parseFastaRecord(TNameStr name, TSeqStr seq) {
		int c;
		if(!gotCaret) {
			// Skip over caret and non-newline whitespace
			c = peek();
			while(isspace_notnl(c) || c == '>') { get(); c = peek(); }
		} else {
			// Skip over non-newline whitespace
			c = peek();
			while(isspace_notnl(c)) { get(); c = peek(); }
		}
		double namecur = 0, seqcur = 0;
		// c is the first character of the fasta name record, or is the first
		// newline character if the name record is empty
		while(!isnewline(c) && c != -1) {
			name[namecur++] = c; get(); c = peek();
		}
		// sequence consists of all the non-whitespace characters between here
		// and the next caret
		while(true) {
			// skip over whitespace
			while(isspace(c)) { get(); c = peek(); }
			// if we see caret or EOF, break
			if(c == '>' || c == -1) break;
			// append and continue
			seq[seqcur++] = c;
			get(); c = peek();
		}
	}
	
	public void parseFastaRecordLength(double nameLen, double seqLen) {
		int c;
		nameLen = seqLen = 0;
		if(!gotCaret) {
			// Skip over caret and non-newline whitespace
			c = peek();
			while(isspace_notnl(c) || c == '>') { get(); c = peek(); }
		} else {
			// Skip over non-newline whitespace
			c = peek();
			while(isspace_notnl(c)) { get(); c = peek(); }
		}
		// c is the first character of the fasta name record, or is the first
		// newline character if the name record is empty
		while(!isnewline(c) && c != -1) {
			nameLen++; get(); c = peek();
		}
		// sequence consists of all the non-whitespace characters between here
		// and the next caret
		while(true) {
			// skip over whitespace
			while(isspace(c)) { get(); c = peek(); }
			// if we see caret or EOF, break
			if(c == '>' || c == -1) break;
			// append and continue
			seqLen++;
			get(); c = peek();
		}
	}
	
	public void resetLastN() {
		_lastn_cur = 0;
	}
	
	public double copyLastN(String buf) {
		_lastn_cur = buf;
		return _lastn_cur;
	}
	
	public String lastN() {
		return _lastn_buf;
	}
	
	public double lastNLen() {
		return _lastn_cur;
	}
	
	private void init() {
		_in = null;
		_zIn = null;
		_inf = null;
		_ins = null;
		_cur = _buf_sz = BUF_SZ;
		_done = false;
		_lastn_cur = 0;
	}
}
