package com.lma.core.service;

import com.lma.core.proto.entity.Task;
import com.lma.core.proto.repository.TaskRepository;
import com.lma.core.proto.service.ProtoDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TaskService extends ProtoDataService<Task> {

    @Autowired
    private TaskRepository taskRepository;

    @PostConstruct
    @Override
    protected void postConstructInit() {
        super.setJpaRepository(taskRepository);
    }

    public Task getTaskById(Long id) {
        Optional<Task> task = taskRepository.findById(id);
        return task.isPresent() ? task.get() : null;
    }

    public Set<Task> getTasksByCaseId(Long caseId) {
        Set<Task> tasks = taskRepository.findByCaseId(caseId);
        return tasks;
    }
}
