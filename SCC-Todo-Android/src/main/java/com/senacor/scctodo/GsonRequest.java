/**
 * Copyright 2013 Ognyan Bankov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/license-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.senacor.scctodo;

import com.android.volley.*;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Volley adapter for JSON requests with POST method that will be parsed into Java objects by Gson.
 * <p>Copied and adapted from {@literal http://blog.aimanbaharum.com/2014/02/android-development-5-using-volley-and.html} on April 9th, 2014</p>
 *
 * @author Aiman Baharum, http://blog.aimanbaharum.com/
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public class GsonRequest<T> extends Request<T> {
    private Gson mGson = new Gson();
    private Class<T> clazz;
    private Map<String, String> headers;
    private Map<String, String> params;
    private Listener<T> listener;

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url   URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     */
    public GsonRequest(int method, String url, Class<T> clazz, Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.listener = listener;
        mGson = new Gson();
    }

    /**
     * Make a POST request and return a parsed object from JSON.
     *
     * @param url   URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     */
    public GsonRequest(int method, String url, Class<T> clazz, Map<String, String> params, Listener<T> listener,
                       ErrorListener errorListener) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.params = params;
        this.listener = listener;
        this.headers = null;
        mGson = new Gson();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=" + getParamsEncoding();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        JsonObject json = new JsonObject();
        for (Map.Entry<String, String> entry : getParams().entrySet()) {
            json.addProperty(entry.getKey(), entry.getValue());
        }
        try {
            return mGson.toJson(json).getBytes(getParamsEncoding());
        } catch(UnsupportedEncodingException e) {
            return mGson.toJson(json).getBytes();
        }
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    mGson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}