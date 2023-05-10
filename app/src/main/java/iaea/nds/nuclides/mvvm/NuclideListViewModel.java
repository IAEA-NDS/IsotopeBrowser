package iaea.nds.nuclides.mvvm;

import android.app.Application;
import android.content.Intent;
import android.widget.TextView;

import androidx.paging.PagedList;
import iaea.nds.nuclides.BaseActivity;
import iaea.nds.nuclides.Formatter;
import iaea.nds.nuclides.NuclidesActivity;
import iaea.nds.nuclides.db.entities.L_decays;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import iaea.nds.nuclides.db.entities.Nuclides;
import iaea.nds.nuclides.db.NuclidesAndRadiation;
import iaea.nds.nuclides.db.DecayForDetails;


public class NuclideListViewModel extends BaseViewModel {


    private LiveData<PagedList<NuclidesAndRadiation>> nuclidesPaged;
    private String lblRadTypeSelected = "";

    public NuclideListViewModel(@NonNull Application application) {
        super(application);

    }

    public LiveData<PagedList<NuclidesAndRadiation>> getNuclidesPaged(){
        return nuclidesPaged;
    }

    public boolean checkvalidity(String query){
        try {
            repository.compileStatement(query);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public void setNuclidesPagedAndRadiation(String qry){
        nuclidesPaged = repository.getNuclidesAndRadiationPaged(qry);
    }

    public void itemClicked(String nucPk){
        int nucRowid = repository.getRowidromPk(nucPk);
        BaseActivity.setNucSelectedRowid(nucRowid+"");

        Intent nucidIntent = new Intent(getApplication().getBaseContext(), NuclidesActivity.class);
        nucidIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(nucidIntent);
    }

    public void setNuclideDecayAbundance(String rowid, String abundance, String abundance_unc, TextView txt){

        if(rowid != null && rowid.length() > 0){
            List<DecayForDetails> l_decays=  nuclideDecays(rowid);
            L_decays decay =  null;

            if(l_decays.size() > 0) {
                decay = l_decays.get(0);
                Formatter.getDecayLabel(txt, decay.getDec_type(), decay.getPerc(), decay.getUnc(), decay.getPerc_oper());

            } else if(abundance_unc!= null){
                Formatter.abundance(txt, abundance, abundance_unc);

            } else {
                Formatter.decayUncertain(txt);
            }
        }

    }

    public void setLblRadTypeSelected(String lbl){
        this.lblRadTypeSelected = lbl;
    }

    public String getLblRadTypeSelected(){
        return lblRadTypeSelected;
    }



}
