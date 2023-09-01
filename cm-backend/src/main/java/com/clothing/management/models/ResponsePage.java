package com.clothing.management.models;

import java.util.List;

public class ResponsePage {

        private List result;
        private int currentPage;
        private long totalItems;
        private int totalPages;

        private ResponsePage(Builder builder) {
            this.result = builder.result;
            this.currentPage = builder.currentPage;
            this.totalItems = builder.totalItems;
            this.totalPages = builder.totalPages;
        }

        public List getResult() {
            return result;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public long getTotalItems() {
            return totalItems;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public static class Builder {
            private List result;
            private int currentPage;
            private long totalItems;
            private int totalPages;

            public Builder result(List result) {
                this.result = result;
                return this;
            }

            public Builder currentPage(int currentPage) {
                this.currentPage = currentPage;
                return this;
            }

            public Builder totalItems(long totalItems) {
                this.totalItems = totalItems;
                return this;
            }

            public Builder totalPages(int totalPages) {
                this.totalPages = totalPages;
                return this;
            }

            public ResponsePage build() {
                return new ResponsePage(this);
            }
        }
    }
