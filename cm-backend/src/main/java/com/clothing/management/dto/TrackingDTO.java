package com.clothing.management.dto;

public class TrackingDTO {
    private int id;
    private double barrecode;
    private String status_label;
    private String update_date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getBarrecode() {
        return barrecode;
    }

    public void setBarrecode(double barrecode) {
        this.barrecode = barrecode;
    }

    public String getStatus_label() {
        return status_label;
    }

    public void setStatus_label(String status_label) {
        this.status_label = status_label;
    }

    public String getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(String update_date) {
        this.update_date = update_date;
    }

    public TrackingDTO(int id, double barrecode, String status_label, String update_date) {
        this.id = id;
        this.barrecode = barrecode;
        this.status_label = status_label;
        this.update_date = update_date;
    }

    @Override
    public String toString() {
        return "TruckingDTO{" +
                "id=" + id +
                ", barrecode=" + barrecode +
                ", status_label='" + status_label + '\'' +
                ", update_date='" + update_date + '\'' +
                '}';
    }

    public TrackingDTO() {
    }
}
