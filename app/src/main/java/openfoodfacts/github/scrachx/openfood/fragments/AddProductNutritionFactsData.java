package openfoodfacts.github.scrachx.openfood.fragments;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import openfoodfacts.github.scrachx.openfood.utils.CustomValidatingEditTextView;

public class AddProductNutritionFactsData {
    static final String PREFIX_NUTRIMENT_LONG_NAME = "nutriment_";
    static final List<String> PARAMS_OTHER_NUTRIENTS = Collections.unmodifiableList(Arrays.asList(PREFIX_NUTRIMENT_LONG_NAME + "alpha-linolenic-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "arachidic-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "arachidonic-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "behenic-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "bicarbonate",
        PREFIX_NUTRIMENT_LONG_NAME + "biotin",
        PREFIX_NUTRIMENT_LONG_NAME + "butyric-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "caffeine",
        PREFIX_NUTRIMENT_LONG_NAME + "calcium",
        PREFIX_NUTRIMENT_LONG_NAME + "capric-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "caproic-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "caprylic-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "casein",
        PREFIX_NUTRIMENT_LONG_NAME + "cerotic-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "chloride",
        PREFIX_NUTRIMENT_LONG_NAME + "cholesterol",
        PREFIX_NUTRIMENT_LONG_NAME + "chromium",
        PREFIX_NUTRIMENT_LONG_NAME + "copper",
        PREFIX_NUTRIMENT_LONG_NAME + "dihomo-gamma-linolenic-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "docosahexaenoic-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "eicosapentaenoic-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "elaidic-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "erucic-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "fluoride",
        PREFIX_NUTRIMENT_LONG_NAME + "fructose",
        PREFIX_NUTRIMENT_LONG_NAME + "gamma-linolenic-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "glucose",
        PREFIX_NUTRIMENT_LONG_NAME + "gondoic-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "iodine",
        PREFIX_NUTRIMENT_LONG_NAME + "iron",
        PREFIX_NUTRIMENT_LONG_NAME + "lactose",
        PREFIX_NUTRIMENT_LONG_NAME + "lauric-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "lignoceric-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "linoleic-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "magnesium",
        PREFIX_NUTRIMENT_LONG_NAME + "maltodextrins",
        PREFIX_NUTRIMENT_LONG_NAME + "maltose",
        PREFIX_NUTRIMENT_LONG_NAME + "manganese",
        PREFIX_NUTRIMENT_LONG_NAME + "mead-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "melissic-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "molybdenum",
        PREFIX_NUTRIMENT_LONG_NAME + "monounsaturated-fat",
        PREFIX_NUTRIMENT_LONG_NAME + "montanic-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "myristic-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "nervonic-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "nucleotides",
        PREFIX_NUTRIMENT_LONG_NAME + "oleic-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "omega-3-fat",
        PREFIX_NUTRIMENT_LONG_NAME + "omega-6-fat",
        PREFIX_NUTRIMENT_LONG_NAME + "omega-9-fat",
        PREFIX_NUTRIMENT_LONG_NAME + "palmitic-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "pantothenic-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "ph",
        PREFIX_NUTRIMENT_LONG_NAME + "phosphorus",
        PREFIX_NUTRIMENT_LONG_NAME + "polyols",
        PREFIX_NUTRIMENT_LONG_NAME + "polyunsaturated-fat",
        PREFIX_NUTRIMENT_LONG_NAME + "potassium",
        PREFIX_NUTRIMENT_LONG_NAME + "selenium",
        PREFIX_NUTRIMENT_LONG_NAME + "serum-proteins",
        PREFIX_NUTRIMENT_LONG_NAME + "silica",
        PREFIX_NUTRIMENT_LONG_NAME + "starch",
        PREFIX_NUTRIMENT_LONG_NAME + "stearic-acid",
        PREFIX_NUTRIMENT_LONG_NAME + "sucrose",
        PREFIX_NUTRIMENT_LONG_NAME + "taurine",
        PREFIX_NUTRIMENT_LONG_NAME + "trans-fat",
        PREFIX_NUTRIMENT_LONG_NAME + "vitamin-a",
        PREFIX_NUTRIMENT_LONG_NAME + "vitamin-b1",
        PREFIX_NUTRIMENT_LONG_NAME + "vitamin-b12",
        PREFIX_NUTRIMENT_LONG_NAME + "vitamin-b2",
        PREFIX_NUTRIMENT_LONG_NAME + "vitamin-pp",
        PREFIX_NUTRIMENT_LONG_NAME + "vitamin-b6",
        PREFIX_NUTRIMENT_LONG_NAME + "vitamin-b9",
        PREFIX_NUTRIMENT_LONG_NAME + "vitamin-c",
        PREFIX_NUTRIMENT_LONG_NAME + "vitamin-d",
        PREFIX_NUTRIMENT_LONG_NAME + "vitamin-e",
        PREFIX_NUTRIMENT_LONG_NAME + "vitamin-k",
        PREFIX_NUTRIMENT_LONG_NAME + "zinc"));

    private AddProductNutritionFactsData() {
    }

    static String getCompleteEntryName(CustomValidatingEditTextView editText) {
        return PREFIX_NUTRIMENT_LONG_NAME + editText.getEntryName();
    }

    static String getShortName(String init) {
        return StringUtils.removeStart(init, PREFIX_NUTRIMENT_LONG_NAME);
    }
}
