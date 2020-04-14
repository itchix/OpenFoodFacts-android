package openfoodfacts.github.scrachx.openfood.views;

import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;

import org.apache.commons.lang.ArrayUtils;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

import java.io.IOException;

import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;
import openfoodfacts.github.scrachx.openfood.BuildConfig;
import openfoodfacts.github.scrachx.openfood.dagger.component.AppComponent;
import openfoodfacts.github.scrachx.openfood.dagger.module.AppModule;
import openfoodfacts.github.scrachx.openfood.models.DaoMaster;
import openfoodfacts.github.scrachx.openfood.models.DaoSession;
import openfoodfacts.github.scrachx.openfood.models.DatabaseHelper;

public class OFFApplication extends MultiDexApplication {
    public static DaoSession daoSession;
    public static final String OFF = "off";
    public static final String OPFF = "opff";
    public static final String OPF = "opf";
    public static final String OBF = "obf";
    private boolean DEBUG = false;
    private static OFFApplication application;
    private static AppComponent appComponent;

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    public static OFFApplication getInstance() {
        return application;
    }

    public static boolean isFlavor(String... flavors) {
        return ArrayUtils.contains(flavors, BuildConfig.FLAVOR);
    }

    public static boolean isFlavor(String flavor) {
        return BuildConfig.FLAVOR.equals(flavor);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        // Use only during development: DaoMaster.DevOpenHelper (Drops all table on Upgrade!)
        // Use only during production: DatabaseHelper (see on Upgrade!)
        String nameDB;
        if ((isFlavor(OFF))) {
            nameDB = "open_food_facts";
        } else if (isFlavor(OPFF)) {
            nameDB = "open_pet_food_facts";
        } else if (isFlavor(OPF)) {
            nameDB = "open_products_facts";
        } else {
            nameDB = "open_beauty_facts";
        }
        
        DatabaseHelper helper = new DatabaseHelper(this, nameDB);
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

        // DEBUG
        QueryBuilder.LOG_VALUES = DEBUG;
        QueryBuilder.LOG_SQL = DEBUG;

        appComponent = AppComponent.Initializer.init(new AppModule(this));
        appComponent.inject(this);

        RxJavaPlugins.setErrorHandler(e -> {
            if (e instanceof UndeliverableException) {
                e = e.getCause();
            }
            if (e instanceof IOException) {

                // fine, irrelevant network problem or API that throws on cancellation
                Log.i(OFFApplication.class.getSimpleName(), "network exception", e);
                return;
            }
            if (e instanceof InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                return;
            }
            if ((e instanceof NullPointerException) || (e instanceof IllegalArgumentException)) {
                // that's likely a bug in the application
                Thread.currentThread().getUncaughtExceptionHandler()
                    .uncaughtException(Thread.currentThread(), e);
                return;
            }
            if (e instanceof IllegalStateException) {
                // that's a bug in RxJava or in a custom operator
                Thread.currentThread().getUncaughtExceptionHandler()
                    .uncaughtException(Thread.currentThread(), e);
                return;
            }
            Log.w(OFFApplication.class.getSimpleName(), "Undeliverable exception received, not sure what to do", e);
        });
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
