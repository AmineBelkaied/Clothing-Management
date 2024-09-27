package com.clothing.management.dto;

import com.clothing.management.entities.User;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductHistoryDTO {

    private  long id;
    private String description;
    private long productId;
    private long quantity;
    private Date date;
    @Nullable
    private User user;
    private String comment;
}
