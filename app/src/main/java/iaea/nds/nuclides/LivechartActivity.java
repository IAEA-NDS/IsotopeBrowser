package iaea.nds.nuclides;


import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProviders;
import iaea.nds.nuclides.db.NuclideForChart;
import iaea.nds.nuclides.mvvm.LivechartViewModel;

public class LivechartActivity extends BaseActivity {



    private LivechartPlot livechartPlot;

    LivechartActivity mThis ;
    boolean update_db = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if( Config.db_needs_update(this)) {
            update_db = true;
            progressStartLoadDb(this);

        } else {
            progressStart(this);
        }

        setContentView(R.layout.livechart);
        livechartPlot = findViewById(R.id.livechart);

        LivechartViewModel livechartVM = ViewModelProviders.of(this).get(LivechartViewModel.class);
        livechartPlot.livechartVM = livechartVM;

        mThis = this;
        livechartVM.getNuclidesAll().observe(this, nuclideForCharts -> {
            if(nuclideForCharts != null){
                if(isQueryActive()) {
                    livechartPlot.setNUCS_GRAY(nuclideForCharts.toArray(new NuclideForChart[nuclideForCharts.size()]));
                } else {
                    livechartPlot.setNUCS_COLORED(nuclideForCharts.toArray(new NuclideForChart[nuclideForCharts.size()]));
                    livechartPlot.setNUCS_GRAY(new NuclideForChart[0]);
                }
            }
            progressDismiss();
            if(mThis.update_db) finish();
        });



        livechartVM.getNuclidesFilter().observe(this, nuclideForCharts -> {
            if(nuclideForCharts != null){
                livechartPlot.setNUCS_COLORED(nuclideForCharts.toArray(new NuclideForChart[nuclideForCharts.size()]));
            }
        });


        livechartVM.setNuclidesForChartAll("");

        if(isQueryActive()){
            livechartVM.setNuclidesForChartFilter(tables(), where());
        }


    }

    public float[] getChartSavedState(){
        return getChartSavedQuantities();
    }


    public String getNucidForDecayChainInChart(){
        return getNucidForDecayChain();
    }

    public void setNucidForDecayChainInChart(String nucid){
         setNucidForDecayChain(nucid);
    }


    public void showNuclideDetail(String pk){

        Intent nucidIntent = new Intent(getApplicationContext(), NuclidesActivity.class);
        nucidIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseActivity.setNucSelectedRowid(pk);
        startActivity(nucidIntent);

    }

    @Override
    public void onStop(){
        super.onStop();
        if(livechartPlot != null && livechartPlot.canvas_initialised){
            saveChartQuantities(livechartPlot.getQttToSave());
            saveChartDecays(livechartPlot.getOffsprings());
            saveChartNZatCentre(livechartPlot.getCenterNZ());

        }
    }

}
