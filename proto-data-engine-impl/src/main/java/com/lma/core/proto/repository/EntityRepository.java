package com.lma.core.proto.repository;

import com.lma.core.proto.entity.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntityRepository extends JpaRepository<Entity, Long> {

    List<Entity> findAllByParentId(Long parentId);
}
