package indicators;

import core.StxTS;
import core.StxRec;
import jl.StxJL;
import jl.StxxJL;

import java.lang.Math;
import java.util.ArrayList;

public class StxOBV {

    private StxTS<StxRec> data;
    private StxxJL sjl;

    public StxOBV(StxTS<StxRec> data, StxxJL sjl) {
        this.data = data;
        this.sjl = sjl;
    }

    public float obv(int start, int end) {
	float obv = 0;
	StxRec srs = data.get(start), sre = data.get(end);
	int jls = sjl.recs().find(srs.date, 0);
	int jle = sjl.recs().find(sre.date, 0);
	System.err.printf("start = %d, end = %d, size = %d\n", 
			  start, end, sjl.size());
	StxJL start_rec = sjl.data(jls), end_rec = sjl.data(jle);
	if (start_rec.p && start_rec.p2)
	    obv = start_rec.obv2;
	else {
	    obv = start_rec.obv3;	    
	    if (start_rec.p || start_rec.p2) {
		int piv_state = start_rec.p? start_rec.s: start_rec.s2;
		if ((sjl.up(piv_state) && srs.hiB4Lo()) || 
		    (sjl.dn(piv_state) && !srs.hiB4Lo()))
			obv += start_rec.obv2;
	    } else 
		obv += (start_rec.obv1 + start_rec.obv2);
	}
	if (start == end) 
	    return obv;
	obv += end_rec.obv1;
	if (end_rec.p || end_rec.p2) {
	    StxRec sr = data.get(end);
	    int piv_state = end_rec.p? end_rec.s: end_rec.s2;
	    if ((sjl.up(piv_state) && !sre.hiB4Lo()) || 
		(sjl.dn(piv_state) && sre.hiB4Lo()))
		obv += end_rec.obv2;
	} else 
	    obv += (end_rec.obv2 + end_rec.obv3);
	for(int ix = jls + 1; ix < jle; ix++) {
	    StxJL jlr = sjl.data(ix);
	    obv += (jlr.obv1 + jlr.obv2 + jlr.obv3);
	}
	return obv;
    }
}
