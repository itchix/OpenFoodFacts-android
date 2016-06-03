package openbeautyfacts.github.scrachx.openfood.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.steamcrafted.loadtoast.LoadToast;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import openbeautyfacts.github.scrachx.openfood.R;
import openbeautyfacts.github.scrachx.openfood.models.Additive;
import openbeautyfacts.github.scrachx.openfood.models.FoodAPIRestClientUsage;

public class SplashActivity extends BaseActivity {

    private SharedPreferences settings;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        settings = getSharedPreferences("prefs", 0);
        SharedPreferences.Editor editor = settings.edit();
        boolean firstRun = settings.getBoolean("firstRun", true);
        boolean errorAdditives = settings.getBoolean("errorAdditives", true);
        boolean errorAllergens = settings.getBoolean("errorAllergens", true);
        if(!errorAdditives && ! errorAllergens) {
            editor.putBoolean("firstRun", false);
            editor.apply();
            firstRun = false;
        }
        if (!firstRun ) {
            Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
            SplashActivity.this.startActivity(mainIntent);
            SplashActivity.this.finish();
        } else {
            new GetJson(this).execute();
        }
    }

    private class GetJson extends AsyncTask<Void, Boolean, Boolean> {

        private Context context;
        private LoadToast lt;

        public GetJson(Context ctx) {
            context = ctx;
            lt = new LoadToast(ctx);
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lt.setText(context.getString(R.string.toast_retrieving));
            lt.setBackgroundColor(context.getResources().getColor(R.color.indigo_600));
            lt.setTextColor(context.getResources().getColor(R.color.white));
            lt.show();
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {

            String json = null;
            String json1 = null;
            try {
                boolean errorAdditives = settings.getBoolean("errorAdditives", true);
                if(errorAdditives) {
                    InputStream is = getAssets().open("additives_fr.json");
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    json = new String(buffer, "UTF-8");
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<Additive> la = objectMapper.readValue(json, new TypeReference<List<Additive>>() {});
                    for (Additive a : la) {
                        Additive ta = new Additive(a.getCode(), a.getName(), a.getRisk());
                        ta.save();
                    }

                    InputStream is1 = getAssets().open("additives_en.json");
                    int size1 = is1.available();
                    byte[] buffer1 = new byte[size1];
                    is1.read(buffer1);
                    is1.close();
                    json1 = new String(buffer1, "UTF-8");
                    ObjectMapper objectMapper1 = new ObjectMapper();
                    List<Additive> la1 = objectMapper1.readValue(json1, new TypeReference<List<Additive>>() {});
                    for (Additive a : la1) {
                        Additive ta = new Additive(a.getCode(), a.getName(), a.getRisk());
                        ta.save();
                    }
                }
                return true;
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            super.onPostExecute(result);
            final SharedPreferences.Editor editor = settings.edit();
            boolean errorAllergens = settings.getBoolean("errorAllergens", true);
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if(isConnected) {
                if(errorAllergens) {
                    FoodAPIRestClientUsage api = new FoodAPIRestClientUsage();
                    api.getAllergens(new FoodAPIRestClientUsage.OnAllergensCallback() {
                        @Override
                        public void onAllergensResponse(boolean value) {
                            if (result && value) {
                                lt.success();
                                editor.putBoolean("firstRun", false);
                                editor.apply();
                            }
                            if(!value){
                                lt.error();
                                editor.putBoolean("errorAllergens", true);
                                editor.apply();
                            } else {
                                editor.putBoolean("errorAllergens", false);
                                editor.apply();
                            }
                            Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        }
                    });
                }
            } else {
                if(!result){
                    lt.error();
                    editor.putBoolean("errorAdditives", true);
                    editor.apply();
                } else {
                    editor.putBoolean("errorAdditives", false);
                    editor.apply();
                }
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }
    }
}