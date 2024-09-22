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
public class DeliveryResponse {

    @JsonIgnore
    public int responseCode;
    @JsonIgnore
    public String responseMessage;

    public int status;

    public String link;

    public String state;

    public String barCode;

    public Boolean isError;

    public String message;
    public DeliveryResponseFirst.Result result;
}