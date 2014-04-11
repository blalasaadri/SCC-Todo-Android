package com.senacor.scctodo;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Based on the JsonSearchResultAdapter from the example on {@literal https://www.captechconsulting.com/blog/clinton-teegarden/android-volley-library-tutorial}
 *
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public class JsonTodoAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<TodoItem> results;

    public JsonTodoAdapter(Context context, ArrayList<TodoItem> results) {
        super();
        this.context = context;
        this.results = results;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int position) {
        return results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.todo_item, null, false);
            ViewHolder holder = new ViewHolder();
            holder.content = (TextView) view.findViewById(R.id.todoText);
            view.setTag(holder);
        }
        TodoItem selectedResult = (TodoItem) getItem(position);
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.content.setText(Html.fromHtml("<b>" + selectedResult.getText()) + "</b>");
        return view;
    }

    static class ViewHolder {
        TextView content;
    }
}
