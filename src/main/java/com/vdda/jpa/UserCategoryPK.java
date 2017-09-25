package com.vdda.jpa;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class UserCategoryPK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private Long categoryId;

    protected UserCategoryPK() {
        // no-contestArguments constructor required by JPA spec
        // this one is protected since it shouldn't be used directly
    }

    public UserCategoryPK(User user, Long categoryId) {
        this.user = user;
        this.categoryId = categoryId;
    }
}
