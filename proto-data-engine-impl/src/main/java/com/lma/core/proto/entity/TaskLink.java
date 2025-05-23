package com.lma.core.proto.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name="links")
public class TaskLink {

    @EmbeddedId
    private TaskLinkId taskLinkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("taskId")
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("linkedId")
    @JoinColumn(name = "linked_id")
    private Task linkedTask;

    @Column(name="type_link")
    private String typeOfLink;

    public TaskLinkId getTaskLinkId() {
        return taskLinkId;
    }

    public void setTaskLinkId(TaskLinkId taskLinkId) {
        this.taskLinkId = taskLinkId;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Task getLinkedTask() {
        return linkedTask;
    }

    public void setLinkedTask(Task linkedTask) {
        this.linkedTask = linkedTask;
    }

    public String getTypeOfLink() {
        return typeOfLink;
    }

    public void setTypeOfLink(String typeOfLink) {
        this.typeOfLink = typeOfLink;
    }
}
