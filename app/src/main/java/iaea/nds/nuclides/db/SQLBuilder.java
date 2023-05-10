package iaea.nds.nuclides.db;

import iaea.nds.nuclides.Config;


public class SQLBuilder {

    public static final String NUCLIDES_TABLE = "nuclides";
    public static final String L_DECAYS_TABLE = "l_decays";
    public static final String RADIATIONS_TABLE = "decay_radiations";
    public static final String NUCLIDES_DAUGHTER_TABLE = "nuclides";
    public static final String NUCLIDES_DAUGHTER_TABLE_ALIAS = "daughter";
    public static final String NUCLIDES_PARENT_TABLE = "nuclides";
    public static final String NUCLIDES_PARENT_TABLE_ALIAS = "parent";
    public static final String CUMULATIVE_FY_TABLE = "cumulative_fission";
    public static final String TCS_TABLE = "thermal_cross_sect";

    /* when using explicit sql, the rowid is used. Only when using the ROOM ORM, the pk column is used. See the NuclidesAndRadiation*/
    public static final String NUCLIDES_PK_ROWID = "ROWID";
    public static final String NUCLIDES_PK_ROWID_ALIAS = "pk"; // this must match the DecayForDetails column daughter_pk or parent_pk



    /**
     * decay nucrad " remove gamma from SF
     */
    public static final int DEC_TYPE_SF = 8;


    /**
     * THE NUCLIDES TABLE
     */

    public static final String mass = "mass";

    private static final String half_life_Label_alias = " CASE WHEN half_life in ('STABLE') THEN ' Stable' ELSE half_life END AS half_life";
    /**
     * order by on half_life in seconds. Takes care of proper ordering of NULL which are actually stable
     */
    private static String half_life_sec_orderby = " CASE WHEN half_life_sec is null THEN 9E99 ELSE half_life_sec END ";


    public static final String nucid = "nucid";
    public static final String l_seqno = "l_seqno";
    public static final String z = "z";
    public static final String n = "n";
    public static final String atomic_mass = "atomic_mass";

    public static final String symbol = "symbol";
    public static final String jp = "jp";
    public static final String half_life = "half_life";
    public static final String half_life_unc = "half_life_unc";
    public static final String half_life_unit = "half_life_unit";
    public static final String half_life_sec = "half_life_sec";
    public static final String mass_excess = "mass_excess";
    public static final String mass_excess_unc = "mass_excess_unc";
    public static final String binding = "binding";
    public static final String binding_unc = "binding_unc";

    public static final String atomic_mass_unc = "atomic_mass_unc";
    public static final String beta_decay_en = "beta_decay_en";
    public static final String beta_decay_en_unc = "beta_decay_en_unc";
    public static final String qa = "qa";
    public static final String qa_unc = "qa_unc";
    public static final String qec = "qec";
    public static final String qec_unc = "qec_unc";
    public static final String sn = "sn";
    public static final String sn_unc = "sn_unc";
    public static final String sp = "sp";
    public static final String sp_unc = "sp_unc";
    public static final String radii_val = "radii_val";
    public static final String radii_del = "radii_del";
    public static final String el_mom = "el_mom";
    public static final String mag_mom = "mag_mom";
    public static final String abundance = "abundance";
    public static final String abundance_unc = "abundance_unc";
    public static final String ther_capture = "ther_capture";
    public static final String ther_capture_unc = "ther_capture_unc";
    public static final String westcott_g = "westcott_g";
    public static final String resonance_integ = "resonance_integ";
    public static final String resonance_integ_unc = "resonance_integ_unc";
    public static final String tentative = "tentative";


    /**
     * THE DECAYS TABLE
     */

    public static final String dec_l_seqno = "l_seqno";
    public static final String dec_l_seqno_alias = L_DECAYS_TABLE +"."+"l_seqno as "+dec_l_seqno;
    public static final String dec_perc_oper = "perc_oper";
    public static final String dec_perc = "perc";
    public static final String dec_perc_num = "perc_num";
    public static final String dec_perc_unc = "unc";
    public static final String dec_dec_type = "dec_type";
    public static final String dec_daughter_nucid = "daughter_nucid";

    public static final String nuclide_daughter_rowid =  NUCLIDES_DAUGHTER_TABLE_ALIAS + "." + NUCLIDES_PK_ROWID;
    public static final String nuclide_daughter_rowid_alias =  NUCLIDES_DAUGHTER_TABLE_ALIAS + "_" + NUCLIDES_PK_ROWID_ALIAS;

    public static final String nuclide_parent_rowid =  NUCLIDES_PARENT_TABLE_ALIAS + "." + NUCLIDES_PK_ROWID;
    public static final String nuclide_parent_rowid_alias =  NUCLIDES_PARENT_TABLE_ALIAS + "_" + NUCLIDES_PK_ROWID_ALIAS;

    public static final String[] TBL_L_DECAYS_COL_NAMES = 			new String[]{dec_l_seqno_alias,dec_perc_oper,dec_perc,dec_perc_num,dec_perc_unc,dec_dec_type, dec_daughter_nucid};
    public static final String[] TBL_L_DECAYS_COL_NAMES_DAU_ROWID = new String[]{dec_l_seqno_alias,dec_perc_oper,dec_perc,dec_perc_num,dec_perc_unc,dec_dec_type, dec_daughter_nucid, nuclide_daughter_rowid + " as " + nuclide_daughter_rowid_alias};
    public static final String[] TBL_L_DECAYS_COL_NAMES_PAR_ROWID = new String[]{
            NUCLIDES_PARENT_TABLE_ALIAS + "." + nucid + " as " + nucid, NUCLIDES_PARENT_TABLE_ALIAS + "." +dec_l_seqno + " as " + l_seqno,dec_perc_oper,dec_perc,dec_perc_num,dec_perc_unc,dec_dec_type, dec_daughter_nucid, nuclide_parent_rowid + " as " + nuclide_parent_rowid_alias};

    /**
     * THE RADIATIONS TABLE
     */

    public static final String rad_parent_nucid = "parent_nucid";
    public static final String rad_parent_l_seqno = "parent_l_seqno";
    public static final String rad_dec_type = "dec_type";
    public static final String rad_type_a = "type_a";
    public static final String rad_type_c = "type_c";
    public static final String rad_intensity = "intensity";
    public static final String rad_intensity_unc = "intensity_unc";
    public static final String rad_intensity_num = "intensity_num";
    public static final String rad_energy = "energy";
    public static final String rad_energy_unc = "energy_unc";
    public static final String rad_energy_num = "energy_num";
    public static final String rad_endpoint = "endpoint";
    public static final String rad_endpoint_unc = "endpoint_unc";
    public static final String rad_selected = "rad_selected";

    public static final String[] TBL_RADIATIONS_COL_NAMES_NORADSEL = new String[]{rad_parent_nucid,rad_parent_l_seqno,rad_dec_type,rad_type_a,rad_type_c,
            rad_intensity,rad_intensity_unc,rad_intensity_num,rad_energy,rad_energy_unc,rad_energy_num,rad_endpoint,rad_endpoint_unc};
           //, rad_selected};

    /**
     * alias for the maximum intensity
     */
    public static final String maxint = "maxint";


    /**
     * THE CUMULATIVE_FY TABLE
     */
    public static String cfy_parent_nucid = "parent_nucid";
    public static String cfy_nucid = "nucid";
    public static String cfy_l_seqno = "l_seqno";
    public static String cfy_ther_yield_num = "ther_yield_num";
    public static String cfy_ther_yield_unc = "ther_yield_unc";


    public static final String version = "version";
    public static final String subversion = "subversion";
    public static final String nuclides_metadata = "nuclides_metadata";


    private static  String tablesToQueryLast = "";
    private static String whereForQueryLast = "";
    private static String queryDescLast = "";
    private static String orderbycode = "";
    private static String whereForRadtype = "";
    private static String whereForEnergy = "";
    private static int idxRadTypeSelected = -1;
    private static String nucidForDecayChain = "";
    private static String nucidsInDecayChain = "";
    private static String nucSelectedRowid = "";


    /**
     * THERMAL CS
     */

    public static String tcs_nucid = "nucid";
    public static String tcs_z = "z";
    public static String tcs_n = "n";
    public static String tcs_iso = "iso";
    public static String tcs_l_seqno = "l_seqno";
    public static String tcs_jp = "jp";
    public static String tcs_maxw_ng = "maxw_ng" ;
    public static String tcs_maxw_ng_unc = "maxw_ng_unc";
    public static String tcs_ng = "ng";
    public static String tcs_ng_unc = "ng_unc";
    public static String tcs_nel = "nel";
    public static String tcs_nel_unc = "nel_unc";
    public static String tcs_na = "na";
    public static String tcs_na_unc = "na_unc";
    public static String tcs_np = "np";
    public static String tcs_np_unc = "np_unc";
    public static String tcs_nf = "nf";
    public static String tcs_nf_unc = "nf_unc";
    public static String tcs_r = "r";
    public static String tcs_r_unc = "r_unc";
    public static String tcs_dd0d1 = "dd0d1";
    public static String tcs_dd0d1_unc = "dd0d1_unc";
    public static String tcs_ic = "ic";
    public static String tcs_ic_unc = "ic_unc";
    public static String tcs_ia = "ia";
    public static String tcs_ia_unc = "ia_unc";
    public static String tcs_ip = "ip";
    public static String tcs_ip_unc = "ip_unc";
    public static String tcs_iff = "iff";
    public static String tcs_iff_unc = "iff_unc";
    public static String tcs_iabs = "iabs";
    public static String tcs_iabs_unc = "iabs_unc";
    public static String tcs_macs30 = "macs30";
    public static String tcs_macs30_unc = "macs30_unc";



    public static String[] TBL_TCS_COL_NAMES = new String[]{tcs_nucid ,
            tcs_z ,
            tcs_n ,
            tcs_iso ,
            tcs_l_seqno ,
            tcs_jp ,
            tcs_maxw_ng ,
            tcs_maxw_ng_unc ,
            tcs_ng,
            tcs_ng_unc,
            tcs_nel ,
            tcs_nel_unc ,
            tcs_na ,
            tcs_na_unc ,
            tcs_np ,
            tcs_np_unc ,
            tcs_nf ,
            tcs_nf_unc,
            tcs_r ,
            tcs_r_unc ,
            tcs_dd0d1 ,
            tcs_dd0d1_unc ,
            tcs_ic ,
            tcs_ic_unc,
            tcs_ia ,
            tcs_ia_unc,
            tcs_ip ,
            tcs_ip_unc,
            tcs_iff ,
            tcs_iff_unc ,
            tcs_iabs ,
            tcs_iabs_unc ,
            tcs_macs30,
            tcs_macs30_unc};

    public static String query = "";


    public static void resetQuery(){
        tablesToQueryLast = "";
        whereForQueryLast = "";
        queryDescLast = "";
        whereForEnergy = "";
        whereForRadtype = "";

    }

    public static String update_pk(){
        return "update " + NUCLIDES_TABLE + " set pk = rowid";
    }

    public static String qryCumulativeFY(String rowid){
        return " select "
                //+ CUMULATIVE_FY_TABLE +"."
                + cfy_parent_nucid + " , "
                //+ CUMULATIVE_FY_TABLE +"."
                + cfy_ther_yield_num + " ,  "
                //+ CUMULATIVE_FY_TABLE +"."
                + cfy_ther_yield_unc + " , "
                + "np.rowid as parent_rowid,"
                + " CASE "
                + " WHEN " + CUMULATIVE_FY_TABLE +"."+ cfy_parent_nucid + "= '233U' THEN '1' "
                + " WHEN " + CUMULATIVE_FY_TABLE +"."+ cfy_parent_nucid + "= '235U' THEN '2' "
                + " WHEN " + CUMULATIVE_FY_TABLE +"."+ cfy_parent_nucid + "= '237NP' THEN '3' "
                + " WHEN " + CUMULATIVE_FY_TABLE +"."+ cfy_parent_nucid + "= '239PU' THEN '4' "
                + " WHEN " + CUMULATIVE_FY_TABLE +"."+ cfy_parent_nucid + "= '241PU' THEN '5' "
                + " WHEN " + CUMULATIVE_FY_TABLE +"."+ cfy_parent_nucid + "= '241AM' THEN '6' "
                + " ELSE '8' END as ordering"
                + " from " + CUMULATIVE_FY_TABLE + "," + NUCLIDES_TABLE + ","+ NUCLIDES_TABLE + " np "
                + " where "
                + CUMULATIVE_FY_TABLE +"."+ cfy_nucid + "=" + NUCLIDES_TABLE +"."+ nucid
                + " and " + CUMULATIVE_FY_TABLE +"."+ cfy_parent_nucid + "= np" +"."+ nucid
                + " and " + CUMULATIVE_FY_TABLE +"."+ cfy_l_seqno + "=" + NUCLIDES_TABLE +"."+ l_seqno
                + " and " + NUCLIDES_TABLE + "." + NUCLIDES_PK_ROWID + " = " + rowid + " order by ordering ";

    }

    public static String qryDecays(String rowId){
        String select  = "select ";
        String selcols = "";

        for (int i = 0; i < TBL_L_DECAYS_COL_NAMES_DAU_ROWID.length; i++) {
            selcols += TBL_L_DECAYS_COL_NAMES_DAU_ROWID[i] + " ";
            if(i != TBL_L_DECAYS_COL_NAMES_DAU_ROWID.length - 1){
                selcols += ",";
            }
        }


        select += selcols;
        select += " from " + L_DECAYS_TABLE + "," + NUCLIDES_TABLE + "," + NUCLIDES_DAUGHTER_TABLE + " as " + NUCLIDES_DAUGHTER_TABLE_ALIAS;

        String where = " where " + NUCLIDES_TABLE + "." + NUCLIDES_PK_ROWID_ALIAS + " = " + rowId
                + " and " + L_DECAYS_TABLE + "." + nucid + "=" + NUCLIDES_TABLE + "." + nucid
                + " and " + L_DECAYS_TABLE + "." + l_seqno + "=" + NUCLIDES_TABLE + "." + l_seqno
                + " and "+ NUCLIDES_DAUGHTER_TABLE_ALIAS +"."+ nucid +" = "+ L_DECAYS_TABLE +"."+ dec_daughter_nucid ;
        select += where;
        select += " union select " ;

        selcols = "";
        for (int i = 0; i < TBL_L_DECAYS_COL_NAMES.length; i++) {
            selcols +=  TBL_L_DECAYS_COL_NAMES[i] + " ";
            if(i != TBL_L_DECAYS_COL_NAMES.length - 1){
                selcols += ",";
            }
        }
        selcols += ", nuclides.ROWID as " + nuclide_daughter_rowid_alias;
        select += selcols;
        select += " from " + L_DECAYS_TABLE + "," + NUCLIDES_TABLE + " ";
        select += " where " + NUCLIDES_TABLE + "." + NUCLIDES_PK_ROWID_ALIAS + " = " + rowId
                + " and " + L_DECAYS_TABLE + "." + nucid + "=" + NUCLIDES_TABLE + "." + nucid
                + " and " + L_DECAYS_TABLE + "." + l_seqno + "=" + NUCLIDES_TABLE + "." + l_seqno
                + " and " + L_DECAYS_TABLE + "." + dec_daughter_nucid + " is null ";

        select += "  order by "+  dec_perc_num + " desc";

        return select;
    }

    public static String qryThermal_cs(String nucid, String l_seqno){
        String sel = " select ";
        for (int i = 0; i < TBL_TCS_COL_NAMES.length; i++){
            sel += (i == 0 ? " ": " , ") + TCS_TABLE + "." + TBL_TCS_COL_NAMES[i];
        }
        sel += " from " +  TCS_TABLE + " where " + TCS_TABLE + "." + tcs_nucid + " ='" + nucid + "'"
        + " and " +  TCS_TABLE + "." + tcs_l_seqno + " = " +l_seqno;
        return sel;
    }

    public static String qryDaughters(String nucid){
        return "select " + dec_daughter_nucid + ", " +
                dec_dec_type + ", " +
                z + ", " +
                n + " " +
                " from " + L_DECAYS_TABLE + "," + NUCLIDES_TABLE +
                " where " + L_DECAYS_TABLE + "." + dec_l_seqno + " = 0 " +
                " and " + dec_perc_num + " > 0 " +
                " and " + dec_daughter_nucid + " is not null " +
                " and " + L_DECAYS_TABLE + "." + SQLBuilder.nucid + "='" + nucid + "' " +
                " and " + L_DECAYS_TABLE + "." + SQLBuilder.nucid  + "=" + NUCLIDES_TABLE + "." + SQLBuilder.nucid  +
                " and " + L_DECAYS_TABLE + "." + dec_l_seqno + "=" + NUCLIDES_TABLE + "." + l_seqno +
                " order by " + dec_perc_num + " desc ";

    }

    public static String qryParentsForChart(String nucid){
        return "select " + SQLBuilder.nucid  + ", " +
                dec_dec_type +
                " from " + L_DECAYS_TABLE +
                " where "+ L_DECAYS_TABLE + "." + dec_l_seqno +" = 0 " +
                " and " + dec_perc_num +" > 0 " +
                " and " + SQLBuilder.nucid   +" is not null " +
                " and " + dec_daughter_nucid+ "='" + nucid + "' " +
                " order by " + dec_perc_num+ " desc ";

    }
/*MV
check how it fills the L_decay class
 */
    public static String qryParents(String daughter_nucid) {
        String where = L_DECAYS_TABLE + "." + dec_daughter_nucid + " = '" + daughter_nucid + "'"
                + " and " + L_DECAYS_TABLE + "." + nucid + "=" + NUCLIDES_PARENT_TABLE_ALIAS + "." + nucid
                + " and " + L_DECAYS_TABLE + "." + l_seqno + "=" + NUCLIDES_PARENT_TABLE_ALIAS + "." + l_seqno;

        //"nuclides LEFT OUTER JOIN l_decays ON (nuclides.nucid = l_decays.nucid and l_decays.l_seqno = 0 )"

        return " select " + buildColumnsString(TBL_L_DECAYS_COL_NAMES_PAR_ROWID)
                + " from " + L_DECAYS_TABLE  + "," + NUCLIDES_PARENT_TABLE + " as " + NUCLIDES_PARENT_TABLE_ALIAS + " where " + where
                + " order by " + dec_perc_num + " desc";


    }

    private static String buildColumnsString(String[] columns){
        String col = "";
        for (int i = 0 ; i < columns.length ; i++){
            col += ", " + columns[i];
        }
       return col.substring(1);
    }

    public static String qryNuclideRadiations(String nuc_rowId , String orderbyRad) {


        String order = rad_intensity_num;

        if(orderbyRad.equals(Config.PREFS_VALUE_RADIATION_ORDER_EN)){
            order = rad_energy_num;
        }

        String case_col = "";
        if(whereForRadtype.length() != 0){
            case_col +=    RADIATIONS_TABLE + "." + whereForRadtype;
        }

        if(whereForEnergy.length() != 0){
            if( case_col.length() > 0 ){
                case_col += " and ";
            }
            case_col +=   RADIATIONS_TABLE + "." + whereForEnergy;
        }


        String where = NUCLIDES_TABLE + "." + NUCLIDES_PK_ROWID + " = " + nuc_rowId
                + " and " + RADIATIONS_TABLE + "." + rad_parent_nucid + "=" + NUCLIDES_TABLE + "." + nucid
                + " and " + RADIATIONS_TABLE + "." + rad_parent_l_seqno + "=" + NUCLIDES_TABLE + "." + l_seqno
                + " and " +  RADIATIONS_TABLE + "." + dec_dec_type + "!=" + DEC_TYPE_SF;


        String columns = buildColumnsString(TBL_RADIATIONS_COL_NAMES_NORADSEL);

        String colradsel = " 'N' as rad_selected ";
        if (case_col.length() != 0){
            colradsel = " CASE WHEN ( " + case_col + " ) THEN 'Y' ELSE 'N' END AS " + rad_selected;
        }

        /* SQLite < 3.7.15 does not support instr*/
           String ordering ="case " +
                "when  type_a='A'" +
                "   then 0 " +
                " else " +
                " case when type_a ='B+' " +
                " then 1" +
                " ELSE " +
                " case when type_a = 'B-'" +
                " then 2" +
                " else " +
                "   case   when type_a='G' then 3 " +
                " else " +
                " case when type_a='AN' " +
                " then 4 else case when type_a='X' then 5 else case when type_A='AU' then 6 else 7  end end end end end end end as ordering ";
        columns += ", " + colradsel + ", " +ordering;

        return " select " + columns + " from " + RADIATIONS_TABLE + "," + NUCLIDES_TABLE + " where " + where + " order by " + dec_dec_type + ", ordering, " + order + " desc";


    }

    public static void setIdxRadTypeSelected(int myidxRadTypeSelected) {
        idxRadTypeSelected = myidxRadTypeSelected;
    }

    public boolean isFilter(){
        if(whereForQueryLast.length() == 0){
            return false;
        }
        return true;
    }

    public static boolean isFilterByRad(){
        if(whereForRadtype.length() == 0){
            return false;
        }
        return true;
    }

    public static void setWhereForRadtype(String mywhereForRadtype) {
        whereForRadtype = mywhereForRadtype;
    }

    public static String getWhereForEnergy() {
        return whereForEnergy;
    }

    public static void setWhereForEnergy(String mywhereForEnergy) {
        whereForEnergy = mywhereForEnergy;
    }

    public static void setQueryParts(String tablesToQuery,String whereForQuery, String queryDesc){

        tablesToQueryLast = tablesToQuery;
        whereForQueryLast = whereForQuery;
        queryDescLast = queryDesc;
    }


    public static void setQueryPartsFromElement(String element, int z, String desc){
        whereForQueryLast = ( NUCLIDES_TABLE + ".z = " + z + "" );
        tablesToQueryLast = NUCLIDES_TABLE;
        String elem = element.substring(0,1).toUpperCase() + element.substring(1, element.length()).toLowerCase();
        queryDescLast = ( " " + desc + " " +elem);
    }

    public static  boolean isQueryEmpty(){
        return whereForQueryLast.length() == 0;
    }

    /**
     * constructs the query
     * @param isViewInRadMode boolean true, order by intensity
     * @return
     */
    public static String buildQueryForList(boolean isViewInRadMode){


        if(getNucidsInDecayChain().length() > 0){

           /* SQLite < 3.7.15 does not support instr
            String in = "  CASE " +
                    "WHEN instr (nuclides.nucid, 'm2') != 0  then replace (nuclides.nucid, 'm2', '')  " +
                    "WHEN instr (nuclides.nucid, 'm1') != 0  then replace (nuclides.nucid, 'm1', '')  " +
                    "WHEN instr (nuclides.nucid, 'm') != 0  then replace (nuclides.nucid, 'm', '') " +
                    " else nuclides.nucid end in ( " + getNucidsInDecayChain() + " ) " ;
                    */

            String in = "  CASE " +
                    "WHEN (nuclides.nucid like '%m2')    then replace (nuclides.nucid, 'm2', '')  " +
                    "WHEN (nuclides.nucid like '%m1')    then replace (nuclides.nucid, 'm1', '')  " +
                    "WHEN (nuclides.nucid like '%m')    then replace (nuclides.nucid, 'm', '') " +
                    " else nuclides.nucid end in ( " + getNucidsInDecayChain() + " ) " ;
            String repl = "upper\\(nuclides.nucid\\) like '%" + getNucidForDecayChain()  + "%' ";

            whereForQueryLast = whereForQueryLast.replaceAll(repl,in);

        }

        if(whereForRadtype.length() > 0){

            return " SELECT nuclides.pk, nuclides.z,  nuclides.n , nuclides.symbol , nuclides.tentative, nuclides.jp, " +
                    " decay_radiations.energy_num ,max(decay_radiations.intensity_num) as intensity_num, intensity, nuclides.nucid , decay_radiations.dr_pk, " +
                    //" parent_l_seqno as plevel " +
                    half_life_Label_alias + ", " +
                    half_life_unc+ ", " +
                    half_life_unit +
                    " FROM " + getTablesToQueryLast() +
                    " WHERE   " +
                    whereForQueryLast+
                    " GROUP BY nuclides.nucid ORDER BY " + (isViewInRadMode ? "intensity_num desc" : getOrderbySQL()  );

        }

        return " select * from " + getTablesToQueryLast() +  " where " + getWhereForQueryLast() + " order by " + getOrderbySQL();


    }

    private static String getOrderbySQL(){
        String _orderBy = NUCLIDES_TABLE + "." + z + ", " +  NUCLIDES_TABLE + "." + n;
        if(getOrderbyCode().equals(Config.PREFS_VALUE_NUCLIDES_ORDER_NZ)){
            _orderBy = NUCLIDES_TABLE + "." + n + ", " +  NUCLIDES_TABLE + "." + z;
        }
        else if(getOrderbyCode().equals(Config.PREFS_VALUE_NUCLIDES_ORDER_HALF)){
            _orderBy =  half_life_sec_orderby;
        }
        else if(getOrderbyCode().equals(Config.PREFS_VALUE_RADIATION_ORDER_INT)){
            _orderBy =  rad_intensity_num + " desc ";
        }
        return _orderBy;
    }

    /**
     * query when the where is only on nuclides table, if where = '' all nuclides
     * @param addWhere
     * @return
     */
    public static String buildQueryForChart(String addWhere) {

        return NuclideForChart.select_fields_all +
                " from nuclides , l_decays l" +
                " where nuclides.nucid=l.nucid and nuclides.l_seqno = l.l_seqno and l.l_seqno=0 " +
                " and ifnull(perc_num,0) in (select max(ifnull(perc_num,0)) from l_decays ll where l.nucid=ll.nucid and ll.l_seqno = 0) " +
//				" and ifnull(dec_type,0) in (select min(ifnull(dec_type,0)) from l_decays ll where l.nucid=ll.nucid and ll.l_seqno = 0 and ll.perc_num=l.perc_num) " + // this is for cases 50-50
                (addWhere.length() == 0 ? "" : " and " + addWhere) +
                " union " +
                " select  nuclides.nucid as nucid, nuclides.tentative as tentative, nuclides.z as z, nuclides.n as n,  (nuclides.z + nuclides.n) as mass, case when half_life like 'STABLE' then -1 else -2 end as dec_type, half_life, half_life_unit, half_life_unc, nuclides.rowid as rowid, abundance " +
                " from nuclides " +
                " where not exists (select null from l_decays l where nuclides.nucid = l.nucid  and nuclides.l_seqno = l.l_seqno  ) " +
                (addWhere.length() == 0 ? "" : " and " + addWhere) +
                " and nuclides.mass_excess is not null " + // to avoid metastable
                " order by nuclides.z desc, nuclides.nucid";
    }

    /**
     * query for the chart
     * @param tables
     * @param where
     * @return
     */
    public static String buildQueryFilteredForChart(String tables, String where){
        if(tables.indexOf(L_DECAYS_TABLE) == -1 && tables.indexOf(RADIATIONS_TABLE) == -1){
            return buildQueryForChart(where);
        }

        if(tables.indexOf(L_DECAYS_TABLE) == -1){
            tables += ", " + L_DECAYS_TABLE ;
            where += " and "+ NUCLIDES_TABLE+"."+nucid+" = "+L_DECAYS_TABLE+"."+nucid
                    + " and "+ NUCLIDES_TABLE+"."+l_seqno+" = "+L_DECAYS_TABLE+"."+dec_l_seqno
                    +" and "+L_DECAYS_TABLE+"."+dec_l_seqno+" = 0";

        } else {
            where += " and "+ L_DECAYS_TABLE+"."+dec_l_seqno+" = 0";
            int idx1 = where.indexOf(L_DECAYS_TABLE+"."+dec_dec_type+" in");
            int idx2 = where.indexOf(")",idx1);
            String and = " ";
            if(where.substring(0, idx1).trim().length() != 0 && !where.substring(0, idx1).trim().endsWith("and")){
                and = " and ";
            }
            where = where.substring(0, idx1)
                    +  and + " exists (select null from "+L_DECAYS_TABLE+" l where "
                    + " l."+nucid+" = "+NUCLIDES_TABLE+"."+nucid
                    + " and l."+dec_l_seqno+" = "+NUCLIDES_TABLE+"."+l_seqno
                    +" and l."+dec_dec_type+" in "
                    + where.substring(idx1 + (L_DECAYS_TABLE+"."+dec_dec_type+" in").length(), idx2 + 1) + ") "
                    + where.substring(idx2 + 2);

        }

        where += " and ifnull("+dec_perc_num+",0) in (select max(ifnull("+dec_perc_num+",0)) from "+L_DECAYS_TABLE+" ll where "+L_DECAYS_TABLE+"."+nucid+"=ll."+nucid+" and ll."+dec_l_seqno+" = 0) ";

        String sel =  NuclideForChart.select_fields_filter + " from " +  tables +  " where " + where;

        return sel;

    }

    public static String getTablesToQueryLast() {
        return tablesToQueryLast;
    }

    public static void setTablesToQueryLast(String tablesToQueryLast) {
        SQLBuilder.tablesToQueryLast = tablesToQueryLast;
    }

    public static String getWhereForQueryLast() {
        return whereForQueryLast;
    }


    public static String getQueryDescLast() {
        return queryDescLast;
    }

    public static String getOrderbyCode() {
        return orderbycode;
    }

    public static void setOrderbyCode(String orderby) {
        SQLBuilder.orderbycode = orderby;
    }

    public static String getNucidForDecayChain() {
        return SQLBuilder.nucidForDecayChain;
    }

    public static void setNucidForDecayChain(String nucidForDecayChain) {
        SQLBuilder.nucidForDecayChain = nucidForDecayChain;
    }

    public static String getNucidsInDecayChain() {
        return nucidsInDecayChain;
    }

    public static void setNucidsInDecayChain(String mynucidsInDecayChain) {
        SQLBuilder.nucidsInDecayChain = mynucidsInDecayChain;
    }

    public static String getNucSelectedRowid() {
        return nucSelectedRowid;
    }

    public static void setNucSelectedRowid(String nucSelectedRowid) {
        SQLBuilder.nucSelectedRowid = nucSelectedRowid;
    }

    public static int getIdxRadTypeSelected() {
        return idxRadTypeSelected;
    }



}
