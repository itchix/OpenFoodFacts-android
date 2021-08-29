package openfoodfacts.github.scrachx.openfood.models

import openfoodfacts.github.scrachx.openfood.R
import openfoodfacts.github.scrachx.openfood.models.Nutriment.*

enum class Nutriment(val key: String) {
    ENERGY_KCAL("energy-kcal"),
    ENERGY_KJ("energy-kj"),
    ENERGY_FROM_FAT("energy-from-fat"),
    FAT("fat"),
    SATURATED_FAT("saturated-fat"),
    BUTYRIC_ACID("butyric-acid"),
    CAPROIC_ACID("caproic-acid"),
    CAPRYLIC_ACID("caprylic-acid"),
    CAPRIC_ACID("capric-acid"),
    LAURIC_ACID("lauric-acid"),
    MYRISTIC_ACID("myristic-acid"),
    PALMITIC_ACID("palmitic-acid"),
    STEARIC_ACID("stearic-acid"),
    ARACHIDIC_ACID("arachidic-acid"),
    BEHENIC_ACID("behenic-acid"),
    LIGNOCERIC_ACID("lignoceric-acid"),
    CEROTIC_ACID("cerotic-acid"),
    MONTANIC_ACID("montanic-acid"),
    MELISSIC_ACID("melissic-acid"),
    MONOUNSATURATED_FAT("monounsaturated-fat"),
    POLYUNSATURATED_FAT("polyunsaturated-fat"),
    OMEGA_3_FAT("omega-3-fat"),
    ALPHA_LINOLENIC_ACID("alpha-linolenic-acid"),
    EICOSAPENTAENOIC_ACID("eicosapentaenoic-acid"),
    DOCOSAHEXAENOIC_ACID("docosahexaenoic-acid"),
    OMEGA_6_FAT("omega-6-fat"),
    LINOLEIC_ACID("linoleic-acid"),
    ARACHIDONIC_ACID("arachidonic-acid"),
    GAMMA_LINOLENIC_ACID("gamma-linolenic-acid"),
    DIHOMO_GAMMA_LINOLENIC_ACID("dihomo-gamma-linolenic-acid"),
    OMEGA_9_FAT("omega-9-fat"),
    OLEIC_ACID("oleic-acid"),
    ELAIDIC_ACID("elaidic-acid"),
    GONDOIC_ACID("gondoic-acid"),
    MEAD_ACID("mead-acid"),
    ERUCIC_ACID("erucic-acid"),
    NERVONIC_ACID("nervonic-acid"),
    TRANS_FAT("trans-fat"),
    CHOLESTEROL("cholesterol"),
    CARBOHYDRATES("carbohydrates"),
    SUGARS("sugars"),
    SUCROSE("sucrose"),
    GLUCOSE("glucose"),
    FRUCTOSE("fructose"),
    LACTOSE("lactose"),
    MALTOSE("maltose"),
    MALTODEXTRINS("maltodextrins"),
    STARCH("starch"),
    POLYOLS("polyols"),
    FIBER("fiber"),
    PROTEINS("proteins"),
    CASEIN("casein"),
    SERUM_PROTEINS("serum-proteins"),
    NUCLEOTIDES("nucleotides"),
    SALT("salt"),
    SODIUM("sodium"),
    ALCOHOL("alcohol"),
    VITAMIN_A("vitamin-a"),
    BETA_CAROTENE("beta-carotene"),
    VITAMIN_D("vitamin-d"),
    VITAMIN_E("vitamin-e"),
    VITAMIN_K("vitamin-k"),
    VITAMIN_C("vitamin-c"),
    VITAMIN_B1("vitamin-b1"),
    VITAMIN_B2("vitamin-b2"),
    VITAMIN_PP("vitamin-pp"),
    VITAMIN_B6("vitamin-b6"),
    VITAMIN_B9("vitamin-b9"),
    WATER_HARDNESS("water-hardness"),
    GLYCEMIC_INDEX("glycemic-index"),
    NUTRITION_SCORE_UK("nutrition-score-uk"),
    NUTRITION_SCORE_FR("nutrition-score-fr"),
    CARBON_FOOTPRINT("carbon-footprint"),
    CHLOROPHYL("chlorophyl"),
    COCOA("cocoa"),
    COLLAGEN_MEAT_PROTEIN_RATIO("collagen-meat-protein-ratio"),
    FRUITS_VEGETABLES_NUTS("fruits-vegetables-nuts"),
    PH("ph"),
    TAURINE("taurine"),
    CAFFEINE("caffeine"),
    IODINE("iodine"),
    MOLYBDENUM("molybdenum"),
    CHROMIUM("chromium"),
    SELENIUM("selenium"),
    FLUORIDE("fluoride"),
    MANGANESE("manganese"),
    COPPER("copper"),
    ZINC("zinc"),
    VITAMIN_B12("vitamin-b12"),
    BIOTIN("biotin"),
    PANTOTHENIC_ACID("pantothenic-acid"),
    SILICA("silica"),
    BICARBONATE("bicarbonate"),
    POTASSIUM("potassium"),
    CHLORIDE("chloride"),
    CALCIUM("calcium"),
    PHOSPHORUS("phosphorus"),
    IRON("iron"),
    MAGNESIUM("magnesium");

    companion object {
        fun findbyKey(key: String) = values().find { it.key == key }
        fun requireByKey(key: String) = findbyKey(key) ?: throw IllegalArgumentException("Cannot find nutriment with key '$key'")
    }
}

val MINERALS_MAP = mapOf(
    SILICA to R.string.silica,
    BICARBONATE to R.string.bicarbonate,
    POTASSIUM to R.string.potassium,
    CHLORIDE to R.string.chloride,
    CALCIUM to R.string.calcium,
    CALCIUM to R.string.calcium,
    PHOSPHORUS to R.string.phosphorus,
    IRON to R.string.iron,
    MAGNESIUM to R.string.magnesium,
    ZINC to R.string.zinc,
    COPPER to R.string.copper,
    MANGANESE to R.string.manganese,
    FLUORIDE to R.string.fluoride,
    SELENIUM to R.string.selenium,
    CHROMIUM to R.string.chromium,
    MOLYBDENUM to R.string.molybdenum,
    IODINE to R.string.iodine,
    CAFFEINE to R.string.caffeine,
    TAURINE to R.string.taurine,
    PH to R.string.ph,
    FRUITS_VEGETABLES_NUTS to R.string.fruits_vegetables_nuts,
    COLLAGEN_MEAT_PROTEIN_RATIO to R.string.collagen_meat_protein_ratio,
    COCOA to R.string.cocoa,
    CHLOROPHYL to R.string.chlorophyl

)


val FAT_MAP = mapOf(
    SATURATED_FAT to R.string.nutrition_satured_fat,
    MONOUNSATURATED_FAT to R.string.nutrition_monounsaturatedFat,
    POLYUNSATURATED_FAT to R.string.nutrition_polyunsaturatedFat,
    OMEGA_3_FAT to R.string.nutrition_omega3,
    OMEGA_6_FAT to R.string.nutrition_omega6,
    OMEGA_9_FAT to R.string.nutrition_omega9,
    TRANS_FAT to R.string.nutrition_trans_fat,
    CHOLESTEROL to R.string.nutrition_cholesterol
)

val CARBO_MAP = mapOf(
    SUGARS to R.string.nutrition_sugars,
    SUCROSE to R.string.nutrition_sucrose,
    GLUCOSE to R.string.nutrition_glucose,
    FRUCTOSE to R.string.nutrition_fructose,
    LACTOSE to R.string.nutrition_lactose,
    MALTOSE to R.string.nutrition_maltose,
    MALTODEXTRINS to R.string.nutrition_maltodextrins
)

val PROT_MAP = mapOf(
    CASEIN to R.string.nutrition_casein,
    SERUM_PROTEINS to R.string.nutrition_serum_proteins,
    NUCLEOTIDES to R.string.nutrition_nucleotides
)

val VITAMINS_MAP = mapOf(
    VITAMIN_A to R.string.vitamin_a,
    BETA_CAROTENE to R.string.vitamin_a,
    VITAMIN_D to R.string.vitamin_d,
    VITAMIN_E to R.string.vitamin_e,
    VITAMIN_K to R.string.vitamin_k,
    VITAMIN_C to R.string.vitamin_c,
    VITAMIN_B1 to R.string.vitamin_b1,
    VITAMIN_B2 to R.string.vitamin_b2,
    VITAMIN_PP to R.string.vitamin_pp,
    VITAMIN_B6 to R.string.vitamin_b6,
    VITAMIN_B9 to R.string.vitamin_b9,
    VITAMIN_B12 to R.string.vitamin_b12,
    BIOTIN to R.string.biotin,
    PANTOTHENIC_ACID to R.string.pantothenic_acid
)