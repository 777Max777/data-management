package com.lma.core.proto.repository;

import com.lma.core.proto.entity.Instance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Repository
public interface InstanceRepository extends JpaRepository<Instance, Long> {

    @Query(value = "select i.* " +
            "from " +
            "   fields f, " +
            "   instances i " +
            "where f.id = :fieldId " +
            "and f.rel_entity_id = i.entity_id " +
            "LIMIT :limit", nativeQuery = true)
    List<Instance> loadRelInstancesByFieldId(@Param("fieldId") Long fieldId, @Param("limit") Long limit);

    @Query(value = "select i.* " +
            "from " +
            "   fields f, " +
            "   instances i " +
            "where f.id = :fieldId " +
            "and f.rel_entity_id = i.entity_id " +
            "and i.name like :param", nativeQuery = true)
    List<Instance> loadFilteredRelInstancesByFieldId(@Param("fieldId") Long fieldId, @Param("param") String param);

    List<Instance> findAllByEntityId(Long entityId);
}