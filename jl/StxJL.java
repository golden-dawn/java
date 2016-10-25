package jl;

import core.StxRecord;

import java.util.Arrays;
import java.util.List;

public class StxJL extends StxRecord {

    static  public final int None= -1;
    static  public final int SRa= 0;
    static  public final int NRa= 1;
    static  public final int UT= 2;
    static  public final int DT= 3;
    static  public final int NRe= 4;
    static  public final int SRe= 5;
    static  public final int m_NRa= 6;
    static  public final int m_NRe= 7;

    static String[] states= { "SRa", "NRa", "UT", "DT", "NRe", "SRe"};
    public float c, c2, vr, arg;
    public int s, s2, xx;
    public boolean p, p2;
    private Integer px= null, px2= null, nx= null, lx= null;

    public StxJL( String d, float c, float vr, float arg, int s) {
        super( d, c); this.c= c; this.s= s; this.vr= vr; p= false;
        c2= 0; s2= None; p2= false; this.arg= arg;
    }
    public StxJL( String d, float arg) {
        super( d, 0); c= 0; s= None; this.vr= 0; p= false;
        c2= 0; s2= None; p2= false; this.arg= arg;
    }
    public StxJL( String d, float arg, float vi, Integer xx) {
        super( d, 0); c= 0; s= None; this.vr= vi; p= false;
        c2= 0; s2= None; p2= false; this.arg= arg; this.xx= xx;
    }
    public StxJL( StxJL r) {
        super( r.date, 0); c= r.c; s= r.s; vr= r.vr; p= r.p;
        c2= r.c2; s2= r.s2; p2= r.p2; this.arg= r.arg;
    }
    public void split( float s) { c*= s; c2*= s; vr/= s; arg*= s; }
    public void dividend( float d) { c-= d; c2-= d; }

    public float c(){ return( c2== 0)? c: c2; }
    public float cn(){ return( c2== 0)? c: (( s2!= SRe&& s2!= SRa)? c2: c); }
    public float cp(){ return( c2== 0)? c: (( p2== true)? c2: c); }
    public void c( float ic){ if( c== 0) c= ic; else c2= ic; }
    public int s(){ return( c2== 0)? s: s2; }
    public int sp(){ return( c2== 0)? s: (( p2== true)? s2: s); }
    public int sn(){ return( c2== 0)? s: (( s2!= SRe&& s2!= SRa)? s2: s); }
    public boolean hasState( int state) { return ( s== state)|| ( s2== state); }
    public void s( int is){ if( c== 0) s= is; else s2= is;}
    public float arg() { return arg; }
    // public void s( int is){
    //  System.err.print( "Setting state for date "+ date);
    //  if( c== 0) { s= is; System.err.println( " s= "+ is);}
    //  else{ s2= is; System.err.println( " s2= "+ is);}
    // }
    public boolean p(){ return( c2== 0)? p: ( p2? p2: p); }
    public void p( boolean ip){ p( ip, null);}
    public void p( boolean ip, Integer ix){
        if(( c2== 0)|| ( s2== SRa|| s2== SRe)) p= ip; else p2= ip;
        if( ip&& ix!= null) { 
            if(( c2== 0)|| ( s2== SRa|| s2== SRe)) px= ix; else px2= ix;
        }
    }
    public List<Integer> pxx() { return Arrays.asList( px, px2);}
    public Integer xx() { return xx; }
    public void lnx( Integer nx, Integer lx) { this.nx= nx; this.lx= lx; } 
    public String toString() {
        StringBuilder sb= new StringBuilder();
        if( s== None)
            sb.append( String.format( "%9s %60s %10.2f\n", date, " ", vr));
        else
            sb.append( String.format( "%9s %"+ ( 10+ 10* s)+ ".2f %"+
                                      ( 10+ 10* ( 5- s))+ ".2f %s %s\n",
                                      date, c, vr, states[ s], p? "p": ""));
        if( s2!= None) 
            sb.append( String.format( "%9s %"+ ( 10+ 10* s2)+ ".2f %"+
                                      ( 10+ 10* ( 5- s2))+ ".2f %s %s\n",
                                      date, c2, vr, states[ s2], p2? "p": ""));         
        return sb.toString();
        // return ( s== None)? "":
        //     String.format( "%9s %"+ ( 10+ 10* s)+ ".2f %"+ ( 10+ 10* ( 5- s))+ ".2f \n", date, c, vr);
    }
    public String toStr() {
        return ( s== None)? "":
            String.format( "%9s %"+ ( 10+ 10* s)+ ".2f %"+ ( 10+ 10* ( 5- s))+ ".2f", date, c, vr);
    }
    public String dbgStr() {
        StringBuilder sb= new StringBuilder();
        sb.append( date).append( " ").append( c).append( " ").
            append( state( s)).append( " ").append( p);
        if( c2> 0)
            sb.append( " ||| ").append( c2).append( " ").append( state( s2)).
                append( " ").append( p2);
        return sb.toString();
    }
    public String state( int st) {
        switch( st) {
        case SRa: return "SRa"; case NRa: return "NRa";
        case UT:  return "UT"; case DT:  return "DT";
        case NRe: return "NRe"; case SRe: return "SRe";
        }
        return "None";
    }
    public Integer nx() { return nx;}
    public Integer lx() { return lx;}
    public static void main( String[] args) {
        //               StxJL sjl= new StxJL( "28-Dec-09", 50, 0, SRa);
        //               System.err.println( sjl.toString());
        //               sjl= new JLRec( "28-Dec-09", 50, 0, NRa);
        //               System.err.println( sjl.toString());
        //               sjl= new JLRec( "28-Dec-09", 50, 0, UT);
        //               System.err.println( sjl.toString());
        //               sjl= new JLRec( "28-Dec-09", 50, 0, DT);
        //               System.err.println( sjl.toString());
        //               sjl= new JLRec( "28-Dec-09", 50, 0, NRe);
        //               System.err.println( sjl.toString());
        //               sjl= new JLRec( "28-Dec-09", 50, 0, SRe);
        //               System.err.println( sjl.toString());
    }
}
