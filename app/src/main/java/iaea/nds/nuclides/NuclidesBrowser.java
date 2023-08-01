package iaea.nds.nuclides;

import iaea.nds.mendel.PeriodicTableActivity;
import iaea.nds.nuclides.db.SQLBuilder;
import iaea.nds.nuclides.mvvm.IbRepository;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.RelativeSizeSpan;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * implements TextWatcher: every time an input field changes , check what to do and prepare the query
 */
public class NuclidesBrowser extends BaseActivity implements TextWatcher, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener, Html.ImageGetter {

    /*
    * when the periodic table is called, the selected element is returned with startActivityForResult
    * */
    public static final int INTENT_REQUEST_ELEMENT = 0;

    /**
     * whether the advanced panel is active
     */
    private boolean advancedActive = false;

    /**
     * reference to be passed to inner classes or lambda functions
     */
    NuclidesBrowser mThis;

    private TextView mTextView;

    private Button mBtnSearch;
    private Button mBtnAdvSearch;
    private LinearLayout layoutAdvancedSrc;
    private EditText mTextNucid;
    private EditText mTextN;
    private EditText mTextA;
    private EditText mTextJp;

    private CheckBox chbxStable;

    private Spinner spinHLunitLow;
    private Spinner spinHLunitHigh;
    private EditText txtHLLow;
    private EditText txtHLHigh;

    private Spinner spinDecayModes;
    private EditText mTextDecPercLow;
    private EditText mTextDecPercHigh;

    private Spinner spinRadiationTypes;
    private EditText mTextRadEnLow;
    private EditText mTextRadEnHigh;

    private EditText mTextRadIntLow;
    private EditText mTextRadIntHigh;
    private CheckBox chbxDecChain;
    private LinearLayout layoutDecCahin;

    private String tablesToQuery = "";
    private String whereForQuery = "";
    private String queryDesc = "";
    private String warningMsg = "";
    private String radtypeForQuery = "";
    private String radenergyForQuery = "";


    @Override
    public Drawable getDrawable(String arg0) {
        // TODO Auto-generated method stub
        int id = 0;

        if(arg0.equals("ib.png")){
            id = R.drawable.ib;
        }

        LevelListDrawable d = new LevelListDrawable();
        Drawable empty = getResources().getDrawable(id);
        d.addLevel(0, 0, empty);
        d.setBounds(0, 0, 80, 80);

        return d;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.nuclides_browser);
        super.setupUI(findViewById(R.id.layMain)); //hiding of the keyboard
        super.initButtonDrawer();

        mThis = this;


        TextView mTextView_html = findViewById(R.id.news);
        mTextView_html.setMovementMethod(LinkMovementMethod.getInstance());
        String hl =  getResources().getString(R.string.news);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mTextView_html.setText(Html.fromHtml(hl, Html.FROM_HTML_MODE_COMPACT, this, null ));
        } else {
            mTextView_html.setText(Html.fromHtml(hl, this, null));
        }

        /*for html*/
        mTextView = findViewById(R.id.text);
        mTextView.setMovementMethod(LinkMovementMethod.getInstance());
        hl =  getResources().getString(R.string.help);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mTextView.setText(Html.fromHtml(hl, Html.FROM_HTML_MODE_COMPACT,this, null ));
        } else {
            mTextView.setText(Html.fromHtml(hl));
        }
        /** previous formatting/
        //Formatter.setHelpText(mTextView, getText(R.string.help) + "\n" + getDisplayVersion(), isRightAligned());

        /*splits the titles two lines*/
        TextView mTextHello = (TextView) findViewById(R.id.helloTxt);
        String hello = getString(R.string.app_name) + '\n' + Formatter.spanNuclideLabelColor + getString(R.string.iaeands) + Formatter.spanNuclideLabelColor;
        mTextHello.setText(Formatter.setSpanBetweenTokens(hello, Formatter.spanNuclideLabelColor,
                new CharacterStyle[]{new RelativeSizeSpan(0.7f)} //new ForegroundColorSpan(getResources().getInteger(R.color.nuclide_labels))
        ));

        chbxDecChain = findViewById(R.id.chkDecayChain);
        layoutDecCahin = findViewById(R.id.layCbDecChain) ;
        mTextNucid = findViewById(R.id.txtNucid);
        mTextNucid.setOnKeyListener((v,keyCode,event)-> {
                // keyboard enter with only nucid active: start
                if ( !advancedActive && (event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    showResults();
                    return true;
                }
                //else set the search in the decay chain
                chbxDecayChainEnable(spinRadiationTypes.getSelectedItemPosition());
                return false;
        });


        mTextNucid.addTextChangedListener(new TextWatcher() {
            // set the search in the decay chain
            public void afterTextChanged(Editable s) {
                chbxDecayChainEnable(spinRadiationTypes.getSelectedItemPosition());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) { }}
            );

        mTextN = findViewById(R.id.txtN);
        mTextA = findViewById(R.id.txtA);
        mTextJp = findViewById(R.id.txtJp);

        findViewById(R.id.btnChart).setOnClickListener(v -> {
                // clear the decays in the chart
                saveChartDecays(new LivechartPlot.Decay[0]);
                Intent i = new Intent(mThis,LivechartActivity.class);
                i.putExtra("FILTER", isQueryActive());
                startActivity(i);

        });

        Button mBtnElements = findViewById(R.id.btnElements);
        mBtnElements.setOnClickListener(v -> {
                Intent i = new Intent(mThis, PeriodicTableActivity.class);
                startActivityForResult(i, INTENT_REQUEST_ELEMENT);
        });

        mBtnElements.setFocusableInTouchMode(true);
        mBtnElements.setFocusable(true);
        mBtnElements.requestFocus();

        mBtnSearch = findViewById(R.id.btnGo);

        mBtnSearch.setOnClickListener(v -> {
                ProgressDialog progress = ProgressDialog.show(mThis, getString(R.string.load_data_title),
                        getString(R.string.lbl_message_wait), true);
                showResults();
                progress.dismiss();
        });

        layoutAdvancedSrc = findViewById(R.id.layAdvSearch);
        mBtnAdvSearch =  findViewById(R.id.btnAdvSearch);
        mBtnAdvSearch.setOnClickListener(v ->{ hideShowAdvanced();});

        findViewById(R.id.buttonClear).setOnClickListener( v-> reset());

        spinHLunitLow = findViewById(R.id.spinHLunitLow);
        fillSpin(R.string.half_life_unit_prompt, spinHLunitLow, Formatter.getHalfLifeUnits(), false, false);
        spinHLunitLow.setSelection(Formatter.defHalfLifeUnitLow);

        spinHLunitHigh = findViewById(R.id.spinHLunitHigh);
        fillSpin(R.string.half_life_unit_prompt, spinHLunitHigh, Formatter.getHalfLifeUnits(), false, false);
        spinHLunitHigh.setSelection(Formatter.defHalfLifeUnitHigh);

        txtHLLow =  findViewById(R.id.txtHLLow);
        txtHLHigh = findViewById(R.id.txtHLHigh);

        chbxStable = findViewById(R.id.chkStable);
        chbxStable.setChecked(false);

        spinDecayModes = findViewById(R.id.spindDecayModes);
        fillSpin(R.string.decay_prompt, spinDecayModes, Formatter.getDecayModes(), true, false);
        mTextDecPercLow = findViewById(R.id.txtDecPercLow);
        mTextDecPercHigh =  findViewById(R.id.txtDecPercHigh);

        spinRadiationTypes = findViewById(R.id.spinRadTypes);
        fillSpin(R.string.Radiation_prompt, spinRadiationTypes, Formatter.getRadiations(), true, false);
        mTextRadEnLow =  findViewById(R.id.txtRadEnLow);
        mTextRadEnHigh = findViewById(R.id.txtRadEnHigh);

        mTextRadIntLow =  findViewById(R.id.txtRadIntLow);
        mTextRadIntHigh = findViewById(R.id.txtRadIntHigh);

        mTextNucid.addTextChangedListener(this);
        mTextN.addTextChangedListener(this);
        mTextA.addTextChangedListener(this);
        mTextJp.addTextChangedListener(this);
        chbxStable.setOnCheckedChangeListener(this);
        txtHLLow.addTextChangedListener(this);
        txtHLHigh.addTextChangedListener(this);
        spinDecayModes.setOnItemSelectedListener(this);;
        mTextDecPercLow.addTextChangedListener(this);
        mTextDecPercHigh.addTextChangedListener(this);
        spinRadiationTypes.setOnItemSelectedListener(this);
        mTextRadEnLow.addTextChangedListener(this);
        mTextRadEnHigh.addTextChangedListener(this);

        /* when closing the dialog check if the db needs update, if so call the charts that after the loading closes back.
        * it is the only way I found to show properly the dialogues*/
        showWhatsNew();


    }

    private boolean load_database(){

        if( Config.db_needs_update(this)) {

            /*to trigger the db update*/
            IbRepository repository = new IbRepository(mThis.getApplication());
            repository.compileStatement("select null from nuclides_metadata");

            return true;

        }

        return false;




    }

    /**
     * checkbox for the search in the decay chain of the input nucid
     * @param radTypeIdxSelected: int idx of the radiation type
     */
    private void chbxDecayChainEnable(int radTypeIdxSelected){

        if(mTextNucid.getText().length() > 0 ){
            layoutDecCahin.setVisibility(View.VISIBLE);

        } else {
            layoutDecCahin.setVisibility(View.GONE);

        }

        boolean bb = mTextNucid.getText().length() > 0 && radTypeIdxSelected > 0;

        chbxDecChain.setClickable(bb);
        chbxDecChain.setTextColor( (bb ? getResources().getColor(R.color.text_foreground ): getResources().getColor(R.color.spin_background)));
        chbxDecChain.setChecked(false);
    }

    /**
     * get ready in case the go button is pressed
     * called when an input field has changed
     */
    private void setGoButtonEnabled(){

        buildQuery();
        boolean enable = (whereForQuery.trim().length() > 0);
        mBtnSearch.setTextColor( getResources().getColor((enable ? R.color.button_pressed : R.color.clickable)));
        resetQueryParts();

    }

    private void resetQueryParts(){
        tablesToQuery = "";
        whereForQuery = "";
        queryDesc = "";
        warningMsg = "";
        radenergyForQuery = "";
        radtypeForQuery = "";
        setIdxRadTypeSelected(-1);
        setNucidForDecayChain("");
    }
    /**
     * When the app is opened check if there is a message to display
     * Nothing displayed if in the properties there is the flag for the version specified in R.string.whatsnew_versioncode
     *
     */
    private void showWhatsNew(){
        int lastversion = -1;
        try {
            lastversion = Integer.parseInt(getString(R.string.whatsnew_versioncode));
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }


        if(getWhatIsNewRead(lastversion)){
            return;
        }

        String msg  = getString(R.string.whatsnew);
        String btnlbl = getResources().getString(R.string.read);
        if(Config.db_needs_update(this)){
            msg += getResources().getString(R.string.warning_load_db);
            btnlbl = getResources().getString(R.string.load_db);

        }
        if(msg.length() == 0){
            return;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.new_in_ib));
        alert.setMessage(msg);
        alert.setInverseBackgroundForced(true);
        alert.setIcon(R.drawable.nuclide);
        alert.setCancelable(false);

        final int mylast = lastversion;

        alert.setPositiveButton(btnlbl, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mThis.setWhatIsNewRead(mylast, "Y");


                dialog.dismiss();

                if(Config.db_needs_update(mThis)) {
                    /*IbDatabase.loadDbFromSQL takes care of the progress*/
                    progressStartLoadDb(mThis);

                    Runnable runnable = new Runnable() {
                        public void run() {
                            load_database();
                        }
                    };
                    Thread thread = new Thread(runnable);
                    thread.start();
                }



            }
        });

        alert.show();


    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tablesToQuery = getTablesToQueryLast();
        whereForQuery = getWhereForQueryLast();
        queryDesc = getQueryDescLast();

    }

    /**
     * form the pieces contruct where, tables, description
     * @param qryPieces
     * @param qryDescPieces
     */
    private void buildQueryJoinPieces(Vector<String> qryPieces, Vector<String> qryDescPieces  ){
        String[] tk = new String[qryPieces.size()];
        qryPieces.copyInto(tk);
        for (int j = 0; j < tk.length; j++) {
            whereForQuery += " and " + tk[j];
        }

        String[] tkDesc = new String[qryDescPieces.size()];
        qryDescPieces.copyInto(tkDesc);
        for (int j = 0; j < tkDesc.length; j++) {
            queryDesc += " and " + tkDesc[j];
        }

        whereForQuery = (whereForQuery.length() > 0 ? whereForQuery.substring(4) : whereForQuery) ;
        tablesToQuery = (tablesToQuery.length() > 0 ? tablesToQuery.substring(1) : tablesToQuery) ;
        queryDesc = (queryDesc.length() > 0 ? queryDesc.substring(5) + " ": queryDesc) ;


    }

    /**
     *
     */
    private void buildQuery(){
        resetQueryParts();
        // the parts of the where
        Vector<String> qryPieces = new Vector<String>();
        // the parts of the where desciption
        Vector<String> qryDescPieces = new Vector<String>();

        buildQueryGatherPieces(qryPieces, qryDescPieces);
        buildQueryJoinPieces(qryPieces, qryDescPieces);

        if(whereForQuery.trim().length() == 0 && warningMsg.length() ==0){
            warningMsg = getResources().getString(R.string.warn_no_input);
        }


        queryDesc += (queryDesc.equals("") ? "" : getResources().getString(R.string.orderby) + getNuclidesOrderByDescription());


    }

    /**
     * query pieces from the nuclide input field
     * @param qryPieces
     * @param qryDescPieces
     * @return boolean : whether to process the expert fields
     */
    private boolean queryPiecesStandard(Vector<String> qryPieces, Vector<String> qryDescPieces ){

        String dmOrig = mTextNucid.getText().toString().trim();
        String dm = dmOrig.toUpperCase();

        if(dm.length() > 0){
            addTableToQuery(SQLBuilder.NUCLIDES_TABLE);
            // the full list of nuclides
            if(dm.equals("*")){
                qryPieces.add(" 1=1 " );
                qryDescPieces.add( " All nuclides" );
                return false;
            }

            // only zeta
            if(isInteger(dm)){
                qryPieces.add( SQLBuilder.NUCLIDES_TABLE + "." + SQLBuilder.z + " = " + dm + "" );
                qryDescPieces.add( " Z =  " + dmOrig);

                // only element symbol
            } else if(isAlpha(dm)){
                qryPieces.add( SQLBuilder.NUCLIDES_TABLE + "." + SQLBuilder.z + " = " +
                        Formatter.getElementZeta(dm) + "" );
                String elem = dmOrig.substring(0,1).toUpperCase() + dmOrig.substring(1, dmOrig.length()).toLowerCase();
                qryDescPieces.add( " " + getResources().getString(R.string.elements_prompt) + " " + elem);

            } else {
                // nuclide id
                // ** this will be removed if the search is decay chain is active. See queryadvanced
                qryPieces.add( " (z+n) = " + mass_sym_from_nucid(dm)[0] + " and upper(" + SQLBuilder.NUCLIDES_TABLE + "." + SQLBuilder.symbol + ") = '" + mass_sym_from_nucid(dm)[1].toUpperCase() + "'" );
                qryDescPieces.add( getResources().getString(R.string.nuclide) + dmOrig);
                /* see queryPiecesAdvanced - decay radiation for the use of this*/
                setNucidForDecayChain( parseNucid(dm));
            }
        }

        return true;
    }

    /**
     * .3 is converted to 0.3
     * @param s
     * @return
     */
    private String decimal_dot_start(String s){
        if(s.startsWith(".") || s.startsWith(",")){
            s = "0" + s;
        }
        return s;
    }
    /**
     * query pieces from the expert panel
     * @param qryPieces
     * @param qryDescPieces
     * @return void
     */
    private void queryPiecesAdvanced(Vector<String> qryPieces, Vector<String> qryDescPieces ){
        String hl = getResources().getString(R.string.half_life);

        int i = -1;
        // NUCLIDE N
        String dm = mTextN.getText().toString();
        if(dm.length() > 0){
            qryPieces.add( SQLBuilder.NUCLIDES_TABLE + "." + SQLBuilder.n + " = " + dm  );
            addTableToQuery(SQLBuilder.NUCLIDES_TABLE);
            qryDescPieces.add( " N = " + dm);
        }
        // NUCLIDE A
        dm = mTextA.getText().toString();
        if(dm.length() > 0){
            qryPieces.add( "(" + SQLBuilder.NUCLIDES_TABLE + "." + SQLBuilder.z + " + " + SQLBuilder.NUCLIDES_TABLE + "." + SQLBuilder.n + ") = " + dm  );
            addTableToQuery(SQLBuilder.NUCLIDES_TABLE);
            qryDescPieces.add( " A = " + dm);
        }

        // NUCLIDE JP
        dm = mTextJp.getText().toString();
        if(dm.length() > 0){
            String jpCol = (SQLBuilder.NUCLIDES_TABLE + "." + SQLBuilder.jp );
            String jpWhere = "(" + jpCol  + " like '%" + dm + "%'" ;

            // if you search for '1', remove 11, etc..
            if(!dm.equals("+") && !dm.equals("-") ){
                for (int j = 0; j < 10; j++) {
                    jpWhere += " and " + jpCol  + " not like '%" + dm + j + "%'" ;
                    jpWhere += " and " + jpCol  + " not like '%" + j + dm + "%'" ;
                }
            }

            jpWhere += " and " + jpCol  + " not like '%" + dm + "/%'" ;
            jpWhere += " and " + jpCol  + " not like '%/" + dm + "%' )" ;
            qryPieces.add( jpWhere);
            addTableToQuery(SQLBuilder.NUCLIDES_TABLE);
            qryDescPieces.add( getResources().getString(R.string.jplike) + dm);
        }

        // HALF_LIFE
        String hlLow = decimal_dot_start(txtHLLow.getText().toString().trim() );
        String hlHigh = decimal_dot_start(txtHLHigh.getText().toString().trim()) ;

        String unitLow = spinHLunitLow.getSelectedItem().toString().trim();
        String unitHigh = spinHLunitHigh.getSelectedItem().toString().trim();

        if(hlLow.length() > 0 || hlHigh.length() > 0){

            double hlLowConv = (Formatter.halfLifeUnitConversion[spinHLunitLow.getSelectedItemPosition()]);
            double hlHighConv = (Formatter.halfLifeUnitConversion[spinHLunitHigh.getSelectedItemPosition()]);

            if(hlLow.length() > 0 && hlHigh.length() > 0){
                double low = Double.parseDouble(hlLow) * hlLowConv;
                double high = Double.parseDouble(hlHigh) * hlHighConv;

                qryPieces.add( SQLBuilder.half_life_sec + " between " + low + " and " + high );
                addTableToQuery(SQLBuilder.NUCLIDES_TABLE);
                qryDescPieces.add( hl + getResources().getString(R.string.between) +
                        hlLow + " " +
                        unitLow + getResources().getString(R.string.and) +
                        hlHigh + " " +
                        unitHigh );

            } else     if(hlLow.length() > 0 ){
                double low = Double.parseDouble(hlLow) * hlLowConv;


                qryPieces.add( SQLBuilder.half_life_sec + " > " + low  );
                addTableToQuery(SQLBuilder.NUCLIDES_TABLE);
                qryDescPieces.add(  hl +" > " +
                        hlLow + " " +
                        unitLow );

            } else     if(hlHigh.length() > 0 ){
                double high = Double.parseDouble(hlHigh) * hlHighConv;


                qryPieces.add( SQLBuilder.half_life_sec + " < " + high  );
                addTableToQuery(SQLBuilder.NUCLIDES_TABLE);
                qryDescPieces.add( hl + " < " +
                        hlHigh + " " +
                        unitHigh );
            }

        }

        // STABLE

        if(chbxStable.isChecked()){
            qryPieces.add( (SQLBuilder.half_life) + " like '%stable%'" );
            addTableToQuery(SQLBuilder.NUCLIDES_TABLE);
            qryDescPieces.add( getResources().getString(R.string.stable_prompt));
        }

        // DECAYS **** FK on l_seqno removed since nucid is unique (it has m1, m2, etc) ***
        i =  spinDecayModes.getSelectedItemPosition();
        String decPerclow = decimal_dot_start(mTextDecPercLow.getText().toString().trim()) ;
        String decPercup = decimal_dot_start(mTextDecPercHigh.getText().toString().trim());
        if(i>0){
            qryPieces.add( SQLBuilder.L_DECAYS_TABLE + "." + SQLBuilder.dec_dec_type
                    + " in (" + Formatter.decayModesCode[i-1] + " )" );
            addTableToQuery(SQLBuilder.L_DECAYS_TABLE);
            qryDescPieces.add( " Decay Mode " + spinDecayModes.getSelectedItem().toString().trim());
            // FK nuclides - l_decays
            qryPieces.add( SQLBuilder.NUCLIDES_TABLE + "." + SQLBuilder.nucid + " = " + SQLBuilder.L_DECAYS_TABLE + "." + SQLBuilder.nucid );
            // 	qryPieces.add( SQLBuilder.NUCLIDES_TABLE + "." + SQLBuilder.l_seqno + " = " + SQLBuilder.L_DECAYS_TABLE + "." + SQLBuilder.dec_l_seqno );
            addTableToQuery(SQLBuilder.NUCLIDES_TABLE);


            if(decPerclow.length() > 0 || decPercup.length() > 0){
                if(decPerclow.length() == 0) decPerclow = "0";
                if(decPercup.length() == 0) decPercup = "100";

                qryPieces.add( SQLBuilder.dec_perc_num + " between " + decPerclow + " and " +  decPercup);
                qryDescPieces.add( getResources().getString(R.string.decay) +" % " + getResources().getString(R.string.between)  + decPerclow + getResources().getString(R.string.and) +  decPercup);
            }
        }

        // RADIATION  **** FK on l_seqno not needed since nucid is unique (it has m1, m2, etc) ***
        i =  spinRadiationTypes.getSelectedItemPosition();
        String radEnlow = decimal_dot_start(mTextRadEnLow.getText().toString() );
        String radEnup = decimal_dot_start(mTextRadEnHigh.getText().toString());

        String radIntlow = decimal_dot_start(mTextRadIntLow.getText().toString() );
        String radIntup = decimal_dot_start(mTextRadIntHigh.getText().toString());

        if(i>0){
            radtypeForQuery = SQLBuilder.rad_type_a + " in ('" + Formatter.radiationsCode[i-1] + "' )";
            qryPieces.add(radtypeForQuery );
            addTableToQuery(SQLBuilder.RADIATIONS_TABLE);
            qryDescPieces.add( " " + getResources().getString(R.string.decay_rad) + " " + spinRadiationTypes.getSelectedItem().toString().trim());

            // FK nuclides - radiations
            qryPieces.add( SQLBuilder.NUCLIDES_TABLE + "." + SQLBuilder.nucid + " = " + SQLBuilder.RADIATIONS_TABLE + "." + SQLBuilder.rad_parent_nucid );
            addTableToQuery(SQLBuilder.NUCLIDES_TABLE);


            if(radEnlow.length() > 0 && radEnup.length() > 0){
                radenergyForQuery =  SQLBuilder.rad_energy_num + " between " + radEnlow + " and " +  radEnup;
                qryDescPieces.add(getResources().getString(R.string.radiation_energy_lbl) +" between " + radEnlow + " keV " + getResources().getString(R.string.and) +  radEnup + " keV");

            } else 	if(radEnlow.length() > 0){
                radenergyForQuery =  SQLBuilder.rad_energy_num + " > " + radEnlow ;
                qryDescPieces.add(getResources().getString(R.string.radiation_energy_lbl) + " > " + radEnlow + " keV ");

            } else 	if(radEnup.length() > 0){
                radenergyForQuery =  SQLBuilder.rad_energy_num + " < " + radEnup ;
                qryDescPieces.add(getResources().getString(R.string.radiation_energy_lbl) + " < " + radEnup + " keV ");

            }
            /*in case of annihilation, reject the energy input*/
            if(i == Formatter.radiationsAnnihiIdx){
                radenergyForQuery = "";
            }
            if(radenergyForQuery.length() > 0)
                qryPieces.add(radenergyForQuery);


            if(radIntlow.length() > 0 || radIntup.length() > 0){
                if(radIntlow.length() == 0) radIntlow = "0";
                if(radIntup.length() == 0) radIntup = "100";

                qryPieces.add( SQLBuilder.rad_intensity_num + " between " + radIntlow + " and " +  radIntup);
                qryDescPieces.add( " % " + getResources().getString(R.string.between)  + radIntlow + getResources().getString(R.string.and) +  radIntup);
            }

            setWhereForRadtype(radtypeForQuery);
            setWhereForEnergy(radenergyForQuery);
            setIdxRadTypeSelected(i-1);

            if(!chbxDecChain.isChecked()){
                setNucidForDecayChain("");
            } else{
                for(int j = 0; j < qryPieces.size(); j++){
                    String p = qryPieces.elementAt(j);
                    if (p.trim().startsWith("(z")){
                        qryPieces.remove(j);
                        qryPieces.add("upper(nuclides.nucid) like '%" + getNucidForDecayChain()  + "%' ");
                    }
                }

                qryDescPieces.add( " " + getResources().getString(R.string.decaychain_prompt) + " ");
            }
        } else {
            /* remove the nucid for the decay chain in case there is no radiation selection*/
            setNucidForDecayChain("");
        }

        warningMsg = "";
        // check for warnings
        if(spinRadiationTypes.getSelectedItemPosition() <= 0
                && ( radEnlow.length() > 0
                || radEnup.length() > 0 )
        ){
            warningMsg += getResources().getString(R.string.choose_rad);
        }
        if(spinDecayModes.getSelectedItemPosition() <= 0
                && ( decPerclow.length() > 0
                || decPercup.length() > 0 )
        ){
            warningMsg += getResources().getString(R.string.choose_dec);
        }
    }

    /**
     * constructs the pieces of the sql-where by reading the input fields
     * @param qryPieces
     * @param qryDescPieces
     */
    private void buildQueryGatherPieces(Vector<String> qryPieces, Vector<String> qryDescPieces ){

        boolean bnext = queryPiecesStandard(qryPieces, qryDescPieces);

        if(advancedActive && bnext){
            queryPiecesAdvanced(qryPieces, qryDescPieces);
        }
    }


    private String addTableToQuery(String tbl){
        if(tablesToQuery.indexOf(tbl) == -1){
            tablesToQuery += "," + tbl;
        }
        return tablesToQuery;
    }


    private void fillSpin(int promptId, Spinner spin, String[] values, boolean allowNoChoice, boolean addCounter){

        List<String> list = new ArrayList<String>();

        if(allowNoChoice ){
            list.add(" "+getString(promptId));
        }

        for (int i = 0; i < values.length; i++) {
            list.add( (addCounter ? i + " - " : "   " ) + values[i] + "  ");
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_spinner_face_item,
                list);
        dataAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);

        spin.setAdapter(dataAdapter);
    }

    //************************

    /**
     *
     * Check if there are warnings, if not
     * Searches the database and displays results.
     */
    public void showResults() {

        buildQuery();
        setQueryParts(tablesToQuery,whereForQuery,queryDesc);

        if(warningMsg.length() > 0){
            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setTitle( getResources().getString(R.string.warning));
            ad.setMessage(warningMsg);
            ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            ad.show();
            return;
        }

        resetChartStatus();
        launchResultListActivity();

    }

    private int checkLayout(){

        Configuration conf = getResources().getConfiguration();
        int screenLayout = 1; // application default behavior
        try {
            Field field = conf.getClass().getDeclaredField("screenLayout");
            screenLayout = field.getInt(conf);
        } catch (Exception e) {
            // NoSuchFieldException or related stuff
        }

        int screenType = screenLayout & 15;

        if (screenType == 1) {
            System.out.println("SCREENLAYOUT_SIZE_SMALL");
        } else if (screenType == 2) {
            System.out.println("SCREENLAYOUT_SIZE_NORMAL");
        } else if (screenType == 3) {
            System.out.println("SCREENLAYOUT_SIZE_LARGE");
        } else if (screenType == 4) {
            System.out.println("SCREENLAYOUT_SIZE_XLARGE");
        } else { // undefined
            System.out.println("SCREENLAYOUT_SIZE_UNK");
        }

        return screenType;
    }

    /**
     * clears the fields and the query
     */
    private void reset(){

        mTextNucid.setText("");

        mTextN.setText("");
        mTextA.setText("");
        mTextJp.setText("");

        spinHLunitLow.setSelection(Formatter.defHalfLifeUnitLow);
        spinHLunitHigh.setSelection(Formatter.defHalfLifeUnitHigh);
        txtHLLow.setText("");
        txtHLHigh.setText("");
        chbxStable.setChecked(false);

        spinDecayModes.setSelection(0);
        mTextDecPercLow.setText("");
        mTextDecPercHigh.setText("");

        spinRadiationTypes.setSelection(0);
        mTextRadEnLow.setText("");
        mTextRadEnHigh.setText("");

        resetQueryParts();
        resetQuery();
        resetChartStatus();

        saveChartDecays(new LivechartPlot.Decay[0]);
    }

    private boolean isInteger(String s ){
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * from the input field gets mass and element
     * @param s nucid e.g. 135XE, 135 xe, 135-xe, etc...
     * @return String[]{135, XE}
     */
    private String[] mass_sym_from_nucid(String s){
        char[] c = s.replaceAll("-", "").replaceAll(" ", "").toUpperCase()
                .toCharArray();

        String num = "";
        String sym = "";

        String nm = "0123456789";

        for (int i = 0; i < c.length; i++) {
            if (nm.indexOf(c[i]) != -1) {
                num += c[i];
            } else {
                sym += c[i];
            }
        }

        return new String[]{num, sym};
    }

    /**
     * rom the input field gets 135XE
     * @param s nucid e.g. 135XE, 135 xe, 135-xe, etc...
     * @return String nucid e.g. 135XE
     */
    private String parseNucid(String s) {
        String[] dm = mass_sym_from_nucid(s);
        return dm[0] + dm[1];
    }
    /**
     * Whether a string has only a-zA-Z
     *
     * @param name
     * @return
     */
    private boolean isAlpha(String name) {
        return name.matches("[a-zA-Z]+");
    }

    /**
     * resturn from the periodic table. If the expert search is not active, calls immediately the search
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (INTENT_REQUEST_ELEMENT) : {
                if (resultCode == Activity.RESULT_OK) {
                    String newText = data.getStringExtra(PeriodicTableActivity.ELEMENT_CHOSEN);
                    mTextNucid.setText(newText);

                    if(!advancedActive){
                        showResults();
                    }
                }

                break;
            }
        }
    }

    public void hideShowAdvanced(){

        if(layoutAdvancedSrc.getVisibility() == View.GONE){
            layoutAdvancedSrc.setVisibility(View.VISIBLE);
            mBtnAdvSearch.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_expand_less_white_36dp),null,null,null);
            //left top right bottom  getContext().getResources().getDrawable( R.drawable.smiley );

        } else {
            layoutAdvancedSrc.setVisibility(View.GONE);
            mBtnAdvSearch.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_expand_more_white_36dp),null,null,null);

        }
        advancedActive = !advancedActive;
    }

    /**
     * every time an input field changes , check what to do and prepare the query
     * @param arg0
     */
    @Override
    public void afterTextChanged(Editable arg0) {
        setGoButtonEnabled();

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        setGoButtonEnabled();
        if(arg0 == spinRadiationTypes){
            chbxDecayChainEnable(arg2);
            if(arg2 == Formatter.radiationsAnnihiIdx){
                mTextRadEnLow.setText("511");
                mTextRadEnHigh.setText("511");
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setGoButtonEnabled();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged( Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }


}
