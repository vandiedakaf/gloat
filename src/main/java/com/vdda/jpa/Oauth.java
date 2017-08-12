package com.vdda.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Oauth {
    @Id
    private String teamId;
    @Column(nullable = false)
    private String accessToken;

    protected Oauth() {
        // no-args constructor required by JPA spec
        // this one is protected since it shouldn't be used directly
    }

    public Oauth(String teamId, String accessToken) {
        this.teamId = teamId;
        this.accessToken = accessToken;
    }
}
