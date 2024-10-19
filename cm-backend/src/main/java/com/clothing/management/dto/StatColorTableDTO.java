package com.clothing.management.dto;

import com.clothing.management.entities.Color;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class StatColorTableDTO extends StatTableDTO {
    private String hex;

    public StatColorTableDTO(Color color) {
        super(color.getName());
        this.hex = color.getHex();
    }
}
