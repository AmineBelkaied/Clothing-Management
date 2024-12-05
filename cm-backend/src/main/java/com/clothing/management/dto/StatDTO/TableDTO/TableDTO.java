package com.clothing.management.dto.StatDTO.TableDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableDTO {
    private long countPaid;
    private long countProgress;
    private long countReturn;
    private double profits;
}
