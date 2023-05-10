package iaea.nds.nuclides;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Vector;

import android.content.res.Resources;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;

import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;

import iaea.nds.nuclides.db.entities.Nuclides;
import iaea.nds.nuclides.db.SQLBuilder;
import iaea.nds.nuclides.db.CumFYForDetail;
import iaea.nds.nuclides.db.DecayForDetails;
import iaea.nds.nuclides.db.RadiationForDetail;
import iaea.nds.nuclides.db.entities.Thermal_cross_sect;

import static iaea.nds.nuclides.Formatter.resources;


public class Nuclide {

    private String DECAY_CHAIN_TAG = Formatter.DECAY_CHAIN_TAG();

    public boolean isFrench = false;

    Nuclides mynuc = null;
    public DecayForDetails[] nucdecs = null;
    public DecayForDetails[] nucparents = null;
    public RadiationForDetail [] nucrad = null;
    public CumFYForDetail[] nuccfy = null;
    public Thermal_cross_sect[] nuctcs = new Thermal_cross_sect[0];

    boolean ismeta = false;
    boolean showdecaychain = false;

    boolean isbqg = true;

    public static String UNK = "?";

    public static String alpha = "\u03B1" ;
    public static String beta = "\u03B2" ;
    public static String gamma = "\u03B3" ;
    public static String GAMMA = "\u0393" ;
    public static String epsilon = "\u03B5";
    public static String pi = "\u03C0" ;
    public static String DELTA = "\u0394";
    //public static String pm = " \u00B1 ";
    public static String micro = "\u00B5";

    public static String approx = "\u2245";
    public static String gt = "\u003E";
    public static String ge = "\u2265";
    public static String lt = "\u003C";
    public static String le = "\u2264";

    String leftOffset = "";//"    " ;
    String ndsTag = "";
    String tentative = "";

    int nuclideLabelColor = 0;
    int decayradtbltitleColor = 0;

    int decayradselecColor = 0;

    /* RESERVED CAHARCTERS - DO NOT USE IN THE DETAIL TEXT*/
    public static String spanNuclideLabelColor = "^";
    public static String spanExponential = "§";
    String spanSmallSize = "smsz";
    public static String spanClickable = "±";
    String spanBold = "@";
    String spanRadiation ="*";
    String spanBackgroundColor = "clr";
    String spanSel = "tzq";


    MyClickableSpan[] clickSpans = new MyClickableSpan[0];
    String allclickablenucid = "";
    String allclickablerowid = "";

    public Nuclide(Nuclides mynuc ) {
        this.mynuc = mynuc;
    }


    private boolean isMeta(String nucid){
        return nucid.indexOf("m") != -1;
    }


    /**
     * if asymmetric, return the symmetrised
     * @param value
     * @param ensdf_unc
     * @return
     */
    public   String unc_ensdf_to_absolute(String value, String ensdf_unc) {
        if(value == null || value.length() == 0){
            return "";
        }

        if(ensdf_unc == null || ensdf_unc.length() == 0){
            return "";
        }

        // symmetrise
        try {
        if(ensdf_unc.indexOf("-") != -1){
            if(ensdf_unc.indexOf("+") != -1 ){
                ensdf_unc = ensdf_unc.substring(1);
            }
            String[] dm = ensdf_unc.split("-");
            ensdf_unc = ((int) ((Integer.parseInt(dm[0]) + Integer.parseInt(dm[1])) /2 )) +"";
        }} catch (Exception e){
            return "";
        }

        /*prefix E suffix where prefix and suffix might have a sign*/
        int exp_offset = value.toUpperCase().indexOf("E");
        String prefix = (exp_offset != -1 ? value.substring(0, exp_offset) : value);
        String exp = (exp_offset != -1 ? value.substring(exp_offset) : "");

        String abs_unc = ensdf_unc;
        try{
            BigDecimal bdprefix = new BigDecimal(prefix);
            BigDecimal bdensdf_unc = new BigDecimal(ensdf_unc);
            BigDecimal bd_abs_unc = bdensdf_unc.movePointLeft(bdprefix.scale());

            abs_unc = bd_abs_unc.toString() + exp;

        } catch (Exception e){

        }

        return abs_unc;

    }
    public  String[] round_abs_to_significant(BigDecimal abs_value_unscaled, BigDecimal abs_unc_unscaled, int round_upper_limit){
        double abs_unc_d= abs_unc_unscaled.doubleValue();

        double digits_d = Math.log10(round_upper_limit/abs_unc_d);
        int digits = (int) digits_d;
        if(digits_d < 0){
            digits = digits - 1;
        }
        long unc_ensdf = (long) (abs_unc_d * Math.pow(10, digits));

        double round_val = (double) ( abs_value_unscaled.doubleValue() *  Math.pow(10,digits) );

        String round_str = ((long)round_val)+"";

        int add_digits = round_str.length() - 1;
        if(add_digits > 0){
            round_str = round_str.substring(0,1) + "." + round_str.substring(1);;
        }

        if(digits != 0){
            digits = -digits ;
        }
        digits = digits + add_digits;
        round_str = round_str + "E";
        round_str = round_str + digits;

        String unc_ret =  unc_ensdf +"";

        return new String[] {round_str ,unc_ret};
    }

    public  String[] round_abs_to_significant_o(BigDecimal abs_value_unscaled, BigDecimal abs_unc_unscaled, int round_upper_limit){
        String[] ret = new String[] {"","",""};

        double ENSDF_UNC_AP_LIMIT_PERCENT = 80.;
        int ROUND_HALF_POLICY = BigDecimal.ROUND_HALF_UP;


        if(abs_unc_unscaled == null){
            ret[0] = abs_value_unscaled.toString();
            return ret;

        }

        /* cases like 81.2 pm 28.3*/
        if(abs_unc_unscaled.doubleValue() >= round_upper_limit) {
            ret[0] = Math.round(abs_value_unscaled.doubleValue())+"";
            ret[1] = Math.round(abs_unc_unscaled.doubleValue())+"";
        }

        /*  threshold for AP is ~80% if I remember well from Balraj */
        if(Math.abs(abs_value_unscaled.doubleValue()) * ENSDF_UNC_AP_LIMIT_PERCENT / 100 < Math.abs(abs_unc_unscaled.doubleValue())  ){
            ret[0] = abs_value_unscaled.toString();
            ret[2] = "~";
            return ret;

        }

        /* remove exponential notation*/
        String str_int = abs_value_unscaled.toPlainString() ;
        String str_unc = abs_unc_unscaled.toPlainString();
        BigDecimal _int_abs_unscaled = new BigDecimal(str_int);
        BigDecimal _int_unc_unscaled = new BigDecimal(str_unc);

        /* correct cases like 0.588 pm 0.21*/
        int scale_int = _int_abs_unscaled.scale();
        int scale_unc = _int_unc_unscaled.scale();
        int scale_diff = (scale_int - scale_unc > 0 ? scale_int - scale_unc : 0);
        for (int i = 0; i < scale_diff; i++) {
            str_unc += "0";
        }


        /* now cases like 0.588 pm 0.210000*/
        if(scale_int < scale_unc) {
            str_unc = abs_unc_unscaled.setScale(scale_int, ROUND_HALF_POLICY).toPlainString();
        }

        _int_unc_unscaled = new BigDecimal(str_unc);
        /* start with scale 0 */
        int scale = 0;
        BigDecimal bd_int_abs = _int_abs_unscaled.setScale(scale, ROUND_HALF_POLICY);
        BigDecimal bd_unc_scaled = _int_unc_unscaled.setScale(scale,ROUND_HALF_POLICY);

        /* convert to ensdf style*/
        String unc_ensdf = bd_unc_scaled.toPlainString().replaceAll("\\.", "").replaceFirst("^0+(?!$)", "");

        /* now reduce the scale until the ensdf uncertainty upper limit*/
        while((new BigDecimal(unc_ensdf)).doubleValue()  < round_upper_limit){
            scale++;
            /* cases 5.1 pm 0.2  lead to infinite loop*/
            if(scale > _int_unc_unscaled.scale()){
                break;
            }
            bd_int_abs = _int_abs_unscaled.setScale(scale, ROUND_HALF_POLICY);
            bd_unc_scaled = _int_unc_unscaled.setScale(scale,ROUND_HALF_POLICY);
            unc_ensdf = bd_unc_scaled.toPlainString().replaceAll("\\.", "").replaceFirst("^0+(?!$)", "");
        }

        while((new BigDecimal(unc_ensdf)).doubleValue() >= round_upper_limit){
            scale--;
            bd_int_abs = _int_abs_unscaled.setScale(scale, ROUND_HALF_POLICY);
            bd_unc_scaled = _int_unc_unscaled.setScale(scale, ROUND_HALF_POLICY);
            unc_ensdf = bd_unc_scaled.toPlainString().replaceAll("\\.", "").replaceFirst("^0+(?!$)", "");
            /* when the scale is negative , it brings unc_ensdf in exponential form */
            for (int i = 0; i < -scale; i++) {
                if(unc_ensdf.endsWith("0")){
                    unc_ensdf = unc_ensdf.substring(0,unc_ensdf.length() - 1);
                }
            }
        }

        /* create the  measurement  */
        ret[0] = bd_int_abs.toPlainString();
        ret[1] = unc_ensdf;

        return ret;

    }


    /**
     * given 135xem1 returns {"135XE","m1"}
     * @param nucid
     * @return
     */
    private String[] splitMetaSuffix(String nucid){
        String[] ret = new String[]{"",""};
        if (! isMeta(nucid)){
            ret[0] = nucid;
            return ret;
        }
        String[] dm = nucid.split("m");
        ret[0] = dm[0];
        if(dm.length == 1){
            ret[1] = "m";
            return ret;
        }

        ret[1] = "m"+dm[1];
        return ret;

    }

    /**
     * from 135XEm1 returns 135m1-Xe
     * @param nucid
     * @return
     */

    private String formatNucid(String nucid){
        if(nucid == null || nucid.length() < 3) return nucid;

        boolean ismeta = isMeta(nucid);

        String ch = nucid.substring(0, 1);
        int i = 0;
        String mass = "";
        while (i < nucid.length() -1) {
            if ("0123456789".indexOf(ch) != -1) {
                mass += ch;
                i++;
                ch = nucid.substring(i, i + 1);

                continue;
            }

            i++;
            ch = nucid.substring(i, i + 1);
            ch = ch.toLowerCase();
            nucid = nucid.substring(0,i) + ch + nucid.substring(i+1);

        }
        if (ismeta){
            String[] dm = splitMetaSuffix(nucid);
            nucid = nucid.replaceAll(mass, mass + dm[1] +"-");
            nucid = nucid.substring(0, nucid.length() - dm[1].length());
        } else{
            nucid = nucid.replaceAll(mass, mass + "-");
        }
        return nucid;
    }

    private String getDecayDisplay(){
        String ret = "";

        if(nucdecs == null || nucdecs.length == 0){
            return ret;
        }
        int decCode ;
        showdecaychain = false;
        String unknown = "? ";

        for (int i = 0; i < nucdecs.length ; i++) {
            decCode = nucdecs[i].getDec_type();
            String dcy = Formatter.decodeDecay(decCode + "");

            String oper = nucdecs[i].getPerc_oper();
            oper = (oper == null ? "" : oper);
            String br = prepareExponential(prepareString(nucdecs[i].getPerc()));
            if (br.equals("")) {
                br = unknown;
            } else {
                showdecaychain = true;
            }

            String unc = nucdecs[i].getUnc();
            unc = (unc == null ? "" :  unc );
            String dau = formatNucid(nucdecs[i].getDaughter_nucid());

            if (decCode != SQLBuilder.DEC_TYPE_SF) {
                allclickablenucid += dau + ";";
                allclickablerowid += nucdecs[i].getDaughter_pk()+ ";";
            } else {
                dau = null;
            }

            dau = (dau == null ? "" : " \u2192 " + spanClickable + dau + spanClickable);

            ret += getLeftOffset() + spanTagForDecay(decCode) + dcy + " " + oper + br + (br.equals(unknown) ? "" : "%") + unc  + dau + "\n";
        }

        return decodeOper(ret);

    }
    private String getColorHexForDecay(int dcy){
        int[] color = Formatter.getColorRGBFromDecayCode(dcy);
        return String.format("#%02X%02X%02X", color[0], color[1], color[2]);
    }

    private String spanTagForDecay(int dcy){
        return spanBackgroundColor + getColorHexForDecay(dcy) + spanBackgroundColor + " ";
    }
    private String getParentDisplay(){
        String ret = "";
        if(nucparents == null){
            return ret;
        }

        /* ad hoc for 116 In */
        if(mynuc.getNucid().equals("116IN") && mynuc.getL_seqno() == 0){
            return ret;
        }
        int decCode;

        for (int i = 0; i < nucparents.length ; i++) {

            decCode = nucparents[i].getDec_type();
            String dcy = Formatter.decodeDecay(decCode + "");

            String oper = nucparents[i].getPerc_oper();
            oper = (oper == null ? "" : oper);

            String br = prepareExponential(prepareString(nucparents[i].getPerc()));
            br = (br.equals("") ? "? " : br + " %");

            String unc = nucparents[i].getUnc();
            unc = (unc == null ? "" : unc);

            String parent = formatNucid(nucparents[i].getNucid());
            allclickablenucid += parent + ";";
            allclickablerowid += nucparents[i].getParent_pk()+ ";";

            parent = (parent == null ? "" : spanTagForDecay(decCode) + spanClickable + parent + spanClickable);

            ret += getLeftOffset() + parent + " " + dcy + " " + oper + br + unc + "\n";
        }

        return decodeOper(ret);

    }


    private String decodeRadType(String s){
        if(s.equals("A")) return alpha;
        if(s.equals("B+")) return beta + "+";
        if(s.equals("B-")) return beta + "-";
        if(s.equals("G")) return gamma;
        if(s.equals("X")) return "X";
        if(s.equals("AN")) return "Annihilation";
        if(s.equals("AU")) return "Auger Electrons";
        if(s.equals("CE")) return "Conversion Electrons";
        return "";

      //  return s.replaceAll("A", alpha).replaceAll("B", beta).replaceAll("G", gamma);
    }

    private String getRadiationDecayHeader(String dcy){

        String ret = Formatter.decodeDecay(dcy) + " " + Formatter.decayLbl() +"  ";
        if(isFrench )	{
            ret = Formatter.decayLbl()  +"  " + Formatter.decodeDecay(dcy) + " " ;
        }

        if (ret.indexOf(beta) != -1){
            ret += "";//" average energy ";
        }
        return ret;
    }
    /*
     * Gamma from spontaneous fission removed
     * 24NAm no gammas from IT
     * type_a=A
              B+
              B-
              G
              AN
              X
              AU
              CE
     */
    private String getRadDisplay(){

        String ret = "";
        if(nucrad == null || nucrad.length == 0){
            return ret;
        }

        boolean isbeta = true;
        boolean isbetap = true;
        boolean isX = true;
        boolean isAnnih = true;
        boolean isAu = true;
        boolean isCe = true;
        boolean isGm = true;

        String dec_type = "";
        String dec_type_old = "";
        String rad_typeDB = "";
        String rad_typeDBOld = "";

        Vector<String[]> v = new Vector<String[]>();

        rad_typeDBOld = rad_typeDB;
        dec_type_old = dec_type;


        for (int i = 0 ; i < nucrad.length ; i++){

            dec_type = nucrad[i].getDec_type();
            rad_typeDB = nucrad[i].getType_a();

            isbeta = (rad_typeDB.indexOf("B-") != -1);
            isbetap = (rad_typeDB.indexOf("B+") != -1);
            isX = (rad_typeDB.indexOf("X") != -1);

            isAnnih = (rad_typeDB.indexOf("AN") != -1);
            isAu = (rad_typeDB.indexOf("AU") != -1);
            isCe = (rad_typeDB.indexOf("CE") != -1);
            isGm = (rad_typeDB.indexOf("G") != -1);

            // new decay, write the header
            if( ! dec_type.equals(dec_type_old)){
                dec_type_old = dec_type;
                rad_typeDBOld = rad_typeDB ;
                ret = getRadDisplay_head(v,isbetap,isbeta, isX,dec_type,rad_typeDB,ret);
            }
            // new radiation type, write the header
            if(!rad_typeDB.equals(rad_typeDBOld)){
                rad_typeDBOld = rad_typeDB;
                ret = getRadDisplay_head(v,isbetap,isbeta, isX,null,rad_typeDB,ret);
            }

            getRadDisplay_lines(v, nucrad[i], isbeta || isbetap, isX || isAu || isCe);

        }
        ret = getRadDisplay_head(v,isbeta, isbetap, isX,null,null,ret);

        return ret;

    }

    /**
     * adds a radiation line to the vector
     * @param v Vector with the lines
     * @param nuc_rad RadiationForDetail radiation to be written
     * @param isbeta
     * @param isX
     */
    private void getRadDisplay_lines(Vector<String[]> v, RadiationForDetail nuc_rad, boolean isbeta, boolean isX){
        String span_selected = nuc_rad.getRad_selected().equals("Y") ? spanSel : "";

        String en = nuc_rad.getEnergy();
        en = (en == null ? "? " : en + addUnc(nuc_rad.getEnergy_unc() ));
        String inten = nuc_rad.getIntensity();
        String int_unc = nuc_rad.getIntensity_unc();
        inten = (inten == null ? "?" : unc_as_operator(int_unc) + inten + (addUnc(int_unc)));
        if(isbeta ){
            String endpoint = nuc_rad.getEndpoint();
            endpoint = (endpoint == null ? "" : endpoint + (addUnc(nuc_rad.getEndpoint_unc())));
            v.add(new String[]{span_selected + en + span_selected, inten, endpoint});
        } else if (isX) {
            v.add(new String[]{span_selected + en + span_selected, decode_shell(nuc_rad.getType_c() ), inten});
        }else{
            v.add(new String[]{span_selected + en + span_selected, inten});
        }
    }

    /**
     * an header of the radiations table
     * First write the lines before the new header stored in v, then add the header
     * @param v Vector with the lines before the new header
     * @param dec_type String if not null adds the header for a new decay type
     * @param rad_typeDB type of radiation
     * @param ret String already prepared
     * @return String the additional part
     */
    private String getRadDisplay_head( Vector<String[]> v , boolean isbetapp, boolean isbetaa, boolean isXx, String dec_type,String rad_typeDB, String ret ){

        String[] col_titles = resources.getString(R.string.table_title_rad).split("AND");

        if(!v.isEmpty()) {
            //String[][] tbl = new String[v.size()][(isbetap || isbeta  ? 3 : 2)];
            String[][] tbl = new String[v.size()][v.elementAt(0).length];

            for (int j = 0; j < tbl.length; j++) {
                tbl[j] = v.elementAt(j);
            }
            v.clear();
            ret += spanRadiation + table(tbl) + spanRadiation;
        }

        if(dec_type != null)
            ret += spanTagForDecay(Integer.parseInt(dec_type)) + resources.getString(R.string.from)  + " " + getRadiationDecayHeader(dec_type) + "\n";

        if(rad_typeDB != null) {
            boolean isbeta = (rad_typeDB.indexOf("B-") != -1);;
            boolean isbetap = (rad_typeDB.indexOf("B+") != -1);;
            boolean isX = (rad_typeDB.indexOf("X") != -1);;
            boolean isAnnih = (rad_typeDB.indexOf("AN") != -1);
            boolean isAu = (rad_typeDB.indexOf("AU") != -1);
            boolean isCe = (rad_typeDB.indexOf("CE") != -1);
            boolean isGm = (rad_typeDB.indexOf("G") != -1);


            ret += getLeftOffset() + spanNuclideLabelColor + decodeRadType(rad_typeDB) + spanNuclideLabelColor;
            if (isbetap) {
                v.add(new String[]{spanBold + col_titles[0] + spanBold, spanBold + col_titles[1] + spanBold, spanBold + col_titles[3] + spanBold});
            } else if (isbeta) {
                v.add(new String[]{spanBold + col_titles[0] + spanBold, spanBold + col_titles[1] + spanBold, spanBold + col_titles[2] + spanBold});
            } else if (isX || isAu || isCe) {
                v.add(new String[]{spanBold + col_titles[4] + spanBold, spanBold + spanSmallSize + "Transition" + spanSmallSize + spanBold, spanBold + col_titles[1] + spanBold});
            } else if( isAnnih){
                v.add(new String[]{spanBold +col_titles[4] + spanBold, spanBold + col_titles[1] + spanBold});
            } else {
                v.add(new String[]{spanBold +col_titles[4] + spanBold, spanBold + col_titles[1] + spanBold});
            }
        }
        return ret;

    }

    /**
     * Prepares a table to be displayed monospaced (e.g. the radiations)
     *
     * @param tbl String[][] : table
     * @return String text where each row has the same # of char, and columns are terminated with new line
     */
    private String table(String[][] tbl){

        float sp = 1;//bounds.width();
        int tblrowlen = tbl.length;
        int tblcollen = tbl[0].length;
        float[] max_col_cell_len = new float[tblcollen];

        float[][] cell_str_len = new float[tblrowlen][tblcollen];
        for (int i = 0; i < tblcollen; i++) {
            for (int j = 0; j < tblrowlen; j++) {
                //p.getTextBounds(tbl[j][i],0,tbl[j][i].length(),bounds);
                try {
                    tbl[j][i] = (tbl[j][i] == null ? " " : tbl[j][i]);
                } catch (Exception e){

                }
                cell_str_len[j][i] = tbl[j][i].length();//bounds.width()  ;
                if(tbl[j][i].indexOf(spanSmallSize) != -1 || tbl[j][i].indexOf(spanSel) != -1){ // this is because of extra character for the spans
                    cell_str_len[j][i] = cell_str_len[j][i] - 12;
                }
                if(tbl[j][i].indexOf(spanSel) != -1){ // this is because of extra character for the spans
                    cell_str_len[j][i] = cell_str_len[j][i] + 5;
                }
                max_col_cell_len[i] = ( cell_str_len[j][i] > max_col_cell_len[i] ? cell_str_len[j][i]  : max_col_cell_len[i]);
            }
        }

        String ret = "";
        for (int i = 0; i < tblrowlen; i++) {
            for (int j = 0; j < tblcollen; j++) {
                int n = (int) ((max_col_cell_len[j] - cell_str_len[i][j]) / (sp));
                for (int k = 0; k < n; k++) {
                    tbl[i][j] += " "  ;
                }
                tbl[i][j] += " "  ;
            }
            tbl[i][tblcollen-1] += '\n';
        }

        for (int i = 0; i < tblrowlen; i++) {
            for (int j = 0; j < tblcollen; j++) {
                ret += tbl[i][j];
            }

        }

        return '\n' + ret;

    }
    /**
     *
     *
     * */
    private String getCumFYDisplay(){
        String ret = "";
        if(nuccfy == null || nuccfy.length == 0){
            return ret;
        }

        String[][] tbl = new String[nuccfy.length][3];

        int i = 0;
        String parent =  formatNucid(nuccfy[i].getParent_nucid());
        tbl[0][0]  = spanClickable +  parent + spanClickable ;
        tbl[0][1] = nuccfy[i].getTher_yield_num()+"";
        tbl[0][2] =  "(" + nuccfy[i].getTher_yield_unc() + ")";

        allclickablenucid += parent  + ";";
        allclickablerowid += nuccfy[i].getParent_rowid()+ ";";


        for (i = 1 ; i < nuccfy.length ; i++){
            parent =  formatNucid(nuccfy[i].getParent_nucid());

            allclickablenucid += parent  + ";";
            allclickablerowid += nuccfy[i].getParent_rowid() + ";";


            tbl[i][0]  =  spanClickable + parent + spanClickable ;
            tbl[i][1] =  nuccfy[i].getTher_yield_num()+"";
            tbl[i][2] =  "(" + nuccfy[i].getTher_yield_unc() + ")";

        }

        ret = table(tbl);
        return ret;

    }

    public  static String decodeOper(String s){
        if(s == null) return s;
        return s.replaceAll("STABLE", resources.getString(R.string.stable_prompt)).replaceAll("EQ", "").replaceAll("AP", approx).replaceAll("GT", gt).replaceAll("LT", lt).
                replaceAll("LE", le).replaceAll("GE", ge).trim().replaceAll("CA","");
    }

    /**
     * if the uncertainty field contains a limit
     * @param s
     * @return
     */
    public String unc_as_operator(String s){
        String ret = decodeOper(s);
        if(ret == null || ret.equals(s)){
            ret = "";
        }
        return ret;
    }

    public  static boolean hasOper(String s){
        if(s==null) return false;
        if(!s.equals(decodeOper(s))){
            return true;
        }
        return false;
    }

    public  static String removeNull(String str){
        return (str == null ? "" : str);
    }


    /**
     * converts an uncertainty from absolute to ensdf
     */
    private String absToEnsdf(String unc){
        String ret = "";

        if( unc == null || unc.length() == 0){
            return ret;
        }


        //3e-4 : return 3
        int off = unc.toLowerCase().indexOf("e");
        if( off != -1){
            ret = unc.substring(0, off);
            ret = ret.replaceAll("\\.", "");
            while(ret.startsWith("0")){
                ret = ret.substring(1);
            }

            return ret;
        }

        ret = unc.replaceAll("\\.", "");
        String tk = ret.substring(0,1);
        while(tk.equals("0") ) {
            ret = ret.substring(1);
            if(ret.length() == 0){
                break;
            }
            tk = ret.substring(0,1);
        }
        return ret;
    }

    public  static String addUnc(String unc){
        if(unc == null || unc.length() == 0 || hasOper(unc)){
            return "";
        }
        return " (" + unc + ")";
    }

    private String prepareString(String value){

            return  value == null ? "" : value;

    }


    private String convertAtomicMass(String atomicMass){
        if( atomicMass == null){
            return "";
        }
        BigDecimal tenSix = new BigDecimal("1000000");
        String strAtomic = atomicMass;
        BigDecimal atomic = new BigDecimal(strAtomic);
        atomic = atomic.divide(tenSix);
        return "" + atomic;

    }

    public static String decodeUnits(String unit){
        unit = unit.toUpperCase();
        String[] units = new String[]{"D","FS","KEV","MEV","EV","MS","AS","NS","PS","US","Y","M","S","H","mineV"};
        String[] uniDe = new String[]{"d","fs","keV","MeV", "eV","ms","as","ns","ps",micro+"s","Y","min","s","h","MeV"};
        if(unit != null){
            for (int i = 0; i < uniDe.length; i++) {
                unit = unit.replaceAll(units[i], uniDe[i]);
            }
        }
        return unit;
    }

    public static String prepareExponential(String s){
        int o = s.indexOf("E+");
        String expon = "";
        if(o != -1){
            expon = s.substring(o + 2);
            if(expon.equals("0")){
                s = s.substring(0, o);
            } else {
                s = s.substring(0, o) + " 10" + spanExponential + s.substring(o + 2) + spanExponential;
            }
        }
        o = s.indexOf("E-");
        if(o != -1){
            expon = s.substring(o + 1);
            if(expon.equals("0")){
                s = s.substring(0, o);
            } else {
                s = s.substring(0, o) + " 10" + spanExponential + s.substring(o + 1) + spanExponential;
            }
        }

        o = s.indexOf("E");
        if(o >0 && "0123456789".indexOf(s.substring(o-1,o)) != -1 && "0123456789".indexOf(s.substring(o+1,o+2)) != -1){
            expon = s.substring(o + 1);
            if(expon.equals("0")){
                s = s.substring(0, o);
            } else {
                s = s.substring(0, o) + " 10" + spanExponential + s.substring(o + 1) + spanExponential;
            }
        }

        return Html.fromHtml(s).toString();


    }

    /**
     * H-L for the chart
     * @param half_life
     * @param unc
     * @param unit
     * @return
     */
    public static String buildHalfLifeForJava(String half_life, String unc, String unit){

       if(half_life == null && half_life.length() == 0){
           return "";
       }

        String halfLife =  (hasOper(unc) ? decodeOper(unc) : " ") + removeNull(half_life);

        halfLife = removeNull(decodeOper((halfLife)));
        halfLife += addUnc(unc );
        halfLife += " " + decodeUnits(removeNull(unit ));

        /*correct db and remove !*/
        halfLife = halfLife.replaceAll("MS", "ms");
        return halfLife;

    }

    public  String buildHalfLife(String half_life, String unc, String unit){
        if(half_life == null ){
            return "";
        }
        if ( unc == null ){
            unc = "";
        }
        if ( unit == null){
            unit = "";
        }
        String halfLife =  (hasOper(unc) ? decodeOper(unc) : " ") +removeNull(half_life);
        halfLife = removeNull(decodeOper(prepareExponential(halfLife)));
        halfLife += " " + decodeUnits(removeNull(unit ));
        if(unc == null || unc.length() == 0 || hasOper(unc)){
            unc =  "";
        }
        halfLife +=  " " + (unc );

        return halfLife;

    }

    private String valAndUnc(String val, String unc){
       return prepareString(val) + addUnc(absToEnsdf(unc));
    }

    public String[] thermalCsRi(){

        if(nuctcs == null || nuctcs.length == 0) return new String[]{"",""};

        String[] titlesCs = ("(n,"+gamma+");Maxw.(n,"+gamma+");(n,e);(n,"+alpha+");(n,p);(n,fission);").split(";") ;
                //"D/D0/D1;"
        String[] titlesRi =  new String[] {resources.getString(R.string.radiative_capture), "alpha" ,"proton", resources.getString(R.string.fission), resources.getString(R.string.absorption)};

        Thermal_cross_sect tcs = nuctcs[0];

        String[] valsCs = new String[]{valAndUnc(tcs.getNg(),tcs.getNg_unc()) ,
                valAndUnc(tcs.getMaxw_ng(),tcs.getMaxw_ng_unc()) ,
                valAndUnc(tcs.getNel(),tcs.getNel_unc()) ,
                valAndUnc(tcs.getNa(),tcs.getNa_unc()) ,
                valAndUnc(tcs.getNp(),tcs.getNp_unc()) ,
                valAndUnc(tcs.getNf(),tcs.getNf_unc()) };
               // valAndUnc(tcs.getDd0d1(),tcs.getDd0d1_unc()) ,
        String[] valsRi = new String[]{valAndUnc(tcs.getIc(),tcs.getIc_unc()) ,
                valAndUnc(tcs.getIa(),tcs.getIa_unc()) ,
                valAndUnc(tcs.getIp(),tcs.getIp_unc()) ,
                valAndUnc(tcs.getIff(),tcs.getIff_unc()) ,
                valAndUnc(tcs.getIabs(),tcs.getIabs_unc()) };
               // ,valAndUnc(tcs.getMacs30(),tcs.getMacs30_unc())
       // };

        String retCs = "";
        for (int i = 0; i < valsCs.length; i++) {
            if(valsCs[i].trim().length() > 0){
                retCs += (valsCs[i].length() > 0 ? titlesCs[i] + " " + valsCs[i] + "\n" : "");
            }
        }
        retCs = retCs.length() > 0 ? retCs.substring(0,retCs.length() -1) :retCs;

        String retRi = "";
        for (int i = 0; i < valsRi.length; i++) {
            if(valsRi[i].trim().length() > 0){
                retRi += (valsRi[i].length() > 0 ? titlesRi[i] + " " + valsRi[i] + "\n" : "");
            }
        }
        retRi = retRi.length() > 0 ? retRi.substring(0,retRi.length() -1) :retRi;
        return new String[]{retCs, retRi};

    }

    /**
     * sp. act (Bq/g) = dec const * Num avog / molar mass
     * molar mass = atomic mass * Mu = 0.99999999965(30)×10−3 kg⋅mol−1 (molar mass constant)
     * @param hl
     * @param hl_unc_ensdf
     * @param hl_sec
     * @param ame
     * @param ame_unc_ensdf
     * @return
     */
    public String[] decay_constant_and_specific_activity(String hl, String hl_unc_ensdf, Double hl_sec, String ame, String ame_unc_ensdf){

        String[] ret = new String[]{"","","",""};

        if(hl == null || hl.trim().length() == 0){
            return ret;
        }

        try{
            Double.parseDouble(hl);
        } catch (Exception e ) {
            return ret;
        }

        try{
            boolean nounc = false;
            if(hl_unc_ensdf == null || hl_unc_ensdf.trim().length() == 0){
                hl_unc_ensdf ="1";
                nounc = true;
            }

            String unc_abs = unc_ensdf_to_absolute(hl, hl_unc_ensdf);
            if(unc_abs.length() == 0){
                unc_abs = "0";
            }
            double unc_sec_d = Double.parseDouble(unc_abs) / Double.parseDouble(hl) * hl_sec;


            double dec_const = (0.693/hl_sec);
            double dec_const_unc = (unc_sec_d / hl_sec ) * dec_const  ;

            String[] ret_dec_const = round_abs_to_significant(new BigDecimal(dec_const+""), new BigDecimal(dec_const_unc+""),35);


           // if(ret_dec_const[0].length() > 5){
              //  DecimalFormat df = new DecimalFormat("0.########E0");
              //  try {
                  //  ret_dec_const[0] = df.format(Double.parseDouble(ret_dec_const[0]));
                    ret_dec_const[0] = prepareExponential(ret_dec_const[0]);
              //  } catch(Exception e){

               // }
           // }

            double AVOGADRO = 6.02214076E23; //mol -1
            double MOLAR_MASS = 0.99999999965E-3;//(30)×10−3 kg⋅mol−1
            // i need grams
            //MOLAR_MASS = MOLAR_MASS * 1E3;
            String ame_unc_abs = unc_ensdf_to_absolute(ame, ame_unc_ensdf);

            double mol_mass = (Double.parseDouble(ame) ) /1E6;
            double mol_mass_unc = (Double.parseDouble(ame_unc_abs)  /1E6);

            //3.7×E10 Bq = 1 Ci
            double sp_act = dec_const  / mol_mass;
            double sp_act_unc = Math.sqrt(Math.pow(dec_const_unc/dec_const ,2) + Math.pow(mol_mass_unc/mol_mass ,2))*Math.abs(dec_const/mol_mass);

            double bq_tocurie_q = 3.7E10;
            //double toMbq_kg = 1.E6;
            double toBq_g = 1.E3;

            double ratio = isbqg ? toBq_g : toBq_g*bq_tocurie_q;
            sp_act = sp_act * AVOGADRO / MOLAR_MASS /ratio;
            sp_act_unc = sp_act_unc * AVOGADRO / MOLAR_MASS/ratio;

            String[] ret_sp_act = round_abs_to_significant(new BigDecimal(sp_act+""), new BigDecimal(sp_act_unc+""),35);

           // if(Double.parseDouble(ret_sp_act[0]) > 10000){
             //   DecimalFormat df = new DecimalFormat("0.##E0");
             //   try {
               //     ret_sp_act[0] = df.format(Double.parseDouble(ret_sp_act[0]));
                    ret_sp_act[0] = prepareExponential(ret_sp_act[0]);

               // } catch(Exception e){

               // }
           // }

            if(nounc){
                ret_dec_const[1] = "";
                ret_sp_act[1] = "";
            }
            return new String[]{ret_dec_const[0],ret_dec_const[1], ret_sp_act[0], ret_sp_act[1] };



        } catch (Exception e){
            return ret;
        }

    }


    /**
     * the text to be displayed
     * @param filterByRad
     * @return
     */
    public CharSequence getDetailDisplay(boolean filterByRad){


        String zeta = removeNull(mynuc.getZ().toString());
        String enne = removeNull(mynuc.getN().toString());
        String bind = prepareString((mynuc.getBinding()));
        bind += addUnc(absToEnsdf(mynuc.getBinding_unc()));

        String mass_exc = prepareString(mynuc.getMass_excess());
        mass_exc += addUnc(absToEnsdf(mynuc.getMass_excess_unc()));

        String par = removeNull(getParentDisplay());
        String decayModes = removeNull(getDecayDisplay());
        String decayRad = removeNull(getRadDisplay());
        if(filterByRad){
            decayRad =  " " + spanBold +  " " + spanSmallSize + resources.getString(R.string.radiationfiltered)  +  spanSmallSize + " " + spanBold +  "\n" + decayRad;
        }

        String cumFY = removeNull(getCumFYDisplay());

        String halfLife = buildHalfLife(
                mynuc.getHalf_life(),
                mynuc.getHalf_life_unc(),
                mynuc.getHalf_life_unit());

        String[] dec_const_sp_act = decay_constant_and_specific_activity(mynuc.getHalf_life(),
                mynuc.getHalf_life_unc(), mynuc.getHalf_life_sec(), mynuc.getAtomic_mass(), mynuc.getAtomic_mass_unc());
        String decayConstant = "";
        String spactivity = "";
        if(dec_const_sp_act[0].length() > 0){
            decayConstant = valAndUnc(dec_const_sp_act[0], dec_const_sp_act[1]);
            spactivity = valAndUnc(dec_const_sp_act[2], dec_const_sp_act[3]);
        }

        String jp = removeNull( mynuc.getJp());
        String abund = prepareString( mynuc.getAbundance());
        String abund_unc = prepareString( mynuc.getAbundance_unc());
        abund_unc = abund_unc.replaceAll(".0","");
        abund = ( abund_unc.equals("") ? abund : abund + "%" + abund_unc );

        //String thCS = prepareString(mynuc.getTher_capture());
       // thCS += addUnc(absToEnsdf(mynuc.getTher_capture_unc()));
        String[] thCsRi = thermalCsRi();

        String qa = valAndUnc(mynuc.getQa() , mynuc.getQa_unc() );
        String qb = valAndUnc(mynuc.getBeta_decay_en() , mynuc.getBeta_decay_en_unc() );

        String qec = valAndUnc(mynuc.getQec() , mynuc.getQec_unc() == null ? "" : mynuc.getQec_unc().toString() );
        String sn = valAndUnc(mynuc.getSn() , mynuc.getSn_unc() );
        String sp = valAndUnc(mynuc.getSp() , mynuc.getSp_unc() );
        String atom =  valAndUnc( convertAtomicMass(mynuc.getAtomic_mass() ), mynuc.getAtomic_mass_unc() );
       // String resInt = valAndUnc(mynuc.getResonance_integ() , mynuc.getResonance_integ_unc() );
        String westG = prepareString(mynuc.getWestcott_g() );

        String chRadius = valAndUnc(mynuc.getRadii_val() , mynuc.getRadii_del() );
        chRadius = (chRadius.equals("") ? chRadius : chRadius  +(zeta.equals("0") ? " " +  resources.getString(R.string.mean_square) + " fm**2 " : " rms fm"));

        String elMom = removeNull(decodeOper(mynuc.getEl_mom()));
        String[] dm = elMom.split(" ");
        if(dm.length == 2){
            elMom =  dm[0] + " (" + dm[1] + ")";
        }

        String mgMom = removeNull(decodeOper(mynuc.getMag_mom()));
        dm = mgMom.split(" ");
        if(dm.length == 2){
            mgMom =  dm[0] + " (" + dm[1] + ")";
        }

        String tentative = prepareString(mynuc.getTentative());
        tentative = (tentative.trim().length() == 0 ? "" :
                zeta.equals("92") ? spanSmallSize + getTentative() + '\n' + resources.getString(R.string.uraniumTentative) + '\n'+'\n' + spanSmallSize
                        : spanSmallSize + getTentative() + '\n'+'\n' + spanSmallSize);



        // http://www.fileformat.info/info/unicode/char/search.htm
        String decaychain = decayModes.equals("") || ismeta || !showdecaychain ? "" : "     " + spanSmallSize
                + resources.getString(R.string.see_the)+ spanSmallSize +" " +spanClickable + DECAY_CHAIN_TAG + spanClickable ;

        String desc = tentative;//getUncWarning() +"\n\n";
        desc += "" + spanNuclideLabelColor + "Z" + spanNuclideLabelColor + " " + zeta + "  " + spanNuclideLabelColor + "N" + spanNuclideLabelColor + " " + enne +  " " + spanNuclideLabelColor + "J" + pi + "" + spanNuclideLabelColor + " " + jp + "\n" +
                (abund.equals("") ? "" : ("" + spanNuclideLabelColor
                        + resources.getString(R.string.abundance_lbl)
                        + spanNuclideLabelColor + " " + abund + "\n" ))+
                (halfLife.indexOf("eV") == -1 ? "" + spanNuclideLabelColor
                        + 	resources.getString(R.string.half_life)
                        + spanNuclideLabelColor + " " : "" + spanNuclideLabelColor
                        + 	resources.getString(R.string.width) + spanNuclideLabelColor + "" ) + halfLife + "\n" +
                (decayConstant.length() > 0 ? ""+spanNuclideLabelColor + resources.getString(R.string.decay_constant) + spanNuclideLabelColor + " " + decayConstant + " s" + spanExponential + "-1" +spanExponential + "\n" :"" )+
                (spactivity.length() > 0 ? ""+spanNuclideLabelColor + resources.getString(R.string.specific_activity) + spanNuclideLabelColor + " "
                        + spactivity +  " " + spanClickable + (isbqg ? resources.getString(R.string.specific_activity_bqg) +"\n" : resources.getString(R.string.specific_activity_cig) + "\n" )  + spanClickable:"" )+

                (par.equals("") ? "" : ("\n" + spanNuclideLabelColor
                        + 	resources.getString(R.string.parents) + spanNuclideLabelColor + " \n" + par + "\n") )  +
                (decayModes.equals("") ? "" : ("\n" + spanNuclideLabelColor
                        + resources.getString(R.string.decays) + spanNuclideLabelColor + 		decaychain +		" \n" + decayModes + "\n") )  +
                (qa.equals("") ? "" : ("\n" + spanNuclideLabelColor + "Q" + alpha + "" + spanNuclideLabelColor + " " + qa + " keV\n") ) +
                (qb.equals("") ? "" : ("" + spanNuclideLabelColor + "Q" + beta + "" + spanNuclideLabelColor + " " + qb + " keV\n") ) +
                (qec.equals("") ? "" : ("" + spanNuclideLabelColor + "Qec" + spanNuclideLabelColor + " " + qec + " keV\n") ) +
                (sn.equals("") ? "" : ("" + spanNuclideLabelColor + "Sn" + spanNuclideLabelColor + " " + sn + " keV\n") ) +
                (sp.equals("") ? "" : ("" + spanNuclideLabelColor + "Sp" + spanNuclideLabelColor + " "  + sp + " keV\n") ) +
                (elMom.equals("") ? "\n" : ("\n" + spanNuclideLabelColor
                        + resources.getString(R.string.electric_mom)+"\n" + spanNuclideLabelColor + ""  + elMom + " barn\n") ) +
                (mgMom.equals("") ? "" : ("" + spanNuclideLabelColor
                        + resources.getString(R.string.magnetic_mom)+"\n" + spanNuclideLabelColor + ""  + mgMom + " " + micro + "N\n") ) +


                (mass_exc.equals("") ? "" : "" + spanNuclideLabelColor
                        + resources.getString(R.string.mass_excess) + "\n" + spanNuclideLabelColor + "" + mass_exc + " keV\n") +

                (bind.equals("") ? "" : "" + spanNuclideLabelColor
                        + resources.getString(R.string.binding_energy) + "/A\n" + spanNuclideLabelColor + "" + bind + " keV\n") +
                (atom.equals("") ? "" : "" + spanNuclideLabelColor
                        + resources.getString(R.string.mass) + "\n" + spanNuclideLabelColor + "" + atom + " " + "AMU\n") +
                (thCsRi[0].equals("") ? "" : "" + spanNuclideLabelColor
                        + resources.getString(R.string.ther_n_capt) +" [b]\n" + spanNuclideLabelColor + "" + thCsRi[0] +  "\n" )+
                (thCsRi[1].equals("") ? "" : "" + spanNuclideLabelColor
                        + resources.getString(R.string.resonance_int)+ " [b]\n" + spanNuclideLabelColor + "" + thCsRi[1] + "\n") +
                (westG.equals("") ? "" : "" + spanNuclideLabelColor + "Westcott g" + spanNuclideLabelColor + " " + westG + "\n") +
                (chRadius.equals("") ? "" : "" + spanNuclideLabelColor
                        + resources.getString(R.string.charge_rad)+"\n" + spanNuclideLabelColor + "" + chRadius + " \n") +
                (cumFY.equals("") ? "" : "" + spanNuclideLabelColor
                        + resources.getString(R.string.cum_ther_fy) + spanNuclideLabelColor + "" + cumFY + " \n") +
                (decayRad.equals("") ? "" : ("\n" + spanNuclideLabelColor
                        + resources.getString(R.string.decay_rad) + " " + spanNuclideLabelColor
                        + spanClickable + resources.getString(R.string.ordering) + spanClickable
                        + " \n" + decayRad + "\n" ) )  ;
        //+ getNdsTag();
        //"\n#Provided by the IAEA Nuclear Data Section \nvisit www-nds.iaea.org for more#";

        //desc = "!" + desc + "!";

        // Typeface robotoRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");

        //  TypefaceSpan robotoRegularSpan = new CustomTypefaceSpan("", robotoRegular);
        //TypefaceSpan robotoBoldSpan = new CustomTypefaceSpan("", robotoBold);
//TypefaceSpan tt = new TypefaceSpan("monospace") ;

        CharSequence cc =   Formatter.setSpanBetweenTokens(desc,spanNuclideLabelColor,
                new CharacterStyle[]{new ForegroundColorSpan(getNuclideLabelColor())
                        , new StyleSpan(android.graphics.Typeface.BOLD)
                        ,new RelativeSizeSpan(0.96f)
                });//0xFF4444FF FFFFD700 verdino 81A594

        //cc = setSpanBetweenTokens(cc, "!", new RelativeSizeSpan(0.75f));
        cc = Formatter.setSpanBetweenTokens(cc, spanSmallSize, new RelativeSizeSpan(0.75f));
        cc = Formatter.setSpanBetweenTokens(cc, spanBold, new ForegroundColorSpan(getDecayradtbltitleColor()
                //new StyleSpan(android.graphics.Typeface.BOLD
        ));
        cc = Formatter.setSpanBetweenTokens(cc, spanSel, new ForegroundColorSpan(getDecayradselecColor()
                //new StyleSpan(android.graphics.Typeface.BOLD
        ));

        cc = Formatter.setSpanBetweenTokens(cc, spanRadiation,  new TypefaceSpan("monospace"),new RelativeSizeSpan(0.96f));
        // cc = setSpanBetweenTokens(cc, "clr",  new BackgroundColorSpan(Color.parseColor("#BFFFC6")));

        CharacterStyle[] cs = new CharacterStyle[]{new RelativeSizeSpan(0.75f),new SuperscriptSpan()};
        cc = Formatter.setSpanBetweenTokens(cc, spanExponential,cs );
        cc = setClickableSpanBetweenTokens(cc, spanClickable );

        cc = setBackgroundColorSpanBetweenTokens(cc,spanBackgroundColor);

        return cc;

    }

    private String decode_shell(String shell){
        if(shell == null) return "";
        return shell.replaceAll("A",alpha).replaceAll("B",beta);

    }





    public static String  neutronSymbol(String sym ){
        return (sym.toUpperCase().equals("NN") ?  "n" : sym);
    }


    public CharSequence getHeaderDisplay( ){
        int zeta = mynuc.getZ();
        int enne = mynuc.getN();
        String sym = mynuc.getSymbol();
        String meta = mynuc.getNucid();
        if(sym != null){
            sym = neutronSymbol(sym);
        }
        String mass = (zeta + enne) + "";
        int o = meta.indexOf("m");
        if(o != -1){
            ismeta = true;
            mass += "<small>" + meta.substring(o) + "</small>";
            //sym = sym.substring(0, o);
        }

        return Html.fromHtml("<sup><small>" + mass + "</small></sup>" + sym + " " +  Formatter.getElemNames()[zeta]);
    }


    private SpannableStringBuilder formatText(CharSequence txt, CharacterStyle c){
        SpannableStringBuilder ssb = new SpannableStringBuilder(txt);
        //for (CharacterStyle c : cs)

        ssb.setSpan(c, 0, txt.length(), 0);

        return ssb;
    }

    private CharSequence setClickableSpanBetweenTokens(CharSequence text,
                                                       String token)
    {

        // Start and end refer to the points where the span will apply
        int tokenLen = token.length();
        int start = text.toString().indexOf(token) + tokenLen;
        int end = text.toString().indexOf(token, start);
        MyClickableSpan click;
        Vector<MyClickableSpan> vClick = new Vector<MyClickableSpan>();
        String clickText = "";
        String[] dm;

        while (start > -1 && end > -1)
        {
            // Copy the spannable string to a mutable spannable string
            SpannableStringBuilder ssb = new SpannableStringBuilder(text);

            click = new MyClickableSpan();
            clickText = (text.subSequence(start, end)).toString();
            dm = allclickablenucid.split(";");
            for (int i = 0; i < dm.length; i++) {
                if(dm[i].equals(clickText)){
                    click.clickaction = MyClickableSpan.actiondetail;
                    click.mytext = allclickablerowid.split(";")[i];
                    break;
                }
            }

            if(clickText.startsWith(DECAY_CHAIN_TAG)){
                click.clickaction = MyClickableSpan.actiondecaychain;
                click.mytext = mynuc.getNucid();
            }

            if(click.mytext.length() == 0){
                click.clickaction = MyClickableSpan.actionprefs;
            }

            ssb.setSpan(click, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            vClick.add(click);

            // Delete the tokens before and after the span
            ssb.delete(end, end + tokenLen);
            ssb.delete(start - tokenLen, start);

            text = ssb;

            start = text.toString().indexOf(token) + tokenLen;
            end = text.toString().indexOf(token, start);
        }

        start = text.toString().indexOf(token) + tokenLen;
        end = text.toString().indexOf(token, start);

        clickSpans = new MyClickableSpan[vClick.size()];
        vClick.copyInto(clickSpans);

        return text;
    }
    private CharSequence setBackgroundColorSpanBetweenTokens(CharSequence text,
                                                             String token)
    {

        // Start and end refer to the points where the span will apply
        int tokenLen = token.length();
        int start = text.toString().indexOf(token) + tokenLen;
        int end = text.toString().indexOf(token, start);
        BackgroundColorSpan bkg;
        ForegroundColorSpan frg;

        while (start > -1 && end > -1)
        {
            // Copy the spannable string to a mutable spannable string
            SpannableStringBuilder ssb = new SpannableStringBuilder(text);
            bkg = new BackgroundColorSpan(Color.parseColor(text.subSequence(start, end).toString()));
            frg = new ForegroundColorSpan(Color.parseColor(text.subSequence(start, end).toString()));
            ssb.replace(start, end, "AA");
            end = end - 5;


            ssb.setSpan(bkg.wrap(bkg), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(frg.wrap(frg), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //ssb.setSpan(bkg, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


            // Delete the tokens before and after the span
            ssb.delete(end, end + tokenLen);
            ssb.delete(start - tokenLen, start);

            text = ssb;

            start = text.toString().indexOf(token) + tokenLen;
            end = text.toString().indexOf(token, start);
        }

        start = text.toString().indexOf(token) + tokenLen;
        end = text.toString().indexOf(token, start);



        return text;
    }



    public  String getLeftOffset() {
        return leftOffset;
    }


    public void setLeftOffset(String leftOffset) {
        this.leftOffset = leftOffset;
    }



    public int getNuclideLabelColor() {
        return nuclideLabelColor;
    }


    public void setNuclideLabelColor(int nuclideLableColor) {
        this.nuclideLabelColor = nuclideLableColor;
    }




    public void setNdsTag(String ndsTag) {
        this.ndsTag = ndsTag;
    }

    public MyClickableSpan[] getClickSpans() {
        return clickSpans;
    }

    public void setClickSpans(MyClickableSpan[] clickSpans) {
        this.clickSpans = clickSpans;
    }


    public int getDecayradtbltitleColor() {
        return decayradtbltitleColor;
    }

    public void setDecayradtbltitleColor(int decayradtbltitleColor) {
        this.decayradtbltitleColor = decayradtbltitleColor;
    }

    public int getDecayradselecColor() {
        return decayradselecColor;
    }

    public void setDecayradselecColor(int decayradselecColor) {
        this.decayradselecColor = decayradselecColor;
    }

    public String getTentative() {
        return tentative;
    }

    public void setTentative(String tentative) {
        this.tentative = tentative;
    }

    public Nuclides getMynuc(){
        return mynuc;
    }
}
