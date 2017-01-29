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
    private Integer contestTotal;
    @Column(nullable = false)
    private Integer contestWins;
    @Column(nullable = false)
    private Integer k;

    protected UserCategory() {
        // no-args constructor required by JPA spec
        // this one is protected since it shouldn't be used directly
    }

    public UserCategory(UserCategoryPK userCategoryPK, Integer elo) {
        this.userCategoryPK = userCategoryPK;
        this.elo = elo;
        this.contestTotal = 0;
        this.contestWins = 0;
        this.k = 0;
    }
}
