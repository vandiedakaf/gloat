package com.vdda.jpa;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class UserCategoryPK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    private Long userId;
    private Long categoryId;

    protected UserCategoryPK() {
        // no-args constructor required by JPA spec
        // this one is protected since it shouldn't be used directly
    }

    public UserCategoryPK(Long userId, Long categoryId) {
        this.userId = userId;
        this.categoryId = categoryId;
    }



}
