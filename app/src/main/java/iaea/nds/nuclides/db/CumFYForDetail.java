package iaea.nds.nuclides.db;

public class CumFYForDetail {



        private String parent_nucid;
        private String nucid;
        private Integer l_seqno;
        private String ther_yield_num;
        private String ther_yield_unc;
        private String parent_rowid;

    public String getParent_nucid() {
        return parent_nucid;
    }

    public void setParent_nucid(String parent_nucid) {
        this.parent_nucid = parent_nucid;
    }

    public String getNucid() {
        return nucid;
    }

    public void setNucid(String nucid) {
        this.nucid = nucid;
    }

    public Integer getL_seqno() {
        return l_seqno;
    }

    public void setL_seqno(Integer l_seqno) {
        this.l_seqno = l_seqno;
    }

    public String getTher_yield_num() {
        return ther_yield_num;
    }

    public void setTher_yield_num(String ther_yield_num) {
        this.ther_yield_num = ther_yield_num;
    }

    public String getTher_yield_unc() {
        return ther_yield_unc;
    }

    public void setTher_yield_unc(String ther_yield_unc) {
        this.ther_yield_unc = ther_yield_unc;
    }

    public String getParent_rowid() {
        return parent_rowid;
    }

    public void setParent_rowid(String parent_rowid) {
        this.parent_rowid = parent_rowid;
    }

    public CumFYForDetail(String parent_nucid, String nucid, Integer l_seqno, String ther_yield_num, String ther_yield_unc, String parent_rowid){
            this.parent_nucid = parent_nucid;
            this.nucid = nucid;
            this.l_seqno = l_seqno;
            this.ther_yield_num = ther_yield_num;
            this.ther_yield_unc = ther_yield_unc;
            this.parent_rowid = parent_rowid;
        }

}
