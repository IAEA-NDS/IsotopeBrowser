package iaea.nds.nuclides;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;


import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import androidx.recyclerview.widget.SimpleItemAnimator;
import iaea.nds.nuclides.db.NuclidesAndRadiation;
import iaea.nds.nuclides.mvvm.NuclideListItemAdapterPagedRad;
import iaea.nds.nuclides.mvvm.NuclideListItemAdapterPagedStd;
import iaea.nds.nuclides.mvvm.NuclideListViewModel;

public class NuclidesListActivity extends BaseActivity {

    NuclideListViewModel nuclideListViewModel;
    RecyclerView recyclerView;

    private int firstVisiblePosition = 0;
    private int lastVisiblePosition = 0;

    private TextView txtDesc;

    private Button mBtnHL;
    private Button mBtnInt;
    /**
     * list shows h-l and decay mode
     */
    NuclideListItemAdapterPagedStd adapterPagedStd;
    /**
     * list shows radiation int. and en.
     */
    NuclideListItemAdapterPagedRad adapterPagedRad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if(Config.db_needs_update(this)) {
            progressStartLoadDb(this);
        } else {
            progressStart(this);
        }

        initView(isFilterByRad());

        nuclideListViewModel = ViewModelProviders.of(this).get(NuclideListViewModel.class);

        recyclerView = findViewById(R.id.recycler_nuclides_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false); // putting true would not display anything when going back and forth
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        adapterPagedStd = new NuclideListItemAdapterPagedStd();
        adapterPagedStd.setNuclideListViewModel(nuclideListViewModel);

        adapterPagedRad = new NuclideListItemAdapterPagedRad();
        adapterPagedRad.setNuclideListViewModel(nuclideListViewModel);

        // in case the "search in decay chain" is active, now fills the decay chains
        setNucidsInDecayChain(nuclideListViewModel.nucidsInChain(getNucidForDecayChain()));

        if(nuclideListViewModel.checkvalidity(queryForList())){
            nuclideListViewModel.setNuclidesPagedAndRadiation(queryForList()); // the SQL
            nuclideListViewModel.getNuclidesPaged().observe(this, nuclides -> {
                setResSetCountLbl(nuclides);
                submitToAdapter(nuclides);
                progressDismiss();
            });
        } else {
            progressDismiss();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(this.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Query", "*" + queryDesc() +"*"); //queryForList()
            clipboard.setPrimaryClip(clip);
            txtDesc.setText("** Error **\nDetails about the error are already in your clipboard\nPlease past it in an email to nds.contact-point@iaea.org\n\nThanks for your help\n*" + queryDesc()+"*"); //queryForList()
        }


        if(isFilterByRad()){
            recyclerView.setAdapter(adapterPagedRad);
            nuclideListViewModel.setLblRadTypeSelected(lblRadTypeSelected());
        } else {
            recyclerView.setAdapter(adapterPagedStd);
        }


    }

    /**
     * the label with the result count
     * @param nuclides
     */
    private void setResSetCountLbl(PagedList nuclides){
        if(nuclides == null  || nuclides.size() == 0){
            txtDesc.setText(Html.fromHtml(getString(R.string.no_results) +  " " + queryDesc() ));
        } else {
            int count = nuclides.size();
            String countString = getResources().getQuantityString(R.plurals.search_results,
                    count, count,  queryDesc().trim());

            int o = countString.indexOf(" ");

            countString = "<b>" + countString.substring(0,o) + "</b>" + countString.substring(o);
            txtDesc.setText(Html.fromHtml(countString));

        }
    }
    private void submitToAdapter(PagedList<NuclidesAndRadiation> nuclides){
            adapterPagedRad.submitList(nuclides);
            adapterPagedStd.submitList(nuclides);
    }

    private void initView(boolean isFilterByRad){

        if(isFilterByRad) {
            setContentView(R.layout.nuclides_list_intensity);
            mBtnHL = findViewById(R.id.btnSortHL);
            mBtnInt = findViewById(R.id.btnSortIR);

            mBtnHL.setEnabled(true);
            mBtnInt.setEnabled(false);

            mBtnHL.setOnClickListener(v -> {

                    if(mBtnInt.isEnabled()){ return; }
                    setNuclidesListDisplayMode(Config.PREFS_VALUE_NUCLIST_ORDER_HL);
                    mBtnHL.setEnabled(false);
                    mBtnInt.setEnabled(true);
                    recyclerView.setAdapter(adapterPagedStd);

            });

            mBtnInt.setOnClickListener(v ->{

                    if(mBtnHL.isEnabled()){ return; }
                    mBtnHL.setEnabled(true);
                    mBtnInt.setEnabled(false);
                    setNuclidesListDisplayMode(Config.PREFS_VALUE_NUCLIST_ORDER_I);
                    recyclerView.setAdapter(adapterPagedRad);

            });

        } else {
            setContentView(R.layout.nuclides_list);
        }

        txtDesc = findViewById(R.id.text);
        initButtonDrawer();

        final Activity mThis = this;
        findViewById(R.id.btnChartSelect).setOnClickListener(v->
             {
                progressStart(mThis);
                saveChartDecays(new LivechartPlot.Decay[0]);
                //saveChartNucidAtCentre("");
                saveChartNZatCentre(new float[0]);
                Intent i = new Intent(mThis,LivechartActivity.class);
                startActivity(i);


        });



    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState (Bundle savedInstanceState) {

    }

    @Override
    protected void onPause() {
        super.onPause();
       firstVisiblePosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
       lastVisiblePosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();


    }

    @Override
    public void onResume(){
        /* otherwise resuming after the back button would display less nuclides */
        recyclerView.getAdapter().notifyDataSetChanged();
        super.onResume();



        try {
            Thread.sleep(300);
        } catch(InterruptedException e) {
            // Process exception
        }
      ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(firstVisiblePosition,0);


    }
}

