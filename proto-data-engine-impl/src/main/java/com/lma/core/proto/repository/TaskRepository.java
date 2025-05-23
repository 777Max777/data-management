package com.lma.core.proto.repository;

import com.lma.core.proto.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query(nativeQuery = true, value = "select t.* from tasks t, map_case_task map where map.case_id = :caseId and map.task_id = t.id")
    Set<Task> findByCaseId(@Param("caseId") Long caseId);
}
