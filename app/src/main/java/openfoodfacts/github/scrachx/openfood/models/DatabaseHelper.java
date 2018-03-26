package openfoodfacts.github.scrachx.openfood.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.greenrobot.greendao.database.Database;

import static openfoodfacts.github.scrachx.openfood.models.DaoMaster.dropAllTables;

public class DatabaseHelper extends DaoMaster.OpenHelper {

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    public DatabaseHelper(Context context, String name) {
        super(context, name);
    }


    @Override
    public void onCreate(Database db) {
        Log.i("greenDAO", "Creating tables for schema version " + DaoMaster.SCHEMA_VERSION);
        DaoMaster.createAllTables(db, true);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        Log.i("greenDAO", "migrating schema from version " + oldVersion + " to " + newVersion);
        //dropAllTables(db, true);
        for (int migrateVersion = oldVersion + 1; migrateVersion <= newVersion; migrateVersion++) {
            upgrade(db, migrateVersion);
        }

    }

    /**
     * in case of android.database.sqlite.SQLiteException, the schema version is
     * left untouched just fix the code in the version case and push a new
     * release
     *
     * @param db             database
     * @param migrateVersion
     */
    private void upgrade(Database db, int migrateVersion) {
        Log.e("MIGRATE VERSION", "" + migrateVersion);
        switch (migrateVersion) {
            case 2:
                db.execSQL("ALTER TABLE send_product ADD COLUMN 'lang' TEXT NOT NULL DEFAULT 'fr';");
                break;
            case 3:
                ToUploadProductDao.createTable(db, true);
                break;
            case 4:
                TagDao.createTable(db, true);
                break;
            case 5: {
                db.execSQL("ALTER TABLE history_product ADD COLUMN 'quantity' TEXT NOT NULL DEFAULT '';");
                db.execSQL("ALTER TABLE history_product ADD COLUMN 'nutrition_grade' TEXT NOT NULL DEFAULT '';");
                break;
            }
            case 6: {
                LabelDao.createTable(db, true);
                LabelNameDao.createTable(db, true);

                AllergenDao.dropTable(db, true);
                AllergenDao.createTable(db, true);
                AllergenNameDao.createTable(db, true);

                AdditiveDao.dropTable(db, true);
                AdditiveDao.createTable(db, true);
                AdditiveNameDao.createTable(db, true);

                CountryDao.createTable(db, true);
                CountryNameDao.createTable(db, true);

                CategoryDao.createTable(db, true);
                CategoryNameDao.createTable(db, true);
                break;
            }
            case 7: {
                //  db.execSQL("ALTER TABLE additive_name ADD COLUMN 'wikiDataId' TEXT NOT NULL DEFAULT '';");
                //db.execSQL("ALTER TABLE additive_name ADD COLUMN 'isWikiDataIdPresent' INTEGER NOT NULL DEFAULT '';");
                AdditiveDao.dropTable(db,true);
                AdditiveDao.createTable(db, true);

                AdditiveNameDao.dropTable(db,true);
                AdditiveNameDao.createTable(db, true);

                CategoryNameDao.dropTable(db,true);
                CategoryNameDao.createTable(db, true);

                CategoryDao.dropTable(db,true);
                CategoryDao.createTable(db, true);

                LabelNameDao.dropTable(db,true);
                LabelNameDao.createTable(db, true);

                LabelDao.dropTable(db,true);
                LabelDao.createTable(db, true);

                break;
            }
        }
    }
}