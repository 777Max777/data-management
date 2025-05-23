package com.lma.core.proto.repository;

import com.lma.core.proto.entity.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldRepository extends JpaRepository<Field, Long> {

    @Query(value = "select f.* " +
            "from fields f," +
            "   mappings m " +
            "where m.entity_id = :entityId " +
            "and m.field_id = f.id", nativeQuery = true)
    List<Field> loadFieldsByEntityId(@Param("entityId") Long entityId);
}
