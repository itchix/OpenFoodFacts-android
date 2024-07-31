package openfoodfacts.github.scrachx.openfood.features.preferences

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import openfoodfacts.github.scrachx.openfood.R
import openfoodfacts.github.scrachx.openfood.jobs.ProductUploaderWorker
import javax.inject.Inject


class PreferencesListener @Inject constructor(
    @ApplicationContext private val context: Context
) : SharedPreferences.OnSharedPreferenceChangeListener {


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        when (key) {
            context.getString(R.string.pref_enable_mobile_data_key) -> {
                ProductUploaderWorker.scheduleProductUpload(context, sharedPreferences)
            }
        }
    }
}