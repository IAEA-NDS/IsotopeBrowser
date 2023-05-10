package iaea.nds.nuclides.db.entities;

 import androidx.room.Entity;
 import androidx.annotation.NonNull;

@Entity(tableName = "decay_radiations", primaryKeys = {"dr_pk"})
 public class Decay_radiations  {

	private Integer parent_l_seqno;
	private String parent_nucid;
	private String dec_type;
	private String type_a;
	private String type_c;
	private String logft;
	private String logft_unc;
	private Double logft_num;
	private String intensity;
	private String intensity_unc;
	private Double intensity_num;
	private Double energy_num;
	private String energy;
	private String energy_unc;
	private String endpoint;
	private String endpoint_unc;
	@NonNull
	private Integer dr_pk;
 
public Decay_radiations(Integer parent_l_seqno,String parent_nucid,String dec_type,String type_a,String type_c,String logft,String logft_unc,Double logft_num,String intensity,String intensity_unc,Double intensity_num,Double energy_num,String energy,String energy_unc,String endpoint,String endpoint_unc
		,Integer dr_pk
){
    this.parent_l_seqno = parent_l_seqno;
    this.parent_nucid = parent_nucid;
    this.dec_type = dec_type;
    this.type_a = type_a;
    this.type_c = type_c;
    this.logft = logft;
    this.logft_unc = logft_unc;
    this.logft_num = logft_num;
    this.intensity = intensity;
    this.intensity_unc = intensity_unc;
    this.intensity_num = intensity_num;
    this.energy_num = energy_num;
    this.energy = energy;
    this.energy_unc = energy_unc;
    this.endpoint = endpoint;
    this.endpoint_unc = endpoint_unc;
    this.dr_pk = dr_pk;
}
 
public Integer getParent_l_seqno() {
	return this.parent_l_seqno;
}
public String getParent_nucid() {
	return this.parent_nucid;
}
public String getDec_type() {
	return this.dec_type;
}
public String getType_a() {
	return this.type_a;
}
public String getType_c() {
	return this.type_c;
}
public String getLogft() {
	return this.logft;
}
public String getLogft_unc() {
	return this.logft_unc;
}
public Double getLogft_num() {
	return this.logft_num;
}
public String getIntensity() {
	return this.intensity;
}
public String getIntensity_unc() {
	return this.intensity_unc;
}
public Double getIntensity_num() {
	return this.intensity_num;
}
public Double getEnergy_num() {
	return this.energy_num;
}
public String getEnergy() {
	return this.energy;
}
public String getEnergy_unc() {
	return this.energy_unc;
}
public String getEndpoint() {
	return this.endpoint;
}
public String getEndpoint_unc() {
	return this.endpoint_unc;
}
public Integer getDr_pk() {
	return this.dr_pk;
}
}
