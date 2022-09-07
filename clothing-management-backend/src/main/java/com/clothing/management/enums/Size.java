package com.clothing.management.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Size {
   S, M, L, XL, XXL , XXXL , XXXXL , XXXXXL , XXXXXXL;

   public static List<Size> getEnumValues(){
      return new ArrayList<>(Arrays.asList(values()));
   }
}
