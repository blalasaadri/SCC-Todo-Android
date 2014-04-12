package com.senacor.scctodo;

import com.android.volley.RequestQueue;

import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * TODO AC: JavaDoc
 *
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public final class TodoUtils {

    //private static String url = "http://10.0.2.2:8080/rest_ws/tasks/";
    public static final String URL = "http://tomcat-senacor.rhcloud.com/rest_ws-1.0/tasks/";

    public static final String TAG = "ToDoLo";

    private static AbstractHttpClient client;

    public static AbstractHttpClient getClient() {
        if(client == null) {
            client = new DefaultHttpClient();
        }
        return client;
    }
}
