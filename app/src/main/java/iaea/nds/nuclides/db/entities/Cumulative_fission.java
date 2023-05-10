 package iaea.nds.nuclides.db.entities;
 
 import androidx.annotation.NonNull;
 import androidx.room.Entity;
 
@Entity(tableName = "cumulative_fission", primaryKeys = {"parent_nucid","nucid","l_seqno"})
 public class Cumulative_fission  {
 
	@NonNull
	private String parent_nucid;
	@NonNull
	private String nucid;
	@NonNull
	private Integer l_seqno;
	private String ther_yield_num;
	private String ther_yield_unc;
 
public Cumulative_fission(String parent_nucid,String nucid,Integer l_seqno,String ther_yield_num,String ther_yield_unc){
    this.parent_nucid = parent_nucid;
    this.nucid = nucid;
    this.l_seqno = l_seqno;
    this.ther_yield_num = ther_yield_num;
    this.ther_yield_unc = ther_yield_unc;
}
 
public String getParent_nucid() {
	return this.parent_nucid;
}
public String getNucid() {
	return this.nucid;
}
public Integer getL_seqno() {
	return this.l_seqno;
}
public String getTher_yield_num() {
	return this.ther_yield_num;
}
public String getTher_yield_unc() {
	return this.ther_yield_unc;
}
}
