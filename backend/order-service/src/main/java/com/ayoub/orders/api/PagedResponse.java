package com.ayoub.orders.api;

import org.springframework.data.domain.Page;
import java.util.List;

public class PagedResponse<T> {

    private List<T> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;

    public static <T> PagedResponse<T> from(Page<T> p) {
        PagedResponse<T> r = new PagedResponse<>();
        r.setItems(p.getContent());
        r.setPage(p.getNumber());
        r.setSize(p.getSize());
        r.setTotalElements(p.getTotalElements());
        r.setTotalPages(p.getTotalPages());
        r.setHasNext(p.hasNext());
        r.setHasPrevious(p.hasPrevious());
        return r;
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

    public boolean isHasPrevious() { return hasPrevious; }
    public void setHasPrevious(boolean hasPrevious) { this.hasPrevious = hasPrevious; }
}
