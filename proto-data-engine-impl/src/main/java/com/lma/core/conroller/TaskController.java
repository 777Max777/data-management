package com.lma.core.conroller;

import com.lma.core.builder.CommonBuilder;
import com.lma.core.dto.TaskDto;
import com.lma.core.proto.controller.ProtoDataCRUDController;
import com.lma.core.proto.entity.Case;
import com.lma.core.proto.entity.Task;
import com.lma.core.proto.entity.TaskLink;
import com.lma.core.proto.entity.TaskLinkId;
import com.lma.core.service.TaskService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/task")
public class TaskController extends ProtoDataCRUDController<TaskDto> {

    @Autowired
    private TaskService taskService;

    @PostConstruct
    @Override
    protected void postConstructInit() {
        super.setProtoDataService(taskService);
    }

    @RequestMapping(value="/id/{id}", method=RequestMethod.GET)
    public ResponseEntity getTaskById(@PathVariable("id") Long id) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return new ResponseEntity("There are no any task by id = " + id, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(usuallyConvertDto(task), HttpStatus.OK);
    }

    @RequestMapping(value="/case/{id}", method=RequestMethod.GET)
    public ResponseEntity getTasksByCaseId(@PathVariable("id") Long caseId) {
        Set<Task> tasks = taskService.getTasksByCaseId(caseId);
        if (tasks == null) {
            return new ResponseEntity("There are no any task by case id = " + caseId, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(tasks.stream()
                    .map(this::usuallyConvertDto)
                    .collect(Collectors.toList()),
                HttpStatus.OK);
    }

    @Override
    protected Object convertPostDto(Object createdData) {
        return usuallyConvertDto((Task) createdData);
    }

    @Override
    protected Object convertPutDto(Object updatedData) {
        return usuallyConvertDto((Task) updatedData);
    }

    @Override
    protected Object defaultConvert(Object object) {
        return usuallyConvertDto((TaskDto) object);
    }

    private Object usuallyConvertDto(Task task) {
        return CommonBuilder.of(TaskDto::new)
                .with(TaskDto::setId, task.getId())
                .with(TaskDto::setName, task.getName())
                .with(TaskDto::setMergeData, task.getMergeData())
                .with(TaskDto::setInitData, task.getInitData())
                .with(TaskDto::setRemoveData, task.getRemoveData())
                .with(TaskDto::setTaskData, task.getTaskData())
                .with(TaskDto::setSynchronous, task.getSynchronous())
                .with(TaskDto::setDescription, task.getDescription())
                .with(TaskDto::setType, task.getType())
                .with(TaskDto::setCaseIds, CollectionUtils.isNotEmpty(task.getCases())
                        ? task.getCases()
                            .stream()
                            .map(Case::getId)
                            .collect(Collectors.toList())
                        : null)
                .with(TaskDto::setLinks, CollectionUtils.isNotEmpty(task.getLinkedTasks())
                        ? task.getLinkedTasks()
                            .stream()
                            .collect(
                                    Collectors.groupingBy(TaskLink::getTypeOfLink,
                                            Collectors.mapping(TaskLink::getLinkedTask, Collectors.toList())))
                            .entrySet()
                            .stream()
                            .map(entry -> Pair.of(entry.getKey(), entry.getValue().stream().map(t -> t.getId()).collect(Collectors.toList())))
                            .collect(Collectors.toMap(Pair::getLeft, Pair::getRight))
                        : null)
                .with(TaskDto::setParentIds, CollectionUtils.isNotEmpty(task.getParentTasks())
                        ? task.getParentTasks()
                            .stream()
                            .map(Task::getId)
                            .collect(Collectors.toList())
                        : null)
                .build();
    }

    private Object usuallyConvertDto(TaskDto task) {
        return CommonBuilder.of(Task::new)
                .with(Task::setId, task.getId())
                .with(Task::setName, task.getName())
                .with(Task::setMergeData, task.getMergeData())
                .with(Task::setInitData, task.getInitData())
                .with(Task::setRemoveData, task.getRemoveData())
                .with(Task::setTaskData, task.getTaskData())
                .with(Task::setSynchronous, task.getSynchronous())
                .with(Task::setDescription, task.getDescription())
                .with(Task::setType, task.getType())
                .with(Task::setCases, CollectionUtils.isNotEmpty(task.getCaseIds())
                        ? task.getCaseIds()
                            .stream()
                            .map(id -> {
                                Case c = new Case();
                                c.setId(id);
                                return c;
                            })
                            .collect(Collectors.toSet())
                        : null)
                .with(Task::setLinkedTasks, MapUtils.isNotEmpty(task.getLinks())
                        ? task.getLinks()
                            .entrySet()
                            .stream()
                            .map(entry -> {
                                String typeOfLink = StringUtils.isBlank(entry.getKey()) ? "" : entry.getKey();
                                List<TaskLink> links = entry.getValue().stream()
                                        .map(id -> CommonBuilder.of(TaskLink::new)
                                                .with(TaskLink::setTaskLinkId, CommonBuilder.of(TaskLinkId::new)
                                                        .with(TaskLinkId::setTaskId, task.getId())
                                                        .with(TaskLinkId::setLinkedId, id)
                                                        .build())
                                                .with(TaskLink::setTask, CommonBuilder.of(Task::new)
                                                        .with(Task::setId, task.getId())
                                                        .build())
                                                .with(TaskLink::setLinkedTask, CommonBuilder.of(Task::new)
                                                        .with(Task::setId, id)
                                                        .build())
                                                .with(TaskLink::setTypeOfLink, typeOfLink)
                                                .build())
                                        .collect(Collectors.toList());
                                return links;
                            })
                            .flatMap(s -> s.stream())
                            .collect(Collectors.toList())
                        : null)
                .with(Task::setParentTasks, CollectionUtils.isNotEmpty(task.getParentIds())
                        ? task.getParentIds()
                            .stream()
                            .map(id -> CommonBuilder.of(Task::new)
                                    .with(Task::setId, id)
                                    .build())
                            .collect(Collectors.toSet())
                        : null)
                .build();
    }
}
