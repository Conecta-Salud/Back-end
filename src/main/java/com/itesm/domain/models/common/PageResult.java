package com.itesm.domain.models.common;

import java.util.List;

public class PageResult<T> {

    private List<T> items;
    private long totalItems;
    private int page;
    private int size;
    private int totalPages;

    public PageResult(
            List<T> items,
            long totalItems,
            int page,
            int size
    ) {
        this.items = items;
        this.totalItems = totalItems;
        this.page = page;
        this.size = size;
        this.totalPages = size > 0
                ? (int) Math.ceil((double) totalItems / size)
                : 0;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
