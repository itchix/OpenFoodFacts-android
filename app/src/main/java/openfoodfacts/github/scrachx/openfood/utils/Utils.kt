/*
 * Copyright 2016-2020 Open Food Facts
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("NOTHING_TO_INLINE")

package openfoodfacts.github.scrachx.openfood.utils

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.core.net.toUri
import androidx.core.view.children
import com.squareup.picasso.Callback
import com.squareup.picasso.RequestCreator
import openfoodfacts.github.scrachx.openfood.BuildConfig
import openfoodfacts.github.scrachx.openfood.R
import java.io.File
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


object Utils {
    const val NO_DRAWABLE_RESOURCE = 0
    const val FORCE_REFRESH_TAXONOMIES = "force_refresh_taxonomies"


    fun makeOrGetPictureDirectory(context: Context): File {
        // determine the profile directory
        var dir = context.filesDir
        if (isExternalStorageWritable()) {
            dir = context.getExternalFilesDir(null)
        }
        val picDir = File(dir, "Pictures")
        if (picDir.exists()) {
            return picDir
        }
        // creates the directory if not present yet
        val mkdir = picDir.mkdirs()
        if (!mkdir) {
            Log.e(Utils::class.simpleName, "Can create dir $picDir")
        }
        return picDir
    }

    fun isExternalStorageWritable() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    fun getOutputPicUri(context: Context): Uri =
        File(makeOrGetPictureDirectory(context), "${System.currentTimeMillis()}.jpg").toUri()

    @JvmStatic
    fun firstNotEmpty(vararg args: String?) = args.firstOrNull { it != null && it.isNotEmpty() }
}

internal inline fun Int.isGranted() = this == PERMISSION_GRANTED

internal inline fun IntArray.allGranted() = isNotEmpty() && all(Int::isGranted)


/**
 * @param type Type of call (Search or Scan)
 * @return Returns the header to be put in network call
 */
fun getUserAgent(type: String) = "${getUserAgent()} $type"
fun getUserAgent() = "${BuildConfig.APP_NAME} Official Android App ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"


@Deprecated("Use activity results")
const val MY_PERMISSIONS_REQUEST_CAMERA = 1

@Deprecated("Use activity results")
const val MY_PERMISSIONS_REQUEST_STORAGE = 2


private val LOG_TAG = Utils::class.simpleName!!

fun <T : View> ViewGroup.getViewsByType(klass: Class<T>): List<T?> {
    val result = mutableListOf<T?>()
    children.forEach { view ->
        if (view is ViewGroup) {
            result += view.getViewsByType(klass)
        }
        if (klass.isInstance(view)) {
            result += klass.cast(view)
        }
    }
    return result
}

inline fun <reified T : View> ViewGroup.getViewsByType(): List<T?> {
    return getViewsByType(T::class.java)
}


/**
 * Check if the device has a camera installed.
 *
 * @return true if installed, false otherwise.
 */
fun isHardwareCameraInstalled(context: Context) =
    context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)


internal fun RequestCreator.into(
    target: ImageView,
    onSuccess: () -> Unit = {},
    onError: (Exception) -> Unit = { throw it },
) = into(target, object : Callback {
    override fun onSuccess() = onSuccess()
    override fun onError(e: Exception) = onError(e)
})


fun @receiver:ColorInt Int.darken(ratio: Float) =
    ColorUtils.blendARGB(this, Color.BLACK, ratio)

fun @receiver:ColorInt Int.lighten(ratio: Float) =
    ColorUtils.blendARGB(this, Color.WHITE, ratio)

/**
 * Check if a certain application is installed on a device.
 *
 * @param context the applications context.
 * @param packageName the package name that you want to check.
 * @return true if the application is installed, false otherwise.
 */
fun isApplicationInstalled(context: Context, packageName: String) = runCatching {
    // Check if the package name exists, if exception is thrown, package name does not
    // exist.
    context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
}.isSuccess

/**
 * Returns the NOVA group explanation given the group
 */
fun getNovaGroupExplanation(novaGroup: String, context: Context) = when (novaGroup) {
    "1" -> context.resources.getString(R.string.nova_grp1_msg)
    "2" -> context.resources.getString(R.string.nova_grp2_msg)
    "3" -> context.resources.getString(R.string.nova_grp3_msg)
    "4" -> context.resources.getString(R.string.nova_grp4_msg)
    else -> null
}

fun getRoundNumber(measurement: Measurement, locale: Locale = Locale.getDefault()) =
    getRoundNumber(measurement.value, locale)

fun getRoundNumber(value: Number, locale: Locale = Locale.getDefault()) =
    getRoundNumber(value.toString(), locale)


/**
 * Return a round float value **with 2 decimals**
 *
 * **BE CAREFUL:** THE METHOD DOESN'T CHECK THE NUMBER AS A NUMBER.
 *
 * @param value float value
 * @return round value **with 2 decimals** or 0 if the value is empty or equals to 0
 */
fun getRoundNumber(value: CharSequence, locale: Locale = Locale.getDefault()) = when {
    value.isEmpty() -> "?"
    value == "0" -> value
    else -> value.toString().toDoubleOrNull()
        ?.let { DecimalFormat("##.##", DecimalFormatSymbols(locale)).format(it) }
        ?: "?"
}