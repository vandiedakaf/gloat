package com.vdda.jpa;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by francois on 2016-10-23 for
 * vandiedakaf solutions
 */
@Entity
@Getter
@Setter
public class Contest {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "winner_id")
    private User winner;
    @ManyToOne
    @JoinColumn(name = "loser_id")
    private User loser;
    @Column(nullable = false)
    private Boolean processed;
    @Basic(optional = false)
    @Column(insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    protected Contest() {
        // no-args constructor required by JPA spec
        // this one is protected since it shouldn't be used directly
    }

    public Contest(Category category, User winner, User loser) {
        this.category = category;
        this.winner = winner;
        this.loser = loser;
        this.processed = false;
    }
}

