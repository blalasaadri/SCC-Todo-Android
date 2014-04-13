package com.senacor.scctodo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * A utility class with items used throughout the application
 *
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public final class TodoUtils {

    //private static String url = "http://10.0.2.2:8080/rest_ws/tasks/";
    public static final String URL = "http://tomcat-senacor.rhcloud.com/rest_ws-1.0/tasks/";

    public static final String TAG = "ToDoLo";

    private static AbstractHttpClient client;

    public static AbstractHttpClient getClient() {
        if (client == null) {
            client = new DefaultHttpClient();
        }
        return client;
    }

    /**
     * Return the server URL. Try loading it from the settings; if this isn't possible use the default URL.
     *
     * @param context The context in which this is used. Normally you can use {@link android.app.Activity#getBaseContext()}
     * @param subUrls If you want to add elements to the path, this is where to do that
     * @return the server URL
     */
    public static String getUrl(Context context, String... subUrls) {
        String prefUrl = PreferenceManager.getDefaultSharedPreferences(context).getString("url", TodoUtils.URL);

        StringBuilder result = new StringBuilder();
        result.append(prefUrl);

        boolean lastSlash = prefUrl.charAt(prefUrl.length() - 1) == '/';
        for(String subUrl : subUrls) {
            if(!lastSlash) {
                result.append("/");
                lastSlash = false;
            }
            result.append(subUrl);
        }
        return result.toString();
    }
}
