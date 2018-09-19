package com.coopel.jpa.model;

import org.springframework.data.domain.Persistable;

public interface CommonEntity extends ArchivedEntity, AuditedEntity, Persistable<Integer> {
}
