 package iaea.nds.nuclides.db.entities;
 
 import androidx.room.Entity;
 import androidx.annotation.NonNull;
 
@Entity(tableName = "thermal_cross_sect", primaryKeys = {"nucid","iso"})
 public class Thermal_cross_sect  {
 
	@NonNull
	private String nucid;
	private Integer z;
	private Integer n;
	@NonNull
	private Integer iso;
	private Integer l_seqno;
	private String jp;
	private String maxw_ng;
	private String maxw_ng_unc;
	private String ng;
	private String ng_unc;
	private String nel;
	private String nel_unc;
	private String na;
	private String na_unc;
	private String np;
	private String np_unc;
	private String nf;
	private String nf_unc;
	private String r;
	private String r_unc;
	private String dd0d1;
	private String dd0d1_unc;
	private String ic;
	private String ic_unc;
	private String ia;
	private String ia_unc;
	private String ip;
	private String ip_unc;
	private String iff;
	private String iff_unc;
	private String iabs;
	private String iabs_unc;
	private String macs30;
	private String macs30_unc;
 
public Thermal_cross_sect(String nucid,Integer z,Integer n,Integer iso,Integer l_seqno,String jp,String maxw_ng,String maxw_ng_unc,String ng,String ng_unc,String nel,String nel_unc,String na,String na_unc,String np,String np_unc,String nf,String nf_unc,String r,String r_unc,String dd0d1,String dd0d1_unc,String ic,String ic_unc,String ia,String ia_unc,String ip,String ip_unc,String iff,String iff_unc,String iabs,String iabs_unc,String macs30,String macs30_unc){
    this.nucid = nucid;
    this.z = z;
    this.n = n;
    this.iso = iso;
    this.l_seqno = l_seqno;
    this.jp = jp;
    this.maxw_ng = maxw_ng;
    this.maxw_ng_unc = maxw_ng_unc;
    this.ng = ng;
    this.ng_unc = ng_unc;
    this.nel = nel;
    this.nel_unc = nel_unc;
    this.na = na;
    this.na_unc = na_unc;
    this.np = np;
    this.np_unc = np_unc;
    this.nf = nf;
    this.nf_unc = nf_unc;
    this.r = r;
    this.r_unc = r_unc;
    this.dd0d1 = dd0d1;
    this.dd0d1_unc = dd0d1_unc;
    this.ic = ic;
    this.ic_unc = ic_unc;
    this.ia = ia;
    this.ia_unc = ia_unc;
    this.ip = ip;
    this.ip_unc = ip_unc;
    this.iff = iff;
    this.iff_unc = iff_unc;
    this.iabs = iabs;
    this.iabs_unc = iabs_unc;
    this.macs30 = macs30;
    this.macs30_unc = macs30_unc;
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
public Integer getIso() {
	return this.iso;
}
public Integer getL_seqno() {
	return this.l_seqno;
}
public String getJp() {
	return this.jp;
}
public String getMaxw_ng() {
	return this.maxw_ng;
}
public String getMaxw_ng_unc() {
	return this.maxw_ng_unc;
}
public String getNg() {
	return this.ng;
}
public String getNg_unc() {
	return this.ng_unc;
}
public String getNel() {
	return this.nel;
}
public String getNel_unc() {
	return this.nel_unc;
}
public String getNa() {
	return this.na;
}
public String getNa_unc() {
	return this.na_unc;
}
public String getNp() {
	return this.np;
}
public String getNp_unc() {
	return this.np_unc;
}
public String getNf() {
	return this.nf;
}
public String getNf_unc() {
	return this.nf_unc;
}
public String getR() {
	return this.r;
}
public String getR_unc() {
	return this.r_unc;
}
public String getDd0d1() {
	return this.dd0d1;
}
public String getDd0d1_unc() {
	return this.dd0d1_unc;
}
public String getIc() {
	return this.ic;
}
public String getIc_unc() {
	return this.ic_unc;
}
public String getIa() {
	return this.ia;
}
public String getIa_unc() {
	return this.ia_unc;
}
public String getIp() {
	return this.ip;
}
public String getIp_unc() {
	return this.ip_unc;
}
public String getIff() {
	return this.iff;
}
public String getIff_unc() {
	return this.iff_unc;
}
public String getIabs() {
	return this.iabs;
}
public String getIabs_unc() {
	return this.iabs_unc;
}
public String getMacs30() {
	return this.macs30;
}
public String getMacs30_unc() {
	return this.macs30_unc;
}
}
