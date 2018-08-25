package gui;

public class StxTrade {
    String cp, expiry, in_date, crt_date;
    float strike, in_spot, in_range, crt_spot, in_bid, in_ask, crt_bid, crt_ask;
    int num_contracts;
    
    public StxTrade(String cp, String expiry, String in_date, float strike,
		    float in_spot, float in_range, float in_bid, float in_ask,
		    float capital) {
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
    }

    public String key() {
	return String.format("%s_%s_%.2f", cp, expiry, strike);
    }

    public void update(String date, float spot, float bid, float ask) {
	crt_date = date;
	crt_spot = spot;
	crt_bid = bid;
	crt_ask = ask;
    }

    public String ui_entry(boolean invisible) {
	StringBuilder sb = new StringBuilder(cp);
	sb.append(" ").append(String.format("%6.2f expires in ", strike)).
	    append(String.format("%2d ", StxCal.numBusDays(crt_date, expiry))).
	    append("IN Spot: %6.2f, In Opt: %5.2f, "
	if(invisible) 
    }

    public String log_entry() {

    }
}
