package com.coopel.auth.service;

import com.coopel.auth.model.Cooperative;
import com.coopel.auth.repository.CooperativeRepository;
import com.coopel.jpa.service.bl.AbstractService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class CooperativeService extends AbstractService<Cooperative> {

    private final CooperativeRepository cooperativeRepository;

    @Inject
    public CooperativeService(CooperativeRepository cooperativeRepository) {
        this.cooperativeRepository = cooperativeRepository;
    }

    @Override
    protected Cooperative executeCreate(Cooperative newEntity) {
        return cooperativeRepository.save(newEntity);
    }

    @Override
    protected void executeUpdate(Cooperative merged, Cooperative update) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected JpaRepository<Cooperative, Integer> getRepository() {
        return cooperativeRepository;
    }
}
