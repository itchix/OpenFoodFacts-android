package openbeautyfacts.github.scrachx.openfood.network;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class FoodUserClient {

    private static final String BASE_URL = "http://world.openbeautyfacts.org";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(5000);
        client.setResponseTimeout(70000);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(5000);
        client.setResponseTimeout(70000);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void postImg(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(5000);
        client.setResponseTimeout(70000);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
