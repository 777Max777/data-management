package com.lma.core.service;

import com.lma.core.proto.entity.Case;
import com.lma.core.proto.repository.CaseRepository;
import com.lma.core.proto.service.ProtoDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class CaseService extends ProtoDataService<Case> {

    @Autowired
    private CaseRepository caseRepository;

    @Override
    @PostConstruct
    protected void postConstructInit() {
        super.setJpaRepository(caseRepository);
    }
}
