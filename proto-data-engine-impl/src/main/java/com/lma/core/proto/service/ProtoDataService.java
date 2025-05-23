package com.lma.core.proto.service;

import com.lma.core.proto.entity.Base;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public abstract class ProtoDataService<S extends Base> {

    private JpaRepository jpaRepository;

    protected abstract void postConstructInit();

    public S createProtoData(S newProtoData) {
        return saveProtoData(newProtoData);
    }

    public List<S> createBatchProtoData(List<S> newBatchProtoData) {
        return saveBatchProtoData(newBatchProtoData);
    }

    public S updateProtoData(S updatingProtoData) {
        return saveProtoData(updatingProtoData);
    }

    public List<S> updateBatchProtoData(List<S> updatingBatchProtoData) {
        return saveBatchProtoData(updatingBatchProtoData);
    }

    public void deleteProtoData(S protoData) {
        jpaRepository.delete(protoData);
    }

    public void deleteBatchProtoData(List<S> batchProtoData) {
        jpaRepository.deleteInBatch(batchProtoData);
    }

    private S saveProtoData(S protoData) {
        return (S) jpaRepository.save(protoData);
    }

    private List<S> saveBatchProtoData(List<S> batchProtoData) {
        return jpaRepository.saveAll(batchProtoData);
    }

    public JpaRepository getJpaRepository() {
        return jpaRepository;
    }

    public void setJpaRepository(JpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
}
