package com.senacor.scctodo;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.HashMap;

/**
 * TODO AC: JavaDoc
 *
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public class MainActivity extends ListActivity {

    /**
     * TODO AC: document
     */
    private Gson gson;

    // This is the Adapter being used to display the list's data
    private TodoListAdapter adapter;

    // Networking stuff
    private Request getRequest;
    private Request postRequest;
    private RequestQueue queue;

    // UI Elements
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a progress bar to display while the list loads
        progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        // Set up the networking components
        queue = Volley.newRequestQueue(this, new HttpClientStack(TodoUtils.getClient()));

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        adapter = new TodoListAdapter(this);
        onActionRefresh();
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Do something when a list item is clicked
        Intent intent = new Intent(this, DetailsActivity.class);
        TodoItem clicked = (TodoItem) getListView().getItemAtPosition(position);
        intent.putExtra("item", clicked);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                return onActionAdd();
            case R.id.action_refresh:
                return onActionRefresh();
            case R.id.action_settings:
                return onActionSettings();
            case R.id.action_delete: // Not handled here
                return false;
            case R.id.action_save: // Not handled here
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * TODO AC: JavaDoc
     *
     * @return
     */
    private boolean onActionAdd() {
        /*putRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        TodoItem item = gson.fromJson(s, TodoItem.class);
                        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                        intent.putExtra("item", item);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // TODO
                        boolean t = true;
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("title", "");
                        params.put("closed", "false");
                        return params;
                    }
                };*/
                /*putRequest = new GsonRequest<String>(Request.Method.PUT, url, String.class, null, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        TodoItem item = gson.fromJson(s, TodoItem.class);
                        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                        intent.putExtra("item", item);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // TODO
                        boolean t = true;
                    }
                });*/
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("title", String.format("<%s>", R.string.enter_text_here));
        params.put("completed", "false");

        postRequest = new GsonRequest<String>(
                Request.Method.POST, TodoUtils.getUrl(), String.class, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("Json Response", response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMsg = error.getMessage();

                Toast.makeText(MainActivity.this, String.format("%s (%s)", R.string.error_adding_to_server, errorMsg),
                        Toast.LENGTH_LONG).show();
            }
        }
        );

        queue.add(postRequest);
        return true;
    }

    /**
     * TODO AC: JavaDoc
     *
     * @return
     */
    private boolean onActionRefresh() {
        getListView().setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        getRequest = new GsonRequest<TodoItems>(Request.Method.GET, TodoUtils.getUrl(), TodoItems.class,
                new Response.Listener<TodoItems>() {
                    @Override
                    public void onResponse(TodoItems response) {
                        try {
                            Log.d("Json Response", response.getContent().toString());
                            adapter.setItems(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            progressBar.setVisibility(View.GONE);
                            getListView().setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMsg = error.getMessage();

                Toast.makeText(MainActivity.this, String.format("%s (%s)", R.string.error_loading_from_server, errorMsg),
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                getListView().setVisibility(View.VISIBLE);
            }
        }
        );
        queue.add(getRequest);
        return true;
    }

    private boolean onActionSettings() {
        // TODO AC: Open settings
        return true;
    }
}
