package com.lma.core.service;

import com.lma.core.proto.entity.Instance;
import com.lma.core.proto.repository.InstanceRepository;
import com.lma.core.proto.service.ProtoDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InstanceService extends ProtoDataService<Instance> {

    @Autowired
    private InstanceRepository instanceRepository;

    @Override
    protected void postConstructInit() {
        super.setJpaRepository(instanceRepository);
    }

    public Optional<Instance> findById(Long id) {
        return instanceRepository.findById(id);
    }
}
