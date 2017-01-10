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
    private Long elo;

    protected UserCategory() {
        // no-args constructor required by JPA spec
        // this one is protected since it shouldn't be used directly
    }

    public UserCategory(UserCategoryPK userCategoryPK, Long elo) {
        this.userCategoryPK = userCategoryPK;
        this.elo = elo;
    }
}
