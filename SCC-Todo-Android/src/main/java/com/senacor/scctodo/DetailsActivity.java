package com.senacor.scctodo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;

import org.apache.http.impl.client.AbstractHttpClient;

import java.util.HashMap;

/**
 * TODO AC: JavaDoc
 *
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public class DetailsActivity extends Activity {

    // Networking Stuff
    private RequestQueue queue;
    private AbstractHttpClient client;

    private TodoItem item;

    private EditText editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        item = intent.getParcelableExtra("item");

        editor = (EditText) findViewById(R.id.edit_area);
        editor.setText(item.getText());

        queue = Volley.newRequestQueue(this, new HttpClientStack(TodoUtils.getClient()));

        // Show main icon as back button
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                return onActionSave();
            case R.id.action_delete:
                return onActionDelete();
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean onActionSave() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", Integer.toString(item.getID()));
        params.put("title", editor.getText().toString());
        params.put("completed", Boolean.toString(item.getClosed()));

        Request putRequest = new GsonRequest<TodoItems>(
                Request.Method.PUT, TodoUtils.getUrl() + item.getID(), TodoItems.class, params,
                new Response.Listener<TodoItems>() {
                    @Override
                    public void onResponse(TodoItems response) {
                        try {
                            Log.d("Json Response", response.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMsg = error.getMessage();

                Toast.makeText(DetailsActivity.this, String.format("%s (%s)", R.string.error_adding_to_server, errorMsg),
                        Toast.LENGTH_LONG).show();
            }
        }
        );
        queue.add(putRequest);
        return true;
    }

    private boolean onActionDelete() {
        // Delete item
        return true;
    }
}
