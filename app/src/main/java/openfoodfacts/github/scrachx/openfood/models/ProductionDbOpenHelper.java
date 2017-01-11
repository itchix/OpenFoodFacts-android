package openfoodfacts.github.scrachx.openfood.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.greenrobot.greendao.database.Database;

public class ProductionDbOpenHelper extends DaoMaster.OpenHelper {

    public ProductionDbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onCreate(Database db) {
        Log.i("greenDAO", "Creating tables for schema version " + DaoMaster.SCHEMA_VERSION);
        DaoMaster.createAllTables(db, false);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("greenDAO", "migrating schema from version " + oldVersion + " to " + newVersion);
        //for (int migrateVersion = oldVersion + 1; migrateVersion <= newVersion; migrateVersion++) {
        //    upgrade(db, migrateVersion);
        //}
    }

    /**
     * in case of android.database.sqlite.SQLiteException, the schema version is
     * left untouched just fix the code in the version case and push a new
     * release
     *
     * @param db
     * @param migrateVersion
     */
    /*private void upgrade(SQLiteDatabase db, int migrateVersion) {
        switch (migrateVersion) {
            case 2:
                db.execSQL("ALTER TABLE INHABITANT ADD COLUMN 'GENDER' INTEGER NOT NULL DEFAULT '0';");
                break;
            case 3:
                db.execSQL("ALTER TABLE INHABITANT ADD COLUMN 'SPECIES' TEXT;");
                db.execSQL("ALTER TABLE INVERTEBRATE ADD COLUMN 'SPECIES' TEXT;");
                db.execSQL("ALTER TABLE PLANT ADD COLUMN 'SPECIES' TEXT;");
                db.execSQL("ALTER TABLE CORAL ADD COLUMN 'SPECIES' TEXT;");
                break;
        }
    }*/
}