package com.clothing.management.dto.DayCount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagesStatCountDTO extends DayCountDTO {

    private String pageName;
    private Long countReturn;
}
