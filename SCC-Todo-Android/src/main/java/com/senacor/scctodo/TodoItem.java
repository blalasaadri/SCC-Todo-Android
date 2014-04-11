package com.senacor.scctodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Bean für ein TodoItem.
 *
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public class TodoItem implements Parcelable {

    /**
     * Needed for the Parcelable interface to create new TodoItem instances from parcels
     */
    public static final Parcelable.Creator<TodoItem> CREATOR
            = new Parcelable.Creator<TodoItem>() {
        @Override
        public TodoItem createFromParcel(Parcel parcel) {
            return new TodoItem(parcel.readString(), parcel.readInt(), parcel.readByte() == 0 ? false : true);
        }

        @Override
        public TodoItem[] newArray(int size) {
            return new TodoItem[size];
        }
    };

    /**
     * TODO AC: JavaDoc
     */
    @SerializedName("title")
    private String text;

    /**
     * TODO AC: JavaDoc
     */
    @SerializedName("id")
    private final int id;

    /**
     * TODO AC: JavaDoc
     */
    @SerializedName("completed")
    private boolean closed;

    /**
     * TODO AC: JavaDoc
     *
     * @param text
     */
    public TodoItem(String text, int id, boolean closed) {
        this.text = text;
        this.id = id;
        this.closed = closed;
    }

    /**
     * TODO AC: JavaDoc
     *
     * @param id
     */
    public TodoItem(int id) {
        this.id = id;
        this.text = "";
    }

    /**
     * TODO AC: JavaDoc
     *
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * TODO AC: JavaDoc
     *
     * @param text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * TODO AC: JavaDoc
     *
     * @return
     */
    public int getID() {
        return id;
    }

    /**
     * TODO AC: JavaDoc
     *
     * @return
     */
    public boolean getClosed() {
        return closed;
    }

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
        return String.format("Todo (id: %d, closed: %b): \"%s\"", id, closed, text);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || TodoItem.class != o.getClass()) return false;

        TodoItem todoItem = (TodoItem) o;

        if (!(id == todoItem.getID())) return false;
        if (!text.equals(todoItem.text)) return false;
        return (closed == todoItem.getClosed());
    }

    @Override
    public int hashCode() {
        int result = text != null ? text.hashCode() : 0;
        result = 31 * result + id;
        result = 31 * result + (closed ? 1 : 0);
        return result;
    }
}
