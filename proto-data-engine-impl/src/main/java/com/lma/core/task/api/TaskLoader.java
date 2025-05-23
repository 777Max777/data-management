package com.lma.core.task.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lma.core.proto.entity.TaskLink;
import com.lma.core.proto.entity.TaskName;
import com.lma.core.proto.repository.TaskRepository;
import com.lma.core.task.impl.ExpressionTask;
import com.lma.core.task.impl.IfTask;
import com.lma.core.task.consumer.DefaultConsumers;
import com.lma.core.task.consumer.SwitchConsumers;
import com.lma.core.task.impl.RandomTask;
import com.lma.core.task.impl.collection.ForEachTask;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Component
public class TaskLoader {

    @Autowired
    private TaskRepository taskRepository;

    private ObjectMapper mapper = new ObjectMapper();

    public List<BiConsumer> load(Long caseId) {
        Set<com.lma.core.proto.entity.Task> dbTasks = taskRepository.findByCaseId(caseId);
        return getChildConsumers(dbTasks);
    }

    private void buildTaskNode(Task task, com.lma.core.proto.entity.Task dbTask, HierarchyLevelProperties properties) {
        Set<com.lma.core.proto.entity.Task> childDbTasks = dbTask.getChildTasks();
        List<TaskLink> linkedTasks = dbTask.getLinkedTasks();

        Consumers children = new DefaultConsumers(getChildConsumers(childDbTasks));
        Consumers neighbors = resolveConsumers(dbTask.getType(), linkedTasks, properties);

        TaskOutput output = new TaskOutput(neighbors, children);

        String body = dbTask.getMergeData();
        if (StringUtils.isNotBlank(body)) {
            Map<String, Object> masterData = null;
            try {
                masterData = mapper.readValue(body, new TypeReference<Map<String, Object>>() {});
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            if (MapUtils.isNotEmpty(masterData)) {
                task.setMergeData(new ArrayList<>(masterData.keySet()));
//                task.setAttrs(masterData);
            }
        }

        task.setOutput(output);
    }

    private List<BiConsumer> getChildConsumers(Collection<com.lma.core.proto.entity.Task> dbTasks) {
        List<BiConsumer> consumers = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dbTasks)) {
            for (com.lma.core.proto.entity.Task child : dbTasks) {
                Task t = createTask(child);
                HierarchyLevelProperties properties = new HierarchyLevelProperties(new HashMap<>());
                buildTaskNode(t, child, properties);
                consumers.add(t);
            }
        }
        return consumers;
    }

    private List<BiConsumer> getLinkConsumers(Collection<com.lma.core.proto.entity.Task> dbTasks, HierarchyLevelProperties properties) {
        List<BiConsumer> consumers = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dbTasks)) {
            for (com.lma.core.proto.entity.Task link : dbTasks) {
                Map<Task,com.lma.core.proto.entity.Task> mapTasks = properties.getMapTasks();
                Optional<Map.Entry<Task,com.lma.core.proto.entity.Task>> entryOptional = mapTasks.entrySet().stream()
                        .filter(entry -> entry.getValue().equals(link.getId()))
                        .findFirst();
                if (entryOptional.isPresent()) {
                    consumers.add(entryOptional.get().getKey());
                    if (entryOptional.get().getValue().getSynchronous()) {
                        entryOptional.get().getKey().incrementUsage();
                    }
                    continue;
                }

                Task t = createTask(link);
                mapTasks.put(t, link);
                buildTaskNode(t, link, properties);
                consumers.add(t);
            }
        }
        return consumers;
    }

    private Task createTask(com.lma.core.proto.entity.Task dbTask) {
        Task task = createTaskByType(dbTask);

        try {
            if (StringUtils.isNotBlank(dbTask.getInitData())) {
                Map<String, List<String>> initData = mapper.readValue(dbTask.getInitData(), new TypeReference<Map<String, List<String>>>() {});
                task.setInitValues(initData);
            }
            if (StringUtils.isNotBlank(dbTask.getRemoveData())) {
                List<String> removeData = mapper.readValue(dbTask.getRemoveData(), new TypeReference<List<String>>() {});
                task.setRemoveData(removeData);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return task;
    }

    private Task createTaskByType(com.lma.core.proto.entity.Task dbTask) {
        String jsonData = dbTask.getTaskData();
        TaskName taskName = dbTask.getType();

        if (taskName != null) {
            try {
                switch (taskName) {
                    case IF: {
                        return StringUtils.isNotBlank(jsonData) ? mapper.readValue(jsonData, IfTask.class) : new Task();
                    }
                    case EXPRESSION: {
                        return StringUtils.isNotBlank(jsonData) ? mapper.readValue(jsonData, ExpressionTask.class) : new Task();
                    }
                    case RANDOM: {
                        return StringUtils.isNotBlank(jsonData) ? mapper.readValue(jsonData, RandomTask.class) : new Task();
                    }
                    case FOR_EACH: {
                        return StringUtils.isNotBlank(jsonData) ? mapper.readValue(jsonData, ForEachTask.class) : new ForEachTask();
                    }
                    default: {
                        return new Task();
                    }
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return new Task();
    }

    private Consumers resolveConsumers(TaskName type, List<TaskLink> linkedTasks, HierarchyLevelProperties properties) {
        if (CollectionUtils.isNotEmpty(linkedTasks)) {
            if (type != null
                    && (
                    type.equals(TaskName.IF)
                            || type.equals(TaskName.SWITCH))) {

                Map<String, List<com.lma.core.proto.entity.Task>> linkTypedTasks = linkedTasks.stream()
                        .collect(
                                Collectors.groupingBy(TaskLink::getTypeOfLink,
                                        Collectors.mapping(TaskLink::getLinkedTask, Collectors.toList())));
                Map<String, List<BiConsumer>> consumerChannels = linkTypedTasks.entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                (entry) -> getLinkConsumers(entry.getValue(), properties))
                        );
                return new SwitchConsumers(consumerChannels);
            } else {
                List<BiConsumer> consumers = getLinkConsumers(linkedTasks.stream()
                        .map(link -> link.getLinkedTask())
                        .collect(Collectors.toList()), properties);
                return new DefaultConsumers(consumers);
            }
        }
        return new DefaultConsumers();
    }

    public class HierarchyLevelProperties {
        private Map<Task, com.lma.core.proto.entity.Task> mapTasks;

        public HierarchyLevelProperties(Map<Task, com.lma.core.proto.entity.Task> mapTasks) {
            this.mapTasks = mapTasks;
        }

        public Map<Task, com.lma.core.proto.entity.Task> getMapTasks() {
            return mapTasks;
        }

        public void setMapTasks(Map<Task, com.lma.core.proto.entity.Task> mapTasks) {
            this.mapTasks = mapTasks;
        }
    }
}
