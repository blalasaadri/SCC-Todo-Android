package com.senacor.scctodo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
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
 * This Activity shows details of either a selected TodoItem or a newly created one.
 *
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public class DetailsActivity extends Activity {

    /**
     * The request queue for networking tasks
     */
    private RequestQueue queue;
    /**
     * The business item
     */
    private TodoItem item;
    /**
     * Is this a newly created business item?
     */
    private boolean newItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Is this a newly created item?
        newItem = getIntent().getBooleanExtra("new", false);
        if(newItem) {
            // If so, create a new dummy item
            item = new TodoItem(getResources().getString(R.string.enter_text_here), 0, false);
        } else {
            // Otherwise get business item and set the text field to show its content
            item = getIntent().getParcelableExtra("item");
            getTextfield().setText(item.getText());
        }

        // Create a new request queue for network tasks
        queue = Volley.newRequestQueue(this, new HttpClientStack(TodoUtils.getClient()));

        // Show main icon as back button
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem toggle = menu.findItem(R.id.toggle_done);
        // A new item may not be closed before it has been saved
        if(newItem) {
            toggle.setEnabled(false);
        } else {
            toggle.setEnabled(true);
            // Check whether the business item is closed and set the icon and text accordingly
            if(item.getClosed()) {
                toggle.setIcon(R.drawable.btn_check_on);
                toggle.setTitle(R.string.state_closed);
            } else {
                toggle.setIcon(R.drawable.btn_check_off);
                toggle.setTitle(R.string.state_open);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // React to the various button presses possible
        switch (item.getItemId()) {
            case R.id.toggle_done:
                onToggleDone(item);
                break;
            case R.id.action_save:
                onActionSave();
                break;
            case R.id.action_delete:
                onActionDelete();
                break;
            case android.R.id.home:
                returnToMainActivity();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * Handle the activation of our "completed" toggle.
     *
     * @param button The "completed" toggle button. This must be reset after (de)activation.
     */
    private void onToggleDone(final MenuItem button) {
        // We will need to pass the following parameters so that the entry on the server may be modified
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", Integer.toString(item.getID()));
        params.put("title", getTextfield().getText().toString());
        params.put("completed", Boolean.toString(!item.getClosed()));

        Request putRequest = new GsonRequest<TodoItems>(
                Request.Method.PUT, TodoUtils.URL + item.getID(), TodoItems.class, params,
                new Response.Listener<TodoItems>() {
                    @Override
                    public void onResponse(TodoItems response) {
                        // If the request was successful, we have to modify the appearance accordingly
                        item.setClosed(!item.getClosed());
                        if(item.getClosed()) {
                            button.setIcon(R.drawable.btn_check_on);
                            button.setTitle(R.string.state_closed);
                        } else {
                            button.setIcon(R.drawable.btn_check_off);
                            button.setTitle(R.string.state_open);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // If there was an error, show it
                Toast.makeText(DetailsActivity.this, String.format("%s (%s)", getResources().getString(R.string.error_marking_as_done, error.getMessage())),
                        Toast.LENGTH_LONG).show();
            }
        }
        );
        queue.add(putRequest);
    }

    /**
     * Actions to do when the "save" button is activated. These actions will differ depending on whether the business object is newly created (POST) or must be updated (PUT).
     */
    private void onActionSave() {
        // We will need to pass the following parameters so that the entry on the server may be created or modified
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("title", getTextfield().getText().toString());
        params.put("completed", "false");

        Request request;
        if(newItem) {
            // If the business item is new we will create a new entry on the server via a POST request.
            request = new GsonRequest<Void>(
                    Request.Method.POST, TodoUtils.URL, Void.class, params,
                    new Response.Listener<Void>() {
                        @Override
                        public void onResponse(Void response) {
                            returnToMainActivity();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(DetailsActivity.this, String.format("%s (%s)", getResources().getString(R.string.error_adding_to_server, error.getMessage())),
                            Toast.LENGTH_LONG).show();
                }
            }
            );
        } else {
            // If the business item is already known we have to modify it with a PUT request; this requires an ID.
            params.put("id", Integer.toString(item.getID()));
            request = new GsonRequest<Void>(
                    Request.Method.PUT, TodoUtils.URL + item.getID(), Void.class, params,
                    new Response.Listener<Void>() {
                        @Override
                        public void onResponse(Void response) {
                            returnToMainActivity();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(DetailsActivity.this, String.format("%s (%s)", getResources().getString(R.string.error_adding_to_server, error.getMessage())),
                            Toast.LENGTH_LONG).show();
                }
            }
            );
        }
        queue.add(request);
    }

    /**
     * Actions to do when the "delete" button is activated
     */
    private void onActionDelete() {
        // When deleting a confirmation is required
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm_delete).setMessage(item.getText());
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The actual deletion of an item on the server is very straight forward; just send a DELETE request to the URL with the correct ID
                Request putRequest = new GsonRequest<Void>(
                        Request.Method.DELETE, TodoUtils.URL + item.getID(), Void.class,
                        new Response.Listener<Void>() {
                            @Override
                            public void onResponse(Void response) {
                                returnToMainActivity();

                                Toast.makeText(DetailsActivity.this, R.string.deleted, Toast.LENGTH_LONG).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DetailsActivity.this, String.format("%s (%s)", getResources().getString(R.string.error_marking_as_done, error.getMessage())),
                                Toast.LENGTH_LONG).show();
                    }
                }
                );
                queue.add(putRequest);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });
        builder.create().show();
    }

    /**
     * @return the text field
     */
    private EditText getTextfield() {
        return (EditText) findViewById(R.id.edit_area);
    }

    /**
     * This will take us back to the MainActivity
     */
    private void returnToMainActivity() {
        NavUtils.navigateUpFromSameTask(DetailsActivity.this);
    }
}
