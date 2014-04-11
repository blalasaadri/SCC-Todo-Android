package com.senacor.scctodo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * TODO AC: JavaDoc
 *
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public class TodoItems {
    List<TodoItem> content;
    private Page page;
    private boolean sorted = false;

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

    public void setContent(List<TodoItem> content) {
        this.content = content;
        sorted = false;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public static class Page {
        int size;
        int totalElements;
        int totalPages;
        int number;

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(int totalElements) {
            this.totalElements = totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }
}
