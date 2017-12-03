package com.vdda.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

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
	@Column(nullable = false)
	private Integer streakCount;
    @Convert(converter = ContestOutcomeConverter.class)
    private ContestOutcome streakType;

    protected UserCategory() {
        // no-contestArguments constructor required by JPA spec
        // this one is protected since it shouldn't be used directly
    }

    public UserCategory(UserCategoryPK userCategoryPK) {
        this.userCategoryPK = userCategoryPK;
        this.elo = 0;
        this.wins = 0;
        this.losses = 0;
        this.draws = 0;
        this.k = 32;
        this.streakCount = 0;
        this.streakType = ContestOutcome.DRAW;
    }
}
