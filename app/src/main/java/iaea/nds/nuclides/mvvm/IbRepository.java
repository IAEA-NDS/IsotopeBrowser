package iaea.nds.nuclides.mvvm;

import android.app.Application;
import android.os.AsyncTask;

import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.sqlite.db.SupportSQLiteStatement;
import iaea.nds.nuclides.db.*;


import java.util.List;
import java.util.Vector;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;
import iaea.nds.nuclides.db.CumFYForDetail;
import iaea.nds.nuclides.db.DecayForDetails;
import iaea.nds.nuclides.db.NuclideForChart;
import iaea.nds.nuclides.db.RadiationForDetail;
import iaea.nds.nuclides.db.entities.Nuclides;
import iaea.nds.nuclides.db.entities.Thermal_cross_sect;


public class IbRepository {

    private IbDao ibDao;
  //  LiveData<List<Nuclides>> nuclides;
  //  private MutableLiveData<List<Nuclides>> nuclidesForList = new MutableLiveData<List<Nuclides>>();
  //  private MutableLiveData<PagedList<Nuclides>> nuclidesForListPaged = new MutableLiveData<PagedList<Nuclides>>();

    private MutableLiveData<List<NuclideForChart>> nuclidesForChartAll = new MutableLiveData<List<NuclideForChart>>();
    private MutableLiveData<List<NuclideForChart>> nuclidesForChartFilter = new MutableLiveData<List<NuclideForChart>>();

    private IbDatabase ibDatabase;


    public IbRepository(Application application){
        ibDatabase= IbDatabase.getInstance(application);
        ibDao = ibDatabase.ibDao();
        /* important : since it was not possible anymore to copy the db binaries
         there were problems of mismatch between pk column and rowid
         */
        ibDao.updatepk();

        //ibDao.update_in116();

    }

    public SupportSQLiteStatement compileStatement(String query){

        return ibDatabase.compileStatement(query);


    }

    public int getRowidromPk(String pk){
        return ibDao.getRowidFromPk(pk);
    }

    public LiveData<Nuclides> getNuclide(String rowid){
       return ibDao.getNuclide(rowid);

    }

    public Nuclides[] getNuclideFromNucid(String nucid){
        return ibDao.getNuclideFromNucid(nucid);
    }

    public List<DecayForDetails> nuclideDecays(String rowid){
        return ibDao.getDecays(new SimpleSQLiteQuery(SQLBuilder.qryDecays(rowid)));

    }

    public List<DecayForDetails> nuclideParents(String daughter_nucid){
        return ibDao.getDecays(new SimpleSQLiteQuery(SQLBuilder.qryParents(daughter_nucid)));

    }

    public List<DecayForDetails> decaysFromParents(String nucid){
        return ibDao.getDecays(new SimpleSQLiteQuery(SQLBuilder.qryParentsForChart(nucid)));

    }

    public List<DecayForDetails> decaysToDaughters(String nucid){
        return ibDao.getDecays(new SimpleSQLiteQuery(SQLBuilder.qryDaughters(nucid)));

    }

    public List<RadiationForDetail> radiationForDetails(String rowId , String orderbyRad){
        return ibDao.getRadiationForDetail( new SimpleSQLiteQuery(SQLBuilder.qryNuclideRadiations(rowId ,  orderbyRad)));
    }

    public List<CumFYForDetail> cumFYForDetails(String rowId){
        return ibDao.getCumFYForDetail( new SimpleSQLiteQuery(SQLBuilder.qryCumulativeFY(rowId )));
    }

    public List<Thermal_cross_sect> thermal_cs(String nucid, String l_seqno){
        return ibDao.getTcs( new SimpleSQLiteQuery(SQLBuilder.qryThermal_cs(nucid, l_seqno )));
    }
/*
    public LiveData<PagedList<Nuclides>> getNuclidesPagedStd(String qry) {return (LiveData<PagedList<Nuclides>>) new LivePagedListBuilder<>(
            ibDao.getNuclidesPagedStd(new SimpleSQLiteQuery(qry)), 20).build();
    }
*/
    public LiveData<PagedList<NuclidesAndRadiation>> getNuclidesAndRadiationPaged(String qry) {return (LiveData<PagedList<NuclidesAndRadiation>>) new LivePagedListBuilder<>(

            ibDao.getNuclidesAndRadiationPaged(new SimpleSQLiteQuery(qry)), 20).build();

    }

    public MutableLiveData<List<NuclideForChart>> getNuclidesForChartAll(){
        return nuclidesForChartAll;
    }

    public void setNuclidesForChartAllAsync(String where){
        (new setNuclidesForChartAllAsyncTask(where)).execute();
    }

    private  class setNuclidesForChartAllAsyncTask extends AsyncTask<Void, Void,Void> {
        List<NuclideForChart> _nuclidesForChart;
        String _where;
        private setNuclidesForChartAllAsyncTask(String where ){
            this._where = where;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            _nuclidesForChart = ibDao.getNuclidesForChart(new SimpleSQLiteQuery( SQLBuilder.buildQueryForChart(_where)));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            nuclidesForChartAll.setValue(_nuclidesForChart);
        }

    }

    public MutableLiveData<List<NuclideForChart>> getNuclidesForChartFilter(){
        return nuclidesForChartFilter;
    }


    public void setNuclidesForChartFilterAsync(String tables, String where){
        (new setNuclidesForChartFilterAsyncTask(tables, where)).execute();
    }

    private  class setNuclidesForChartFilterAsyncTask extends AsyncTask<Void, Void,Void> {
        List<NuclideForChart> _nuclidesForChart;
        String _where;
        String _tables;
        private setNuclidesForChartFilterAsyncTask(String tables, String where ){
            this._where = where;
            this._tables = tables;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            _nuclidesForChart = ibDao.getNuclidesForChart(new SimpleSQLiteQuery( SQLBuilder.buildQueryFilteredForChart(_tables, _where)));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            nuclidesForChartFilter.setValue(_nuclidesForChart);
        }

    }

    public String nucidsInChain(String parentNucid){
        if(parentNucid == null || parentNucid.length() == 0) return "";
        Vector<String> dtrs = decayChain(parentNucid,null);
        String in = "";
        for (int i = 0 ; i < dtrs.size(); i++){
            in += ",'"+ dtrs.elementAt(i)+"'";
        }
        return in.length()  > 0 ? in.substring(1) : in;
    }

    /*
String with NUCIDs in the decay chain call this with decayChain = nil;
it first creates a decay chain, then recursively calls itself
*/
    public Vector<String> decayChain(String nucid , Vector<String> decayChain){

        /* first  call : create the string*/
        if(decayChain == null){
            decayChain = new Vector<String>();
            decayChain.add(nucid);
        }

        List<DecayForDetails> daughters = decaysToDaughters( nucid);
        if(daughters == null || daughters.size() == 0) return decayChain;

        String daug = daughters.get(0).getDaughter_nucid();
        if(!decayChain.contains(daug)) {
            decayChain.add(daug);
            decayChain(daug,decayChain);
        }


       for (int i = 1 ; i < daughters.size(); i++){
            daug = daughters.get(i).getDaughter_nucid();
            boolean skip = false;

            if(decayChain.contains(daug)){
                skip = true;
            }

            if(! skip){
                decayChain.add(daug);
            }
            decayChain(daug,decayChain);

        }

        return decayChain;
    }



    //public MutableLiveData<List<Nuclides>> getNuclidesForList() {return nuclidesForList;}


    /*
    public void setNuclidesForChartAllAsync(){
        (new setNuclidesForListAsyncTask()).execute();
    }


    private List<Nuclides> fetchNuclidesForList(){
        return ibDao.getNuclidesForList(new SimpleSQLiteQuery("select *, rowid from nuclides _where nucid like '%1%' "));

    }

    private  class setNuclidesForListAsyncTask extends AsyncTask<Void, Void,Void> {
        List<Nuclides> _nuclidesForList;
        private setNuclidesForListAsyncTask( ){
        }

        @Override
        protected Void doInBackground(Void... voids) {
            _nuclidesForList = fetchNuclidesForList();
            Nuclides nuclideForList;
            List<L_decays> l_decay;
            for (int i = 0 ; i < _nuclidesForList.size(); i++){
                nuclideForList = _nuclidesForList.get(i);
                l_decay = nuclideDecays(nuclideForList.getDr_pk());
                if(l_decay.size() > 0) {
                    nuclideForList.setDecay_label(l_decay.get(0).getDecay_label());
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            nuclidesForList.setValue(_nuclidesForList);
        }

    }
    */


}
