package com.lma.core.proto.repository;

import com.lma.core.proto.entity.FieldInstanceRelations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationRepository extends JpaRepository<FieldInstanceRelations, Long> {

    @Modifying
    @Query(value = "update relations set relation_id where instance_id = :instance_id and relation_id = :relation_id", nativeQuery = true)
    int updateRelation(@Param("instance_id") String instance_id, @Param("relation_id") String relation_id);

    void deleteAllById_InstanceIdAndId_RelatedFieldIdAndId_RelationId(Long instanceId, Long fieldId, Long relationId);

    void deleteAllById_InstanceId(Long instanceId);

    @Query("select complexId from FieldInstanceRelations complexId where complexId.id.instance.id = :instanceId and complexId.id.relatedField.id in :relatedFieldIds")
    List<FieldInstanceRelations> findAllByInstanceRelFields(@Param("instanceId") Long instanceId, @Param("relatedFieldIds")Long... relatedFieldIds);
}
