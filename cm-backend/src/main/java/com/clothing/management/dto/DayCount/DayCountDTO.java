package com.clothing.management.dto.DayCount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.ConstructorArgs;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DayCountDTO {
    private long countPayed;
    private long countProgress;

}
