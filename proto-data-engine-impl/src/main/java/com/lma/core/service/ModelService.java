package com.lma.core.service;

import com.lma.core.proto.entity.Entity;
import com.lma.core.proto.repository.EntityRepository;
import com.lma.core.proto.service.ProtoDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class ModelService extends ProtoDataService<Entity> {

    @Autowired
    private EntityRepository entityRepository;

    @PostConstruct
    @Override
    protected void postConstructInit() {
        super.setJpaRepository(entityRepository);
    }

    public List<Entity> getAllModels() {
        return entityRepository.findAll();
    }
}
