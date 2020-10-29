package openfoodfacts.github.scrachx.openfood.features.categories.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.browser.customtabs.CustomTabsIntent;

import java.util.Objects;

import openfoodfacts.github.scrachx.openfood.R;
import openfoodfacts.github.scrachx.openfood.databinding.ActivityCategoryBinding;
import openfoodfacts.github.scrachx.openfood.features.categories.fragment.CategoryListFragment;
import openfoodfacts.github.scrachx.openfood.features.listeners.CommonBottomListenerInstaller;
import openfoodfacts.github.scrachx.openfood.features.shared.BaseActivity;

public class CategoryActivity extends BaseActivity {
    private ActivityCategoryBinding binding;

    public static void start(Context context) {
        Intent starter = new Intent(context, CategoryActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarInclude.toolbar);
        setTitle(R.string.category_drawer);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // chrome custom tab for category hunger game
        binding.gameButton.setOnClickListener(v -> openHungerGame());

        // set fragment container view
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, new CategoryListFragment()).commitNow();

        CommonBottomListenerInstaller.selectNavigationItem(binding.bottomNavigationInclude.bottomNavigation, 0);
        CommonBottomListenerInstaller.install(this, binding.bottomNavigationInclude.bottomNavigation);
    }

    private void openHungerGame() {
        final String url = getString(R.string.hunger_game_url);
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(CategoryActivity.this, Uri.parse(url));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

}
