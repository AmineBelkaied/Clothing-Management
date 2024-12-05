package com.clothing.management.dto.StatDTO.ChartDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartDTO {
    private Date date;
    private IDNameDTO idName;
    private long value;

    public ChartDTO( Date date, Long id, String name,long value) {
        this.value = value;
        this.date = date;
        this.idName = new IDNameDTO(id,name);
    }
}
