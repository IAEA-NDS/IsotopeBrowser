package iaea.nds.nuclides.mvvm;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import iaea.nds.nuclides.db.DecayForDetails;

public class BaseViewModel extends AndroidViewModel {

    protected IbRepository repository;

    public BaseViewModel(@NonNull Application application) {
        super(application);
        repository = new IbRepository(application);
    }

    public List<DecayForDetails> nuclideDecays(String rowid){
        return repository.nuclideDecays(rowid);
    }

    public String nucidsInChain(String parentNucid){
        String ret = "";
        if(parentNucid != null && parentNucid.length() != 0){
            try{
            ret = repository.nucidsInChain(parentNucid);
            } catch (Exception e){
                ret = "";
            }
        }
        return ret;
    }
}
