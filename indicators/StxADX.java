import static java.lang.Math.*;
import core.StxCal;
import core.StxRec;
import core.StxTS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StxADX {

    private StxTS<StxRec> data; 
    private int adxd;
    private float tpdm, tmdm, ttr, pdi, mdi, tadx;


    public StxADX(StxTS<StxRec> data) {
        this.data = data;
	init(14);
    }

    public StxADX(StxTS<StxRec> data, int adxd) {
        this.data = data;
	init(adxd);
    }
    
    private void init(int adxd) {
        this.adxd = adxd;
	tpdm = 0;
	tmdm = 0;
	ttr = 0;
	pdi= 0;
	mdi= 0;
	tadx= 0;
    }	
	
    public int adxi(int ix) { return (int) adx(ix); }
    
    public HashMap<String, Float> daily_calc(int ix) {
	HashMap<String, Float> res = new HashMap<String, Float>();
        StxRec sr = data.get(ixx), sr_1 = data.get(ixx - 1);
        /** The indicators to compute */
        float pdm= 0, mdm= 0, tr= 0;
	/** compute +DM and -DM */
	pdm = sr.h - sr_1.h;
	mdm = sr_1.l - sr.l;
	if((pdm < 0) || (pdm < mdm))
	    pdm = 0;
	if((mdm < 0) || (mdm < pdm))
	    mdm = 0;
	/** Compute True Range */
	tr = sr.trueRange(sr_1);
	res["pdm"] = pdm;
	res["mdm"] = mdm;
	res["tr"] = tr;
	return res;
    }
    
    public HashMap<String, Float> adx(int ix) {
	HashMap<String, Float> res = new HashMap<String, Float>();
        /** The indicators to compute */
        float ypdm = 0, ymdm = 0, ytr= 0;
	float dx= 0, ydx= 0, tdx= 0, yadx= 0;
        /** for how many days have we already computed adx? */
        int dx_idx = -1;
        /** utility index */
        int ixx;
        for( ixx = 0; ixx <= ix; ixx++) {
            dx_idx++;
            if(ixx == 0)
                continue;
	    HashMap<String, Float> dly = daily_calc(ixx);

            /** start adding up +DM, -DM and True Range */
            if( dx_idx <= adxd) {
                tpdm += dly.get("pdm");
                tmdm += dly.get("mdm");
                ttr += dly.get("tr");
            }
            /** got first +DI and -DI; start computing DX*/
            if( dx_idx == adxd) {
                if(ttr == 0) return 0;
                pdi = tpdm / ttr * 100;
                mdi = tmdm / ttr * 100;
                di_diff = abs(pdi - mdi);
                di_sum = pdi + mdi;
                tdx = di_diff * 100 / di_sum;
            }
            /** Smooth average +DI, -DI and True Range */
            if(dx_idx > adxd) {
                ypdm = tpdm; 
                ymdm = tmdm; 
                ytr = ttr; 
                tpdm = ypdm - ypdm / adxd + dly["pdm"];
                tmdm = ymdm - ymdm / adxd + dly["mdm"];
                ttr =  ytr -   ytr / adxd +  dly["tr"];
                if((pdi + mdi == 0) || (ttr == 0))
                    return 0;
                pdi = tpdm * 100 / ttr;
                mdi = tmdm * 100 / ttr;
                dx = abs(pdi - mdi) * 100 / (pdi + mdi);
                if(dx_idx < 2 * adxd - 1) /** Start adding up DX */
                    tdx += dx;
                else if(dx_idx == 2 * adxd - 1) { 
                    /** Compute first ADX by averaging DX */
                    tdx += dx;
                    tadx = tdx/ adxd;
                }
                else { /** Smooth average ADX */
                    yadx = tadx;
                    tadx = ((adxd - 1) * yadx + dx) / adxd;
                }
            } /** end ( dx_idx > adxd)) */
//             System.err.printf( "%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f\n", sr.date, sr.h, sr.l, sr.c, tr, pdm, mdm, ttr, tpdm, tmdm, pdi, mdi, di_diff, di_sum, tdx, tadx);
        } /** end fgets */
	
        return tadx * ((pdi > mdi)? 1: -1);
    }

    public String usage() {
	return "java StxADX stk_name <-sd start_date> " +
	    "<-ed end_date> <-d number of adx days>";
    }
    
    public static void main(String[] args) {
	String stk = null, sd = null, ed = null;
	int adxd = 14;
	int ix = 0, s_ix = 0, e_ix = 0;
	while (ix < args.length) {
            arg = args[ix++];
            if(ix == 1)
		stk = arg;
            else if(arg.equals("-sd")) {
                if(ix < args.length)
                    sd = args[ix++];
                else
                    System.err.println("-sd requires a start date");
            } else if(arg.equals("-ed")) {
                if(ix < args.length)
                    ed = args[ix++];
                else
                    System.err.println("-ed requires an end date");
            } else if(arg.equals("-d")) {
                if(ix < args.length)
                    try {
			adxd = Integer.parseInt(args[ix++]);
		    } catch(Exception ex) {
			System.err.println("Failed to parse integer "+
					   "for # of ADX days" +
					   args[ix]);
		    }
                else
                   System.err.println("-d requires # of days");
            } else
		System.err.println("Unknown argument " + arg +
				   "\nUsage: " + usage());
	}
	if(stk == null) {
	    System.err.println("Missing mandatory argument stock name" +
			       "\nUsage: " + usage());
	    System.exit(0);
	}
	StxTS<StxRec> ts = StxTS.loadEod(stk, sd, ed);
	StxADX adx = new StxADX(ts, adxd);
	if(sd == null && ed == null) {
	    // set start and end dates as the beginning and end of the
	    // last gap
	    List<String> start_end = ts.getLastSeries();
	    sd = start_end.get(0);
	    ed = start_end.get(1);
	} else if(sd == null) {
	    List<String> start_end = ts.getSeries(ed);
	    sd = start_end.get(0);
	} else if(ed == null) {
	    List<String> start_end = ts.getSeries(ed);
	    ed = start_end.get(1);
	}
	s_ix = ts.setDay(sd);
	if(pos - ts.start() < 2 * adxd) {
	    s_ix = 2 * adxd + ts.start();
	    sd = ts.get(s_ix).date;
	    s_ix = ts.setDay(sd);
	}
	e_ix = ts.find(ed, 0);
	List<Float> adx_indicators = adx.adx(s_ix);
	for(int ix = s_ix + 1; ix <= e_ix; ix++)
	    adx_indicators
    }
}
