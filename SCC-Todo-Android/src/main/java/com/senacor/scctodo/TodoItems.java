package com.senacor.scctodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The item which is received as a JSON object when loading the current state
 *
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public class TodoItems {

    List<TodoItem> content;
    private boolean sorted = false;

    /**
     * Get the List of {@link com.senacor.scctodo.TodoItems.TodoItem}s that was returned by the server.
     * If required this list will be sorted by ID.
     *
     * @return the sorted List of {@link com.senacor.scctodo.TodoItems.TodoItem}s that was returned by the server.
     */
    public List<TodoItem> getContent() {
        if (!sorted && content != null) {
            Collections.sort(content, new Comparator<TodoItem>() {
                @Override
                public int compare(TodoItem lhs, TodoItem rhs) {
                    return Integer.valueOf(lhs.getID()).compareTo(rhs.getID());
                }
            });
            sorted = true;
        }
        return content;
    }

    /**
     * Setter for the content.
     *
     * @param content The list of {@link com.senacor.scctodo.TodoItems.TodoItem}s that were returned by the server.
     */
    public void setContent(List<TodoItem> content) {
        this.content = content;
        sorted = false;
    }

    /**
     * Bean for a TodoItem.
     *
     * @author Alasdair Collinson, Senacor Technologies AG
     */
    public static class TodoItem implements Parcelable {

        /**
         * Needed for the Parcelable interface to create new TodoItem instances from parcels
         */
        public static final Parcelable.Creator<TodoItem> CREATOR
                = new Parcelable.Creator<TodoItem>() {
            @Override
            public TodoItem createFromParcel(Parcel parcel) {
                return new TodoItem(parcel.readString(), parcel.readInt(), parcel.readByte() != 0);
            }

            @Override
            public TodoItem[] newArray(int size) {
                return new TodoItem[size];
            }
        };
        /**
         * The "id" field from the JSON object this is created from.
         * The annotation isn't strictly required here as the field name is identical to the serialized name.
         */
        @SerializedName("id")
        private final int id;
        /**
         * The "title" field from the JSON object this is created from
         */
        @SerializedName("title")
        private String text;
        /**
         * The "completed" field from the JSON object this is created from
         */
        @SerializedName("completed")
        private boolean closed;

        /**
         * A constructor
         *
         * @param text   The text of the TodoItem
         * @param id     The id of the TodoItem
         * @param closed Is this item closed yet?
         */
        public TodoItem(String text, int id, boolean closed) {
            this.text = text;
            this.id = id;
            this.closed = closed;
        }

        /**
         * @return This items text
         */
        public String getText() {
            return text;
        }

        /**
         * Set the text of this item
         *
         * @param text The text which should be set
         */
        public void setText(String text) {
            this.text = text;
        }

        /**
         * @return This items ID
         */
        public int getID() {
            return id;
        }

        /**
         * @return Is this item closed?
         */
        public boolean getClosed() {
            return closed;
        }

        /**
         * Set whether this item is closed or not.
         *
         * @param closed Should it be closed?
         */
        public void setClosed(boolean closed) {
            this.closed = closed;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(text);
            parcel.writeInt(id);
            parcel.writeByte(closed ? (byte) 1 : (byte) 0);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public String toString() {
            return String.format("Todo; id: '%d', text: '%s', closed: '%b')", id, text, closed);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || TodoItem.class != o.getClass()) return false;

            TodoItem todoItem = (TodoItem) o;
            return id == todoItem.getID() && text.equals(todoItem.text) && closed == todoItem.getClosed();

        }

        @Override
        public int hashCode() {
            int result = text != null ? text.hashCode() : 0;
            result = 31 * result + id;
            result = 31 * result + (closed ? 1 : 0);
            return result;
        }
    }
}
