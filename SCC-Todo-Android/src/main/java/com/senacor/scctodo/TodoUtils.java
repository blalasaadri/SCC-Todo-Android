package com.senacor.scctodo;

/**
 * TODO AC: JavaDoc
 *
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public final class TodoUtils {

    private static String url = "http://10.0.2.2:8080/rest_ws/tasks/";
    //private static String url = "http://14.21.98.14:8080/rest_ws/tasks/";

    public static String getUrl() {
        return url;
    }
}
