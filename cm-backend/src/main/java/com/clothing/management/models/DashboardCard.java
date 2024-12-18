package com.clothing.management.models;

public class DashboardCard {
    private String status;
    private Long statusCount;
    private Long statusByDateCount;


    public DashboardCard(String status, Long statusCount, Long statusByDateCount) {
        this.status = status;
        this.statusCount = statusCount;
        this.statusByDateCount = statusByDateCount;
    }

    public DashboardCard() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getStatusCount() {
        return statusCount;
    }

    public void setStatusCount(Long statusCount) {
        this.statusCount = statusCount;
    }

    public Long getStatusByDateCount() {
        return statusByDateCount;
    }

    public void setStatusByDateCount(Long statusByDateCount) {
        this.statusByDateCount = statusByDateCount;
    }

    @Override
    public String toString() {
        return "DashboardCard{" +
                "status='" + status + '\'' +
                ", statusCount=" + statusCount +
                ", statusByDateCount=" + statusByDateCount +
                '}';
    }
}
