package openfoodfacts.github.scrachx.openfood.analytics

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import io.sentry.Sentry
import io.sentry.android.core.SentryAndroid
import openfoodfacts.github.scrachx.openfood.BuildConfig
import openfoodfacts.github.scrachx.openfood.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SentryAnalytics @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences,
) {

    val prefKey = context.getString(R.string.pref_crash_reporting_key)

    private val enabledFromPrefs get() = sharedPreferences.getBoolean(prefKey, false)

    private val listener: (SharedPreferences, String?) -> Unit = { _, key ->
        if (key == prefKey) refresh()
    }

    init {
        // Init sharedPrefs listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        refresh()
    }

    private fun refresh() {
        if (enabledFromPrefs) {
            SentryAndroid.init(context)
            Sentry.configureScope {
                it.setTag(FLAVOR_TAG, BuildConfig.FLAVOR)
            }
        } else {
            // Init with null dsn == disable sentry
            Sentry.init("")
        }
    }

    fun record(exception: Throwable) {
        Sentry.captureException(exception)
    }

    fun setTag(key: String, value: String) {
        Sentry.setTag(key, value)
    }

    fun setBarcode(barcode: String) = setTag(BARCODE_TAG, barcode)

    companion object {
        private const val FLAVOR_TAG = "flavor"
        private const val BARCODE_TAG = "barcode"
    }
}
