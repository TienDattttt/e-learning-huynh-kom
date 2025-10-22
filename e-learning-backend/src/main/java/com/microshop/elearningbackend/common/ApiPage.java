package com.microshop.elearningbackend.common;

import java.util.List;

public class ApiPage<T> {
    private List<T> items;
    private int page;           // 0-based
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrev;

    public ApiPage() {}

    public ApiPage(List<T> items, int page, int size, long totalElements) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil(totalElements / (double) Math.max(size, 1));
        this.hasPrev = page > 0;
        this.hasNext = page + 1 < totalPages;
    }

    public List<T> getItems() { return items; }
    public void setItems(List<T> items) { this.items = items; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public boolean isHasNext() { return hasNext; }
    public void setHasNext(boolean hasNext) { this.hasNext = hasNext; }

    public boolean isHasPrev() { return hasPrev; }
    public void setHasPrev(boolean hasPrev) { this.hasPrev = hasPrev; }
}
