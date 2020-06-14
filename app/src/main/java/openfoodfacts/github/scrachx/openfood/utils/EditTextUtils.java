package openfoodfacts.github.scrachx.openfood.utils;

import android.text.TextUtils;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.hootsuite.nachos.NachoTextView;

import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.List;

import openfoodfacts.github.scrachx.openfood.models.Nutriments;

public class EditTextUtils {
    @Nullable
    public static String getContent(EditText editText) {
        if (editText != null && editText.getText() != null) {
            return editText.getText().toString();
        }
        return null;
    }

    /**
     * @return true if the edit text string value is empty
     */
    public static boolean isEmpty(EditText editText) {
        return TextUtils.isEmpty(getContent(editText));
    }

    /**
     * @return true if the edit text string value is not empty
     */
    public static boolean isNotEmpty(EditText editText) {
        return !TextUtils.isEmpty(getContent(editText));
    }

    public static boolean isDifferent(EditText editText, @Nullable String toCompare) {
        String textContent = getContent(editText);
        if (TextUtils.isEmpty(textContent) && TextUtils.isEmpty(toCompare)) {
            return false;
        }
        if (TextUtils.isEmpty(textContent)) {
            return true;
        }
        return !textContent.equals(toCompare);
    }

    public static boolean areChipsDifferent(NachoTextView nachoTextView, List<String> toCompare) {
        List<String> nachoValues = nachoTextView.getChipValues();

        Collections.sort(nachoValues, String::compareTo);
        Collections.sort(toCompare, String::compareTo);

        // Using StringUtils because null element -> ""
        String nachoString = StringUtils.join(nachoTextView.getChipValues(), ",");
        String toCompareString = StringUtils.join(toCompare, ",");

        return !nachoString.equals(toCompareString);
    }

    public static boolean hasUnit(CustomValidatingEditTextView editTextView) {
        String shortName = editTextView.getEntryName();
        return !Nutriments.PH.equals(shortName) && !Nutriments.ALCOHOL.equals(shortName);
    }
}
