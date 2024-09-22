package com.clothing.management.dto.DeliveryCompanyDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryResponseNavex extends DeliveryResponse {

    int status;
    String lien;
    String status_message;
    String etat;
    @JsonIgnore
    int responseCode;
    @JsonIgnore
    String responseMessage;
}
