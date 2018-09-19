package com.coopel.jpa.history;

import com.coopel.common.context.Identity;
import com.coopel.common.context.IdentityContextHolder;
import org.hibernate.envers.RevisionListener;

public class CustomRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        CustomRevisionEntity entity = (CustomRevisionEntity) revisionEntity;
        Identity identity = IdentityContextHolder.getInstance().getIdentity();
        // case when non-rest request (context filter doesn't work)
        int userId = identity != null ? identity.getUserId() : Identity.NOT_AUTHENTICATED_USER_ID;
        entity.setUserId(userId);
    }
}
