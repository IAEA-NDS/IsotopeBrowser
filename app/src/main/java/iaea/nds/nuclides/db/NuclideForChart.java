package iaea.nds.nuclides.db;

import android.graphics.RectF;
import android.util.Log;

import androidx.room.Ignore;
import iaea.nds.nuclides.Formatter;
import iaea.nds.nuclides.Nuclide;

public class NuclideForChart {

    public static String select_fields_all = "select nuclides.nucid as nucid, nuclides.tentative as tentative, nuclides.z as z, nuclides.n as n, (nuclides.z + nuclides.n) as mass, l.dec_type as dec_type, half_life, half_life_unit, half_life_unc,  nuclides.ROWID as rowid, abundance " ;
    public static String select_fields_filter = "select nuclides.nucid as nucid, nuclides.tentative as tentative, nuclides.z as z, nuclides.n as n, (nuclides.z + nuclides.n) as mass, l_decays.dec_type as dec_type, half_life, half_life_unit, half_life_unc,  nuclides.ROWID as rowid, abundance " ;


    private String symbol;
    private String nucid;
    private String tentative;
    private Integer z;
    private Integer n;
    private String mass;
    private Integer dec_type;
    private String half_life;
    private String half_life_unit;
    private String half_life_unc;
    private String rowid;
    private String abundance;

    @Ignore String nucidDisplayShort;
    @Ignore String nucidDisplayLong;
    @Ignore String elem = "";
    @Ignore RectF rect = null;
    @Ignore public boolean isindecaychain = false;
    @Ignore int decColor;
    @Ignore String half_life_for_chart = "";


    @Ignore String[][] decaysLabel;


    public NuclideForChart(String nucid,
                           String tentative,
                           Integer z,
                           Integer n,
                           String mass,
                           Integer dec_type,
                           String half_life,
                           String half_life_unit,
                           String half_life_unc,
                           String rowid,
                           String abundance
    ){

        this.nucid = nucid;
        this.tentative = defaultString(tentative);
        this.z = z;
        this.n = n;
        this.mass = mass;
        this.dec_type = dec_type;

        this.half_life = (half_life != null ? half_life : "");
        this.half_life_unc = defaultString(half_life_unc);
        this.half_life_unit = defaultString(half_life_unit);
        this.rowid = rowid;
        this.abundance = defaultString(abundance);
        this.decaysLabel = null;

  //      Log.d("nuc",rowid + " " +nucid);

        nucidDisplayShort = nucid.replaceAll(mass, "").trim().toLowerCase();
        nucidDisplayShort = nucidDisplayShort.substring(0, 1).toUpperCase() + nucidDisplayShort.substring(1) ;
        if(nucidDisplayShort.equals("Nn")){
            nucidDisplayShort = "n";
        }
        elem = nucidDisplayShort;
        String question = (tentative.equals("Y") ? " ?" : "");

        this.nucidDisplayLong = nucidDisplayShort.substring(0, 1).toUpperCase() + nucidDisplayShort.substring(1) ;
        this.nucidDisplayLong = this.z + "-" + nucidDisplayShort + "-" + (this.z + this.n) + question;
        nucidDisplayShort = (this.z + this.n) + "-" + nucidDisplayShort + question;
        // -1 means stable -2 unknown


        if(this.dec_type == Formatter.DECTYPE_STABLE || (half_life != null && half_life.toUpperCase().equals(Formatter.HL_STABLE))){
            decColor = Formatter.COLOR_DECAY_TYPE[7];
        } else if (this.dec_type == Formatter.DECTYPE_UKN){
            decColor = Formatter.COLOR_DECAY_TYPE[8];
        } else{
            try {
                decColor = Formatter.COLOR_DECAY_TYPE[Formatter.linkDecayCodeToType[this.dec_type]];
            } catch (Exception e) {
                // TODO Auto-generated catch block
                decColor = Formatter.COLOR_DECAY_TYPE[8];
            }
        }




    }

    private String defaultString(String value){
        return value == null ? "" : value;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getNucid() {
        return nucid;
    }

    public void setNucid(String nucid) {
        this.nucid = nucid;
    }

    public String getTentative() {
        return tentative;
    }

    public void setTentative(String tentative) {
        this.tentative = tentative;
    }

    public Integer getZ() {
        return z;
    }

    public void setZ(Integer z) {
        this.z = z;
    }

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public Integer getDec_type() {
        return dec_type;
    }

    public void setDec_type(Integer dec_type) {
        this.dec_type = dec_type;
    }

    public String getHalf_life() {
        return half_life;
    }

    public void setHalf_life(String half_life) {
        this.half_life = half_life;
    }

    public String getHalf_life_unit() {
        return half_life_unit;
    }

    public void setHalf_life_unit(String half_life_unit) {
        this.half_life_unit = half_life_unit;
    }

    public String getHalf_life_unc() {
        return half_life_unc;
    }

    public void setHalf_life_unc(String half_life_unc) {
        this.half_life_unc = half_life_unc;
    }

    public String getRowid() {
        return rowid;
    }

    public void setRowid(String rowid) {
        this.rowid = rowid;
    }

    public String getAbundance() {
        return abundance;
    }

    public void setAbundance(String abundance) {
        this.abundance = abundance;
    }

    public String getNucidDisplayShort() {
        return nucidDisplayShort;
    }

    public void setNucidDisplayShort(String nucidDisplayShort) {
        this.nucidDisplayShort = nucidDisplayShort;
    }

    public String getNucidDisplayLong() {
        return nucidDisplayLong;
    }

    public void setNucidDisplayLong(String nucidDisplayLong) {
        this.nucidDisplayLong = nucidDisplayLong;
    }

    public String getMass() {
        return mass;
    }

    public void setMass(String mass) {
        this.mass = mass;
    }

    public String getElem() {
        return elem;
    }

    public void setElem(String elem) {
        this.elem = elem;
    }

    public RectF getRect() {
        return rect;
    }

    public void setRect(RectF rect) {
        this.rect = rect;
    }

    public boolean isIsindecaychain() {
        return isindecaychain;
    }

    public void setIsindecaychain(boolean isindecaychain) {
        this.isindecaychain = isindecaychain;
    }

    public int getDecColor() {
        return decColor;
    }

    public void setDecColor(int decColor) {
        this.decColor = decColor;
    }

    public String[][] getDecaysLabel() {
        return decaysLabel;
    }

    public void setDecaysLabel(String[][] decaysLabel) {
        this.decaysLabel = decaysLabel;
    }

    public String getHalf_life_for_chart() {
        if(half_life_for_chart.length() == 0){
            half_life_for_chart = Nuclide.buildHalfLifeForJava(half_life,half_life_unc,half_life_unit);
        }
        return half_life_for_chart;
    }

    public void setHalf_life_for_chart(String half_life_for_chart) {
        this.half_life_for_chart = half_life_for_chart;
    }


}
