package com.senacor.scctodo;

import android.app.Activity;
import android.content.ClipData;
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
 * TODO AC: JavaDoc
 *
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public class DetailsActivity extends Activity {

    private RequestQueue queue;
    private TodoItem item;
    private boolean newItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        newItem = getIntent().getBooleanExtra("new", false);
        if(newItem) {
            item = new TodoItem(getResources().getString(R.string.enter_text_here), 0, false);
            getEditor().selectAll();
        } else {
            // Get business item and set the text field to show its content
            item = getIntent().getParcelableExtra("item");
            getEditor().setText(item.getText());
        }

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
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem toggle = menu.findItem(R.id.toggle_done);
        if(item.getClosed()) {
            toggle.setIcon(R.drawable.btn_check_on);
            toggle.setTitle(R.string.state_closed);
        } else {
            toggle.setIcon(R.drawable.btn_check_off);
            toggle.setTitle(R.string.state_open);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toggle_done:
                return onToggleDone(item);
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

    /**
     * TODO AC: JavaDoc
     *
     * @param button
     * @return
     */
    private boolean onToggleDone(final MenuItem button) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", Integer.toString(item.getID()));
        params.put("title", getEditor().getText().toString());
        params.put("completed", Boolean.toString(!item.getClosed()));

        Request putRequest = new GsonRequest<TodoItems>(
                Request.Method.PUT, TodoUtils.getUrl() + item.getID(), TodoItems.class, params,
                new Response.Listener<TodoItems>() {
                    @Override
                    public void onResponse(TodoItems response) {
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
                String errorMsg = error.getMessage();

                Toast.makeText(DetailsActivity.this, String.format("%s (%s)", getResources().getString(R.string.error_marking_as_done, errorMsg)),
                        Toast.LENGTH_LONG).show();
            }
        }
        );
        queue.add(putRequest);
        return true;
    }

    /**
     * TODO AC: JavaDoc
     *
     * @return
     */
    private boolean onActionSave() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("title", getEditor().getText().toString());
        params.put("completed", "false");
        if(newItem) {
            Request postRequest = new GsonRequest<Void>(
                    Request.Method.POST, TodoUtils.getUrl(), Void.class, params,
                    new Response.Listener<Void>() {
                        @Override
                        public void onResponse(Void response) {
                            try {
                                //startActivity(new Intent(DetailsActivity.this, MainActivity.class));
                                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errorMsg = error.getMessage();

                    Toast.makeText(DetailsActivity.this, String.format("%s (%s)", getResources().getString(R.string.error_adding_to_server, errorMsg)),
                            Toast.LENGTH_LONG).show();
                }
            }
            );
            queue.add(postRequest);
        } else {
            params.put("id", Integer.toString(item.getID()));
            Request putRequest = new GsonRequest<Void>(
                    Request.Method.PUT, TodoUtils.getUrl() + item.getID(), Void.class, params,
                    new Response.Listener<Void>() {
                        @Override
                        public void onResponse(Void response) {
                            try {
                                //startActivity(new Intent(DetailsActivity.this, MainActivity.class));
                                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errorMsg = error.getMessage();

                    Toast.makeText(DetailsActivity.this, String.format("%s (%s)", getResources().getString(R.string.error_adding_to_server, errorMsg)),
                            Toast.LENGTH_LONG).show();
                }
            }
            );
            queue.add(putRequest);
        }
        return true;
    }

    /**
     * TODO AC: JavaDoc
     *
     * @return
     */
    private boolean onActionDelete() {
        Request putRequest = new GsonRequest<Void>(
                Request.Method.DELETE, TodoUtils.getUrl() + item.getID(), Void.class,
                new Response.Listener<Void>() {
                    @Override
                    public void onResponse(Void response) {
                        try {
                            //startActivity(new Intent(DetailsActivity.this, MainActivity.class));
                            NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMsg = error.getMessage();

                Toast.makeText(DetailsActivity.this, String.format("%s (%s)", getResources().getString(R.string.error_marking_as_done, errorMsg)),
                        Toast.LENGTH_LONG).show();
            }
        }
        );
        queue.add(putRequest);
        return true;
    }

    private EditText getEditor() {
        return (EditText) findViewById(R.id.edit_area);
    }
}
