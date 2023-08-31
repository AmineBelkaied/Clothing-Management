package com.clothing.management.models;

import org.springframework.data.domain.Page;

import java.util.List;


public class ResponsePage {

    private List result;
    private int currentPage;
    private long totalItems;
    private int totalPages;

    public ResponsePage() {
    }

    public ResponsePage(List result, int currentPage, long totalItems, int totalPages) {
        this.result = result;
        this.currentPage = currentPage;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
    }

    public List getResult() {
        return result;
    }

    public void setResult(List result) {
        this.result = result;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public static ResponsePage mapToResponsePage(Page page) {
        return new ResponsePage(page.getContent(), page.getNumber(), page.getTotalElements(), page.getTotalPages());
    }
    public static ResponsePage mapToResponseList(List list, int pageNumber , long totalElements, int totalPages) {
        return new ResponsePage(list, pageNumber, totalElements, totalPages);
    }
    @Override
    public String toString() {
        return "ResponsePage{" +
                "result=" + result +
                ", currentPage=" + currentPage +
                ", totalItems=" + totalItems +
                ", totalPages=" + totalPages +
                '}';
    }
}
