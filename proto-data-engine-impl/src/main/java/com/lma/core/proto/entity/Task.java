package com.lma.core.proto.entity;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="tasks")
@TypeDef(name = "enumType", typeClass = PostgreSQLEnumType.class)
@TypeDef(
        name="jsonb",
        typeClass= JsonBinaryType.class
)
public class Task extends Base {

    @Type(type="jsonb")
    @Column(name="merge_data")
    private String mergeData;

    @Type(type="jsonb")
    @Column(name="init_data")
    private String initData;

    @Type(type="jsonb")
    @Column(name="task_data")
    private String taskData;

    @Type(type="jsonb")
    @Column(name="remove_data")
    private String removeData;

    @Enumerated(EnumType.STRING)
    @Column(name="type")
    @Type(type = "enumType")
    private TaskName type;

    @Column(name="is_synchronous")
    private Boolean isSynchronous;

    @Column(name="description")
    private String description;

    @ManyToMany
    @JoinTable(name="map_case_task",
            joinColumns=@JoinColumn(name ="task_id"),
            inverseJoinColumns=@JoinColumn(name ="case_id"))
    private Set<Case> cases;

    @ManyToMany
    @JoinTable(name="map_hierarchy_task",
            joinColumns=@JoinColumn(name ="task_id"),
            inverseJoinColumns=@JoinColumn(name ="parent_task_id"))
    private Set<Task> parentTasks;

    /*@ManyToMany
    @JoinTable(name="links",
            joinColumns=@JoinColumn(name ="task_id"),
            inverseJoinColumns=@JoinColumn(name ="linked_task_id"))
    @MapKeyColumn(name = "type_link")
    private Map<String, Task> linkedTasks;*/
    @OneToMany(
            mappedBy = "task",
            cascade = CascadeType.ALL
    )
    private List<TaskLink> linkedTasks;

    @ManyToMany(mappedBy = "parentTasks", cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    private Set<Task> childTasks;

    /*@ManyToMany(mappedBy = "linkedTasks", cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    private List<Task> inverseTasks;*/

    /*public List<Task> getInverseTasks() {
        return inverseTasks;
    }

    public void setInverseTasks(List<Task> inverseTasks) {
        this.inverseTasks = inverseTasks;
    }*/

    public TaskName getType() {
        return type;
    }

    public void setType(TaskName type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Task> getParentTasks() {
        return parentTasks;
    }

    public void setParentTasks(Set<Task> parentTasks) {
        this.parentTasks = parentTasks;
    }

    public List<TaskLink> getLinkedTasks() {
        return linkedTasks;
    }

    public void setLinkedTasks(List<TaskLink> linkedTasks) {
        this.linkedTasks = linkedTasks;
    }

    public Set<Task> getChildTasks() {
        return childTasks;
    }

    public void setChildTasks(Set<Task> childTasks) {
        this.childTasks = childTasks;
    }

    public Set<Case> getCases() {
        return cases;
    }

    public void setCases(Set<Case> cases) {
        this.cases = cases;
    }

    public Boolean getSynchronous() {
        return isSynchronous;
    }

    public void setSynchronous(Boolean synchronous) {
        isSynchronous = synchronous;
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
}
