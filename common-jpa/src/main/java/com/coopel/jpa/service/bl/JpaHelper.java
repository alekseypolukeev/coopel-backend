package com.coopel.jpa.service.bl;

import com.coopel.common.exception.EntityNotFoundServiceException;
import com.coopel.common.exception.ValidationException;
import com.coopel.jpa.model.CommonEntity;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.domain.Persistable;

import javax.annotation.Nullable;
import java.util.function.Function;

import static com.coopel.common.exception.ValidationErrorTypes.FK_CONSTRAINT;

public class JpaHelper {

    private JpaHelper() {
    }

    @Nullable
    public static <R extends CommonEntity, E extends CommonEntity> R touchMTORelation(
            E entity,
            Function<E, R> relationExtractor,
            AbstractService<R> relationService,
            String fkParameterName
    ) {
        R parent = relationExtractor.apply(entity);
        if (parent != null && parent.getId() != null) {
            try {
                return relationService.findById(parent.getId(), true);
            } catch (EntityNotFoundServiceException e) {
                throw new ValidationException(e.getMessage(), FK_CONSTRAINT, fkParameterName);
            }
        }
        return null;
    }

    @Nullable
    public static Integer unwrapEntityId(@Nullable Persistable<Integer> entity) {
        if (entity == null) {
            return null;
        }
        if (entity instanceof HibernateProxy) {
            return (Integer) ((HibernateProxy) entity).getHibernateLazyInitializer().getIdentifier();
        }
        return entity.getId();
    }
}
