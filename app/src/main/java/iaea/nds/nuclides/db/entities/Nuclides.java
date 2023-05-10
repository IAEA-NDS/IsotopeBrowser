package iaea.nds.nuclides.db.entities;

 import androidx.room.ColumnInfo;
 import androidx.room.Entity;
 import androidx.annotation.NonNull;
 import androidx.room.Ignore;

 @Entity(tableName = "nuclides", primaryKeys = {"pk"})
 public class Nuclides  {
 

	private String nucid;
	private Integer z;
	private Integer n;
	private String symbol;
	private Integer l_seqno;
	private String jp;
	private String half_life;
	private String half_life_unc;
	private String half_life_unit;
	private Double half_life_sec;
	private String mass_excess;
	private String mass_excess_unc;
	private String binding;
	private String binding_unc;
	private String atomic_mass;
	private String atomic_mass_unc;
	private String beta_decay_en;
	private String beta_decay_en_unc;
	private String qa;
	private String qa_unc;
	private String qec;
	private Integer qec_unc;
	private String sn;
	private String sn_unc;
	private String sp;
	private String sp_unc;
	private String radii_val;
	private String radii_del;
	private String el_mom;
	private String mag_mom;
	private String abundance;
	private String abundance_unc;
	private String ther_capture;
	private String ther_capture_unc;
	private String westcott_g;
	private String resonance_integ;
	private String resonance_integ_unc;
	private String tentative;
	@NonNull
	private String pk;

	//@ColumnInfo(name = "rowid")
	//private int ROWID;


	@Ignore
	private String decay_label;
	@Ignore
	private int mass;



	 public Nuclides(String nucid, Integer z, Integer n, String symbol, Integer l_seqno, String jp, String half_life
		, String half_life_unc, String half_life_unit, Double half_life_sec, String mass_excess, String mass_excess_unc
		, String binding, String binding_unc, String atomic_mass, String atomic_mass_unc, String beta_decay_en, String beta_decay_en_unc
		, String qa, String qa_unc, String qec, Integer qec_unc, String sn, String sn_unc, String sp, String sp_unc, String radii_val
		, String radii_del, String el_mom, String mag_mom, String abundance, String abundance_unc, String ther_capture
		, String ther_capture_unc, String westcott_g, String resonance_integ, String resonance_integ_unc, String tentative
		, String pk
        ){
    this.nucid = nucid;
    this.z = z;
    this.n = n;
    this.symbol = symbol;
    this.l_seqno = l_seqno;
    this.jp = jp;
    this.half_life = half_life;
    this.half_life_unc = half_life_unc;
    this.half_life_unit = half_life_unit;
    this.half_life_sec = half_life_sec;
    this.mass_excess = mass_excess;
    this.mass_excess_unc = mass_excess_unc;
    this.binding = binding;
    this.binding_unc = binding_unc;
    this.atomic_mass = atomic_mass;
    this.atomic_mass_unc = atomic_mass_unc;
    this.beta_decay_en = beta_decay_en;
    this.beta_decay_en_unc = beta_decay_en_unc;
    this.qa = qa;
    this.qa_unc = qa_unc;
    this.qec = qec;
    this.qec_unc = qec_unc;
    this.sn = sn;
    this.sn_unc = sn_unc;
    this.sp = sp;
    this.sp_unc = sp_unc;
    this.radii_val = radii_val;
    this.radii_del = radii_del;
    this.el_mom = el_mom;
    this.mag_mom = mag_mom;
    this.abundance = abundance;
    this.abundance_unc = abundance_unc;
    this.ther_capture = ther_capture;
    this.ther_capture_unc = ther_capture_unc;
    this.westcott_g = westcott_g;
    this.resonance_integ = resonance_integ;
    this.resonance_integ_unc = resonance_integ_unc;
    this.tentative = tentative;
    this.pk = pk;


    this.mass = z.intValue() + n.intValue();

}
 
public String getNucid() {
	return this.nucid;
}
public Integer getZ() {
	return this.z;
}
public Integer getN() {
	return this.n;
}
public String getSymbol() {
	return this.symbol;
}
public Integer getL_seqno() {
	return this.l_seqno;
}
public String getJp() {
	return this.jp;
}
public String getHalf_life() {
	return this.half_life;
}
public String getHalf_life_unc() {
	return this.half_life_unc;
}
public String getHalf_life_unit() {
	return this.half_life_unit;
}
public Double getHalf_life_sec() {
	return this.half_life_sec;
}
public String getMass_excess() {
	return this.mass_excess;
}
public String getMass_excess_unc() {
	return this.mass_excess_unc;
}
public String getBinding() {
	return this.binding;
}
public String getBinding_unc() {
	return this.binding_unc;
}
public String getAtomic_mass() {
	return this.atomic_mass;
}
public String getAtomic_mass_unc() {
	return this.atomic_mass_unc;
}
public String getBeta_decay_en() {
	return this.beta_decay_en;
}
public String getBeta_decay_en_unc() {
	return this.beta_decay_en_unc;
}
public String getQa() {
	return this.qa;
}
public String getQa_unc() {
	return this.qa_unc;
}
public String getQec() {
	return this.qec;
}
public Integer getQec_unc() {
	return this.qec_unc;
}
public String getSn() {
	return this.sn;
}
public String getSn_unc() {
	return this.sn_unc;
}
public String getSp() {
	return this.sp;
}
public String getSp_unc() {
	return this.sp_unc;
}
public String getRadii_val() {
	return this.radii_val;
}
public String getRadii_del() {
	return this.radii_del;
}
public String getEl_mom() {
	return this.el_mom;
}
public String getMag_mom() {
	return this.mag_mom;
}
public String getAbundance() {
	return this.abundance;
}
public String getAbundance_unc() {
	return this.abundance_unc;
}
public String getTher_capture() {
	return this.ther_capture;
}
public String getTher_capture_unc() {
	return this.ther_capture_unc;
}
public String getWestcott_g() {
	return this.westcott_g;
}
public String getResonance_integ() {
	return this.resonance_integ;
}
public String getResonance_integ_unc() {
	return this.resonance_integ_unc;
}
public String getTentative() {
	return this.tentative;
}
public String getPk() {
		return pk;
	}
/*	public int getROWID() {
		 return ROWID;
	 }
*/
// public void setROWID(int ROWID){this.ROWID = ROWID;}
public String getDecay_label(){
	return decay_label;
}

public void setDecay_label(String decay_label){
	this.decay_label = decay_label;
}
public int getMass() { return mass; }
 }
