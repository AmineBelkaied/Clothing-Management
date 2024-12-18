package com.clothing.management.dto.DeliveryCompanyDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BarcodeStatusDTO {

    private List<String> barcodes;
}
