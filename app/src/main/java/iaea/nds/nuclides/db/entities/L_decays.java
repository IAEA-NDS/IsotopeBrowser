package iaea.nds.nuclides.db.entities;

 import androidx.room.Entity;
 import androidx.annotation.NonNull;
 
@Entity(tableName = "l_decays", primaryKeys = {"nucid","l_seqno","dec_type"})
 public class L_decays  {
 
	@NonNull
	private String nucid;
	@NonNull
	private Integer l_seqno;
	private String perc;
	private Double perc_num;
	private String unc;
	private String perc_oper;
	@NonNull
	private Integer dec_type;
	private String daughter_nucid;
	private String decay_label;
 
public L_decays(String nucid,Integer l_seqno,String perc,Double perc_num,String unc,String perc_oper,Integer dec_type,String daughter_nucid,String decay_label){
    this.nucid = nucid;
    this.l_seqno = l_seqno;
    this.perc = perc;
    this.perc_num = perc_num;
    this.unc = unc;
    this.perc_oper = perc_oper;
    if(dec_type == null){
    	dec_type = new Integer(-1);
	}
    this.dec_type = dec_type;
    this.daughter_nucid = daughter_nucid;
    this.decay_label = decay_label;
}
 
public String getNucid() {
	return this.nucid;
}
public Integer getL_seqno() {
	return this.l_seqno;
}
public String getPerc() {
	return this.perc;
}
public Double getPerc_num() {
	return this.perc_num;
}
public String getUnc() {
	return this.unc;
}
public String getPerc_oper() {
	return this.perc_oper;
}
public Integer getDec_type() {
	return this.dec_type;
}
public String getDaughter_nucid() {
	return this.daughter_nucid;
}
public String getDecay_label() {
	return this.decay_label;
}
}
