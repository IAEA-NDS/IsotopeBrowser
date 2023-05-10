package iaea.nds.nuclides.db.entities;
 
 import androidx.room.Entity;
 import androidx.annotation.NonNull;
 
@Entity(tableName = "nuclides_metadata", primaryKeys = {"version"})
 public class Nuclides_metadata  {

	@NonNull private String version;
	private String subversion;
	private String metadata;


 
public Nuclides_metadata(String version,String subversion,String metadata){
    this.version = version;
    this.subversion = subversion;
    this.metadata = metadata;

}
 
public String getVersion() {
	return this.version;
}
public String getSubversion() {
	return this.subversion;
}
public String getMetadata() {
	return this.metadata;
}

}
