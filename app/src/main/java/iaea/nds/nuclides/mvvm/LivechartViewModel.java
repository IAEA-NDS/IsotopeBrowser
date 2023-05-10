package iaea.nds.nuclides.mvvm;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import iaea.nds.nuclides.db.DecayForDetails;
import iaea.nds.nuclides.db.NuclideForChart;

public class LivechartViewModel extends BaseViewModel {


    public LivechartViewModel(@NonNull Application application) {
        super(application);

    }

    public LiveData<List<NuclideForChart>> getNuclidesAll(){
        return repository.getNuclidesForChartAll();
    }

    public LiveData<List<NuclideForChart>> getNuclidesFilter(){
        return repository.getNuclidesForChartFilter();
    }

    public void setNuclidesForChartAll(String where){
        repository.setNuclidesForChartAllAsync(where);
    }

    public void setNuclidesForChartFilter(String tables, String where){
        repository.setNuclidesForChartFilterAsync(tables, where);
    }


    public List<DecayForDetails> decaysToDaughter(String nucid){
        return repository.decaysToDaughters(nucid);

    }

    public List<DecayForDetails> decaysFromParents(String nucid){
        return repository.decaysFromParents(nucid);

    }




}
