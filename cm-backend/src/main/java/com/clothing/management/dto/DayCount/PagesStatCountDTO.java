package com.clothing.management.dto.DayCount;

import java.util.Date;

public class PagesStatCountDTO extends DayCountDTO {

    private String pageName;
    private Long countReturn;

    public PagesStatCountDTO() {
    }

    public PagesStatCountDTO(Date date, String pageName, Long countPayed, Long countProgress, Long countReturn) {
        super(date, countPayed, countProgress);
        this.pageName = pageName;
        this.countReturn =countReturn;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public Long getCountReturn() {
        return countReturn;
    }

    public void setCountReturn(Long countReturn) {
        this.countReturn = countReturn;
    }

    @Override
    public String toString() {
        return "PacketsPagesStatCountDTO{" +
                "pageName='" + pageName + '\'' +
                "countReturn='" + countReturn + '\'' +
                '}';
    }
}
