package com.lma.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.lma.core.proto.entity.Base;
import com.lma.core.proto.entity.TaskName;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDto extends Base {

    @JsonRawValue
    private String mergeData;
    @JsonRawValue
    private String initData;
    @JsonRawValue
    private String taskData;
    @JsonRawValue
    private String removeData;

    private TaskName type;
    private Boolean isSynchronous;
    private String description;
    private List<Long> caseIds;

    private List<Long> parentIds;
    private Map<String, List<Long>> links;

    @JsonSetter("mergeData")
    public void setMergeData(JsonNode mergeData) {
        this.mergeData = mergeData.toString();
    }

    @JsonSetter("initData")
    public void getInitData(JsonNode initData) {
        this.initData = initData.toString();
    }

    @JsonSetter("taskData")
    public void setTaskData(JsonNode taskData) {
        this.taskData = taskData.toString();
    }

    @JsonSetter("removeData")
    public void setRemoveData(JsonNode removeData) {
        this.removeData = removeData.toString();
    }

    public String getMergeData() {
        return mergeData;
    }

    public void setMergeData(String mergeData) {
        this.mergeData = mergeData;
    }

    public String getInitData() {
        return initData;
    }

    public void setInitData(String initData) {
        this.initData = initData;
    }

    public String getTaskData() {
        return taskData;
    }

    public void setTaskData(String taskData) {
        this.taskData = taskData;
    }

    public String getRemoveData() {
        return removeData;
    }

    public void setRemoveData(String removeData) {
        this.removeData = removeData;
    }

    public TaskName getType() {
        return type;
    }

    public void setType(TaskName type) {
        this.type = type;
    }

    public Boolean getSynchronous() {
        return isSynchronous;
    }

    public void setSynchronous(Boolean synchronous) {
        isSynchronous = synchronous;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getCaseIds() {
        return caseIds;
    }

    public void setCaseIds(List<Long> caseIds) {
        this.caseIds = caseIds;
    }

    public List<Long> getParentIds() {
        return parentIds;
    }

    public void setParentIds(List<Long> parentIds) {
        this.parentIds = parentIds;
    }

    public Map<String, List<Long>> getLinks() {
        return links;
    }

    public void setLinks(Map<String, List<Long>> links) {
        this.links = links;
    }
}
