package com.coopel.jpa.model;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@Audited
@MappedSuperclass
public abstract class AbstractEntity extends AbstractPersistable<Integer> implements CommonEntity {

    @Column(nullable = false)
    private Boolean archived;

    @Version
    @Column(nullable = false)
    private Integer version;

    @Override
    public Integer getVersion() {
        return version;
    }

    @Override
    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public Boolean getArchived() {
        return archived;
    }

    @Override
    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    // change modifier
    @Override
    public void setId(Integer id) {
        super.setId(id);
    }
}
