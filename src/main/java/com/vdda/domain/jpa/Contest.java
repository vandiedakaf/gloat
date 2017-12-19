package com.vdda.domain.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

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
    @JoinColumn(name = "reporter_id")
    private User reporter;
    @ManyToOne
    @JoinColumn(name = "opponent_id")
    private User opponent;
    @Column(nullable = false)
    @Convert(converter = ContestOutcomeConverter.class)
    private ContestOutcome contestOutcome;
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

    public Contest(Category category, User reporter, User opponent, ContestOutcome contestOutcome) {
        this.category = category;
        this.reporter = reporter;
        this.opponent = opponent;
        this.processed = false;
        this.contestOutcome = contestOutcome;
    }
}

