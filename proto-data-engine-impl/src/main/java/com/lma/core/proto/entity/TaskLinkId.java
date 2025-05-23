package com.lma.core.proto.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class TaskLinkId implements Serializable {

    @Column(name="task_id")
    private Long taskId;

    @Column(name="linked_id")
    private Long linkedId;

    public Long getTaskId() {
        return taskId;
    }

    public Long getLinkedId() {
        return linkedId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public void setLinkedId(Long linkedId) {
        this.linkedId = linkedId;
    }
}
