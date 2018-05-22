package indicators;

import core.StxTS;
import core.StxRec;

import java.lang.Math;
import java.util.ArrayList;

public class StxUDV<T extends StxRec> {

    private StxTS<StxRec> data;

    public StxUDV(StxTS<StxRec> data) {
        this.data = data;
    }

    public ArrayList<Float> udv(int start, int end) {
	ArrayList<Float> res = new ArrayList<Float>();
	float tot_vol = 0, sum_vol = 0;
	float tot_day = 0, sum_day = 0;
	float diff;
	int ix = start;
	while(ix <= end) {
	    StxRec sr = data.get(ix);
	    diff = sr.c - data.get(ix - 1).c;
	    if(diff > 0)
		sum_day+= 1;
	    else
		sum_day -= 1;
	    tot_day += 1;
	    tot_vol += sr.v * Math.abs(diff);
	    sum_vol += sr.v * diff;
	    ix++;
	}
	res.add(sum_vol / tot_vol);
	res.add(sum_day / tot_day);
	return res;
    }
}
