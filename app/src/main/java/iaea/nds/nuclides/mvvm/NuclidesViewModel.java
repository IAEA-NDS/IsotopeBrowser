package iaea.nds.nuclides.mvvm;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import iaea.nds.nuclides.db.entities.Nuclides;
import iaea.nds.nuclides.db.CumFYForDetail;
import iaea.nds.nuclides.db.DecayForDetails;
import iaea.nds.nuclides.db.RadiationForDetail;
import iaea.nds.nuclides.db.entities.Thermal_cross_sect;


public class NuclidesViewModel extends BaseViewModel {


    public NuclidesViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Nuclides> getNuclide(String rowid){
        return repository.getNuclide(rowid);
    }


    public List<DecayForDetails> nuclideParents(String nucid){
        return repository.nuclideParents(nucid);

    }

    public List<RadiationForDetail> radiationForDetail(String rowId , String orderbyRad){
        return repository.radiationForDetails( rowId ,  orderbyRad);

    }

    public List<CumFYForDetail> cumFYForForDetail(String rowId ){
        return repository.cumFYForDetails( rowId );

    }

    public List<Thermal_cross_sect> thermal_cs(String nucid, String l_seqno ){
        return repository.thermal_cs( nucid , l_seqno);

    }


}
