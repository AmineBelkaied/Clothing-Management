package com.clothing.management.dto;

import com.clothing.management.entities.User;
import jakarta.annotation.Nullable;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user"})
public class ProductHistoryDTO {
    private long id;
    private long productId;
    private String description;
    private long modelId;
    private long quantity;
    private Date date;
    @Nullable
    private User user;
    private String comment;
}
