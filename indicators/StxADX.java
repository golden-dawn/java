import static java.lang.Math.*;
import core.StxTS;
import core.StxRec;

public class StxADX {

    private StxTS<StxRec> data; 
    private int adxd;


    public StxADX(StxTS<StxRec> data) {
        this.data = data;
        adxd = 14;
    }

    public StxADX(StxTS<StxRec> data, int adxd) {
        this.data = data;
        this.adxd = adxd;
    }
    public int adxi(int ix) { return (int) adx(ix); }

    public float adx(int ix) {

        StxRec sr= null, sr_1= null;
        /** The indicators to compute */
        float pdm= 0, ypdm= 0, tpdm= 0;
        float mdm= 0, ymdm= 0, tmdm= 0;
        float tr= 0, ytr= 0, ttr= 0;
        float pdi= 0, mdi= 0;
        float di_diff= 0, di_sum= 0;
        float dx= 0, ydx= 0, tdx= 0, tadx= 0, yadx= 0;
        /** for how many days have we already computed adx? */
        int dx_idx = -1;
        /** utility index */
        int ixx;
        for( ixx = 0; ixx <= ix; ixx++) {
            dx_idx++;
            if( ixx == 0)
                continue;
            sr = data.get(ixx);
            sr_1 = data.get(ixx - 1);
            /** compute +DM and -DM */
            pdm= sr.h- sr_1.h;
            mdm= sr_1.l- sr.l;
            if(( pdm< 0)|| ( pdm< mdm))
                pdm= 0;
            if(( mdm< 0)|| ( mdm< pdm))
                mdm= 0;
            /** Compute True Range */
            tr= sr.trueRange( sr_1);
            /** start adding up +DM, -DM and True Range */
            if( dx_idx<= adxd) {
                tpdm+= pdm;
                tmdm+= mdm;
                ttr+= tr;
            }
            /** got first +DI and -DI; start computing DX*/
            if( dx_idx== adxd) {
                if( ttr== 0) return 0;
                pdi= tpdm/ ttr* 100;
                mdi= tmdm/ ttr* 100;
                di_diff= abs( pdi- mdi);
                di_sum= pdi+ mdi;
                tdx= di_diff* 100/ di_sum;
            }
            /** Smooth average +DI, -DI and True Range */
            if( dx_idx> adxd) {
                ypdm= tpdm; 
                ymdm= tmdm; 
                ytr= ttr; 
                tpdm= ypdm- ypdm/ adxd+ pdm;
                tmdm= ymdm- ymdm/ adxd+ mdm;
                ttr=  ytr-   ytr/ adxd+  tr;
                if(( pdi+ mdi== 0)|| ( ttr== 0))
                    return 0;
                pdi= tpdm* 100/ ttr;
                mdi= tmdm* 100/ ttr;
                dx= abs( pdi- mdi)* 100/ ( pdi+ mdi);
                if( dx_idx< 2* adxd- 1) /** Start adding up DX */
                    tdx+= dx;
                else if( dx_idx== 2* adxd- 1) { 
                    /** Compute first ADX by averaging DX */
                    tdx+= dx;
                    tadx= tdx/ adxd;
                }
                else { /** Smooth average ADX */
                    yadx= tadx;
                    tadx= (( adxd- 1)* yadx+ dx)/ adxd;
                }
            } /** end ( dx_idx> n)) */
//             System.err.printf( "%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f\n", sr.date, sr.h, sr.l, sr.c, tr, pdm, mdm, ttr, tpdm, tmdm, pdi, mdi, di_diff, di_sum, tdx, tadx);
        } /** end fgets */
        return tadx* (( pdi> mdi)? 1: -1);
    }
}
