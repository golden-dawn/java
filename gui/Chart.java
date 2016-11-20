package gui;

import core.StxCal;
import core.StxRec;
import core.StxTS;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.TreeMap;

import javax.swing.JPanel;

public class Chart extends JPanel {
    private static final long serialVersionUID = 1L;
    HashMap<Integer, String> setups;
    StxTS<StxRec> ts;
    int start, end, start0;
    FontMetrics fm;
    TreeMap<String, Integer> trend= null;
    String stk_name;

    public Chart( String stk_name, String sd, String ed) {
        super( null);
        this.stk_name = stk_name;
        ts= StxTS.loadEod( stk_name, null, null);
        start = ts.find( sd, 1); start0= start;
        end= ts.find( ed, -1);
        repaint();
    }

    public Chart( String stk_name, String sd, String ed,
                  boolean adjust, TreeMap<String, Integer> trend) {
        super( null);
        this.stk_name= stk_name;
        ts= StxTS.loadEod( stk_name, null, null);        
        start= ts.find( sd, 1); start0= start;
        end= ts.find( ed, -1);
        ts.setDay( ed, -1, 1);
        start= end- 60; if( start< 0) start= 0;
        this.trend= trend;
        repaint();
    }

    public void paint( Graphics g) {
        Color green1_transparent= new Color( 0f, 1f, 0f, 0.1f);
        Color green2_transparent= new Color( 0.5f, 1f, 0.5f, 0.1f);
        Color cyan1_transparent= new Color( 0f, 1f, 1f, 0.2f);
        Color cyan2_transparent= new Color( 0.5f, 1f, 1f, 0.2f);
        Color red1_transparent= new Color( 1f, 0f, 0f, 0.1f);
        Color red2_transparent= new Color( 1f, 0.5f, 0.5f, 0.1f);
        Color purple1_transparent= new Color( 1f, 0f, 1f, 0.1f);
        Color purple2_transparent= new Color( 1f, 0.5f, 1f, 0.1f);
        Color blue1_transparent= new Color( 0f, 0f, 1f, 0.1f);
        Color blue2_transparent= new Color( 0.5f, 0.5f, 1f, 0.1f);
        Color white_transparent= new Color( 1f, 1f, 1f, 0.1f);
        Graphics2D g2= ( Graphics2D) g;
        Dimension d= getSize();
        int days= end- start;
        double day_width= ( d.width- 155.0)/ ( double) days;
        double bar_width= 0.5* day_width;
        HashMap<Integer, String> labels= null;
        if( days<= 60)
            labels= setWeeklyLabels();
        else if( days<= 756)
            labels= setMonthlyLabels();
        else
            labels= setYearlyLabels();
        setBackground( Color.black);
        g.setColor( getBackground());
        g.fillRect( 0, 0, d.width, d.height);
        float max_vol= 0, min_vol= 0, vol_rg= 0;
        float min_price= 1000000, max_price= 0, price_rg= 0;
        int ix;
        for( ix= start; ix<= end; ix++) {
            if( max_vol< ts.get( ix).v)
                max_vol= ts.get( ix).v;
            if( max_price< ts.get( ix).h)
                max_price= ts.get( ix).h;
            if( min_price> ts.get( ix).l)
                min_price= ts.get( ix).l;
        }
        vol_rg= max_vol- min_vol;
        price_rg= max_price- min_price;

        double xx= 50, yy= 0.85* d.height;
        double bar_height= 0.2* d.height, hh= 0;
        double yyp= 0.6* d.height, price_height= 0.5* d.height;
        double hho= 0, hhh= 0, hhl= 0, hhc= 0, eps= 4* bar_width/ 9;
        g2.setPaint( Color.darkGray);
        fm = g2.getFontMetrics();

        g2.draw( new Rectangle2D.Double( xx- 5, 0.65* d.height,
                                         d.width- 150+ day_width,
                                         0.2* d.height));
        g2.setPaint( Color.lightGray);
        // draw 3 strings and a line
        g2.drawString( String.format( "%,.0fK", max_vol/ 1000),
                       ( float) ( d.width- 95+ day_width),
                       ( float) ( 0.65* d.height+ 0.25* fm.getHeight()));
        g2.drawString( String.format( "%,.0fK", max_vol/ 2000),
                       ( float) ( d.width- 95+ day_width),
                       ( float) ( 0.75* d.height+ 0.25* fm.getHeight()));
        g2.drawString( "0", ( float) ( d.width- 95+ day_width),
                       ( float) ( 0.85* d.height+ 0.25* fm.getHeight()));
        g2.setPaint( Color.darkGray);
        g2.draw( new Line2D.Double( xx- 5, 0.75* d.height,
                                    d.width- 100+ day_width, 0.75* d.height));

        g2.draw( new Rectangle2D.Double( xx- 5, 0.1* d.height,
                                         d.width- 150+ day_width,
                                         0.5* d.height));
        double step= 0;
        for( ix= 0; ix<= 10; ix++) {
            step= 0.1* d.height+ 0.05* ix* d.height;
            g2.setPaint( Color.lightGray);
            g2.drawString( String.format( "%.2f",
                                          ( max_price- ix* price_rg/ 10)),
                           ( float) ( d.width- 95+ day_width),
                           ( float) ( step+ 0.25* fm.getHeight()));
            g2.setPaint( Color.darkGray);
            g2.draw( new Line2D.Double( xx, step, d.width- 100+ day_width,
                                        step));
        }
        for( ix= start; ix<= end; ix++) {
            if( labels.get( ix)!= null) {
                g2.setPaint( Color.darkGray);
                g2.draw( new Line2D.Double( xx+ eps, 0.1* d.height,
                                            xx+ eps, 0.6* d.height));
                g2.draw( new Line2D.Double( xx+ eps, 0.65* d.height,
                                            xx+ eps, yy));
                g2.setPaint( Color.lightGray);
                g2.drawString( labels.get( ix),
                               ( float)( xx- fm.charWidth( '1')*
                                         ( labels.get( ix).length()/ 2- 1)),
                               ( float)( yy+ fm.getHeight()));
            }
            StxRec r= ts.get( ix);
            hho= yyp- price_height* ( r.o- min_price)/ price_rg;
            hhh= yyp- price_height* ( r.h- min_price)/ price_rg;
            hhl= yyp- price_height* ( r.l- min_price)/ price_rg;
            hhc= yyp- price_height* ( r.c- min_price)/ price_rg;
            if( trend!= null) {
                Integer trnd= trend.get( r.date);
                if( trnd!= null&& trnd!= 0) {
                    if( trnd== 1) g2.setPaint( green1_transparent);
                    else if( trnd== -1) g2.setPaint( red1_transparent);
                    else if( trnd== -2) g2.setPaint( red2_transparent);
                    else if( trnd== -3) g2.setPaint( purple1_transparent);
                    else if( trnd== -4) g2.setPaint( purple2_transparent);
                    else if( trnd== -5) g2.setPaint( white_transparent);
                    else if( trnd== 2) g2.setPaint( green2_transparent);
                    else if( trnd== 3) g2.setPaint( blue1_transparent);
                    else if( trnd== 4) g2.setPaint( blue2_transparent);
                    else if( trnd== 5) g2.setPaint( cyan1_transparent);
                    else if( trnd== 6) g2.setPaint( cyan2_transparent);
                    g2.fill( new Rectangle2D.Double( xx- bar_width/ 2,
                                                     0.1* d.height,
                                                     2* bar_width,
                                                     0.5* d.height));
                }
            }
            if( hho< hhc) {
                g2.setPaint( Color.red);
                g2.fill( new Rectangle2D.Double( xx, hho, bar_width, hhc- hho));
                g2.draw( new Line2D.Double( xx, hhc, xx+ bar_width, hhc));
            } else {
                g2.setPaint( Color.green);
                g2.fill( new Rectangle2D.Double( xx, hhc, bar_width, hho- hhc));
                g2.draw( new Line2D.Double( xx, hhc, xx+ bar_width, hhc));
            }
            g2.draw( new Line2D.Double( xx+ eps, hhl, xx+ eps, hhh));
            hh= yy- bar_height* ( r.v- min_vol)/vol_rg;
            g2.draw( new Line2D.Double( xx+ eps, hh, xx+ eps, yy));
            xx+= day_width;
        }

        g2.setFont( new Font("Lucida Sans Typewriter", Font.PLAIN, 16));
        g2.setPaint( Color.lightGray);
        g2.drawString( stk_name.toUpperCase(), d.width/2- 50, 15);         
    }
    
    HashMap<Integer, String> setWeeklyLabels() {
        HashMap<Integer, String> lbls= new HashMap<Integer, String>();
        for( int ix= start; ix<= end; ix++)
            if( StxCal.dow( ts.get( ix).date)== 1)
                lbls.put( ix, ts.get( ix).date.substring( 5));
        return lbls;
    }

    HashMap<Integer, String> setMonthlyLabels() {
        HashMap<Integer, String> lbls= new HashMap<Integer, String>();
        HashMap<Integer, String> months= new HashMap<Integer, String>();
        months.put( 1, "Jan");
        months.put( 2, "Feb");
        months.put( 3, "Mar");
        months.put( 4, "Apr");
        months.put( 5, "May");
        months.put( 6, "Jun");
        months.put( 7, "Jul");
        months.put( 8, "Aug");
        months.put( 9, "Sep");
        months.put( 10, "Oct");
        months.put( 11, "Nov");
        months.put( 12, "Dec");
        int mm= 0;
        for( int ix= start; ix<= end; ix++) {
            mm= StxCal.month( ts.get( ix).date);
            if(( ix> 0)&& ( StxCal.month( ts.get( ix- 1).date)!= mm)) {
                if( mm== 1)
                    lbls.put( ix, new Integer( StxCal.year( ts.get( ix).date)).toString());
                else
                    lbls.put( ix, months.get( mm));
            }
        }
        return lbls;
    }

    HashMap<Integer, String> setYearlyLabels() {
        HashMap<Integer, String> lbls= new HashMap<Integer, String>();
        for( int ix= start; ix<= end; ix++) {
            int mm= StxCal.month( ts.get( ix).date);
            if(( mm== 1)&& ( StxCal.month( ts.get( ix- 1).date)!= mm))
                lbls.put( ix, new Integer( StxCal.year( ts.get( ix).date)).toString());
        }
        return lbls;
    }

    
    public void setScale( String scale) {
        if( scale.compareTo( "1M")== 0)
            start= end- 22;
        else if( scale.compareTo( "3M")== 0)
            start= end- 60;
        else if( scale.compareTo( "6M")== 0)
            start= end- 126;
        else if( scale.compareTo( "1Y")== 0)
            start= end- 252;
        else if( scale.compareTo( "JL")== 0)
            start= end- 400;
        else if( scale.compareTo( "2Y")== 0)
            start= end- 504;
        else if( scale.compareTo( "3Y")== 0)
            start= end- 756;
        else if( scale.compareTo( "5Y")== 0)
            start= end- 1260;
        else
            start= 0;
        if( start< 0) start= 0;
        repaint();
    }

    StxRec getSR( String d) {
        StxRec res= null;
        try {
            res= ts.get( ts.find( d, 0));
        } catch( Exception e) {
            res= new StxRec( "1900-01-01", 0);
        }
        return res;
    }

    //StxData<StxRec> data(){ return data; }

    //     public static void main( String args[]) {
    //         JFrame f= new JFrame( "Chart");
    //         f.addWindowListener( new WindowAdapter() {
    //             public void windowClosing( WindowEvent e) {
    //                 System.exit(0);
    //             }
    //         });
    //         String name= "dji", sd= "1998-01-02", ed= "1998-04-01";
    //         int len= args.length;
    //         for( int ix= 0; ix< len; ix++) {
    //             if(( args[ ix].compareTo( "-name")== 0)&& ( ++ix< len))
    //                 name= args[ ix];
    //             else if(( args[ ix].compareTo( "-start")== 0)&& ( ++ix< len))
    //                 sd= args[ ix];
    //             if(( args[ ix].compareTo( "-end")== 0)&& ( ++ix< len))
    //                 ed= args[ ix];
    //         }
    //         Chart chrt= new Chart( name, sd, ed);
    //         chrt.setBackground( Color.black);
    //         f.getContentPane().add( "Center", chrt);
    //         f.setSize( new Dimension( 950, 500));
    //         //        f.pack();
    //         f.setVisible(true);
    //     }
}
