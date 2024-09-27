package com.clothing.management.dto.DeliveryCompanyDTOs;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.minidev.json.annotate.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
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
