package com.clothing.management.models;

import java.util.List;

public class PaginatedResult<T> {
    private List<T> data;
    private long totalRecords;
    private long totalPages;

    // Constructor, getters, and setters

    public PaginatedResult() {
    }

    public PaginatedResult(List<T> data, long totalRecords, long totalPages) {
        this.data = data;
        this.totalRecords = totalRecords;
        this.totalPages = totalPages;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }
}