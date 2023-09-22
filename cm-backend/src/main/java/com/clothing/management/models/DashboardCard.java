package com.clothing.management.entities;
public class DashboardCard {
    private String status;
    private int statusCount;

    public DashboardCard(String status, int statusCount) {
        this.status = status;
        this.statusCount = statusCount;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "dashboardCard{" +
                "status='" + status + '\'' +
                ", statusCount=" + statusCount +
                '}';
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusCount() {
        return statusCount;
    }

    public void setStatusCount(int statusCount) {
        this.statusCount = statusCount;
    }
}
