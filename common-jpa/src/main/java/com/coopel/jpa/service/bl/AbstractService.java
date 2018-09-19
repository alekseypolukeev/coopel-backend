package com.coopel.jpa.service.bl;

import com.coopel.common.exception.BadRequestServiceException;
import com.coopel.common.exception.EntityNotFoundServiceException;
import com.coopel.common.exception.ModificationConflictServiceException;
import com.coopel.jpa.model.CommonEntity;
import com.coopel.jpa.service.validation.StringFieldValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.Objects;

public abstract class AbstractService<E extends CommonEntity> {

    private static final Logger log = LoggerFactory.getLogger(AbstractService.class);

    private final StringFieldValidationService<E> validationService = new StringFieldValidationService<>();

    private static void throwEntityNotFound(int id, boolean hideArchived) {
        throw new EntityNotFoundServiceException(String.format(
                "Id %d not found %s.", id,
                hideArchived ? "(except archived)" : "(including archived)"
        ));
    }

    /**
     * @return entity if version changed, {@code null} if not changed
     * and {@link EntityNotFoundServiceException} if specified entity not found
     */
    @Nullable
    public E findByIdIfChanged(int id, int version, boolean hideArchived) {
        E result = findById(id, hideArchived);
        if (Objects.equals(result.getVersion(), version)) {
            return null;
        }
        return result;
    }

    @Transactional
    public E findById(int id, boolean hideArchived) {
        JpaRepository<E, Integer> repository = getRepository();
        E result = repository.findById(id).orElse(null);
        if (result == null || hideArchived && result.getArchived()) {
            throwEntityNotFound(id, hideArchived);
        }
        return result;
    }

    @Transactional
    public E create(E newEntity) {
        if (!newEntity.isNew()) {
            throw new BadRequestServiceException("Use update for existing entity");
        }
        if (newEntity.getVersion() != null) {
            throw new BadRequestServiceException("Version can't be specified");
        }
        validate(null, newEntity, true);
        newEntity.setArchived(false);
        return executeCreate(newEntity);
    }

    private void validate(E merged, E update, boolean create) {
        validationService.validate(update);
        executeValidate(merged, update, create);
    }

    /**
     * If {@code create == true} then {@code merged} is null and {@code update} is new entity
     */
    protected void executeValidate(E merged, E update, boolean create) {
    }

    protected abstract E executeCreate(E newEntity);

    public E update(int id, E update) {
        return update(id, update, null);
    }

    @Transactional
    public E update(int id, E update, UpdateValidator<E> validator) {
        if (!Objects.equals(id, update.getId())) {
            throw new BadRequestServiceException(String.format(
                    "Id specified by path (%s) don't equal id in entity (%s)",
                    id, update.getId()
            ));
        }
        Integer oldVersion = update.getVersion();
        if (oldVersion == null) {
            throw new BadRequestServiceException("Version must be specified for update");
        }
        E merged = findById(id, true);
        if (!Objects.equals(oldVersion, merged.getVersion())) {
            throw new ModificationConflictServiceException();
        }
        validate(merged, update, false);
        if (validator != null) {
            validator.validate(merged, update);
        }
        executeUpdate(merged, update);
        return merged;
    }

    protected abstract void executeUpdate(E merged, E update);

    @Transactional
    public E archive(int id) {
        E result = findById(id, true);
        result.setArchived(true);
        return result;
    }

    @Transactional
    public E unArchive(int id) {
        E result = findById(id, false);
        result.setArchived(false);
        return result;
    }

    public void delete(int id) {
        getRepository().deleteById(id);
    }

    protected abstract JpaRepository<E, Integer> getRepository();

}
