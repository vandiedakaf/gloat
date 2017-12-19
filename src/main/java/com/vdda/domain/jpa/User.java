package com.vdda.domain.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Getter
@Setter
public class User implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String teamId;
    @Column(nullable = false)
    private String userId;

    protected User() {
        // no-contestArguments constructor required by JPA spec
        // this one is protected since it shouldn't be used directly
    }

    public User(String teamId, String userId) {
        this.teamId = teamId;
        this.userId = userId;
    }
}
