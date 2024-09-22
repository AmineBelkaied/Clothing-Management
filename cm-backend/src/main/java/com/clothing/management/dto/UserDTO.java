package com.clothing.management.dto;

import com.clothing.management.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Integer userId;
    private String fullName;
    private String userName;
    private boolean enabled;
    private Set<Role> roles  = new HashSet<>();
}
