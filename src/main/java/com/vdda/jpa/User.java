package com.vdda.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * Created by francois on 2016-10-23 for
 * vandiedakaf solutions
 */
@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String teamId;
    @Column(nullable = false)
    private String userId;

    protected User() {
        // no-args constructor required by JPA spec
        // this one is protected since it shouldn't be used directly
    }

    public User(String teamId, String userId) {
        this.teamId = teamId;
        this.userId = userId;
    }
}
