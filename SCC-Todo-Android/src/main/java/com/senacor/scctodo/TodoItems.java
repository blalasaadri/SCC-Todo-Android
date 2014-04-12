package com.senacor.scctodo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * The item which is received as a JSON object when loading the current state
 *
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public class TodoItems {

    List<TodoItem> content;
    private boolean sorted = false;

    /**
     * TODO AC: JavaDoc
     *
     * @return
     */
    public List<TodoItem> getContent() {
        if(!sorted && content != null) {
            Collections.sort(content, new Comparator<TodoItem>() {
                @Override
                public int compare(TodoItem lhs, TodoItem rhs) {
                    return new Integer(lhs.getID()).compareTo(rhs.getID());
                }
            });
            sorted = true;
        }
        return content;
    }

    /**
     * TODO AC: JavaDoc
     *
     * @param content
     */
    public void setContent(List<TodoItem> content) {
        this.content = content;
        sorted = false;
    }
}
