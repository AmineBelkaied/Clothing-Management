package com.clothing.management.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


public class Address {

    private Long id;
    private Governorate governorate;
    private Delegation delegation;
    //private City city;
}
