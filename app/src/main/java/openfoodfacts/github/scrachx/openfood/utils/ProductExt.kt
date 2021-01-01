package openfoodfacts.github.scrachx.openfood.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import openfoodfacts.github.scrachx.openfood.R
import openfoodfacts.github.scrachx.openfood.models.Product
import openfoodfacts.github.scrachx.openfood.models.Units
import openfoodfacts.github.scrachx.openfood.models.entities.OfflineSavedProduct
import openfoodfacts.github.scrachx.openfood.network.OpenFoodAPIClient

fun OfflineSavedProduct.toState(context: Context) = OpenFoodAPIClient(context).getProductStateFull(barcode)

fun OfflineSavedProduct.toOnlineProduct(context: Context) = toState(context).map { it.product }

fun Product.isPerServingInLiter() = servingSize?.contains(Units.UNIT_LITER, true)

@DrawableRes
fun Product?.getCO2Resource(): Int {
    if (this == null) return Utils.NO_DRAWABLE_RESOURCE

    val tags = environmentImpactLevelTags
    if (tags.isNullOrEmpty()) return Utils.NO_DRAWABLE_RESOURCE

    return when (tags[0].replace("\"", "")) {
        "en:high" -> R.drawable.ic_co2_high_24dp
        "en:low" -> R.drawable.ic_co2_low_24dp
        "en:medium" -> R.drawable.ic_co2_medium_24dp
        else -> Utils.NO_DRAWABLE_RESOURCE
    }
}

@DrawableRes
fun Product?.getEcoscoreResource() = when (this?.ecoscore) {
    "a" -> R.drawable.ic_ecoscore_a
    "b" -> R.drawable.ic_ecoscore_b
    "c" -> R.drawable.ic_ecoscore_c
    "d" -> R.drawable.ic_ecoscore_d
    "e" -> R.drawable.ic_ecoscore_e
    else -> Utils.NO_DRAWABLE_RESOURCE
}

@DrawableRes
fun Product?.getNutriScoreResource() = getNutriScoreResource(this?.nutritionGradeFr)

fun Product?.getImageGradeDrawable(context: Context): Drawable? {
    val gradeID = getNutriScoreResource(this?.nutritionGradeFr)
    return if (gradeID == Utils.NO_DRAWABLE_RESOURCE) null
    else VectorDrawableCompat.create(context.resources, gradeID, null)
}

/**
 * Returns the NOVA group graphic asset given the group
 */
@DrawableRes
fun Product?.getNovaGroupDrawable() = getNovaGroupDrawable(this?.novaGroups)