package openfoodfacts.github.scrachx.openfood.views.product;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.browser.customtabs.CustomTabsIntent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.apache.commons.lang.StringUtils;

import openfoodfacts.github.scrachx.openfood.R;
import openfoodfacts.github.scrachx.openfood.customtabs.CustomTabActivityHelper;
import openfoodfacts.github.scrachx.openfood.customtabs.CustomTabsHelper;
import openfoodfacts.github.scrachx.openfood.customtabs.WebViewFallback;
import openfoodfacts.github.scrachx.openfood.models.entities.additive.AdditiveName;
import openfoodfacts.github.scrachx.openfood.models.entities.additive.AdditiveNameDao;
import openfoodfacts.github.scrachx.openfood.utils.LocaleHelper;
import openfoodfacts.github.scrachx.openfood.utils.SearchType;
import openfoodfacts.github.scrachx.openfood.utils.Utils;
import openfoodfacts.github.scrachx.openfood.views.OFFApplication;
import openfoodfacts.github.scrachx.openfood.views.ProductBrowsingListActivity;

public class ProductAttributeDetailsFragment extends BottomSheetDialogFragment {
    private static final String ARG_OBJECT = "result";
    private static final String ARG_ID = "code";
    private static final String ARG_SEARCH_TYPE = "search_type";
    private static final String ARG_TITLE = "title";
    private AppCompatImageView bottomSheetTitleIcon;
    private AppCompatImageView mpInfantsImage;
    private AppCompatImageView mpToddlersImage;
    private AppCompatImageView mpChildrenImage;
    private AppCompatImageView mpAdolescentsImage;
    private AppCompatImageView mpAdultsImage;
    private AppCompatImageView mpElderlyImage;
    private AppCompatImageView spInfantsImage;
    private AppCompatImageView spToddlersImage;
    private AppCompatImageView spChildrenImage;
    private AppCompatImageView spAdolescentsImage;
    private AppCompatImageView spAdultsImage;
    private AppCompatImageView spElderlyImage;
    private CustomTabsIntent customTabsIntent;

    @NonNull
    public static ProductAttributeDetailsFragment newInstance(String jsonObjectStr, long id, SearchType searchType, String title) {
        ProductAttributeDetailsFragment fragment = new ProductAttributeDetailsFragment();
        Bundle args = new Bundle();

        args.putString(ARG_OBJECT, jsonObjectStr);
        args.putLong(ARG_ID, id);
        args.putSerializable(ARG_SEARCH_TYPE, searchType);
        args.putString(ARG_TITLE, title);

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        CustomTabActivityHelper customTabActivityHelper = new CustomTabActivityHelper();
        customTabActivityHelper.setConnectionCallback(new CustomTabActivityHelper.ConnectionCallback() {
            @Override
            public void onCustomTabsConnected() {

            }

            @Override
            public void onCustomTabsDisconnected() {

            }
        });
        customTabsIntent = CustomTabsHelper.getCustomTabsIntent(requireContext(), customTabActivityHelper.getSession());

        View view = inflater.inflate(R.layout.fragment_product_attribute_details, container,
            false);

        TextView bottomSheetDescription = view.findViewById(R.id.description);
        TextView bottomSheetTitle = view.findViewById(R.id.titleBottomSheet);
        bottomSheetTitleIcon = view.findViewById(R.id.titleBottomSheetIcon);
        Button buttonToBrowseProducts = view.findViewById(R.id.buttonToBrowseProducts);
        Button wikipediaButton = view.findViewById(R.id.wikipediaButton);

        try {
            final String descriptionString;
            final String wikiLink;
            final Bundle arguments = requireArguments();
            String str = arguments.getString(ARG_OBJECT);
            if (str != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode result = mapper.readTree(str);
                JsonNode description = result.get("descriptions");
                JsonNode siteLinks = result.get("sitelinks");

                descriptionString = getDescription(description);
                wikiLink = getWikiLink(siteLinks);
            } else {
                descriptionString = null;
                wikiLink = null;
            }

            String title = arguments.getString(ARG_TITLE);
            bottomSheetTitle.setText(title);
            SearchType searchType = (SearchType) arguments.getSerializable(ARG_SEARCH_TYPE);
            if (descriptionString != null) {
                bottomSheetDescription.setText(descriptionString);
                bottomSheetDescription.setVisibility(View.VISIBLE);
            } else {
                bottomSheetDescription.setVisibility(View.GONE);
            }

            buttonToBrowseProducts.setOnClickListener(v -> ProductBrowsingListActivity.start(getContext(), title, searchType));
            if (wikiLink != null) {
                wikipediaButton.setOnClickListener(v -> openInCustomTab(wikiLink));
                wikipediaButton.setVisibility(View.VISIBLE);
            } else {
                wikipediaButton.setVisibility(View.GONE);
            }

            long id = arguments.getLong(ARG_ID);
            if (SearchType.ADDITIVE.equals(searchType)) {
                AdditiveNameDao dao = Utils.getDaoSession().getAdditiveNameDao();
                AdditiveName additiveName = dao.queryBuilder()
                    .where(
                        AdditiveNameDao.Properties.Id.eq(id)
                    ).unique();
                updateContent(view, additiveName);
            }
        } catch (JsonProcessingException e) {
            Log.e(getClass().getSimpleName(), "onCreateView", e);
        }

        return view;
    }

    private void updateContent(View view, AdditiveName additive) {


        mpInfantsImage = view.findViewById(R.id.mpInfants);
        mpToddlersImage = view.findViewById(R.id.mpToddlers);
        mpChildrenImage = view.findViewById(R.id.mpChildren);
        mpAdolescentsImage = view.findViewById(R.id.mpAdolescents);
        mpAdultsImage = view.findViewById(R.id.mpAdults);
        mpElderlyImage = view.findViewById(R.id.mpElderly);
        spInfantsImage = view.findViewById(R.id.spInfants);
        spToddlersImage = view.findViewById(R.id.spToddlers);
        spChildrenImage = view.findViewById(R.id.spChildren);
        spAdolescentsImage = view.findViewById(R.id.spAdolescents);
        spAdultsImage = view.findViewById(R.id.spAdults);
        spElderlyImage = view.findViewById(R.id.spElderly);

        if (additive!=null && additive.hasOverexposureData()) {
            View exposureEvalTable = view.findViewById(R.id.exposureEvalTable);
            TextView efsaWarning = view.findViewById(R.id.efsaWarning);
            String overexposureRisk = additive.getOverexposureRisk();
            boolean isHighRisk = "high".equalsIgnoreCase(overexposureRisk);
            if (isHighRisk) {
                bottomSheetTitleIcon.setImageResource(R.drawable.ic_additive_high_risk);
            } else {
                bottomSheetTitleIcon.setImageResource(R.drawable.ic_additive_moderate_risk);
            }
            efsaWarning.setText(getString(R.string.efsa_warning_high_risk, additive.getName()));
            bottomSheetTitleIcon.setVisibility(View.VISIBLE);

            // noel will override adi evaluation if present
            updateAdditiveExposureTable(0, additive.getExposureMeanGreaterThanAdi(), R.drawable.yellow_circle);
            updateAdditiveExposureTable(0, additive.getExposureMeanGreaterThanNoael(), R.drawable.red_circle);
            updateAdditiveExposureTable(1, additive.getExposure95ThGreaterThanAdi(), R.drawable.yellow_circle);
            updateAdditiveExposureTable(1, additive.getExposure95ThGreaterThanNoael(), R.drawable.red_circle);

            exposureEvalTable.setVisibility(View.VISIBLE);
        }
    }

    private void updateAdditiveExposureTable(int row, String exposure, int drawableResId) {
        if (exposure != null) {
            if (row == 0) {
                if (exposure.contains("infants")) {
                    mpInfantsImage.setImageResource(drawableResId);
                }

                if (exposure.contains("toddlers")) {
                    mpToddlersImage.setImageResource(drawableResId);
                }

                if (exposure.contains("children")) {
                    mpChildrenImage.setImageResource(drawableResId);
                }

                if (exposure.contains("adolescents")) {
                    mpAdolescentsImage.setImageResource(drawableResId);
                }

                if (exposure.contains("adults")) {
                    mpAdultsImage.setImageResource(drawableResId);
                }

                if (exposure.contains("elderly")) {
                    mpElderlyImage.setImageResource(drawableResId);
                }
            } else if (row == 1) {
                if (exposure.contains("infants")) {
                    spInfantsImage.setImageResource(drawableResId);
                }

                if (exposure.contains("toddlers")) {
                    spToddlersImage.setImageResource(drawableResId);
                }

                if (exposure.contains("children")) {
                    spChildrenImage.setImageResource(drawableResId);
                }

                if (exposure.contains("adolescents")) {
                    spAdolescentsImage.setImageResource(drawableResId);
                }

                if (exposure.contains("adults")) {
                    spAdultsImage.setImageResource(drawableResId);
                }

                if (exposure.contains("elderly")) {
                    spElderlyImage.setImageResource(drawableResId);
                }
            }
        }
    }

    private String getDescription(JsonNode description) {
        String descriptionString = "";
        if (description == null) {
            return descriptionString;
        }
        final String languageCode = LocaleHelper.getLanguage(OFFApplication.getInstance());
        if (languageCode != null && description.has(languageCode)) {
            description = description.get(languageCode);
            if (description != null) {
                descriptionString = description.get("value").asText();
            }
        }
        if (description != null && StringUtils.isEmpty(descriptionString) && description.has("en")) {
            description = description.get("en");
            if (description != null) {
                descriptionString = description.get("value").asText();
            }
        }
        if (StringUtils.isEmpty(descriptionString)) {
            Log.i("ProductActivity", "Result for description is not found in native or english language.");
        }
        return descriptionString;
    }

    private String getWikiLink(@NonNull JsonNode sitelinks) {
        String link = "";
        String languageCode = LocaleHelper.getLanguage(OFFApplication.getInstance());
        languageCode = languageCode + "wiki";
        if (sitelinks.has(languageCode)) {
            sitelinks = sitelinks.get(languageCode);
            link = sitelinks.get("url").asText();
        } else if (sitelinks.has("enwiki")) {
            sitelinks = sitelinks.get("enwiki");
            link = sitelinks.get("url").asText();
        } else {
            Log.i("ProductActivity", "Result for wikilink is not found in native or english language.");
        }
        return link;
    }

    private void openInCustomTab(@NonNull String url) {
        // Url might be empty string if there is no wiki link in english or the user's language
        if (!url.equals("")) {
            Uri wikipediaUri = Uri.parse(url);
            CustomTabActivityHelper.openCustomTab(requireActivity(), customTabsIntent, wikipediaUri, new WebViewFallback());
        } else {
            Toast.makeText(getContext(), R.string.wikidata_unavailable, Toast.LENGTH_SHORT).show();
        }
    }
}
