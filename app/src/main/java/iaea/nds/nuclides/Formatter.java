package iaea.nds.nuclides;

import android.content.res.Resources;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Formatter {

    private static NumberFormat radintfmt = new DecimalFormat("00.####E0");


    private static CharacterStyle[] cs = new CharacterStyle[]{new RelativeSizeSpan(0.75f),new SuperscriptSpan()};

    public static String UNK = "?";

    public static String sep = ",";

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

    static String[] units = new String[]{"D","FS","KEV","MEV","EV","MS","AS","NS","PS","US","Y","M","S","H","mineV"};
    static String[] unitsDecoded = new String[]{"d","fs","keV","MeV", "eV","ms","as","ns","ps",micro+"s","Y","min","s","h","MeV"};

    /* RESERVED CAHARCTERS - DO NOT USE IN THE DETAIL TEXT*/
    public static String spanNuclideLabelColor = "^";
    public static String spanExponential = "§";
    String spanSmallSize = "smsz";
    public static String spanClickable = "±";
    static String spanBold = "@";
    String spanRadiation ="*";
    String spanBackgroundColor = "clr";
    String spanSel = "tzq";

    static String leftOffset = "";//"    " ;

    public static int DECTYPE_STABLE = -1;
    public static int DECTYPE_UKN = -2;
    public static String HL_STABLE = "STABLE";

    static int[] COLOR_GRAY = new int[]{70,70,70};
    public static int[][] colors = new int[][]{
            new int[]{220,135,225},//{150,65,175},//new int[]{220,95,205},
            new int[]{220,109,191},
            new int[]{220,123,177},
            new int[]{220,138,162},
            new int[]{220,152,148},
            new int[]{240,135,60}, //new int[]{220,166,134},
            new int[]{220,180,120},
            new int[]{165,42,42},//new int[]{220,135,86},
            new int[]{220,149,72},
            new int[]{220,163,58},
            new int[]{220,178,43}, //10
            new int[]{220,192,29},
            new int[]{220,206,15},
            new int[]{228,245,76},    //new int[]{200,200,1},
            new int[]{118,200,72},
            new int[]{107,200,93},
            new int[]{95,200,115},
            new int[]{68,185,77},//new int[]{84,170,136},
            new int[]{72,200,158},
            new int[]{61,200,179},
            new int[]{50,200,200}, //20
            new int[]{50,208,200},
            new int[]{50,187,200},
            new int[]{50,165,200},
            new int[]{50,144,200},
            new int[]{50,122,200},
            new int[]{50,101,200},
            new int[]{0,0,0},
            // stable
            new int[]{255,255,255},

            // ?
            new int[]{180,180,180}
    };

    static String[] decay_legend =  new String[0];
    /* index: decay type - value: colors index*/
    public static int[] COLOR_DECAY_TYPE = new int[] {
            0,//al, //0 - 13				0 al
            13,//"EC+ "+bt+"+; //1 - 3		1 ec b+
            17,//bt "-; //2 - 21			2 b-
            7,//"p" , //3 - 10				3 p
            24,//"n;//4 - 24 				4 n
            13,//"EC;//7 - 3 				5 ec
            5,//"SF; //11 - 14 				6 sf
            28,  // stable 28				7 stable
            29,                             //8 ?
            10};//  						9 IT
    /*

     * index: decay code - value color_decay_type index */
    public static int[] linkDecayCodeToType = new int[]{
            0, 	//0
            1, 	//1
            2, //2
            9,//3
            3,//4
            4,//5
            2,//6
            5,//7
            6,//8
            8,//9
            1,//10
            8,//11
            1,//12
            8,//13
            8,//14
            1,//15
            1,//16
            2,//17
            8,//18
            1,//19
            2,//20
            3,//21
            2,//22
            8,//23
            1,//24 //"2"+bt+"+",//24
            1,//25
            1,//26
            8,//27
            1,//28
            2,//29
            1,//30
            8,//31
            8,//32
            8,//33
            1,//34
            8,//35
            8,//36
            8,//37
            8,//38
            8,//39
            8,//40
            8,//41
            8,//42
            8,//43
            8,//44
            1,//45
            1,//46
            8,//47
            8,//48
            2,//49
            1,//50
            8,//51
            8,//52
            1,//53
            1,//54
            2,//55
            8, // skipped in decay_codes
            8,//57
            8,//58
            8,//59
            8,//60
            4, //61
            1, //62
            5, //63
            1, //64
            1, //65
            1, //66
            8, //67
            1, //68
            1, //69
            3 // 70
    };

    public static  String[] decays = new String[]{
            alpha, //0
            "ec " + beta + "+",
            beta +"-",
            "IT",
            "p",
            "n",
            beta + "- n",
            "ec",
            "SF",
            "D",
            "ec p", //10
            "3He",
            beta + "+ p",
            "3H",
            "G",
            beta + "+",
            "ec " + alpha,
            beta + "- 2n",
            "8Be",
            beta + "+ " + alpha,
            "2" + beta + "-", //20
            "2p",
            beta + "- " + alpha,
            "14C",
            "ec 2p",
            beta + "+ 2p",
            "2" + beta + "+",
            "28Mg",
            "ec SF",
            beta + "- 3n",
            "2ec", //30
            "24Ne",
            "ec F",
            "Ne",
            "ec p ec 2p",
            "22Ne",
            "34Si",
            "ec (SF)",
            "24Ne",
            "&beta; fission",
            "SF (ec &beta;+)", //40
            "SF &beta;-",
            beta + "- 4n",
            alpha + "?",
            "SF ec " + beta + "-",
            "IT ec " + beta + "+",
            "ec 3p",
            "20Ne",
            "IT?",
            beta + "- F",
            beta + "+ ec", //50
            "20O", //51
            "Mg", //52
            "ec " + alpha + " p", //53
            "2|e", //54
            beta + "- p", //55
            "",
            "12C", //57 !! 56 skipped
            "20Ne", //58
            "34Si", //59 aa
            "22Ne",  //60
            "2n", //61
            "ec SF", //63
            "SF ec " + beta+ "+", //63
            beta + "- SF", //64
            beta + "- 5n", //65
            beta + "+ F", //66
             "28MG", //67
            beta + "- 6n", //68
            beta + "- 7n", //69
            "3p" // 70


    };

    public static int defHalfLifeUnitLow = 4;
    public static int defHalfLifeUnitHigh = 8;

    public static double[]  halfLifeUnitConversion = new double[] {1.e-12,
            1.e-9,
            1.e-6,
            1.e-3,
            1. ,
            60.,
            3600,
            3600*24,
            365.24219878*3600*24};

    public static String[] decayModesCode = new String[] {
            "0,16,19,22",
            "1,7,15",
            "2",
            "3,48",
            "4",
            "5",
            "8",
            "6",
            "17",
            "29",
            "42",
            "10,12",
            "24,25"};


    public static int radiationsAnnihiIdx = 7;
    public static String[]  radiationsCode = new String[] {"A",
            "B+" ,
            "B-" ,
            "G','X','AN",
            "G",
            "X",
    "AN","AU","CE"};



    static Resources resources = null;

    public static void setResources(Resources _resources){
        resources = _resources;
    }

    public static String decayLbl(){
        return resources.getString(R.string.decay);
    }

    public static String DECAY_CHAIN_TAG() {
        return resources.getString(R.string.decaychain);
    }

    public static String neutronSymbol(String sym) {
        return (sym.toUpperCase().equals("NN") ? "n" : sym);
    }

    public static String nucMassWithMetaHTML(String mass, String nucid) {
        if (mass == null || nucid == null) {
            return "";
        }
        int o = nucid.indexOf("m");
        if (o != -1) {
            String meta = "m";
            String last = nucid.substring(nucid.length() - 1, nucid.length());
            if("12345".indexOf(last) != -1){
                meta += last;
            }

            mass += "<small>" + meta + "</small>";// nucid.substring(o);
        }

        return mass;

    }

    public static String nucSymbol(String elemSymbol, String tentative) {

        return neutronSymbol(elemSymbol) + (tentative != null && tentative.equals("Y") ? " ?" : "");
    }

    public static CharSequence halfLife(String half_life, String unit, String unc) {
        if (half_life == null) {
            return "";
        }

        if (unc == null) {
            unc = "";
        }

        if ( unit == null) {
            unit = "";
        }
        unit = unit.toUpperCase();
       String halfLife = (hasOper(unc) ? decodeOper(unc) : " ") + removeNull(half_life);

        halfLife = removeNull(decodeOper(prepareExponential(halfLife)));

        halfLife += " " + decodeUnits(removeNull(unit));

        if(unc == null || unc.length() == 0 || hasOper(unc)){
            unc = "";
        }
        halfLife += " " + unc;

        if(halfLife.trim().toLowerCase().equals("stable")){
            halfLife = resources.getString(R.string.stable_prompt);
        }


        return setSpanBetweenTokens(halfLife ,spanExponential,cs);


    }

    public static CharSequence radintensity( String radtype, String radint){

        if (radint == null) {
            return "";
        }
        String dm = radint;
        try{
            if(dm.startsWith("0.0"))
                dm = radintfmt.format(Double.parseDouble(radint));
            dm = dm.replaceFirst("E0","");
        } catch (Exception e){
            dm = radint;
        }
        return setSpanBetweenTokens(removeNull(radtype + " " + decodeOper(prepareExponential(dm))) + "%" ,spanExponential,cs);
        }

    public static boolean hasOper(String s){
        if(s==null) return false;
        if(!s.equals(decodeOper(s))){
            return true;
        }
        return false;
    }

    public  static String decodeOper(String s){
        if(s == null) return s;
        return s.replaceAll("STABLE", resources.getString(R.string.stable_prompt)).replaceAll("EQ", "").replaceAll("AP", approx).replaceAll("GT", gt).replaceAll("LT", lt).
                replaceAll("LE", le).replaceAll("GE", ge).trim().replaceAll("CA","");
    }

    public static String removeNull(String str){
        return (str == null ? "" : str);
    }

    public static String prepareExponential(String s){
        int o = s.indexOf("E+");
        if(o != -1){
            s = s.substring(0, o) + " 10" + spanExponential + s.substring(o + 2) + spanExponential;
        }
        o = s.indexOf("E-");
        if(o != -1){
            s = s.substring(0, o) + " 10" + spanExponential + s.substring(o + 1) + spanExponential;
        }

        o = s.indexOf("E");
        if(o >0 && "0123456789".indexOf(s.substring(o-1,o)) != -1 && "0123456789".indexOf(s.substring(o+1,o+2)) != -1){
            s = s.substring(0, o) + " 10" + spanExponential + s.substring(o + 1) + spanExponential;
        }

       return Html.fromHtml(s).toString();




    }

    public  static String addUnc(String unc){
        if(unc == null || unc.length() == 0 || hasOper(unc)){
            return "";
        }
        return " (" + unc + ")";
    }

    public static String decodeUnits(String unit){

        if(unit != null){
            unit = unit.toUpperCase();
            for (int i = 0; i < unitsDecoded.length; i++) {
                unit = unit.replaceAll(units[i], unitsDecoded[i]);
            }
        }
        return unit;
    }

    public static void stripUnderlines(TextView textView) {
        Spannable s = (Spannable)textView.getText();
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span: spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderLine(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    public static CharSequence setClickableSpanBetweenTokens(CharSequence text)
    {

        // Copy the spannable string to a mutable spannable string
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        URLSpan[] spans = ssb.getSpans(0, ssb.length(), URLSpan.class);
        for (URLSpan span: spans) {
            int start = ssb.getSpanStart(span);
            int end = ssb.getSpanEnd(span);
            ssb.removeSpan(span);
            span = new URLSpanNoUnderLine(span.getURL());
            ssb.setSpan(span, start, end, 0);
        }


        return text;
    }
    public  static CharSequence setSpanBetweenTokens(CharSequence text,
                                                     String token, CharacterStyle... cs)
    {


        // Start and end refer to the points where the span will apply
        int tokenLen = token.length();
        int start = text.toString().indexOf(token) + tokenLen;
        int end = text.toString().indexOf(token, start);

        while (start > -1 && end > -1)
        {
            // Copy the spannable string to a mutable spannable string
            SpannableStringBuilder ssb = new SpannableStringBuilder(text);


            for (CharacterStyle c : cs)
                ssb.setSpan(c.wrap(c), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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

    public static CharSequence createIndentedText(Spanned text, int marginFirstLine, int marginNextLines) {

        SpannableStringBuilder result=new SpannableStringBuilder(text);
        result.setSpan(new LeadingMarginSpan.Standard(marginFirstLine, marginNextLines),0,text.length(),0);
        return result;
    }
    public static Spannable createIndentedText(Spannable text, int marginFirstLine, int marginNextLines) {

        text.setSpan(new LeadingMarginSpan.Standard(marginFirstLine, marginNextLines),0,text.length(),0);
        return text;
    }

    public  static String getDecayLabel(String decayCode, String br, String oper, String unc){
        String ret = "";

        String dcy = decodeDecay(decayCode);
        oper = (oper == null ? "" : oper);
        br =  (br == null || br.equals("") ? "? " : br + "%" ) ;
        unc =  (unc == null ?  "" : unc  ) ;
        ret +=  dcy + " " + oper + br + unc + "\n";

        return decodeOper(ret);

    }

    public static void  getDecayLabel( TextView txt, int decayCode,  String perc,  String unc, String oper){

        String dcy = decodeDecay(decayCode + "");
        oper = (oper == null ? "" : oper);
        perc =  ( perc==null || perc.equals("") ? "? " : perc + "%" ) ;
        unc =  (unc == null ?  "" : unc  ) ;

        txt.setText(decodeOper(leftOffset + dcy + " " + oper + perc + unc ) );
        int[] color = Formatter.getColorRGBFromDecayCode(decayCode);
        txt.setBackgroundColor(Color.rgb(color[0], color[1], color[2]));

        if (decayCode == 0) txt.setTextColor(Color.DKGRAY);
        else if (decayCode == 4) txt.setTextColor(Color.rgb(210, 210, 210));
        else txt.setTextColor(Color.DKGRAY);

        return;

    }

    public static void abundance(TextView txt, String abund, String abund_unc){
        abund_unc = (abund_unc == null ? "" :  abund_unc );
        abund_unc = abund_unc.replaceAll(".0","");
        abund =  resources.getString(R.string.abundance) + abund + "%" + abund_unc  ;
        txt.setText(abund);
        txt.setBackgroundColor(Color.rgb(200, 200, 200));
        txt.setTextColor(Color.rgb(20, 20, 20));
    }

    public static void decayUncertain(TextView txt){

        txt.setText(resources.getString( R.string.uncertain));
        txt.setBackgroundColor(Color.rgb(200, 200, 200));
        txt.setTextColor(Color.rgb(20, 20, 20));
    }

    public static  String decodeDecay(String dcy){

        int i = Integer.parseInt(dcy);

        if (i < (decays.length))
            return decays[i];
        return "";
    }

    public static int[] getColorRGBFromDecayCode(int decayCode){
        try {
            return colors[COLOR_DECAY_TYPE[linkDecayCodeToType[decayCode]]];
        } catch (Exception e) {
            return colors[colors.length -1];
        }
    }
    public static int[] getColorRGBFromDecayCode(String decayCode){
        return getColorRGBFromDecayCode(Integer.parseInt(decayCode));
    }



    /**
     * spans in the guide text
     * @param mTextView TextView where the text is displayed
     * @param text CharSequence
     * @param isRightAligned boolean true for arabic
     */
    public static void  setHelpText(TextView mTextView , CharSequence text, boolean isRightAligned){

        mTextView.setText("");
        CharacterStyle cs =  new ForegroundColorSpan(resources.getColor(R.color.labels));

        if (isRightAligned)
            mTextView.setGravity(Gravity.RIGHT);

        //mTextView.setText(setSpanBetweenTokens(text, spanNuclideLabelColor, cs));


        text = setSpanBetweenTokens(text, spanNuclideLabelColor, cs);
        text = setSpanBetweenTokens(text , spanBold, new StyleSpan(android.graphics.Typeface.BOLD));
        mTextView.setText(text);


        //stripUnderlines(mTextView);
    }

    private static String[] loadStringArrayFromRes(int resourceId){
        String s = resources.getString(resourceId);
        s = s.replaceAll("\n", "");
        String[] ret = new String[0];
        if(s != null) {
            ret = s.split(Formatter.sep);
        }
        for(int i = 1 ; i < ret.length; i++){
            ret[i] = ret[i].trim();
        }
        return ret;

    }


    public static String[] getHalfLifeUnits() {
        return loadStringArrayFromRes(R.string.half_life_units);

    }


    public static String[] getDecayModes() {
        return loadStringArrayFromRes(R.string.decay_modes);
    }



    public static String[] getRadiations() {

        return loadStringArrayFromRes(R.string.radiations);

    }

    public  static int getElementZeta(String symbol){
        symbol = symbol.toUpperCase();
        String[] elemSymbols = loadStringArrayFromRes(R.string.elements_symbol);

        // i starts from 1 to avoid conflict n= neutron and N = Nitrogen
        for (int i = 1; i < elemSymbols.length; i++) {
            if( elemSymbols[i].toUpperCase().equals(symbol)){
                return i;
            }
        }
        return -1;
    }

    public static String[] getElemNames() {

        return loadElementNamesfromRes(R.string.elements_names);
    }

    private static String[] loadElementNamesfromRes(int resourceId){
        String s = resources.getString(resourceId);
        s = s.replaceAll(",", "");
        String[] ret = new String[0];
        if(s != null) {
            ret = s.split("\n");
        }
        for(int i = 1 ; i < ret.length; i++){
            ret[i] = ret[i].trim();
        }
        return ret;

    }

    public static String[] getElemSymbols() {

        return loadStringArrayFromRes(R.string.elements_symbol);
    }

    public static String[] elementsSorted(){
        String[] elemSorted = new String[getElemNames().length];
        String dm = "";
        for (int i = 0; i < elemSorted.length; i++) {
            dm = getElemNames()[i] + " Z = " + i;

            elemSorted[i] = dm ;
        }
        java.util.Arrays.sort(elemSorted);

        return elemSorted;

    }

    public static float convertPixelsToDp(float px){
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static String[] getElemGroupLabels() {
        return loadStringArrayFromRes(R.string.element_groups);
    }

    public static  float pxToMm(final float px)
    {
        final DisplayMetrics dm = resources.getDisplayMetrics();
        return px / TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1, dm);
    }




    }
