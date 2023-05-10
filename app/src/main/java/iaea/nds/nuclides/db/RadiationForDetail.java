package iaea.nds.nuclides.db;

public class RadiationForDetail {


    public Integer getParent_l_seqno() {
        return parent_l_seqno;
    }

    public void setParent_l_seqno(Integer parent_l_seqno) {
        this.parent_l_seqno = parent_l_seqno;
    }

    public String getParent_nucid() {
        return parent_nucid;
    }

    public void setParent_nucid(String parent_nucid) {
        this.parent_nucid = parent_nucid;
    }

    public String getDec_type() {
        return dec_type;
    }

    public void setDec_type(String dec_type) {
        this.dec_type = dec_type;
    }

    public String getType_a() {
        return type_a;
    }

    public void setType_a(String type_a) {
        this.type_a = type_a;
    }

    public String getType_c() {
        return type_c;
    }

    public void setType_c(String type_c) {
        this.type_c = type_c;
    }

    public String getIntensity() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    public String getIntensity_unc() {
        return intensity_unc;
    }

    public void setIntensity_unc(String intensity_unc) {
        this.intensity_unc = intensity_unc;
    }

    public Double getIntensity_num() {
        return intensity_num;
    }

    public void setIntensity_num(Double intensity_num) {
        this.intensity_num = intensity_num;
    }

    public Double getEnergy_num() {
        return energy_num;
    }

    public void setEnergy_num(Double energy_num) {
        this.energy_num = energy_num;
    }

    public String getEnergy() {
        return energy;
    }

    public void setEnergy(String energy) {
        this.energy = energy;
    }

    public String getEnergy_unc() {
        return energy_unc;
    }

    public void setEnergy_unc(String energy_unc) {
        this.energy_unc = energy_unc;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint_unc() {
        return endpoint_unc;
    }

    public void setEndpoint_unc(String endpoint_unc) {
        this.endpoint_unc = endpoint_unc;
    }

    public String getRad_selected() {
        return rad_selected;
    }

    public void setRad_selected(String rad_selected) {
        this.rad_selected = rad_selected;
    }

    public String getOrdering() {
        return ordering;
    }

    public void setOrdering(String ordering) {
        this.ordering = ordering;
    }


    private Integer parent_l_seqno;
    private String parent_nucid;
    private String dec_type;
    private String type_a;
    private String type_c;
    private String intensity;
    private String intensity_unc;
    private Double intensity_num;
    private Double energy_num;
    private String energy;
    private String energy_unc;
    private String endpoint;
    private String endpoint_unc;
    private String rad_selected;
    private String ordering;

    public RadiationForDetail(Integer parent_l_seqno,String parent_nucid,String dec_type,String type_a,String type_c,String intensity,String intensity_unc,Double intensity_num,Double energy_num,String energy,String energy_unc,String endpoint,String endpoint_unc,String rad_selected, String ordering){
        this.parent_l_seqno = parent_l_seqno;
        this.parent_nucid = parent_nucid;
        this.dec_type = dec_type;
        this.type_a = type_a;
        this.type_c = type_c;
        this.intensity = intensity;
        this.intensity_unc = intensity_unc;
        this.intensity_num = intensity_num;
        this.energy_num = energy_num;
        this.energy = energy;
        this.energy_unc = energy_unc;
        this.endpoint = endpoint;
        this.endpoint_unc = endpoint_unc;
        this.rad_selected = rad_selected;
        this.ordering = ordering +"";
    }


}
