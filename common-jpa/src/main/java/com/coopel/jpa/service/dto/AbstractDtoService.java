package com.coopel.jpa.service.dto;

import com.coopel.common.exception.EntityNotFoundServiceException;
import com.coopel.jpa.model.CommonEntity;
import com.coopel.jpa.service.bl.AbstractService;
import org.dozer.Mapper;

import javax.annotation.Nullable;

public abstract class AbstractDtoService<P extends CommonEntity, D> {

    protected final Mapper mapper;

    protected AbstractDtoService(Mapper mapper) {
        this.mapper = mapper;
    }

    /**
     * @return entity if version changed, {@code null} if not changed
     * and {@link EntityNotFoundServiceException} if specified entity not found
     */
    @Nullable
    public D findByIdIfChanged(int id, int version, boolean hideArchived) {
        P entity = getService().findByIdIfChanged(id, version, hideArchived);
        return entity == null ? null : toDto(entity);
    }

    public D findById(int id, boolean hideArchived) {
        return toDto(getService().findById(id, hideArchived));
    }

    public D create(D persistentDto) {
        return toDto(getService().create(toEntity(persistentDto)));
    }

    public D update(int id, D persistentDto) {
        return toDto(getService().update(id, toEntity(persistentDto)));
    }

    public D archive(int id) {
        return toDto(getService().archive(id));
    }

    public D unArchive(int id) {
        return toDto(getService().unArchive(id));
    }

    public P toEntity(D dto) {
        return mapper.map(dto, getEntityClass());
    }

    public D toDto(P entity) {
        return mapper.map(entity, getDtoClass());
    }

    protected abstract AbstractService<P> getService();

    protected abstract Class<P> getEntityClass();

    protected abstract Class<D> getDtoClass();

}
