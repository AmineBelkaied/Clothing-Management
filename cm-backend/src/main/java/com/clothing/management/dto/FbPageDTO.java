package com.clothing.management.dto;

import com.clothing.management.entities.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FbPageDTO {
    private Long id;

    private String name;
    public FbPageDTO() {
    }
    public FbPageDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    public FbPageDTO(FbPage fbPage) {
        this.id = fbPage.getId();
        this.name = fbPage.getName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
