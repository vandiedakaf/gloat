package com.vdda.jpa;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by francois on 2016-10-23.
 */
@Entity
@Data
public class Category {
    @Id
    private Long id;

    protected Category() {}

    public Category(Long id) {
        this.id = id;
    }
}
