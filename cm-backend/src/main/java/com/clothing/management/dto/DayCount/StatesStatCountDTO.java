package com.clothing.management.dto.DayCount;

import java.util.Date;

public class StatesStatCountDTO extends DayCountDTO {
    private String governerateName;
    private Long countReturn;

    public StatesStatCountDTO() {
    }

    public StatesStatCountDTO(Date date, String governerateName, Long countPayed, Long countProgress, Long countReturn) {
        super(date, countPayed, countProgress);
        this.governerateName = governerateName;
        this.countReturn =countReturn;
    }

    public String getGovernerateName() {
        return governerateName;
    }

    public void setGovernerateName(String governerateName) {
        this.governerateName = governerateName;
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
                "governerateName='" + governerateName + '\'' +
                "countReturn='" + countReturn + '\'' +
                '}';
    }
}
