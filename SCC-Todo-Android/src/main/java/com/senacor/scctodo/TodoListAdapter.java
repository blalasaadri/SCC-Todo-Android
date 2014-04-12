package com.senacor.scctodo;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * TODO AC: JavaDoc
 *
 * @author Alasdair Collinson, Senacor Technologies AG
 */
// TODO AC: Could this be an ArrayAdapter?
public class TodoListAdapter implements ListAdapter {

    private static final StrikethroughSpan STRIKE_THROUGH_SPAN = new StrikethroughSpan();

    private static TodoItems items;

    private Context context;
    private List<DataSetObserver> observers;

    /**
     * TODO AC: JavaDoc
     *
     * @param context
     */
    public TodoListAdapter(Context context) {
        this.context = context;
        items = new TodoItems();
        if (items.getContent() == null) {
            List<TodoItem> list = new ArrayList<TodoItem>();
            items.setContent(list);
        }
        observers = new LinkedList<DataSetObserver>();
    }

    /**
     * TODO AC: JavaDoc
     *
     * @param items
     */
    public void setItems(TodoItems items) {
        this.items = items;
        for (DataSetObserver observer : observers) {
            observer.onChanged();
        }
    }

    /**
     * TODO AC: JavaDoc
     * @return
     */
    public TodoItems getItems() {
        return items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.todo_item, null, false);
        }
        final TextView content = (TextView) view.findViewById(R.id.todoText);
        /*ViewTreeObserver vto = content.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                content.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                // If API level was at least 16 the following would be better:
                // content.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if(content.getLineCount() > 1){
                    int lineEndIndex = content.getLayout().getLineEnd(1);
                    content.setText(content.getText().subSequence(0, lineEndIndex - 3) + "...");
                }
            }
        });*/
        view.setTag(content);
        TodoItem selectedResult = (TodoItem) getItem(position);
        setText(content, selectedResult);
        return view;
    }

    private void setText(TextView view, TodoItem item) {
        String text = item.getText();
        /*if (text.length() > 15) {
            text = text.substring(0, 15) + "...";
        }*/
        if (item.getClosed()) {
            view.setTypeface(Typeface.DEFAULT);
            view.setText(text, TextView.BufferType.SPANNABLE);
            Spannable spannable = (Spannable) view.getText();
            spannable.setSpan(STRIKE_THROUGH_SPAN, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            view.setTypeface(Typeface.DEFAULT_BOLD);
            view.setText(text);
        }
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        ((LinkedList) observers).addFirst(dataSetObserver);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        observers.remove(dataSetObserver);
    }

    @Override
    public boolean isEmpty() {
        return items.getContent().isEmpty();
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public int getCount() {
        return items.getContent().size();
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return items.getContent().get(i);
    }
}
