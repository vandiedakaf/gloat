package com.vdda.domain.jpa;

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
public class UserUserCategoryPK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
	@ManyToOne
    @JoinColumn(name = "opponent_id")
    private User opponent;
    private Long categoryId;

    protected UserUserCategoryPK() {
        // no-contestArguments constructor required by JPA spec
        // this one is protected since it shouldn't be used directly
    }

    public UserUserCategoryPK(User user, User opponent, Long categoryId) {
        this.user = user;
        this.opponent = opponent;
        this.categoryId = categoryId;
    }
}
