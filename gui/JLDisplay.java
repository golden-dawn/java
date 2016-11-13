package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import core.StxCal;
import core.StxRec;
import jl.StxJL;
import jl.StxxJL;
 
public class JLDisplay extends JScrollPane {
    private static final long serialVersionUID = 1L;
    private DefaultStyledDocument d= new DefaultStyledDocument();
    private SimpleAttributeSet a;
    private JTextPane jtp;
    private String name;
    static final Color lr= new Color( 255, 64, 64);
    static final Color lg= new Color( 127, 255, 127);
    static final Color lb= new Color( 191, 191, 255);

    public JLDisplay(){ 
        super(); jtp= new JTextPane(); jtp.setDocument( d); 
        jtp.setFont( new Font("Lucida Sans Typewriter", Font.PLAIN, 9));
        jtp.setBackground( Color.black); this.name= "";
        a= new SimpleAttributeSet(); this.setViewportView( jtp);
    }
    public JLDisplay( int w, int h){ 
        super(); jtp= new JTextPane(); jtp.setDocument( d); 
        jtp.setFont( new Font("Lucida Sans Typewriter", Font.PLAIN, 9));
        jtp.setBackground( Color.black); this.name= "";
        a= new SimpleAttributeSet(); this.setViewportView( jtp);
        setPreferredSize( new Dimension( w, h));
    }
    public JLDisplay( int w, int h, int fsz){ 
        super(); jtp= new JTextPane(); jtp.setDocument( d); 
        jtp.setFont( new Font("Lucida Sans Typewriter", Font.PLAIN, fsz));
        jtp.setBackground( Color.black); this.name= "";
        a= new SimpleAttributeSet(); this.setViewportView( jtp);
        setPreferredSize( new Dimension( w, h));
    }
    public JLDisplay( int w, int h, int fsz, String name){ 
        super(); jtp= new JTextPane(); jtp.setDocument( d); 
        jtp.setFont( new Font("Lucida Sans Typewriter", Font.PLAIN, fsz));
        jtp.setBackground( Color.black); this.name= name;
        a= new SimpleAttributeSet(); this.setViewportView( jtp);
        setPreferredSize( new Dimension( w, h));
    }
    public void append( String s, Color cf, Color cb, boolean bul, boolean bb) {
        try {
            StyleConstants.setForeground( a, cf);
            StyleConstants.setBackground( a, cb);
            StyleConstants.setUnderline( a, bul);
            StyleConstants.setItalic( a, bb);
            d.insertString( d.getLength(), s, a);
        } catch(Exception e){}
    }
    public void append( String s){ append( s, lb, Color.black, false, false); }
    public void append( String s, Color cf){
        append( s, cf, Color.black, false, false);
    }
    public void append( String s, Color cf, Color cb, boolean bul) {
        append( s, cf, cb, bul, false);
    }
    public void clear() { try { d.remove( 0, d.getLength());}
        catch( Exception e){}}
    private Color fCol( int s, boolean p) {
        return( p? (( s== StxJL.UT|| s== StxJL.NRe)? Color.green: Color.red):
                (( s== StxJL.UT)? lg: ( s== StxJL.DT)? lr: lb));
    }
    private Color dCol( int s, float c, float arg, float ptp, boolean p) {
        if( ptp== 0) return lb;
        if( s== StxJL.UT)
            return(( c> ptp+ arg)? Color.green: Color.yellow);
        else if( s== StxJL.DT)
            return(( c< ptp- arg)? Color.red: Color.yellow);
        else {
            if( p)
                return( s== StxJL.NRe)? Color.green: Color.red;
            else {
                if( s== StxJL.NRe) {
                    if( c>= ptp+ arg) return Color.green;
                    else if( c>= ptp) return Color.yellow;
                    else return Color.yellow;
                } else {
                    if( c< ptp- arg) return Color.red;
                    else if( c< ptp) return Color.yellow;
                    else return Color.yellow;
                }
            }
        }
    }
    private Color bCol( int s, boolean p) { return Color.black; }
    public void printRecBody( StxJL jlr) { 
        int ix;
        for( ix= StxJL.SRa; ix<= StxJL.SRe; ix++) {
            if( ix== jlr.s)
                append( String.format( "%8.2f", jlr.c),
                        fCol( jlr.s, jlr.p), bCol( jlr.s, jlr.p), jlr.p);
            else append( "        ");
            append( "|");
        } 
        append( " ");
        append( String.format( "%6.2f", jlr.arg));
    }
     
    public void printRecBody2( StxJL jlr) { 
        for( int ix= StxJL.SRa; ix<= StxJL.SRe; ix++) {
            if( ix== jlr.s2)
                append( String.format( "%8.2f", jlr.c2),
                        fCol( jlr.s2, jlr.p2),
                        bCol( jlr.s2, jlr.p2), jlr.p2);
            else append( "        ");
            append( "|");
        } 
        append( " ");
        append( String.format( "%6.2f", jlr.arg));
    }
    public void printBody( StxJL jlr) { 
        int ix;
        for( ix= StxJL.SRa; ix<= StxJL.SRe; ix++) {
            if( ix== jlr.s)
                append( String.format( "%6.2f", jlr.c),
                        fCol( jlr.s, jlr.p), bCol( jlr.s, jlr.p), jlr.p);
            else append( "      ");
            append( "|");
        }
        append( String.format( "%6.2f", jlr.arg));
    }
     
    public void printBody2( StxJL jlr) { 
        for( int ix= StxJL.SRa; ix<= StxJL.SRe; ix++) {
            if( ix== jlr.s2)
                append( String.format( "%6.2f", jlr.c2),
                        fCol( jlr.s2, jlr.p2),
                        bCol( jlr.s2, jlr.p2), jlr.p2);
            else append( "      ");
            append( "|");
        } 
        append( String.format( "%6.2f", jlr.arg));
    }
    public void printRec( StxJL jlr) { printRec( jlr, 0); }
    public void printRec( StxJL jlr, int pos) {
        if( jlr.s== StxJL.None) return;
        if(( pos== 0)|| ( pos== 1)) {
            append( String.format( "%10s|", jlr.date));
            printRecBody( jlr); append( "\n");                  
        }
        if(( jlr.c2!= 0)&& (( pos== 0)|| ( pos== 2))) {
            append( String.format( "%10s|", jlr.date));
            printRecBody2( jlr);
            append( "\n");
        }
    }

    public void printPivotRec( StxJL jlr, float ptp, float ptp2) { 
        if( jlr.p== true) {
            append( String.format( "%12s|", StxCal.idd2jl( jlr.date)),
                    dCol( jlr.s, jlr.c, jlr.arg, ptp, jlr.p));
            printRecBody( jlr);
        }
        if(( jlr.c2!= 0)&& ( jlr.p2== true)) {
            append( "\n");
            append( String.format( "%12s|", StxCal.idd2jl( jlr.date)),
                    dCol( jlr.s2, jlr.c2, jlr.arg, ptp2, jlr.p));
            printRecBody2( jlr);
        }
    }
    
    public void printSmallBody( StxJL jlr) { 
        int ix;
        for( ix= StxJL.NRa; ix<= StxJL.NRe; ix++) {
            if( ix== jlr.s)
                append( String.format( "%8.2f", jlr.c),
                        fCol( jlr.s, jlr.p), bCol( jlr.s, jlr.p), jlr.p);
            else append( "        ");
            append( "|");
        } 
        append( String.format( "%6.2f", jlr.arg));
    }
     
    public void printSmallBody2( StxJL jlr) { 
        for( int ix= StxJL.NRa; ix<= StxJL.NRe; ix++) {
            if( ix== jlr.s2)
                append( String.format( "%8.2f", jlr.c2),
                        fCol( jlr.s2, jlr.p2),
                        bCol( jlr.s2, jlr.p2), jlr.p2);
            else append( "        ");
            append( "|");
        } 
        append( String.format( "%6.2f", jlr.arg));
    }
    public void printSmallPivotRec( StxJL jlr, float ptp, float ptp2) { 
        if(( jlr.c2!= 0)&& ( jlr.p2== true)) {
            append( String.format( "%12s|", StxCal.idd2jl( jlr.date)),
                    dCol( jlr.s2, jlr.c2, jlr.arg, ptp2, jlr.p));
            printSmallBody2( jlr);
            append( "\n");
        }
        if( jlr.p== true) {
            append( String.format( "%12s|", StxCal.idd2jl( jlr.date)),
                    dCol( jlr.s, jlr.c, jlr.arg, ptp, jlr.p));
            printSmallBody( jlr); append( "\n");
        }
    }

    public void printSmallRec( StxJL jlr, float ptp, float ptp2) { 
        if( jlr.c2!= 0) {
            append( String.format( "%12s|", StxCal.idd2jl( jlr.date)),
                    dCol( jlr.s2, jlr.c2, jlr.arg, ptp2, jlr.p));
            printSmallBody2( jlr);
            append( "\n");
        } else {
            append( String.format( "%12s|", StxCal.idd2jl( jlr.date)),
                    dCol( jlr.s, jlr.c, jlr.arg, ptp, jlr.p));
            printSmallBody( jlr); append( "\n");
        }
    }

    public void printSmallRec2( StxJL jlr) { 
        printSmallRec2( jlr, false);
    }
    
    public void printSmallRec2( StxJL jlr, boolean piv_only) { 
        if( jlr.s>= 1&& jlr.s<= 4) {
            append( String.format( "%9s|", jlr.date));
            printSmallBody( jlr); append( "\n");
        }
        if( jlr.c2!= 0&& jlr.s2>=1 && jlr.s2<= 4&& (!piv_only|| jlr.p2)) {
            append( String.format( "%9s|", jlr.date));
            printSmallBody2( jlr); append( "\n");
        } 
    }
    
    public void printLastLine( StxRec r, double f) {
        append( String.format( "%s %.2f %.2f %.2f %.2f %,.0f  %.2f", r.date,
                               r.o, r.h, r.l, r.c, r.v, f));
    }

    public void newSize( int w, int h) {
        setPreferredSize( new Dimension( w, h));
    }
    public String getText() throws Exception {
        return d.getText( 0, d.getLength());
    }
    public void setTextPanelBackground( Color col) { jtp.setBackground( col);}
    public void runJL( String stk, String sd, String ed, float ff, int w, 
                       int pivs) {
        clear();
        int vw= 1;              
        StxxJL sjl= new StxxJL( stk, sd, ed, ff, w, vw);
        sjl.jl( ed);
        List<Integer> pivots= sjl.pivots( pivs, false);
        for( int piv: pivots)
            printRec( sjl.data( piv));
            // printSmallRec2( sjl.data( piv));
        for( int ix= pivots.get( pivots.size()- 1)+ 1; ix< sjl.size(); ++ix)
            printRec( sjl.data( ix));
            // printSmallRec2( sjl.data( ix));
        printLastLine( sjl.lastDay(), sjl.avgRg()); append("\n");
    }
}
