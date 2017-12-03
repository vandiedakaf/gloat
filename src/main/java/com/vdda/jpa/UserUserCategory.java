package com.vdda.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class UserUserCategory {

	@EmbeddedId
	private UserUserCategoryPK userUserCategoryPK;
	@Column(nullable = false)
	private Integer wins;
	@Column(nullable = false)
	private Integer losses;
	@Column(nullable = false)
	private Integer draws;
	@Column(nullable = false)
	private Double wilson;

	protected UserUserCategory() {
		// no-contestArguments constructor required by JPA spec
		// this one is protected since it shouldn't be used directly
	}

	public UserUserCategory(UserUserCategoryPK userUserCategoryPK) {
		this.userUserCategoryPK = userUserCategoryPK;
		this.wins = 0;
		this.losses = 0;
		this.draws = 0;
		this.wilson = 0.0;
	}
}
