package com.senacor.scctodo;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import java.util.List;

/**
 * The main Activity for this app. It displays a list of TodoItems retrieved from a server and allows you to create new ones.
 * Clicking on a TodoItem will take you to a DetailsActivity in which further modifications can be made.
 *
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public class MainActivity extends ListActivity {

    /**
     * This is the Adapter being used to display the list's data
     */
    private TodoListAdapter adapter;

    /**
     * The request queue for volley
     */
    private RequestQueue queue;

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
        setListAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onActionRefresh();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // When a list item is clicked open a DetailsActivity with that item
        Intent intent = new Intent(this, DetailsActivity.class);
        TodoItem clicked = (TodoItem) getListView().getItemAtPosition(position);
        intent.putExtra("item", clicked);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Create the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // React to clicked icons
        switch (item.getItemId()) {
            case R.id.action_add:
                onActionAdd();
                break;
            case R.id.action_refresh:
                onActionRefresh();
                break;
            case R.id.action_settings:
                onActionSettings();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * If the "add" button was pressed, open a DetailsActivity with the "new" parameter active
     */
    private void onActionAdd() {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra("new", true);
        startActivity(intent);
    }

    /**
     * If the "refresh" button is pressed (or this function is called programmatically) refresh the list
     */
    private void onActionRefresh() {
        // Hide the list and show the progress bar
        getListView().setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        // Send a request which will return a stream of JSON which will be deserialized to a TodoItems object and put into the adapter
        Request getRequest = new GsonRequest<TodoItems>(Request.Method.GET, TodoUtils.URL, TodoItems.class,
                new Response.Listener<TodoItems>() {
                    @Override
                    public void onResponse(TodoItems response) {
                        try {
                            adapter.setItems(response);
                            Log.d(TodoUtils.TAG, response.getContent().toString());
                        } catch (Exception e) {
                            Log.e(TodoUtils.TAG, "Error loading todo items", e);
                            e.printStackTrace();
                        } finally {
                            progressBar.setVisibility(View.GONE);
                            getListView().setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, String.format("%s (%s)", getResources().getString(R.string.error_loading_from_server, error.getMessage()), error.getMessage()),
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                getListView().setVisibility(View.VISIBLE);
            }
        }
        );
        queue.add(getRequest);
    }

    /**
     * TODO AC: JavaDoc
     */
    private void onActionSettings() {
        // TODO AC: Open settings
    }
}
