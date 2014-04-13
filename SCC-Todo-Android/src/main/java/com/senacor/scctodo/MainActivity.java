package com.senacor.scctodo;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.senacor.scctodo.TodoItems.TodoItem;

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

        // Create an adapter we will use to display the loaded data.
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
            case R.id.set_server:
                onActionSetServer();
                break;
            case R.id.about:
                onLicenseAbout();
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
        Request getRequest = new GsonRequest<TodoItems>(Request.Method.GET, TodoUtils.getUrl(getBaseContext()), TodoItems.class,
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
     * If the Settings->Set Server option was chosen, open a settings dialogue
     */
    private void onActionSetServer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.set_server);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.server_settings, null);
        final EditText text = ((EditText) content.findViewById(R.id.server_url));
        text.setText(TodoUtils.getUrl(getBaseContext()));
        builder.setView(content);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor preferencesEditor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                preferencesEditor.putString("url", text.getText().toString());
                preferencesEditor.commit();
                onActionRefresh();
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.create().show();
    }

    /**
     * If the Settings->License option was chosen, open a dialogue showing the license information
     */
    private void onLicenseAbout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.about).setMessage(loadLicense());
        builder.setNeutralButton(R.string.ok, null);
        builder.create().show();
    }

    /**
     * Adapted from {@literal http://sunil-android.blogspot.de/2013/05/open-read-file-from-assets.html}
     *
     * @return The license as a String object
     */
    private String loadLicense() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[4096];
        int len = 0;
        try {
            InputStream license = getResources().openRawResource(R.raw.license);
            while ((len = license.read(bytes)) > 0)
                byteStream.write(bytes, 0, len);
            return new String(byteStream.toByteArray(), "UTF8");
        } catch (IOException e) {
            return getResources().getString(R.string.error_reading_license);
        }
    }
}
