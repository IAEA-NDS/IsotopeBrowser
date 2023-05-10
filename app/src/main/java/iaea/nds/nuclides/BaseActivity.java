
package iaea.nds.nuclides;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import iaea.nds.mendel.PeriodicTableActivity;
import iaea.nds.nuclides.db.SQLBuilder;

import static iaea.nds.nuclides.Config.PREFS_FILE_NAME;
import static iaea.nds.nuclides.Config.PREFS_NAME_NUCLIST_ORDER;
import static iaea.nds.nuclides.Config.PREFS_VALUE_NONE;
import static iaea.nds.nuclides.Config.PREFS_VALUE_NUCLIST_ORDER_HL;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener ,  DrawerLayout.DrawerListener {

    DrawerLayout mDrawerLayout = null;
    NavigationView navigationView = null;

    private static ProgressDialog progress = null;

    String appversionName ;
    int appVersion ;
    String displayVersion ;

    /**
     * when changing language, activities need restarting
     */
    private static boolean restartLivechartActivity = false;
    private static boolean restartNuclidesActivity = false;
    private static boolean restartNuclideBrowser = false;
    private static boolean restartNuclidesListActivity = false;

    /**
     * values descriping the state of the chart, see LivechartPlot.getQttToSave
     */
    private static float[] chartSavedQuantities = new float[0];


   // private static String chartNucidAtCentre = "";
    private static float[] chartNZatCentre = new float[0];
    private static LivechartPlot.Decay[] chartSavedDecays = new LivechartPlot.Decay[0];

    public static boolean mendel_forced_rotation = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        Formatter.setResources(getResources());

        /*
         * redo the language settings  again just in case
         */
        String language = Config.getLanguageFromPrefs(this);
        Locale locale = new Locale(language, language.toUpperCase());
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        setPackageInfo();

    }

    /**
     * a query is active
     * @return
     */
    public boolean isQueryActive(){
        return !SQLBuilder.isQueryEmpty();
    }

    /**
     * layout and click listener of the button to open the drawer
     */
    protected void initButtonDrawer() {
        mDrawerLayout =  findViewById(R.id.drawer_layout);
        if (mDrawerLayout != null) {
            setDrawerAlpha((float) 1.0);

        }
        ImageButton imgBtnOpenDrawer = findViewById(R.id.imgbuttonopendrawer);
        navigationView = findViewById(R.id.nav_view);

        if (mDrawerLayout != null && imgBtnOpenDrawer != null && navigationView != null) {

            navigationView.setNavigationItemSelectedListener(this);
            imgBtnOpenDrawer.setOnClickListener(v -> {mDrawerLayout.openDrawer(Gravity.LEFT);});
            mDrawerLayout.addDrawerListener(this);
        }

    }

    /**
     * to diplay ancestors on the chart
     * @return
     */
    public String getShowAncestors() {
        return getSharedPreferences(PREFS_FILE_NAME, 0).getString(Config.PREFS_NAME_DECAYCHAIN_PARENTS, Config.PREFS_VALUE_YES);
    }

    public String getNuclidesOrderByCode() {
        return SQLBuilder.getOrderbyCode();
    }

    public boolean isFilterByRad(){
        return SQLBuilder.isFilterByRad();
    }

    public void setNuclidesOrderByCode(String nuclidesOrderBy) {

        SharedPreferences.Editor editor = getSharedPreferences(PREFS_FILE_NAME, 0).edit();
        editor.putString(Config.PREFS_NAME_NUCLIDES_ORDER, nuclidesOrderBy);
        editor.commit();
        SQLBuilder.setOrderbyCode(nuclidesOrderBy);
        restartNuclidesListActivity = true;

        }


    public String getRadiationOrderBy() {
        return getSharedPreferences(PREFS_FILE_NAME, 0).getString(Config.PREFS_NAME_RADIATION_ORDER, Config.PREFS_VALUE_RADIATION_ORDER_INT);
    }

    public String getSpecificActivityUnits() {
        return getSharedPreferences(PREFS_FILE_NAME, 0).getString(Config.PREFS_NAME_SPECIFIC_ACTIVITY, Config.PREFS_VALUE_BQG);
    }

    /**
     *checks in the shared preferences :
     * false if Config.PREFS_WHAT_IS_NEW_READ + version is null or no
     * @param version
     * @return boolean : whether to display an initial message
     */
    public boolean getWhatIsNewRead(int version) {
        if (getSharedPreferences(PREFS_FILE_NAME, 0)
                .getString(Config.PREFS_WHAT_IS_NEW_READ + version, Config.PREFS_VALUE_NO)
                .equals(Config.PREFS_VALUE_NO)) {
            return false;
        }
        return true;
    }

    public String getNuclidesOrderByDescription() {

        String order = getNuclidesOrderByCode();

        if (order.equals(Config.PREFS_VALUE_NUCLIDES_ORDER_HALF)) {
            return getResources().getString(R.string.half_life);//" half-life";
        } else if (order.equals(Config.PREFS_VALUE_NUCLIDES_ORDER_NZ)) {
            return " N, Z";
        }

        return " Z, N";
    }


    public void setRadiationOrderBy(String radiationOrderBy) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_FILE_NAME, 0).edit();
        editor.putString(Config.PREFS_NAME_RADIATION_ORDER, radiationOrderBy);
        editor.commit();
        restartNuclidesActivity = true;

    }

    public void setSpecificActivityUnits(String units) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_FILE_NAME, 0).edit();
        editor.putString(Config.PREFS_NAME_SPECIFIC_ACTIVITY, units);
        editor.commit();
        restartNuclidesActivity = true;

    }

    public void setShowAncestors(String show) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_FILE_NAME, 0).edit();
        editor.putString(Config.PREFS_NAME_DECAYCHAIN_PARENTS, show);
        editor.commit();

    }



    public void setWhatIsNewRead(int version, String YorN) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_FILE_NAME, 0).edit();
        editor.putString(Config.PREFS_WHAT_IS_NEW_READ + version, YorN);
        editor.commit();

    }


    protected void launchResultListActivity() {

        Intent nucListIntent = getIntentForResultListActivity();

        if (nucListIntent != null) {
            startActivity(nucListIntent);
        }
        return;
    }

    protected void setDrawerAlpha(float alpha) {
        if (mDrawerLayout != null && Build.VERSION.SDK_INT >= 11) {
            try {
                mDrawerLayout.setAlpha(alpha);
            } catch (Exception e) {

            }

        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        setDrawerAlpha((float) 0.2);

        if (id == R.id.nav_mainpage) {
            Intent intent = new Intent(this, NuclidesBrowser.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

            startActivity(intent);
        } else if (id == R.id.nav_resulttable) {

            launchResultListActivity();
        } else if (id == R.id.nav_chart) {
            progressStart(this);

            Intent intent = new Intent(this, LivechartActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_elements) {
            Intent intent = new Intent(this, PeriodicTableActivity.class);

            startActivity(intent);

        } else if (id == R.id.nav_prefs) {
            Intent intent = new Intent(this, PreferenceActivity.class);

            startActivity(intent);

        } else if (id == R.id.nav_feedback) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", getResources().getString(R.string.email_contact), null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.email_subject));
            //emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
            try {
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);

        return true;


    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {


        if (this instanceof NuclidesBrowser) {
            navigationView.getMenu().getItem(0).setEnabled(false);
            navigationView.getMenu().getItem(0).setChecked(true);
        } else {
            navigationView.getMenu().getItem(0).setEnabled(true);
            navigationView.getMenu().getItem(0).setChecked(false);
        }

        if (this instanceof NuclidesListActivity) {
            navigationView.getMenu().getItem(1).setEnabled(false);
            navigationView.getMenu().getItem(1).setChecked(true);
        } else {
            if (SQLBuilder.isQueryEmpty()) {
                navigationView.getMenu().getItem(1).setEnabled(false);
            } else {
                navigationView.getMenu().getItem(1).setEnabled(true);
            }
            navigationView.getMenu().getItem(1).setChecked(false);
        }

        if (this instanceof PreferenceActivity) {
            navigationView.getMenu().getItem(4).setEnabled(false);
            navigationView.getMenu().getItem(4).setChecked(true);
        } else {
            navigationView.getMenu().getItem(4).setEnabled(true);
            navigationView.getMenu().getItem(4).setChecked(false);
        }


    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    /**
     * restart procedure to reload languages, called on onResume
     */
    protected void myRestart() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            recreate();
        } else {
            startActivity(getIntent());
            finish();
        }
    }

    /**
     * check if restaring is needed after changing the language
     * @param s class name of the activity to check
     * @return
     */
    public boolean needsRestart(String s){

        if(s.equals(LivechartActivity.class.getName()) && restartLivechartActivity){
            SQLBuilder.resetQuery();
            resetChartStatus();
            restartLivechartActivity = false;
            return true;
        }  else 	if(s.equals(NuclidesActivity.class.getName()) && restartNuclidesActivity){
            restartNuclidesActivity = false;
            return true;
        } else 	if(s.equals(NuclidesBrowser.class.getName()) && restartNuclideBrowser){
            restartNuclideBrowser = false;
            return true;
        } else 	if(s.equals(NuclidesListActivity.class.getName()) && restartNuclidesListActivity){
            restartNuclidesListActivity = false;
            return true;
        }
        return false;

    }

    public void resetChartStatus(){
        chartSavedQuantities = new float[0];
    }

    /**
     * after changing language, write the prefs and flag each activity for restarting
     * @param lang: String language code
     */
    public void changeLanguage(String lang){
        setLanguageOnPrefAndSystem(lang);
        restartLivechartActivity = true;
        restartNuclideBrowser = true;
        restartNuclidesActivity = true;
        restartNuclidesListActivity = true;

    }

    /**
     * write the lang in the pref and in the context of the already running system
     * @param language: String language code
     */
    private void setLanguageOnPrefAndSystem(String language){
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_FILE_NAME, 0).edit();
        editor.putString(Config.PREFS_NAME_LANGUAGE, language);
        editor.commit();
        setLanguageOnSystem(language.toLowerCase());

    }

    // setLocale("de", "DE");

    /**
     * write the lang in the context of the already running system
     * @param language: String language code
     */
    private void setLanguageOnSystem(String language) {

        String country = language.toUpperCase();

        if(getLanguageFormSystem().equals(language))
            return;

        Locale locale = new Locale(language, country);
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,  getBaseContext().getResources().getDisplayMetrics());

    }

    @Override
    public void onResume() {

        super.onResume();

        setDrawerAlpha((float) 1.0);
        if (needsRestart(this.getClass().getName())) {
            myRestart();

        }
    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager == null || activity == null || activity.getCurrentFocus() == null){
            return;
        }
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    /*
    * to manage the hiding of the keyboard
    * */
    public void setupUI(View view) {

        if(view == null) return ;

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(BaseActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    /**
     * the language in use, if not set in the prefs, thenfrom the context
     * @return
     */
    public String getLanguage(){
        if(getLanguageFromPrefs() != PREFS_VALUE_NONE){
            return getLanguageFromPrefs();
        } else {
            return (getLanguageFormSystem());
        }
    }

    private String getLanguageFromPrefs(){
        return getSharedPreferences(PREFS_FILE_NAME, 0).getString(Config.PREFS_NAME_LANGUAGE, PREFS_VALUE_NONE);
    }

    private String getLanguageFormSystem(){
        return getResources().getConfiguration().locale.getLanguage();
    }

    /**
     * whether the text is right aligned
     * @return
     */
    public boolean isRightAligned(){
        if(getLanguage().toLowerCase().equals( Config.PREFS_VALUE_ARABIC)){
            return true;
        }
        return false;
    }

    public void setNuclidesListDisplayMode(String order_type){
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_FILE_NAME, 0).edit();
        editor.putString(PREFS_NAME_NUCLIST_ORDER, order_type);
        editor.commit();
    }

    public boolean isNuclidesListDisplayModeHL(){
        return getSharedPreferences(PREFS_FILE_NAME, 0).getString(PREFS_NAME_NUCLIST_ORDER, PREFS_VALUE_NONE).equals(PREFS_VALUE_NUCLIST_ORDER_HL);
    }


    public static void progressMessage(String message){
        if(progress != null && progress.isShowing()){
            progress.setMessage(message);
        }
    }


    public static void progressStart(Context context){
        if (progress != null && progress.isShowing() ) return;
        progress = ProgressDialog.show(context, context.getString(R.string.load_data_title),
                context.getString(R.string.lbl_message_wait), true);

    }

    public static boolean progress_is_showing(){
        if (progress != null && progress.isShowing() ) return true;
        return false;
    }

    public static void progressStartLoadDb(Context context){
        if (progress != null && progress.isShowing() ) return;
        progress = ProgressDialog.show(context, context.getString(R.string.load_db_title),
                context.getString(R.string.lbl_message_wait), true);
        progress.show();

    }

    public static void progressDismiss(){
        if(progress != null && progress.isShowing()){

            try {
                progress.dismiss();
            } catch (final IllegalArgumentException e) {
                // Do nothing.
            } catch (final Exception e) {
                // Do nothing.
            } finally {
                progress = null;
            }
            //progress.dismiss();
        }
    }

    public String getDisplayVersion(){
        return displayVersion;
    }

    private void setPackageInfo(){
        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appversionName = pInfo.versionName;
            if (android.os.Build.VERSION.SDK_INT >= 28) {
                appVersion = (int) pInfo.getLongVersionCode();
            } else {
                appVersion = pInfo.versionCode;
            }

            displayVersion = "App Code " + appVersion + " Name " + appversionName;
        } catch (
                PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
    }


    /**
     * for the List, gets the query composed in the main activity
     * @return
     */
    public String queryForList(){
        // if in the meanwhile the pref have changed
        String orderby = Config.getNuclidesOrderBy(this);
        if(SQLBuilder.getWhereForEnergy().length() != 0){
            orderby = Config.PREFS_VALUE_RADIATION_ORDER_INT;
        }

        SQLBuilder.setOrderbyCode(orderby);


        return SQLBuilder.buildQueryForList(!isNuclidesListDisplayModeHL());
    }

    public String tables(){
        return SQLBuilder.getTablesToQueryLast();
    }

    public String where(){
        return SQLBuilder.getWhereForQueryLast();
    }

    public String queryDesc(){
        return SQLBuilder.getQueryDescLast();
    }

    public Intent getIntentForResultListActivity(){
        Intent nucListIntent = null;
        if(isQueryActive()) {
            nucListIntent = new Intent(this, NuclidesListActivity.class);

        }
        return nucListIntent;
    }

    public static float[] getChartSavedQuantities(){
        return chartSavedQuantities;
    }

    public static void saveChartQuantities(float[] chartQuantitiesToSave){
        chartSavedQuantities = chartQuantitiesToSave;
    }

    public static void setNucidForDecayChain(String nucid){
        SQLBuilder.setNucidForDecayChain( nucid);
    }
    public static String getNucidForDecayChain(){
        return  SQLBuilder.getNucidForDecayChain();
    }

    public static void saveChartDecays(LivechartPlot.Decay[] decays){
        chartSavedDecays = decays;
    }

    public  LivechartPlot.Decay[] getChartSavedDecays(){
        return chartSavedDecays;
    }


    public static void setNucidsInDecayChain(String nucidsInDecayChain) {
       SQLBuilder.setNucidsInDecayChain(nucidsInDecayChain);
    }

    public static String getNucSelectedRowid() {
        return SQLBuilder.getNucSelectedRowid();
    }

    public static void setNucSelectedRowid(String nucSelectedRowid) {
        SQLBuilder.setNucSelectedRowid(nucSelectedRowid);
    }


    public static String  getTablesToQueryLast() {
        return SQLBuilder.getTablesToQueryLast();
    }

    public static String  getWhereForQueryLast() {
        return SQLBuilder.getWhereForQueryLast();
    }

    public static String  getQueryDescLast() {
        return SQLBuilder.getQueryDescLast();
    }


    public void setQueryParts(String tablesToQuery,String whereForQuery, String queryDesc){
        SQLBuilder.setQueryParts(tablesToQuery,whereForQuery,queryDesc);
    }

    public void resetQuery(){
        SQLBuilder.resetQuery();
    }

    public static void setWhereForRadtype(String whereForRadtype) {
        SQLBuilder.setWhereForRadtype(whereForRadtype);
    }

    public static void setWhereForEnergy(String whereForEnergy) {
        SQLBuilder.setWhereForEnergy(whereForEnergy);
    }

    public void setIdxRadTypeSelected(int idxRadTypeSelected) {
        SQLBuilder.setIdxRadTypeSelected(idxRadTypeSelected);
    }

    public String lblRadTypeSelected() {
        int i = SQLBuilder.getIdxRadTypeSelected();
        if(i != -1){
            return Formatter.getRadiations()[i];
        }
        return "";
    }

    public void setQueryPartsFromElement(String element, int z, String desc){
        SQLBuilder.setQueryPartsFromElement(element,z,desc);
    }


    public float[] getChartNZatCentre() {
        return BaseActivity.chartNZatCentre;
        //return chartNZatCentre;
    }

    public static void saveChartNZatCentre(float[] chartNZatCentre) {
        BaseActivity.chartNZatCentre = chartNZatCentre;
    }

    @Override
    public void onConfigurationChanged( Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // the orientation is changed in some other activity
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && !(this instanceof PeriodicTableActivity) ) {
            mendel_forced_rotation = false;
        }
    }



}
