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
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.senacor.scctodo.TodoItems.TodoItem;

/**
 * A ListAdapter for the list of {@link com.senacor.scctodo.TodoItems.TodoItem}s
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
     * Constructor
     *
     * @param context The context in which the list is displayed; this will normally be an {@link android.app.Activity} of some sort
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
     * Set the items in this list
     *
     * @param items The {@link com.senacor.scctodo.TodoItems.TodoItem}s to be displayed
     */
    public void setItems(TodoItems items) {
        TodoListAdapter.items = items;
        for (DataSetObserver observer : observers) {
            observer.onChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.todo_item, null, false);
        }
        final TextView content = (TextView) view.findViewById(R.id.todoText);
        view.setTag(content);
        TodoItem selectedResult = (TodoItem) getItem(position);
        setText(content, selectedResult);
        return view;
    }

    /**
     * A helper function which will set the value of a {@link android.widget.TextView} to the text in the passed {@link com.senacor.scctodo.TodoItems.TodoItem}.
     * This function will also change the style of the text, depending on whether the item is marked as done or not.
     *
     * @param view The {@link android.widget.TextView} which should be set
     * @param item The {@link com.senacor.scctodo.TodoItems.TodoItem} from which the content should be taken.
     */
    private static void setText(TextView view, TodoItem item) {
        String text = item.getText();
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
        ((LinkedList<DataSetObserver>) observers).addFirst(dataSetObserver);
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
