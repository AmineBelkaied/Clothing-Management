package com.clothing.management.models;
public class DashboardCard {
    private String status;
    private Long statusCount;

    public DashboardCard(String status, Long statusCount) {
        this.status = status;
        this.statusCount = statusCount;
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

    @Override
    public String toString() {
        return "dashboardCard{" +
                "status='" + status + '\'' +
                ", statusCount=" + statusCount +
                '}';
    }
}
