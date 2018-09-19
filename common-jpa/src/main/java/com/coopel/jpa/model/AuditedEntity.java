package com.coopel.jpa.model;

public interface AuditedEntity {

    Integer getVersion();

    void setVersion(Integer version);
}
