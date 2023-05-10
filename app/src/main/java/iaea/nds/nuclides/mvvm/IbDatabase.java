package iaea.nds.nuclides.mvvm;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import iaea.nds.nuclides.BaseActivity;
import iaea.nds.nuclides.Config;
import iaea.nds.nuclides.R;
import iaea.nds.nuclides.db.entities.Cumulative_fission;
import iaea.nds.nuclides.db.entities.Decay_chain;
import iaea.nds.nuclides.db.entities.Decay_radiations;
import iaea.nds.nuclides.db.entities.L_decays;
import iaea.nds.nuclides.db.entities.Nuclides;
import iaea.nds.nuclides.db.entities.Nuclides_metadata;
import iaea.nds.nuclides.db.entities.Thermal_cross_sect;


@Database(entities = {Cumulative_fission.class, Decay_chain.class,Nuclides.class, Decay_radiations.class, L_decays.class, Nuclides_metadata.class, Thermal_cross_sect.class},version = IbDatabase.db_version_apk, exportSchema = false)
public abstract class IbDatabase extends RoomDatabase {

    private static IbDatabase instance = null;

    public static final int db_version_apk = Config.db_version_apk;
    private static String dbname = Config.DATABASE_NAME;
    private static String dbassetname = Config.DATABASE_ASSET_NAME;
    private static String dbsqlfile = Config.DATABASE_ASSET_SQLFILE;

    public abstract IbDao ibDao();

    private static  Context _context;

    public static synchronized IbDatabase getInstance(Context context){

        _context = context;
/* the db is updated if the version hardcoded in Config.db_version_apk is different from the one saved in the preferences*/
        if( Config.db_needs_update(context)) {

            context.deleteDatabase(dbname);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) { //P=28 for Pie version 9. the one before is 27 Oreo 8.1.0
                copyDbFromBinary( context);
            }
        }

        if(instance == null){
            try {
                instance = Room.databaseBuilder(context.getApplicationContext(),
                        IbDatabase.class, dbname  )
                        .allowMainThreadQueries()
                        .addCallback(roomCallback)
                        //.addMigrations(MIGRATION_1_2)
                        .build();
            } catch (Exception e){
                System.out.println("IllegalStateException");
            }
        }

        return instance;
    }

    static final Migration MIGRATION_1_2 = new Migration(59, 60) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

        }
    };

    private static void copyDbFromBinary(Context cntx){
        try {

            SQLiteDatabase db = cntx.openOrCreateDatabase(dbname,0, null);

            File dbPath = cntx.getDatabasePath(dbname);

            InputStream myInput = cntx.getAssets().open(dbassetname);
            OutputStream myOutput = null;

            myOutput = new FileOutputStream(dbPath);


            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            myInput.close();
            myOutput.flush();
            myOutput.close();

            db.close();
            BaseActivity.progressDismiss();
            Config.save_db_version(cntx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void loadDbFromSQL(SupportSQLiteDatabase db, Context cntx){
        try {


            BaseActivity.progressMessage("Loading decay radiations");

            InputStream myInput = cntx.getAssets().open(dbsqlfile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(myInput));
            String s = reader.readLine();
            int i = 0;
            while (s != null) {
                db.execSQL(s);
                s = reader.readLine();
                i++;

                 if(i > 42000){
                    BaseActivity.progressMessage("Loading decay modes");
                } else if(i > 35000){
                     BaseActivity.progressMessage("Loading fission yields");
                 } else if (i > 30000 ){
                    BaseActivity.progressMessage("Loading ground-states");
                }
            }

            myInput.close();

            BaseActivity.progressMessage(i/1000 + "  000 records loaded ");
            try {
                Thread.sleep(2);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Config.save_db_version(cntx);
            BaseActivity.progressDismiss();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                loadDbFromSQL(db, _context);
            }
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };


}
