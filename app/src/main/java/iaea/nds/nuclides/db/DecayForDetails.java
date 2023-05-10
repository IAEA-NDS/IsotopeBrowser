package iaea.nds.nuclides.db;

import iaea.nds.nuclides.db.entities.L_decays;

public class DecayForDetails extends L_decays {

    public String getDaughter_pk() {
        return daughter_pk;
    }

    public void setDaughter_pk(String daughter_pk) {
        this.daughter_pk = daughter_pk;
    }

    String daughter_pk;
    String parent_pk;

    public String getParent_pk() {
        return parent_pk;
    }

    public void setParent_pk(String parent_pk) {
        this.parent_pk = parent_pk;
    }

    public DecayForDetails (String nucid, Integer l_seqno, String perc, Double perc_num, String unc, String perc_oper, Integer dec_type, String daughter_nucid, String decay_label, String daughter_pk, String parent_pk){

        super( nucid, l_seqno, perc, perc_num, unc, perc_oper, dec_type, daughter_nucid, decay_label);
        this.daughter_pk = daughter_pk;
        this.parent_pk = parent_pk;
    }

}
