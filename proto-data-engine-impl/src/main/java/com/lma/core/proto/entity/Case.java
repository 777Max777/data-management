package com.lma.core.proto.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name="cases")
public class Case extends Base {

    @Column(name="description")
    private String description;

    @ManyToMany(mappedBy = "cases", cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    private Set<Task> tasks;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }
}
