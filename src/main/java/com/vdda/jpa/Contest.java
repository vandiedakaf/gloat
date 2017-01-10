package com.vdda.jpa;

import lombok.Getter;
import lombok.Setter;

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
    @Column(nullable = false)
    private Long categoryId;
    @Column(nullable = false)
    private Long winnerId;
    @Column(nullable = false)
    private Long loserId;
    @Basic(optional = false)
    @Column(insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    protected Contest() {
        // no-args constructor required by JPA spec
        // this one is protected since it shouldn't be used directly
    }

    public Contest(Long categoryId, Long winnerId, Long loserId) {
        this.categoryId = categoryId;
        this.winnerId = winnerId;
        this.loserId = loserId;
    }
}

