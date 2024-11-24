package com.clothing.management.models;

import com.clothing.management.dto.ModelDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ModelSaveResponse {

    private ModelDTO modelDTO;
    private String errors;
    private boolean success;

}
