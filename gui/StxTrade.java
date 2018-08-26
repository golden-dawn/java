package gui;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import core.StxCal;
import core.StxDB;

public class StxTrade {
    String stk, cp, expiry, in_date, crt_date;
    float strike, in_spot, in_range, crt_spot, in_bid, in_ask, crt_bid, crt_ask;
    int num_contracts;
    boolean active;
    
    public StxTrade(String stk, String cp, String expiry, String in_date,
		    float strike, float in_spot, float in_range, float in_bid,
		    float in_ask, float capital) {
	this.stk = stk;
	this.cp = cp;
	this.expiry = expiry;
	this.in_date = in_date;
	this.strike = strike;
	this.in_spot = in_spot;
	this.in_range = in_range;
	crt_spot = in_spot;
	this.in_bid = in_bid;
	this.in_ask = in_ask;
	crt_bid = in_bid;
	crt_ask = in_ask;
	num_contracts = (int) (0.01 * capital / in_ask);
	crt_date = in_date;
	active = true;
    }

    public String key() {
	return String.format("%s_%s_%s_%.2f", stk, cp, expiry, strike);
    }

    public void update(String date, float spot) {
	if(active) {
	    crt_date = date;
	    crt_spot = spot;
	    StringBuilder q1 =
		new StringBuilder("SELECT bid, ask FROM options WHERE und='");
	    q1.append(stk).append("' AND expiry='").append(expiry).
		append("' AND date='").append(date).append("' AND cp='").
		append(cp).append("' and strike=").append(strike);
	    try {
		StxDB sdb = new StxDB("stx_ng");
		ResultSet rset = sdb.get(q1.toString());
		crt_bid = rset.getFloat(1);
		crt_ask = rset.getFloat(2);
	    } catch( Exception ex) {
		System.err.println("Failed to bid/ask for " + key() + ":");
		ex.printStackTrace(System.err);
	    }
	}
	if(StxCal.numBusDays(crt_date, expiry) == 0)
	    active = false;
    }

    public void close(String log_fname) {
	active = false;
	try {
	    log_entry(log_fname);
	} catch(IOException ioe) {
	    System.err.printf("Failed to log trade %s on date %s\n",
			      key(), crt_date);
	    ioe.printStackTrace(System.err);
	}
    }

    public boolean is_active() { return active; }
    
    public String ui_entry() {
	int sgn = cp.equals("c")? 1: -1;
	StringBuilder sb = new StringBuilder(cp.equals("c")? "CALL ": " PUT ");
	sb.append(String.format("%6.2f age %2d expires in %2d ", strike,
				StxCal.numBusDays(in_date, crt_date),
				StxCal.numBusDays(crt_date, expiry))).
	    append(String.format("Spot: %6.2f=>%6.2f, PL:%5.2f ", in_spot,
				 crt_spot, 0.5 * (sgn * (crt_spot - in_spot) /
						  in_range - 1))).
	    append(String.format("Opt: %5.2f=>(B:%5.2f, A:%5.2f), PL:%6.0f\n",
				 in_ask, crt_bid, crt_ask,
				 100 * num_contracts * (crt_bid - in_ask)));
	return sb.toString();
    }

    public void log_entry(String log_fname) throws IOException {
	int sgn = cp.equals("c")? 1: -1;
	StringBuffer sb = new StringBuffer();
	float spot_pnl = (sgn * (crt_spot - in_spot) / in_range - 1) / 2;
	if(spot_pnl < -1) spot_pnl = -1;
	float opt_pnl = 100 * num_contracts * (crt_bid - in_ask);

	sb.append(stk).append(',').append(cp.equals('c')? "CALL": "PUT").
	    append(',').append(in_date).append(',').append(in_spot).append(',').
	    append(String.format("%.2f", in_range)).append(',').
	    append(crt_date).append(",").append(crt_spot).append(",").
	    append(String.format("%.2f", spot_pnl)).append(",").
	    append(in_date.substring(0, 4)).append(",");
	if(spot_pnl > 0)
	    sb.append(String.format("1,%.2f,,", spot_pnl));
	else
	    sb.append(String.format("0,,%.2f,", spot_pnl));
	sb.append(strike).append(",").append(expiry).append(",").
	    append(in_ask).append(",").append(crt_bid).append(",").
	    append(num_contracts).append(",").
	    append(String.format("%.2f", opt_pnl)).append(",");
	if(opt_pnl > 0)
	    sb.append(String.format("1,%.2f,,", opt_pnl));
	else
	    sb.append(String.format("0,,%.2f,", opt_pnl));
	
	PrintWriter pw = new PrintWriter(new FileWriter(log_fname, true));
	pw.println(sb.toString());
	pw.close();	
    }
}