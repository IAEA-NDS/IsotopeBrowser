package iaea.nds.nuclides;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class PreferenceActivity extends BaseActivity {

    public PreferenceActivity() {
        super();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.prefs);
        initButtonDrawer();
        TextView mTextAppInfo = (TextView) findViewById(R.id.app_info);
        String sAppinfo = getDisplayVersion();
        if(sAppinfo != null && sAppinfo.length() > 0){
            mTextAppInfo.setText(Html.fromHtml(sAppinfo));
        }
        getResources().getConfiguration().locale.getCountry();

        TextView nds = (TextView)findViewById(R.id.nds_tag);
        nds.setMovementMethod(LinkMovementMethod.getInstance());
        Formatter.stripUnderlines(nds);

    }

    @Override
    protected void onStart() {
        super.onStart();
        manageLanguage();
        manageNuclidesOrderBy();
        manageIntensityOrderBy();
        manageDecaychain();
        manageSpecificActivity();
    }

    private void manageNuclidesOrderBy(){

        RadioGroup radiogroup =  findViewById(R.id.radioGroupNucsOrderBy);
        radiogroup.clearCheck();

        RadioButton rbZN =  findViewById(R.id.radioZandN);
        rbZN.setOnClickListener(v -> {
                setNuclidesOrderByCode(Config.PREFS_VALUE_NUCLIDES_ORDER_ZN);

            });

        RadioButton rbNZ =  findViewById(R.id.radioNandZ);
        rbNZ.setOnClickListener(v -> {
                setNuclidesOrderByCode(Config.PREFS_VALUE_NUCLIDES_ORDER_NZ);
            });


        RadioButton rbHalf = findViewById(R.id.radioHalfLife);
        rbHalf.setOnClickListener(v -> {
               setNuclidesOrderByCode(Config.PREFS_VALUE_NUCLIDES_ORDER_HALF);
            });


        if(getNuclidesOrderByCode().equals(Config.PREFS_VALUE_NUCLIDES_ORDER_HALF)){
            radiogroup.check(R.id.radioHalfLife);
        } else if(getNuclidesOrderByCode().equals(Config.PREFS_VALUE_NUCLIDES_ORDER_NZ)){
            radiogroup.check(R.id.radioNandZ);
        } else {
            radiogroup.check(R.id.radioZandN);
        }

    }


    private void manageIntensityOrderBy(){

        RadioGroup radiogroup =  findViewById(R.id.radioGroupRadOrderBy);
        radiogroup.clearCheck();

        RadioButton rbEnergy =  findViewById(R.id.radioEnergy);
        rbEnergy.setOnClickListener( v->{
                setRadiationOrderBy(Config.PREFS_VALUE_RADIATION_ORDER_EN);
            });

        RadioButton rbInt =  findViewById(R.id.radioIntensity);
        rbInt.setOnClickListener( v-> {
               setRadiationOrderBy(Config.PREFS_VALUE_RADIATION_ORDER_INT);
            });

        if(getRadiationOrderBy().equals(Config.PREFS_VALUE_RADIATION_ORDER_EN)){
            radiogroup.check(R.id.radioEnergy);
        } else {
            radiogroup.check(R.id.radioIntensity);

        }

    }


    private void manageSpecificActivity(){

        RadioGroup radiogroup =  findViewById(R.id.radioGroupSpAct);
        radiogroup.clearCheck();

        RadioButton rbBq =  findViewById(R.id.radio_bqg);
        rbBq.setOnClickListener( v->{
            setSpecificActivityUnits(Config.PREFS_VALUE_BQG);
        });

        RadioButton rbCi =  findViewById(R.id.radio_cu);
        rbCi.setOnClickListener( v-> {
            setSpecificActivityUnits(Config.PREFS_VALUE_CIG);
        });

        if(getSpecificActivityUnits().equals(Config.PREFS_VALUE_CIG)){
            radiogroup.check(R.id.radio_cu);
        } else {
            radiogroup.check(R.id.radio_bqg);
        }

    }

    private void manageDecaychain(){

        final CheckBox chkAncestors = findViewById(R.id.chkAncestors);
        chkAncestors.setOnClickListener( v ->{
                setShowAncestors(
                        chkAncestors.isChecked() ? Config.PREFS_VALUE_YES : Config.PREFS_VALUE_NO
                );
            });

        chkAncestors.setChecked(getShowAncestors().equals(Config.PREFS_VALUE_YES));

    }

    private void manageLanguage(){


        RadioGroup radiogroup = findViewById(R.id.radioGroupLanguage);
        radiogroup.clearCheck();

        RadioButton rbEnglish = findViewById(R.id.radioEnglish);
        rbEnglish.setOnClickListener(v ->{
                languageClicked(Config.PREFS_VALUE_ENGLISH);
            });
        RadioButton rbItalian = findViewById(R.id.radioItalian);
        rbItalian.setOnClickListener(v -> {
            languageClicked(Config.PREFS_VALUE_ITALIAN);
        });
        RadioButton rbGerman = findViewById(R.id.radioGerman);
        rbGerman.setOnClickListener(v -> {
            languageClicked(Config.PREFS_VALUE_GERMAN);
        });
        RadioButton rbJapanese =  findViewById(R.id.radioJapanese);
        rbJapanese.setOnClickListener(v-> {
            languageClicked(Config.PREFS_VALUE_JAPANESE);
        });
        RadioButton rbSlovenian =  findViewById(R.id.radioSlovenian);
        rbSlovenian.setOnClickListener(v ->{
                languageClicked(Config.PREFS_VALUE_SLOVENIAN);
            });
        RadioButton rbFrench =  findViewById(R.id.radioFrench);
        rbFrench.setOnClickListener(v -> {
                languageClicked(Config.PREFS_VALUE_FRENCH);
            });
        RadioButton rbDutch =  findViewById(R.id.radioDutch);
        rbDutch.setOnClickListener(v -> {
               languageClicked(Config.PREFS_VALUE_DUTCH);
            });
        RadioButton rbArabic =  findViewById(R.id.radioArabic);
        rbArabic.setOnClickListener(v -> {
               languageClicked(Config.PREFS_VALUE_ARABIC);
            });
        RadioButton rbChinese =  findViewById(R.id.radioChinese);
        rbChinese.setOnClickListener(v -> {
              languageClicked(Config.PREFS_VALUE_CHINESE);
            });

        rbChinese.setOnClickListener(v -> {
            languageClicked(Config.PREFS_VALUE_CHINESE);
        });
        RadioButton rbChineseTRad =  findViewById(R.id.radioChineseTraditional);
        rbChineseTRad.setOnClickListener(v ->{
               languageClicked(Config.PREFS_VALUE_CHINESE_TRADITIONAL);
            });
        RadioButton rbRussian = findViewById(R.id.radioRussian);
        rbRussian.setOnClickListener(v ->  {
                languageClicked(Config.PREFS_VALUE_RUSSIAN);
            });
        RadioButton rbSpanish =  findViewById(R.id.radioSpanish);
        rbSpanish.setOnClickListener(v -> {
                languageClicked(Config.PREFS_VALUE_SPANISH);
            });


        String lan = getResources().getConfiguration().locale.getLanguage();;
        if(lan.equals(Config.PREFS_VALUE_ENGLISH)){
            radiogroup.check(R.id.radioEnglish);
        } else if(lan.equals(Config.PREFS_VALUE_GERMAN)){
            radiogroup.check(R.id.radioGerman);
        } else if(lan.equals(Config.PREFS_VALUE_ITALIAN)){
            radiogroup.check(R.id.radioItalian);
        } else if(lan.equals(Config.PREFS_VALUE_JAPANESE)){
            radiogroup.check(R.id.radioJapanese);
        } else if(lan.equals(Config.PREFS_VALUE_SLOVENIAN)){
            radiogroup.check(R.id.radioSlovenian);
        } else if(lan.equals(Config.PREFS_VALUE_FRENCH)){
            radiogroup.check(R.id.radioFrench);

        } else if(lan.equals(Config.PREFS_VALUE_DUTCH)){
            radiogroup.check(R.id.radioDutch);
        } else if(lan.equals(Config.PREFS_VALUE_ARABIC)){
            radiogroup.check(R.id.radioArabic);

        } else if(lan.equals(Config.PREFS_VALUE_CHINESE)){
            radiogroup.check(R.id.radioChinese);

        } else if(lan.equals(Config.PREFS_VALUE_CHINESE_TRADITIONAL)){
            radiogroup.check(R.id.radioChineseTraditional);

        } else if(lan.equals(Config.PREFS_VALUE_RUSSIAN)){
            radiogroup.check(R.id.radioRussian);

        } else if(lan.equals(Config.PREFS_VALUE_SPANISH)){
            radiogroup.check(R.id.radioSpanish);

        }


        if(isRightAligned()){

            ((LinearLayout) findViewById(R.id.layout)).setGravity(Gravity.RIGHT);
            ((LinearLayout) findViewById(R.id.layout_languages)).setGravity(Gravity.RIGHT);


            ((RadioGroup)findViewById(R.id.radioGroupLanguage)).setGravity(Gravity.RIGHT);
            ((RadioButton)findViewById(R.id.radioEnglish)).setGravity(Gravity.RIGHT);

            int[] views =  new int[]{R.id.radioEnglish,R.id.radioItalian,R.id.radioJapanese,R.id.radioSlovenian,R.id.radioFrench,R.id.radioDutch,R.id.radioArabic,
                    R.id.radioChinese};
            for (int i = 0 ; i < views.length ; i++){
                ((RadioButton)findViewById(views[i])).setGravity(Gravity.RIGHT);
            }

            ((CheckBox)findViewById(R.id.chkAncestors)).setGravity(Gravity.RIGHT);

        } else {
            ((RadioButton)findViewById(R.id.radioArabic)).setGravity(Gravity.LEFT);
        }

    }

    /**
     * to change on the fly the language
     * @param lang
     */
    private void languageClicked(String lang){
        setDrawerAlpha((float)0.2);
        changeLanguage(lang);
        myRestart();

    }


}
