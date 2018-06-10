package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import core.StxRec;
import core.StxCal;
import jl.StxxJL;

public class ACtx implements KeyListener, ActionListener {
    static JFrame jf; 
    private JTabbedPane jtp_jl;
    private JPanel jpu, jp_trd;
    private JTextField etf, ntf, dtf, dbetf, dbstf, jlf1, jlf2, jlf3, jlp;
    private JButton jb1m, jb3m, jb6m, jb1y, jbjl, jb2y, jb3y, jb5y, jball;
    private JButton rewind, fwd, bak, pick_stk;
    private JButton buy, sell, c_buy, c_sell;
    private JCheckBox invisible;
    private JLDisplay jld1, jld2, jld3;
    private JLabel jlfl1, jlfl2, jlfl3, trade_status;
    private int resX= 1920, resY= 1080, yr;
    private Chart chrt;
    private StxxJL jl1, jl2, jl3;
    String last_scale= "3M";
    private String trade_type = "", trade_date;
    private float trade_price, trade_daily_range;
    List<String[]> entries = new ArrayList<String[]>();
    int crt_pos = 0;
    TreeMap<String, Integer> trend_map= new TreeMap<String, Integer>();
    int idx= -1;
    String log_fname;

    public ACtx() {
        
        Calendar c= new GregorianCalendar();
        yr= c.get( Calendar.YEAR);
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
        dtf= new JTextField( "120"); dtf.setCaretColor( Color.white);
        dbetf= new JTextField( "eods"); dbetf.setCaretColor( Color.white);
        dbstf= new JTextField( "dividends"); dbstf.setCaretColor( Color.white);
        jlf1 = new JTextField( "1.0"); jlf1.setCaretColor( Color.white);
        jlf2 = new JTextField( "1.5"); jlf2.setCaretColor( Color.white);
        jlf3 = new JTextField( "2.0"); jlf3.setCaretColor( Color.white);
        jlfl1 = new JLabel("Factor: "+ jlf1.getText());
        jlfl2 = new JLabel("Factor: "+ jlf2.getText());
        jlfl3 = new JLabel("Factor: "+ jlf3.getText());
        jlp= new JTextField( "16"); jlp.setCaretColor( Color.white);
	invisible = new JCheckBox("Invisible");
	invisible.setSelected(true);
        jpu= new JPanel( null);
        jpu.setBackground( Color.black);
        jpu.setForeground( Color.lightGray);
	addC( jpu, ntf, 15, 7, 60, 25);
        addC( jpu, etf, 75, 7, 75, 25);
        etf.setForeground(invisible.isSelected()? Color.black: Color.lightGray);
        fwd= new JButton( "+"); fwd.addActionListener( this);
        bak= new JButton( "-"); bak.addActionListener( this);
        rewind = new JButton("<");
        rewind.addActionListener(this);
        addC( jpu, rewind, 150, 12, 50, 15);
        addC( jpu, fwd, 200, 12, 50, 15);
        addC( jpu, bak, 250, 12, 50, 15);
        addC( jpu, dtf, 300, 7, 50, 25);
        addC( jpu, invisible, 350, 7, 150, 25);
        addC( jpu, invisible, 350, 7, 150, 25);
        pick_stk = new JButton("R");
        pick_stk.addActionListener(this);
        addC( jpu, pick_stk, 500, 7, 50, 25);
        jb1m= new JButton( "1M");
        jb1m.addActionListener( this);
        addC( jpu, jb1m, 15, 35, 55, 15);
        jb3m= new JButton( "3M");
        jb3m.addActionListener( this);
        addC( jpu, jb3m, 70, 35, 55, 15);
        jb6m= new JButton( "6M");
        jb6m.addActionListener( this);
        addC( jpu, jb6m, 125, 35, 55, 15);
        jb1y= new JButton( "1Y");
        jb1y.addActionListener( this);
        addC( jpu, jb1y, 180, 35, 55, 15);
        jbjl= new JButton( "JL");
        jbjl.addActionListener( this);
        addC( jpu, jbjl, 235, 35, 55, 15);
        jb2y = new JButton( "2Y");
        jb2y.addActionListener( this);
        addC( jpu, jb2y, 290, 35, 55, 15);
        jb3y= new JButton( "3Y");
        jb3y.addActionListener( this);
        addC( jpu, jb3y, 345, 35, 55, 15);
        jb5y= new JButton( "5Y");
        jb5y.addActionListener( this);
        addC( jpu, jb5y, 400, 35, 55, 15);
        jball= new JButton( "All");
        jball.addActionListener( this);
        addC( jpu, jball, 455, 35, 55, 15);
        addC( jpu, new JLabel("JL: "), 15, 55, 25, 20);
        addC( jpu, jlf1,  40, 55, 50, 20);
        addC( jpu, jlf2,  90, 55, 50, 20);
        addC( jpu, jlf3,  140, 55, 50, 20);
        addC( jpu, jlp,  190, 55, 50, 20);
	
        jp_trd = new JPanel(null);
        jp_trd.setBackground(Color.black);
        jp_trd.setForeground(Color.lightGray);
        buy = new JButton("BUY"); buy.addActionListener(this);
        sell = new JButton( "SELL"); sell.addActionListener(this);
        c_buy = new JButton( "CLOSE BUY"); c_buy.addActionListener(this);
        c_sell = new JButton( "CLOSE SELL"); c_sell.addActionListener(this);
	trade_status = new JLabel("GETTING STARTED . . .");
        addC(jp_trd, buy, 25, 5, 150, 15);
        addC(jp_trd, sell, 175, 5, 150, 15);
        addC(jp_trd, c_buy, 325, 5, 150, 15);
        addC(jp_trd, c_sell, 475, 5, 150, 15);
	addC(jp_trd, trade_status, 625, 5, 550, 15);
	
        int hd11= 2* resX/ 3;
        addC( jpu, jlfl1, 5, 90, 80, 20);
        jld1= new JLDisplay( hd11 + 40, 220, 12, invisible.isSelected());
        addC( jpu, jld1, 5, 110, resX- hd11- 80, 290);
        addC( jpu, jlfl2,  5, 400, 80, 20);
        jld2= new JLDisplay( hd11- 10, 220, 12, invisible.isSelected());
        addC( jpu, jld2, 5, 420, resX- hd11- 80, 290);
        addC( jpu, jlfl3,  5, 710, 80, 20);
        jld3= new JLDisplay( hd11- 10, 220, 12, invisible.isSelected());
        addC( jpu, jld3, 5, 730, resX- hd11- 80, 290);
        jtp_jl= new JTabbedPane( JTabbedPane.BOTTOM);

	int vert_div = 17 * resY / 18;
        JSplitPane jspv= new JSplitPane( JSplitPane.VERTICAL_SPLIT,
                                         jtp_jl, jp_trd);
        jspv.setOneTouchExpandable(true);
        jspv.setDividerLocation(vert_div);
	
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
            int cd= e.getKeyCode(); String src= e.getComponent().getName();
            if( src.equals( "ETF")) {
                String ed= etf.getText();
                if( cd== 40) etf.setText( StxCal.prevBusDay( ed));
                if( cd== 38) etf.setText( StxCal.nextBusDay( ed));
                if(cd == 34) {
                    decreaseScale();
                    go();
                }
                if(cd == 33) {
                    increaseScale();
                    go();
                }
                if(cd == 35 || cd == 36) {
                    move(cd);
                    go();
                }
                if( cd== 10) go();
            } else if(src.equals("NTF")) {
                if( cd== 10) go();
                if(cd == 38) {
                    if(entries.size() == 0)
                        return;
                    if(++crt_pos >= entries.size())
                        crt_pos = entries.size() - 1;
                    ntf.setText(entries.get(crt_pos)[0]);
                    etf.setText(entries.get(crt_pos)[1]);
                    go();
                }
                if(cd == 40) {
                    if(entries.size() == 0)
                        return;
                    if(--crt_pos < 0)
                        crt_pos = 0;
                    ntf.setText(entries.get(crt_pos)[0]);
                    etf.setText(entries.get(crt_pos)[1]);
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
                if(cd == 35 || cd == 36) {
                    move(cd);
                    go();
                }
                if(cd == 112) {
                    int sz = entries.size();
                    if(sz == 0) return;
                    crt_pos = 0;
                    while(crt_pos < sz && entries.get(crt_pos)[0].compareTo
                          (ntf.getText()) != 0)
                        ++crt_pos;
                    if(crt_pos == sz) crt_pos = 0;
                    ntf.setText(entries.get(crt_pos)[0]);
                    etf.setText(entries.get(crt_pos)[1]);
                    go();
                        
                }
            }
        } catch( Exception exc) {
            exc.printStackTrace( System.err);
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

    private void move(int cd) {
        int num_bds = 20;
        try {
            num_bds = Integer.parseInt(dtf.getText());
        } catch(Exception ex) {}
        etf.setText(StxCal.moveBusDays(etf.getText(), (cd == 35)?
                                       num_bds: -num_bds));
    }
    // private String getAnalysisDate( String ed, int dir) {
    //     String res= ed;
    //     String[] current_values = analysis_file.get( ed);
    //     if( current_values== null)
    //         return res;
    //     boolean found= false;        
    //     SortedMap<String, String[]> smap= ( dir== 1)? 
    //         analysis_file.tailMap( StxCal.nextBusDay( ed)): 
    //         analysis_file.descendingMap().tailMap( StxCal.prevBusDay( ed));
    //     for( Map.Entry<String, String[]> entry: smap.entrySet()) {
    //         found= true; String[] values= entry.getValue();
    //         for( int ix: idxf) {
    //             if(values[ix].compareTo( current_values[ ix])!= 0) {
    //                 found= false; break;
    //             }
    //         }
    //         if( found== true) { res= entry.getKey(); break;}
    //     }
    //     return res;
    // }
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    public void go() throws Exception {

        String jls, s, e= etf.getText();
        String n= ntf.getText();
        int idx, w= 20, p= Integer.parseInt( jlp.getText());
        float f1= Float.parseFloat( jlf1.getText());
        float f2= Float.parseFloat( jlf2.getText());
        float f3= Float.parseFloat( jlf3.getText());
	etf.setForeground(invisible.isSelected()? Color.black: Color.lightGray);
	ntf.setForeground(invisible.isSelected()? Color.black: Color.lightGray);
        jlfl1.setText("Factor: "+ jlf1.getText());
        jlfl2.setText("Factor: "+ jlf2.getText());
        updateSetupPanel();
        if( StxCal.isBusDay( e)== false)
            e= StxCal.nextBusDay( e);
        jls= ( StxCal.year( e)- 2)+ "-01-01";
        s= "1901-01-02";
        idx= jtp_jl.indexOfTab( n);
        if( idx!= -1)
            jtp_jl.remove( idx); 
        jl1 = jld1.runJL(n, jls, e, f1, w, p, dbetf.getText(),
			 dbstf.getText());
        // jld1.append( analysis( e));
        jl2 = jld2.runJL(n, jls, e, f2, w, p, dbetf.getText(),
			 dbstf.getText());
        // jld2.append( analysis( e));
        jl3 = jld3.runJL(n, jls, e, f3, w, p, dbetf.getText(),
			 dbstf.getText());
        chrt= new Chart(n, s, e, true, dbetf.getText(), dbstf.getText(), 
			jl1, jl2, jl3, invisible.isSelected());
        chrt.setScale( last_scale);
        jtp_jl.add( n, chrt);
        jtp_jl.setSelectedIndex( jtp_jl.indexOfTab( n));
	updateTradeStatus();
    }

    private void updateSetupPanel() {
        jld3.clear();
        if(entries.size() == 0)
            return;
        int ix = 0;
        for(String str: entries.get(crt_pos)) {
            switch(ix) {
            case 0: case 1: case 3: case 4: case 9: case 11: case 16: case 21:
                jld3.append(str + '\n');
                break;
            case 5: case 6: case 7: case 8: case 10: case 12: case 13: case 14:
            case 15:
                float val = Float.parseFloat(str);
                jld3.append(String.format("%.2f\n", val));
                break;
            case 17: case 18: case 19: case 20:
                try {
                    float val1 = Float.parseFloat(str);
                    jld3.append(String.format("%.2f\n", val1));
                } catch(Exception ex) {
                    jld3.append("\n");
                }
                break;
            default:
                break;
            }
            ix++;
        }
    }

    public void addC( JPanel p, JComponent c, int x, int y,
                      int h, int w) {
        p.add( c); c.setBounds( x, y, h, w);
        c.setBackground( Color.black);
        c.setForeground( Color.lightGray);
    }

    private void moveDate( int sign) {
        int num= 20;
        try {
            num= Integer.parseInt( dtf.getText());
        } catch (Exception e1) {}
        num*= sign;
        try {
            String crt_date = etf.getText();
            etf.setText( StxCal.moveBusDays( crt_date, num));
            go();
        } catch (Exception e2) {
            e2.printStackTrace( System.err);
        }
    }

    public void actionPerformed( ActionEvent ae) {
        String cmd_name= ( String) ae.getActionCommand();
        Chart cc= ( Chart) jtp_jl.getSelectedComponent();
        if( cmd_name.equals( "1M")|| cmd_name.equals( "3M")||
            cmd_name.equals( "6M")|| cmd_name.equals( "1Y")||
            cmd_name.equals( "JL")|| cmd_name.equals( "2Y")||
            cmd_name.equals( "3Y")|| cmd_name.equals( "5Y")||
            cmd_name.equals( "All")) {
            cc.setScale( cmd_name);
            last_scale= cmd_name;
        }
        if( ae.getSource() == fwd) moveDate( 1);
        if( ae.getSource() == bak) moveDate( -1);
        if( ae.getSource() == rewind) {
	    String rewind_date = cc.rewind();
	    etf.setText(rewind_date);
	    try {
		go();
	    } catch( Exception exc) {
		exc.printStackTrace(System.err);
	    }
	}
        if(cmd_name.equals("BUY") || cmd_name.equals("SELL") ||
	   cmd_name.equals("CLOSE BUY") || cmd_name.equals("CLOSE SELL"))
	    try {
		log_trade(cmd_name);
	    } catch(IOException ioe) {
		System.err.println("Failed to log trade: ");
		ioe.printStackTrace( System.err);
	    }
    }

    private void log_trade(String cmd_name) throws IOException {
	PrintWriter pw = new PrintWriter(new FileWriter(log_fname, true));
	StringBuffer sb = new StringBuffer(), sb1 = new StringBuffer();
	float pnl = 0, in_price = trade_price, in_range = trade_daily_range;
	String in_date = trade_date;
	int sgn = cmd_name.contains("BUY")? 1: -1;
	trade_type = cmd_name;
	trade_date = etf.getText();
	trade_price = chrt.getSR(trade_date).c;
	trade_daily_range = jl1.avgRg();
	if(trade_type.startsWith("CLOSE")) {
	    pnl = sgn * (trade_price - in_price) / in_range - 1;
	    if(pnl < -2)
		pnl = -2;
	    sb.append(cmd_name).append(',').append(ntf.getText()).append(',').
		append(in_date).append(',').append(in_price).append(',').
		append(String.format("%.2f", in_range)).append(',').
		append(trade_date).append(",").append(trade_price).append(",").
		append(String.format("%.2f", pnl));
	    sb1.append(cmd_name).append("  DAYS: ").
		append(StxCal.numBusDays(in_date, trade_date)).append("  IN: ").
		append(in_price).append("  OUT: ").append(trade_price).
		append("  P&L: ").append(String.format("%.2f", pnl));
	} else {
	    sb.append(trade_type).append(",").append(ntf.getText()).append(",").
		append(trade_date).append(",").append(trade_price).append(",").
		append(trade_daily_range);
	    sb1.append(trade_type).append("  DAYS: 0").append("  IN: ").
		append(trade_price).append("  RG: ").append(trade_daily_range);
	}
	pw.println(sb.toString());
	pw.close();
	trade_status.setText(sb1.toString());
    }

    private void updateTradeStatus() {
	if((trade_type.compareTo("BUY") != 0) && 
	   (trade_type.compareTo("SELL") != 0))
	    return;
	float pnl = 0, crt_price = chrt.getSR(etf.getText()).c;
	int sgn = (trade_type.compareTo("BUY") == 0)? 1: -1;
	StringBuffer sb = new StringBuffer();
	pnl = sgn * (crt_price - trade_price) / trade_daily_range - 1;
	sb.append(trade_type).append("  ").
	    append(StxCal.numBusDays(trade_date, etf.getText())).
	    append("  IN: ").append(trade_price).
	    append("  PX: ").append(crt_price).
	    append("  P&L: ").append(String.format("%.2f", pnl));	
	trade_status.setText(sb.toString());
    }
    
    public static void main( String[] args) {
        try {
            new ACtx();
        } catch( Exception e) {
            e.printStackTrace( System.err);
        }
    }
}
