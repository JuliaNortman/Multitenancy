package com.knu.ynortman.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.knu.ynortman.multitenancy.discriminator.entity.AbstractBaseEntity;

@Data 
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_details")
public class User /*extends AbstractBaseEntity*/ {
    @Id
    private long id;
    private String name;
}
