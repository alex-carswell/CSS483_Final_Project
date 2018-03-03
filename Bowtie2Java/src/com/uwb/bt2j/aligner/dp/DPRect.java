package com.uwb.bt2j.aligner.dp;

import java.io.IOException;
import java.io.OutputStream;

import com.uwb.bt2j.aligner.Interval;

public class DPRect {
	
    public double refl;
    public double refr;
  
    public double refl_pretrim;
    public double refr_pretrim;
  
    public double triml;
    public double trimr;
  
    public double corel;
    public double corer;
    public double maxgap;
  
    public void write(OutputStream os) throws IOException {
    	os.write(( refl + "," + refr + "," + refl_pretrim + ","
		   + refr_pretrim + "," + triml + "," + trimr + ","
		   + corel + "," + corer + "," + maxgap).getBytes());
    }
  
    public boolean entirelyTrimmed() {
    	boolean tr = refr < refl;
    	return tr;
    }
    
    public void initIVal(Interval iv) {
    	iv.setOff(refl_pretrim + (long)corel);
		iv.setLen(corer - corel + 1);
    }
	public DPRect(int cat) {
		refl = refr = triml = trimr = corel = corer = 0;
	}
}
