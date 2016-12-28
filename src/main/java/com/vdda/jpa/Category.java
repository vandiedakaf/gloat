package com.vdda.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by francois on 2016-10-23 for
 * vandiedakaf solutions
 */
@Entity
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue
    private Long id;
    private String teamId;
    private String channelId;

    // jpa constructor
    protected Category() {}

    public Category(String teamId, String channelId) {
        this.teamId= teamId;
        this.channelId = channelId;
    }
}
