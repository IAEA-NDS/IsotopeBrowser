package iaea.nds.nuclides.db.entities;

 import androidx.room.Entity;
 import androidx.annotation.NonNull;
 
@Entity(tableName = "decay_chain", primaryKeys = {"nucid","l_seqno","daughter_nucid","dec_type"})
 public class Decay_chain  {
 
	@NonNull
	private String nucid;
	@NonNull
	private Integer l_seqno;
	@NonNull
	private String daughter_nucid;
	@NonNull
	private String dec_type;
	private String perc;
 
public Decay_chain(String nucid,Integer l_seqno,String daughter_nucid,String dec_type,String perc){
    this.nucid = nucid;
    this.l_seqno = l_seqno;
    this.daughter_nucid = daughter_nucid;
    this.dec_type = dec_type;
    this.perc = perc;
}
 
public String getNucid() {
	return this.nucid;
}
public Integer getL_seqno() {
	return this.l_seqno;
}
public String getDaughter_nucid() {
	return this.daughter_nucid;
}
public String getDec_type() {
	return this.dec_type;
}
public String getPerc() {
	return this.perc;
}
}
