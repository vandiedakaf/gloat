package com.vdda.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * Created by francois on 2016-10-23 for
 * vandiedakaf solutions
 */
@Entity
@Getter
@Setter
public class UserCategory {

    @EmbeddedId
    private UserCategoryPK userCategoryPK;
    @Column(nullable = false)
    private Integer elo;
    @Column(nullable = false)
    private Integer wins;
    @Column(nullable = false)
    private Integer losses;
    @Column(nullable = false)
    private Integer draws;
    @Column(nullable = false)
    private Integer k;

    protected UserCategory() {
        // no-args constructor required by JPA spec
        // this one is protected since it shouldn't be used directly
    }

    public UserCategory(UserCategoryPK userCategoryPK) {
        this.userCategoryPK = userCategoryPK;
        this.elo = 0;
        this.wins = 0;
        this.losses = 0;
        this.draws = 0;
        this.k = 32;
    }
}
