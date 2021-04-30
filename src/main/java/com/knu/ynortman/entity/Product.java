package com.knu.ynortman.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.knu.ynortman.multitenancy.discriminator.entity.AbstractBaseEntity;

@Data 
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Product  {
    @Id
    private long id;
    private String name;
    private float price;
}
