package com.clothing.management.dto;

import com.clothing.management.entities.Color;
import com.clothing.management.entities.Size;

import java.util.List;

public class ModelDTO {

    private Long id;
    private String name;
    private String reference;
    private List<Color> colors;
    private List<Size> sizes;
}
