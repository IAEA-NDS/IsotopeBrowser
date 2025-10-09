package iaea.nds.nuclides;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

public class Config {

    public static final String DATABASE_ASSET_NAME = "nuclides.db";
    public static final String DATABASE_NAME = "nuclides";
    public static final String DATABASE_ASSET_SQLFILE = "nuclides.sql";
    /* this is the version of the database shipped with the app. It is compared with the one saved  in the SharedPreferences
    * of the device. If newer, the db is updated*/
    public static final int db_version_apk = 87;//85;//;
    /**
     * preferences
     */
    public static final String PREFS_FILE_NAME = "MyPrefsFile";

    public static final String PREFS_VALUE_NONE = "MY_NOVALUE";

    public static final String PREFS_NAME_RADIATION_ORDER = "radiation_order";
    public static final String PREFS_VALUE_RADIATION_ORDER_EN = "energy";
    public static final String PREFS_VALUE_RADIATION_ORDER_INT = "intensity";

    public static final String PREFS_NAME_RADIATION_DISPLAY_MODE = "radiation_display";

    public static final String PREFS_NAME_DECAYCHAIN_PARENTS = "show_parents";


    public static final String PREFS_NAME_LANGUAGE = "language";
    public static final String PREFS_VALUE_ENGLISH = Locale.ENGLISH.getLanguage();//"en";
    public static final String PREFS_VALUE_GERMAN = Locale.GERMAN.getLanguage();//"de";
    public static final String PREFS_VALUE_ITALIAN = Locale.ITALIAN.getLanguage();//"it";
    public static final String  PREFS_VALUE_JAPANESE = Locale.JAPANESE.getLanguage();//"ja";
    public static final String  PREFS_VALUE_SLOVENIAN = "sl";
    public static final String  PREFS_VALUE_FRENCH = Locale.FRENCH.getLanguage();;
    public static final String  PREFS_VALUE_DUTCH = "nl";
    public static final String  PREFS_VALUE_ARABIC = "ar";
    public static final String  PREFS_VALUE_CHINESE = "zh";
    public static final String  PREFS_VALUE_CHINESE_TRADITIONAL = "fur";
    public static final String  PREFS_VALUE_RUSSIAN = "ru";
    public static final String  PREFS_VALUE_SPANISH = "es";

    public static final String PREFS_NAME_NUCLIDES_ORDER = "nuclides_order";
    public static final String PREFS_VALUE_NUCLIDES_ORDER_ZN = "zn";
    public static final String PREFS_VALUE_NUCLIDES_ORDER_NZ = "nz";
    public static final String PREFS_VALUE_NUCLIDES_ORDER_HALF = "hl";

    public static final String PREFS_NAME_SPECIFIC_ACTIVITY = "sp_act";
    public static final String PREFS_VALUE_CIG = "cig";
    //public static final String PREFS_VALUE_MBQKG = "mbqkg";
    public static final String PREFS_VALUE_BQG = "bqg";

    public static final String PREFS_WHAT_IS_NEW_READ = "whatsnew_";
    public static final String PREFS_VALUE_NO = "N";
    public static final String PREFS_VALUE_YES = "Y";

    public static final String PREFS_NAME_NUCLIST_ORDER = "list_order";
    public static final String PREFS_VALUE_NUCLIST_ORDER_HL = "hl";
    public static final String PREFS_VALUE_NUCLIST_ORDER_I = "i";

    public static final String PREFS_NAME_DBVERSION = "dbversion";



    public static boolean db_needs_update(Context context){
        int db_version_existing = context.getSharedPreferences(PREFS_FILE_NAME, 0).getInt(PREFS_NAME_DBVERSION,0);
        return  db_version_existing != db_version_apk;

    }

    public static void save_db_version(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_FILE_NAME, 0).edit();
        editor.putInt(PREFS_NAME_DBVERSION, db_version_apk);
        editor.commit();

    }

    public static String getLanguageFromPrefs(Context context){
        return context.getSharedPreferences(PREFS_FILE_NAME, 0).getString(PREFS_NAME_LANGUAGE, PREFS_VALUE_NONE);
    }

    public static String getNuclidesOrderBy(Context context) {
        return context.getSharedPreferences(PREFS_FILE_NAME, 0).getString(PREFS_NAME_NUCLIDES_ORDER, PREFS_VALUE_NUCLIDES_ORDER_ZN);
    }
}
