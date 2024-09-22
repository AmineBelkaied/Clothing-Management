package com.clothing.management.dto.DayCount;

import com.clothing.management.entities.Color;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColorsDayCountDTO extends DayCountDTO {

    private Color color;
}
