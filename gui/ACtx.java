package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import java.nio.charset.Charset;
import java.nio.file.Files;

import core.StxCal;
import core.StxDB;
import core.StxRec;
import jl.StxxJL;

// TODO:
//v1. Add a strike and an expiry text fields, as well as an OK button
//v2. Replace BUY with CALL and SELL with PUT
//v3. Get a text area with the option prices for the selected strike and expiry
//v4. Write SQL code that gets the options from the database
// 5. Create another text area that shows the current open trades
// 6. Create a trading activity label
//v7. Replace the strike and expiry labels with dropdown menus
//v8. Adjust the logging to capture both spots, as well as option prices

public class ACtx implements KeyListener, ActionListener {
    static JFrame jf; 
    private JTabbedPane jtp_jl;
    private JPanel jpu, jp_trd;
    private JTextField etf, ntf, dbetf, dbstf, jlf1, jlf2, jlp;
    private JButton pick_stk;
    private JButton call, put, c_call, c_put;
    private JComboBox exp, strike, capital, chart_scale;
    private JCheckBox invisible, skip_trade;
    private JLDisplay jld1, jld2, opts, trades;
    private JLabel jlfl1, jlfl2, trade_status;
    private int resX= 1366, resY= 768, yr;
    private Chart chrt;
    private StxxJL jl1, jl2, jl3;
    private List<StxTrade> trade_list = new ArrayList<StxTrade>();
    private HashMap<String, Integer> trade_ix = new HashMap<String, Integer>();
    // <cp, expiry, strike> =>
    // <in_date, in_price, in_range, in_opt_px, crt_bid, crt_ask, crt_spot
    String last_scale= "3M";
    private String trade_type = "", trade_date;
    private float trade_price, trade_daily_range;
    List<String[]> entries = new ArrayList<String[]>();
    int crt_pos = 0;
    TreeMap<String, Integer> trend_map= new TreeMap<String, Integer>();
    int idx= -1, num_tabs = 0;
    String log_fname, last_opt_date;

    public ACtx() {
        
        Calendar c= new GregorianCalendar();
        yr= c.get( Calendar.YEAR);
	last_opt_date = String.format("%d-12-31", (yr - 1));
        String d= String.format( "%d-%02d-%02d", yr,
                                 1+ c.get( Calendar.MONTH),
                                 c.get( Calendar.DAY_OF_MONTH));
        new StxCal( yr+ 2);
        if( StxCal.isBusDay( d)== false)
            d= StxCal.prevBusDay( d);
	log_fname = String.format("../trades/%s.txt", d);
        jf= new JFrame( "ACTX");
        etf= new JTextField( d); etf.setCaretColor( Color.white);
        etf.setName( "ETF"); etf.addKeyListener( this);
        ntf= new JTextField(); ntf.setCaretColor( Color.white);
        ntf.setName( "NTF"); ntf.addKeyListener( this);
        dbetf= new JTextField( "eods"); dbetf.setCaretColor( Color.white);
        dbstf= new JTextField( "dividends"); dbstf.setCaretColor( Color.white);
        jlf1 = new JTextField( "1.0"); jlf1.setCaretColor( Color.white);
        jlf2 = new JTextField( "2.0"); jlf2.setCaretColor( Color.white);
        jlfl1 = new JLabel("Factor: "+ jlf1.getText());
        jlfl2 = new JLabel("Factor: "+ jlf2.getText());
        jlp= new JTextField( "16"); jlp.setCaretColor( Color.white);
	invisible = new JCheckBox("Invisible");
	invisible.setSelected(true);
        jpu= new JPanel( null);
        jpu.setBackground( Color.black);
        jpu.setForeground( Color.lightGray);
	addC( jpu, ntf, 555, 7, 60, 25);
        addC( jpu, etf, 615, 7, 75, 25);
        etf.setForeground(invisible.isSelected()? Color.black: Color.lightGray);
        addC( jpu, invisible, 666, 7, 70, 25);
        pick_stk = new JButton("R");
        pick_stk.addActionListener(this);
        addC( jpu, pick_stk, 5, 7, 50, 25);
	skip_trade = new JCheckBox("Skip");
	skip_trade.setSelected(false);
        addC( jpu, skip_trade, 60, 7, 80, 25);
	chart_scale = new JComboBox<String>();
	chart_scale.setEditable(false);
	chart_scale.addItem("1M");
	chart_scale.addItem("3M");
	chart_scale.addItem("6M");
	chart_scale.addItem("1Y");
	chart_scale.addItem("JL");
	chart_scale.addItem("2Y");
	chart_scale.addItem("3Y");
	chart_scale.addItem("5Y");
	chart_scale.addItem("All");
	chart_scale.setSelectedIndex(1);
	chart_scale.addActionListener(this);
	addC(jpu, chart_scale, 15, 35, 80, 20);
        addC( jpu, new JLabel("JL: "), 95, 35, 25, 20);
        addC( jpu, jlf1, 120, 35, 50, 20);
        addC( jpu, jlf2, 170, 35, 50, 20);
        addC( jpu, jlp,  220, 35, 50, 20);
	
        jp_trd = new JPanel(null);
        jp_trd.setBackground(Color.black);
        jp_trd.setForeground(Color.lightGray);
        call = new JButton("CALL"); call.addActionListener(this);
        put = new JButton("PUT"); put.addActionListener(this);
        c_call = new JButton("CLOSE CALL"); c_call.addActionListener(this);
        c_put = new JButton("CLOSE PUT"); c_put.addActionListener(this);
	strike = new JComboBox<Float>();
	strike.setEditable(true);
	strike.getEditor().getEditorComponent().addKeyListener
	    (new KeyAdapter() {
		    @Override
		    public void keyReleased(KeyEvent event) {
			// System.err.printf("event.getKeyCode() = %d\n",
			// 		   event.getKeyCode());
			if (event.getKeyCode() == KeyEvent.VK_F1) {
			    etf.requestFocusInWindow();
			}
		    }
		});	
	exp = new JComboBox<String>();
	exp.setEditable(true);
	exp.getEditor().getEditorComponent().addKeyListener
	    (new KeyAdapter() {
		    @Override
		    public void keyReleased(KeyEvent event) {
			if (event.getKeyCode() == KeyEvent.VK_F1) {
			    etf.requestFocusInWindow();
			}
		    }
		});	
	capital = new JComboBox<Float>();
	capital.getEditor().getEditorComponent().addKeyListener
	    (new KeyAdapter() {
		    @Override
		    public void keyReleased(KeyEvent event) {
			if (event.getKeyCode() == KeyEvent.VK_F1) {
			    etf.requestFocusInWindow();
			}
		    }
		});	
	capital.setEditable(true);
	capital.addItem(125);
	capital.addItem(250);
	capital.addItem(500);
	capital.addItem(750);
	capital.addItem(1000);
	capital.addItem(1500);
	capital.addItem(2000);
	capital.setSelectedIndex(2);
	opts = new JLDisplay(600, 100, 12, invisible.isSelected());
	trades = new JLDisplay(600, 100, 12, invisible.isSelected());
	trade_status = new JLabel("GETTING STARTED . . .");
        addC(jpu, call, 5, 55, 80, 15);
        addC(jpu, put, 85, 55, 80, 15);
        addC(jpu, c_call, 5, 70, 120, 15);
        addC(jpu, c_put, 130, 70, 120, 15);
	addC(jpu, strike, 5, 85, 70, 20);
	addC(jpu, exp, 75, 85, 120, 20);
	addC(jpu, capital, 195, 85, 70, 20);
	addC(jpu, trade_status, 5, 105, 150, 15);
	addC(jpu, opts, 5, 120, 305, 230);
	addC(jpu, trades, 5, 350, 305, 230);
	
        int hd11= 1080;
        addC( jp_trd, jlfl1, 5, 0, 80, 20);
        jld1= new JLDisplay( 540, 200, 12, invisible.isSelected());
        addC( jp_trd, jld1, 0, 20, 540, 200);
        addC( jp_trd, jlfl2, 540, 0, 80, 20);
        jld2= new JLDisplay(540, 200, 12, invisible.isSelected());
        addC( jp_trd, jld2, 540, 20, 540, 200);
        jtp_jl= new JTabbedPane( JTabbedPane.LEFT);

	int vert_div = resY - 240;
        JSplitPane jspv= new JSplitPane( JSplitPane.VERTICAL_SPLIT,
                                         jtp_jl, jp_trd);
        jspv.setOneTouchExpandable(true);
        jspv.setDividerLocation(vert_div);

	jtp_jl.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    Chart cc = (Chart)jtp_jl.getSelectedComponent();
		    if((cc != null) && (ntf.getText() != cc.stk_name)) {
			ntf.setText(cc.stk_name);
			etf.setText(cc.ts.currentDate());
		    }
		}
	    });
	
        JSplitPane jspu= new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,
                                         jspv, jpu);
        jspu.setOneTouchExpandable( true);
        jspu.setDividerLocation( hd11);
        jf.pack();
        jf.setSize( resX, resY);
        jf.getContentPane().add( jspu);
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }
    public void keyPressed( KeyEvent e) {
        try {
	    //  F1 = 112
	    //  F2 = 113
	    //  F3 = 114
	    //  F4 = 115
	    //  F5 = 116
	    //  F6 = 117
	    //  F7 = 118
	    //  F8 = 119
	    //  F9 = 120
	    // F10 = 121
	    // F11 = 122
	    // F12 = 123
            int cd= e.getKeyCode(); String src= e.getComponent().getName();
	    // System.err.printf("cd = %d, src = %s\n", cd, src);
            if( src.equals( "ETF")) {
                String ed= etf.getText();
                // if( cd== 40) etf.setText( StxCal.prevBusDay( ed));
                if( cd== 38) etf.setText( StxCal.nextBusDay( ed));
                if(cd == 34) {
                    decreaseScale();
                    go();
                }
                if(cd == 33) {
                    increaseScale();
                    go();
                }
                if( cd== 10) go();
		if(cd >= 112 && cd <= 123)
		    handle_function_keys(cd);
            } else if(src.equals("NTF")) {
                if(cd== 10) go();
                if(cd == 38) {
		    int sel_ix = jtp_jl.getSelectedIndex() + 1;
		    if(sel_ix >= jtp_jl.getTabCount())
			sel_ix = 0;
		    jtp_jl.setSelectedIndex(sel_ix);
                    go();
                }
                if(cd == 40) {
		    int sel_ix = jtp_jl.getSelectedIndex() - 1;
		    if(sel_ix < 0)
			sel_ix = jtp_jl.getTabCount() - 1;
		    jtp_jl.setSelectedIndex(sel_ix);
                    go();
                }
                if(cd == 34) {
                    decreaseScale();
                    go();
                }
                if(cd == 33) {
                    increaseScale();
                    go();
                }
		if(cd >= 112 && cd <= 123)
		    handle_function_keys(cd);
                // if(cd == 112) {
                //     int sz = entries.size();
                //     if(sz == 0) return;
                //     crt_pos = 0;
                //     while(crt_pos < sz && entries.get(crt_pos)[0].compareTo
                //           (ntf.getText()) != 0)
                //         ++crt_pos;
                //     if(crt_pos == sz) crt_pos = 0;
                //     ntf.setText(entries.get(crt_pos)[0]);
                //     etf.setText(entries.get(crt_pos)[1]);
                //     go();
                        
                // }
            }
        } catch( Exception exc) {
            exc.printStackTrace( System.err);
        }
    }

    private void handle_function_keys(int cd) {
	if(cd == 112)
	    openTrade("CALL");
	else if(cd == 113)
	    openTrade("PUT");
	else if(cd == 114)
	    closeTrade("CLOSE CALL");
	else if(cd == 115)
	    closeTrade("CLOSE PUT");
	else if(cd == 116)
	    strike.requestFocusInWindow();
	else if(cd == 117)
	    exp.requestFocusInWindow();
	else if(cd == 118)
	    capital.requestFocusInWindow();
	else if(cd == 119)
	    skip_trade.setSelected(!skip_trade.isSelected());
	else if(cd == 123) {
	    int sel_ix = jtp_jl.getSelectedIndex();
	    jtp_jl.remove(sel_ix);
	    if(sel_ix >= jtp_jl.getTabCount())
		sel_ix = jtp_jl.getTabCount() - 1;
	    jtp_jl.setSelectedIndex(sel_ix);
	}
    }
    
    private void decreaseScale() {
        switch(last_scale) {
        case "1M":
        case "3M":
            last_scale = "1M";
        break;
        case "6M":
            last_scale = "3M";
            break;
        case "1Y":
            last_scale = "6M";
            break;
        case "JL":
            last_scale = "1Y";
            break;
        case "2Y":
            last_scale = "JL";
            break;
        case "3Y":
            last_scale = "2Y";
            break;
        case "5Y":
            last_scale = "3Y";
            break;
        case "All":
            last_scale = "5Y";
            break;
        }
    }

    private void increaseScale() {
        switch(last_scale) {
        case "1M":
            last_scale = "3M";
            break;
        case "3M":
            last_scale = "6M";
            break;
        case "6M":
            last_scale = "1Y";
            break;
        case "1Y":
            last_scale = "JL";
            break;
        case "JL":
            last_scale = "2Y";
            break;
        case "2Y":
            last_scale = "3Y";
            break;
        case "3Y":
            last_scale = "5Y";
            break;
        case "5Y":
        case "All":
            last_scale = "All";
        break;
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    public void go() throws Exception {

        String jls, s, e= etf.getText();
        String n= ntf.getText();
        int idx, w= 20, p= Integer.parseInt( jlp.getText());
        float f1= Float.parseFloat( jlf1.getText());
        float f2= Float.parseFloat( jlf2.getText());
	etf.setForeground(invisible.isSelected()? Color.black: Color.lightGray);
	ntf.setForeground(invisible.isSelected()? Color.black: Color.lightGray);
        jlfl1.setText("Factor: "+ jlf1.getText());
        jlfl2.setText("Factor: "+ jlf2.getText());
        if( StxCal.isBusDay( e)== false)
            e= StxCal.nextBusDay( e);
        jls= ( StxCal.year( e)- 2)+ "-01-01";
        s= "1901-01-02";
	for(idx = 0; idx < jtp_jl.getTabCount(); idx++) {
	    Chart cc = (Chart)jtp_jl.getComponentAt(idx);
	    if (cc.stk_name.equals(n))
		break;
	}
        // idx= jtp_jl.indexOfTab( n);
        if (idx != jtp_jl.getTabCount())
            jtp_jl.remove( idx); 
        jl1 = jld1.runJL(n, jls, e, f1, w, p, dbetf.getText(),
			 dbstf.getText());
        // jld1.append( analysis( e));
        jl2 = jld2.runJL(n, jls, e, f2, w, p, dbetf.getText(),
			 dbstf.getText());
        // jld2.append( analysis( e));
        chrt= new Chart(n, s, e, true, dbetf.getText(), dbstf.getText(), 
			jl1, jl2, jl3, invisible.isSelected());
        chrt.setScale(last_scale);
        jtp_jl.add(Integer.toString(idx), chrt);	
        // jtp_jl.setSelectedIndex(jtp_jl.indexOfTab(n));
        jtp_jl.setSelectedIndex(idx);
	getOptions();
	updateTradeStatus();
    }

    public void addC( JPanel p, JComponent c, int x, int y,
                      int h, int w) {
        p.add( c); c.setBounds( x, y, h, w);
        c.setBackground( Color.black);
        c.setForeground( Color.lightGray);
    }

    public void actionPerformed(ActionEvent ae) {
	if (ae.getSource() == pick_stk) {
	    String q1= "SELECT * FROM setups ORDER BY random() LIMIT 1";
	    try {
		StxDB sdb = new StxDB(System.getenv("POSTGRES_DB"));
		ResultSet rset = sdb.get(q1);
		while(rset.next()) {
		    etf.setText(rset.getString(1));
		    ntf.setText(rset.getString(2));
		}
		go();
	    } catch( Exception exc) {
		exc.printStackTrace(System.err);
	    }
	}
	else if ((ae.getSource() == call) || (ae.getSource() == put))
	    openTrade((String)ae.getActionCommand());
	else if ((ae.getSource() == c_call) || (ae.getSource() == c_put))
	    closeTrade((String)ae.getActionCommand());
	else if (ae.getSource() == chart_scale) {
	    String new_scale = chart_scale.getSelectedItem().toString();
	    Chart cc= ( Chart) jtp_jl.getSelectedComponent();
	    cc.setScale(new_scale);
            last_scale= new_scale;
        }
    }

    private void openTrade(String cmd_name) {
	String dt = etf.getText();
	String expiry =
	    StxCal.getMonthlyExpiration(dt, exp.getSelectedIndex() + 1);
	StxTrade trd = new StxTrade(ntf.getText(), cmd_name, expiry, dt,
				    (Float)strike.getSelectedItem(),
				    chrt.getSR(dt).c, jl1.avgRg(),
				    Float.parseFloat(capital.
						     getSelectedItem().
						     toString()),
				    skip_trade.isSelected());
	trade_ix.put(trd.key(), trade_list.size());
	trade_list.add(trd);
	updateTradeStatus();
	String trade_key = String.format
	    ("XXXX_%s_%d_%.2f", cmd_name,
	     StxCal.numBusDaysExpiry(dt, expiry), strike.getSelectedItem());
	trade_status.setText(String.format("OPENED %s", trade_key));
    }

    private void closeTrade(String cmd_name) {
	String cp = cmd_name.equals("CLOSE CALL")? "c": "p";
	String dt = etf.getText();
	String expiry =
	    StxCal.getMonthlyExpiration(dt, exp.getSelectedIndex() + 1);
	String trade_key = String.format("%s_%s_%s_%.2f", ntf.getText(), cp,
					 expiry, strike.getSelectedItem());
	int ix = trade_ix.get(trade_key);
	StxTrade trd = trade_list.get(ix);
	trd.close(log_fname);
	updateTradeStatus();
	String trd_key = String.format("XXXX_%s_%d_%.2f", cp,
				       StxCal.numBusDaysExpiry(dt, expiry),
				       strike.getSelectedItem());
	trade_status.setText(String.format("CLOSED %s", trd_key));
    }

    private void updateTradeStatus() {
	String dt = etf.getText();
	float cc = chrt.getSR(dt).c;
	trades.clear();
	for(StxTrade trd: trade_list) {
	    trd.update(dt, cc, log_fname);
	    trades.append(trd.ui_entry());
	}
    }
    private void getOptions() {
	String stk = ntf.getText(), ed = etf.getText();
	if(stk == null || stk.equals("") || ed == null || ed.equals(""))
	    return;
	String und = stk.matches("[A-Z]+\\.[0-9]{6}")?
	    stk.substring(0, stk.indexOf('.')): stk;
	// System.err.printf("stk = %s, und = %s\n", stk, und);
	List<String> expiries = StxCal.expiries(ed, 2);
	List<Float> strikes = new ArrayList<Float>();
	float min_dist = 10000;
	int atm_ix = -1, strike_ix = -1;
	float cc = chrt.getSR(ed).c;
	StringBuilder q1= new StringBuilder("SELECT DISTINCT strike FROM ");
	q1.append("options WHERE und='").append(und).append("' AND ").
	    append("dt='").append(ed).append("' AND expiry='").
	    append(expiries.get(1)).append("' ").append("ORDER BY strike");
	// System.err.println("q1 = " + q1.toString());
 	try {
            StxDB sdb = new StxDB(System.getenv("POSTGRES_DB"));
            ResultSet rset = sdb.get(q1.toString());
	    while(rset.next()) {
		strike_ix++;
		float s = (float)(rset.getInt(1) / 100.0);
		float dist = Math.abs(s - cc);
		if(dist <= min_dist) {
		    min_dist = dist;
		    atm_ix = strike_ix;
		}
		strikes.add(s);
	    }
	    if(strikes.size() == 0) {
		String q1_1 = q1.toString().replace(expiries.get(1),
						    expiries.get(0));
		ResultSet rset1 = sdb.get(q1_1);
		while(rset1.next()) {
		    strike_ix++;
		    float s = (float)(rset1.getInt(1) / 100.0);
		    float dist = Math.abs(s - cc);
		    if(dist <= min_dist) {
			min_dist = dist;
			atm_ix = strike_ix;
		    }
		    strikes.add(s);
		}
	    }
        } catch( Exception ex) {
	    System.err.println("Failed to get strikes for " + und + ":");
            ex.printStackTrace(System.err);
        }
	opts.clear();
	if(invisible.isSelected())
	    opts.append(String.format
			("%10d                            %10d\n",
			 StxCal.numBusDaysExpiry(ed, expiries.get(0)),
			 StxCal.numBusDaysExpiry(ed, expiries.get(1))));
	else
	    opts.append(String.format("%s                            %s\n",
				      expiries.get(0), expiries.get(1)));
	List<Float> sel_strikes = new ArrayList<Float>();
	int len = strikes.size();
	if(atm_ix > 1) sel_strikes.add(strikes.get(atm_ix - 2));
	if(atm_ix > 0) sel_strikes.add(strikes.get(atm_ix - 1));
	if(atm_ix >= 0) sel_strikes.add(strikes.get(atm_ix));
	if(atm_ix < len - 1) sel_strikes.add(strikes.get(atm_ix + 1));
	if(atm_ix < len - 2) sel_strikes.add(strikes.get(atm_ix + 2));
	if(sel_strikes.size() == 0)
	    return;
	HashMap<Float, HashMap<String, HashMap<String, String>>> opt_dct =
	    new HashMap<Float, HashMap<String, HashMap<String, String>>>();
	for(float s: sel_strikes) {
	    HashMap<String, HashMap<String, String>> strike_dct =
		new HashMap<String, HashMap<String, String>>();
	    for(String expiry: expiries) {
		HashMap<String, String> exp_dct = new HashMap<String, String>();
		exp_dct.put("c", "            ");
		exp_dct.put("p", "            ");
		strike_dct.put(expiry, exp_dct);
	    }
	    opt_dct.put(s, strike_dct);
	}
	StringBuffer s_sb = new StringBuffer("(");
	strike_ix = 0;
	for(float s: sel_strikes) {
	    if(strike_ix > 0) s_sb.append(",");
	    s_sb.append((int)(s * 100));
	    ++strike_ix;
	}
	s_sb.append(")");
	StringBuilder q2= new StringBuilder("SELECT * FROM options");
	q2.append(" WHERE und='").append(und).append( "' AND dt='").
	    append(ed).append("' and expiry in ('").
	    append(expiries.get(0)).append("', '").
	    append(expiries.get(1)).append("') and strike in ").
	    append(s_sb.toString()).append(" order by expiry, strike, cp");
	// System.err.println(q2.toString());
	try {
            StxDB sdb = new StxDB(System.getenv("POSTGRES_DB"));
            ResultSet rset = sdb.get(q2.toString());
            while(rset.next()) {
		String expiry = rset.getString(1), cp = rset.getString(3);
		float s = (float)(rset.getInt(4) / 100.0);
		float bid = (float)(rset.getInt(6) / 100.0);
		float ask = (float)(rset.getInt(7) / 100.0);
		opt_dct.get(s).get(expiry).
		    put(cp, String.format("%5.2f/%5.2f ", bid, ask));
		// System.err.printf("expiry = %s, cp = %s, s = %.2f, bid = %.2f, ask = %.2f\n", expiry, cp, s, bid, ask);
	    }
        } catch( Exception ex) {
            System.err.println("Failed to get options for " + und + ":");
            ex.printStackTrace(System.err);
        }
	exp.removeAllItems();
	strike.removeAllItems();
	for(float s: sel_strikes) {
	    StringBuffer s_row = new StringBuffer();
	    s_row.append("C:").
		append(opt_dct.get(s).get(expiries.get(0)).get("c")).
		append("P:").
		append(opt_dct.get(s).get(expiries.get(0)).get("p")).
		append(String.format(" | %6.2f | ", s)).
		append("C:").
		append(opt_dct.get(s).get(expiries.get(1)).get("c")).
		append(" P:").
		append(opt_dct.get(s).get(expiries.get(1)).get("p")).
		append("\n");
	    opts.append(s_row.toString());
	    strike.addItem(s);
	}
	strike.setSelectedItem(strikes.get(atm_ix));

	for(String expiry: expiries)
	    if(invisible.isSelected())
		exp.addItem(String.format("%d",
					  StxCal.numBusDaysExpiry(ed, expiry)));
	    else
		exp.addItem(String.format("%d (%s)",
					  StxCal.numBusDaysExpiry(ed, expiry),
					  expiry));
    }
    
    public static void main( String[] args) {
        try {
            new ACtx();
        } catch( Exception e) {
            e.printStackTrace( System.err);
        }
    }
}
