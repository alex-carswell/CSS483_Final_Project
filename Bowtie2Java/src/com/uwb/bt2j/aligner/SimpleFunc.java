package com.uwb.bt2j.aligner;

public class SimpleFunc<T> {
	public static final byte SIMPLE_FUNC_CONST = 1;
	public static final byte SIMPLE_FUNC_LINEAR = 2;
	public static final byte SIMPLE_FUNC_SQRT = 3;
	public static final byte SIMPLE_FUNC_LOG = 4;
	
	protected int type_;
	protected double I_, X_, C_, L_;
	
	public SimpleFunc() {
		type_ = 0;
		I_ = X_ = C_ = L_ = 0.0;
	}
	
	public SimpleFunc(int type, double I, double X, double C, double L) {
		init(type, I, X, C, L);
	}
	
	public void init(int type, double I, double X, double C, double L) {
		type_ = type; I_ = I; X_ = X; C_ = C; L_ = L;
	}
	
	public void init(int type, double C, double L) {
		type_ = type; C_ = C; L_ = L;
		I_ = -Double.MAX_VALUE;
		X_ = Double.MAX_VALUE;
	}
	
	public void setType(int type) {
		type_ = type;
	}
	
	public void setMin(double mn) {
		I_ = mn;
	}
	
	public void setMax(double mx) {
		X_ = mx;
	}
	
	public void setConst(double co) {
		C_ = co;
	}
	
	public void setCoeff(double ce) {
		L_ = ce;
	}
	
	public int getType() {
		return type_;
	}
	
	public double getMin() {
		return I_;
	}
	
	public double getMax() {
		return X_;
	}
	
	public double getConst() {
		return C_;
	}
	
	public double getCoeff() {
		return L_;
	}
	
	public void mult(double x) {
		if(I_ < Double.MAX_VALUE){
			I_ *= x; X_ *= x; C_ *= x; L_ *= x;
		}
	}
	
	public Boolean initialized() {
		return type_ != 0;
	}
	
	public Boolean alwaysPositive() {
		return f<int>(1.0) > 0 && ((Boolean)SIMPLE_FUNC_CONST || L_ >= 0.0);
	}
	
	public T f(double x) {
		double X;
		if(type_ == SIMPLE_FUNC_CONST) {
			X = 0.0;
		} else if(type_ == SIMPLE_FUNC_LINEAR) {
			X = x;
		} else if(type_ == SIMPLE_FUNC_SQRT) {
			X = Math.sqrt(x);
		} else if(type_ == SIMPLE_FUNC_LOG) {
			X = Math.log(x);
		}
		double ret = Math.max(I_, Math.min(X_, C_ + L_ * X));
		if(ret == Double.MAX_VALUE) {
			return std::numeric_limits<T>::max();
		} else if(ret == Double.MIN_VALUE) {
			return std::numeric_limits<T>::min();
		} else {
			return (T)ret;
		}
	}
	
	public int parseType(String otype) {
		String type = otype;
		if(type == "C" || type == "Constant") {
			return SIMPLE_FUNC_CONST;
		} else if(type == "L" || type == "Linear") {
			return SIMPLE_FUNC_LINEAR;
		} else if(type == "S" || type == "Sqrt") {
			return SIMPLE_FUNC_SQRT;
		} else if(type == "G" || type == "Log") {
			return SIMPLE_FUNC_LOG;
		}
		System.err.println("Error: Bad function type '" + otype
				  + "'.  Should be C (constant), L (linear), "
				  + "S (square root) or G (natural log)." + "\n");
		return 0;
	}
	
	public SimpleFunc parse(String s, double defaultConst, double defaultLinear, double defaultMin, double defaultMax) {
		// Separate value into comma-separated tokens
		EList<String> ctoks(MISC_CAT);
		String ctok;
		istringstream css(s);
		SimpleFunc fv;
		while(getline(css, ctok, ',')) {
			ctoks.push_back(ctok);
		}
		if(ctoks.size() >= 1) {
			fv.setType(parseType(ctoks[0]));
		}
		if(ctoks.size() >= 2) {
			double co;
			istringstream tmpss(ctoks[1]);
			tmpss >> co;
			fv.setConst(co);
		} else {
			fv.setConst(defaultConst);
		}
		if(ctoks.size() >= 3) {
			double ce;
			istringstream tmpss(ctoks[2]);
			tmpss >> ce;
			fv.setCoeff(ce);
		} else {
			fv.setCoeff(defaultLinear);
		}
		if(ctoks.size() >= 4) {
			double mn;
			istringstream tmpss(ctoks[3]);
			tmpss >> mn;
			fv.setMin(mn);
		} else {
			fv.setMin(defaultMin);
		}
		if(ctoks.size() >= 5) {
			double mx;
			istringstream tmpss(ctoks[4]);
			tmpss >> mx;
			fv.setMax(mx);
		} else {
			fv.setMax(defaultMax);
		}
		return fv;
	}
}