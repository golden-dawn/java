package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import core.StxCal;

public class ACtx implements KeyListener, ActionListener {
    static JFrame jf; 
    private JTabbedPane jtp_jl;
    private JPanel jpu;
    private JTextField etf, ntf, dtf, dbetf, dbstf, jlf1, jlf2, jlp;
    private JButton jb1m, jb3m, jb6m, jb1y, jbjl, jb2y, jb3y, jb5y, jball;
    private JButton open_b, fwd, bak;
    private JLDisplay jld1, jld2;
    private JLabel jlfl1, jlfl2;
    private int resX= 1920, resY= 1080, yr;
    private Chart chrt;
    JFileChooser fc;
    String last_scale= "3M";
    List<String[]> entries = new ArrayList<String[]>();
    int crt_pos = 0;
    TreeMap<String, Integer> trend_map= new TreeMap<String, Integer>();
    int idx= -1;

    public ACtx() {
        
        Calendar c= new GregorianCalendar();
        yr= c.get( Calendar.YEAR);
        String d= String.format( "%d-%02d-%02d", yr,
                                 1+ c.get( Calendar.MONTH),
                                 c.get( Calendar.DAY_OF_MONTH));
        new StxCal( yr+ 2);
        if( StxCal.isBusDay( d)== false)
            d= StxCal.prevBusDay( d);
        jf= new JFrame( "ACTX");
        etf= new JTextField( d); etf.setCaretColor( Color.white);
        etf.setName( "ETF"); etf.addKeyListener( this);
        ntf= new JTextField(); ntf.setCaretColor( Color.white);
        ntf.setName( "NTF"); ntf.addKeyListener( this);
        dtf= new JTextField( "20"); dtf.setCaretColor( Color.white);
        dbetf= new JTextField( "eod"); dbetf.setCaretColor( Color.white);
        dbstf= new JTextField( "split"); dbstf.setCaretColor( Color.white);
        jlf1= new JTextField( "0.5"); jlf1.setCaretColor( Color.white);
        jlf2= new JTextField( "1.5"); jlf2.setCaretColor( Color.white);
        jlfl1= new JLabel("Factor: "+ jlf1.getText());
        jlfl2= new JLabel("Factor: "+ jlf2.getText());
        jlp= new JTextField( "16"); jlp.setCaretColor( Color.white);
        jpu= new JPanel( null);
        jpu.setBackground( Color.black);
        jpu.setForeground( Color.lightGray);
 
        addC( jpu, ntf, 40, 7, 60, 25);
        addC( jpu, etf, 100, 7, 75, 25);
        fwd= new JButton( "+"); fwd.addActionListener( this);
        bak= new JButton( "-"); bak.addActionListener( this);
        addC( jpu, fwd, 175, 12, 50, 15);
        addC( jpu, bak, 225, 12, 50, 15);
        addC( jpu, dtf, 275, 7, 50, 25);
        addC( jpu, dbetf, 325, 7, 50, 25);
        addC( jpu, dbstf, 375, 7, 50, 25);

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
        addC( jpu, jlp,  140, 55, 50, 20);

        fc = new JFileChooser();
        fc.setCurrentDirectory( new File( "C:/users/ctin/python/"));
	fc.setAcceptAllFileFilterUsed(false);
	fc.setMultiSelectionEnabled(false);
	//fc.setfsetFileFilter((new FileNameExtensionFilter(wordExtDesc, ".csv"));
	open_b= new JButton( "Open");
        open_b.addActionListener(this);
        addC( jpu, open_b, 15, 975, 160, 20);
        int hd11= 2* resX/ 3;
        addC( jpu, jlfl1,  5, 85, 80, 20);
        jld1= new JLDisplay( hd11- 10, 220, 10);
        addC( jpu, jld1, 5, 105, resX- hd11- 40, 420);
        addC( jpu, jlfl2,  5, 525, 80, 20);
        jld2= new JLDisplay( hd11- 10, 220, 10);
        addC( jpu, jld2, 5, 545, resX- hd11- 40, 420);
        jtp_jl= new JTabbedPane( JTabbedPane.BOTTOM);
        JSplitPane jspu= new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,
                                         jtp_jl, jpu);
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
                // if( cd== 33) etf.setText( getAnalysisDate( ed, -1));
                // if( cd== 34) etf.setText( getAnalysisDate( ed,  1));
                if( cd== 10) go();
            } else if(src.equals("NTF")) {
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
                    go();
                }
                if(cd == 33) {
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
                    go();
                }
                if(cd == 35 || cd == 36) {
                    int num_bds = 20;
                    try {
                        num_bds = Integer.parseInt(dtf.getText());
                    } catch(Exception ex) {}
                    etf.setText(StxCal.moveBusDays(etf.getText(), (cd == 35)?
                                                   num_bds: -num_bds));
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
        jlfl1.setText("Factor: "+ jlf1.getText());
        jlfl2.setText("Factor: "+ jlf2.getText());
        if( StxCal.isBusDay( e)== false)
            e= StxCal.nextBusDay( e);
        jls= ( StxCal.year( e)- 2)+ "-01-01";
        s= "1962-01-01";
        idx= jtp_jl.indexOfTab( n);
        if( idx!= -1)
            jtp_jl.remove( idx); 
        chrt= new Chart( n, s, e, true, dbetf.getText(), dbstf.getText(), 
                         trend_map);
        chrt.setScale( last_scale);
        jtp_jl.add( n, chrt);
        jtp_jl.setSelectedIndex( jtp_jl.indexOfTab( n));
        jld1.runJL( n, jls, e, f1, w, p, dbetf.getText(), dbstf.getText());
        // jld1.append( analysis( e));
        jld2.runJL( n, jls, e, f2, w, p, dbetf.getText(), dbstf.getText());
        // jld2.append( analysis( e));
    }

    // private String analysis( String ed) {
    //     String[] values = analysis_file.get( ed);
    //     if( values== null)
    //         return "";
    //     StringBuilder sb= new StringBuilder(); char c= ' ';
    //     for( Map.Entry<Integer, String> entry: idxd.entrySet()) {
    //         int ix= entry.getKey(); 
    //         if( ix< values.length) 
    //             sb.append( String.format( "%12s=%12s%c", entry.getValue(),
    //                                       values[ix], c));
    //         if( c== ' ') c= '\n'; else c= ' ';
    //     }
    //     return sb.toString();
    // }

    public void addC( JPanel p, JComponent c, int x, int y,
                      int h, int w) {
        p.add( c); c.setBounds( x, y, h, w);
        c.setBackground( Color.black);
        c.setForeground( Color.lightGray);
    }

    private void loadAnalysisFile() throws Exception {
        int ret_val= fc.showOpenDialog(jf);
        if( ret_val!= JFileChooser.APPROVE_OPTION) return;
        File file= fc.getSelectedFile();
        String line= null;
        BufferedReader br= new BufferedReader( new FileReader( file));
        boolean read_hdrs= false;
        while(( line= br.readLine())!= null) {
            if( line.trim()== "") continue;
            if( !read_hdrs) {
                read_hdrs = true;
                continue;
            }
            String[] tokens = line.split(",");
            entries.add(tokens);
        }
        br.close();
        crt_pos = 0;
        jld1.append( "\nSuccessfully loaded "+ file.getAbsolutePath()+ "\n");
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
        if( ae.getSource()== fwd) moveDate( 1);
        if( ae.getSource()== bak) moveDate( -1);
        if( ae.getSource()== open_b) {
            try {
                loadAnalysisFile();
            } catch( Exception e) {
                e.printStackTrace( System.err);
            }
        }
    }

    public static void main( String[] args) {
        try {
            new ACtx();
        } catch( Exception e) {
            e.printStackTrace( System.err);
        }
    }
}
