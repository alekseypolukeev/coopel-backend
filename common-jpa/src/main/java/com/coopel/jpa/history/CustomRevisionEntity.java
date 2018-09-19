package com.coopel.jpa.history;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.ModifiedEntityNames;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@Entity(name = "revinfo")
@RevisionEntity(CustomRevisionListener.class)
public class CustomRevisionEntity extends AbstractPersistable<Long> {

    @RevisionTimestamp
    private long timestamp;

    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "REVCHANGES", joinColumns = @JoinColumn(name = "REV"))
    @Column(name = "ENTITYNAME")
    @Fetch(FetchMode.JOIN)
    @ModifiedEntityNames
    private Set<String> modifiedEntityNames = new HashSet<String>();

    @Column(nullable = false)
    private Integer userId;

    @Override
    @RevisionNumber
    public Long getId() {
        return super.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CustomRevisionEntity that = (CustomRevisionEntity) o;
        return timestamp == that.timestamp &&
                Objects.equals(modifiedEntityNames, that.modifiedEntityNames) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), timestamp, modifiedEntityNames, userId);
    }
}
