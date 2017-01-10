package com.vdda.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
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
    @Column(nullable = false)
    private String teamId;
    @Column(nullable = false)
    private String channelId;

    protected Category() {
        // no-args constructor required by JPA spec
        // this one is protected since it shouldn't be used directly
    }

    public Category(String teamId, String channelId) {
        this.teamId = teamId;
        this.channelId = channelId;
    }
}
