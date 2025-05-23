package com.lma.core.proto.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@javax.persistence.Entity
@Table(name="entities")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Entity extends Base {

    @Column
    private String description;

//    @JsonManagedReference
//    @JsonBackReference
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="entity_parent_id")
    private Entity parent;

//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    @JsonBackReference
//    @JsonManagedReference
    @OneToMany(mappedBy="parent")
    private Set<Entity> children;

    @ManyToMany(mappedBy = "entities", cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    private Set<Field> fields;

    @OneToMany(mappedBy = "relEntity")
    private Set<Field> relatedFields;

    @OneToMany(mappedBy="entity")
    private Set<Instance> instances;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Entity getParent() {
        return parent;
    }

    public void setParent(Entity parent) {
        this.parent = parent;
    }

    public Set<Entity> getChildren() {
        return children;
    }

    public void setChildren(Set<Entity> children) {
        this.children = children;
    }

    public Set<Field> getFields() {
        return fields;
    }

    public void setFields(Set<Field> fields) {
        this.fields = fields;
    }

    public Set<Instance> getinstances() {
        return instances;
    }

    public void setinstances(Set<Instance> instances) {
        this.instances = instances;
    }

    public Set<Field> getRelatedFields() {
        return relatedFields;
    }

    public void setRelatedFields(Set<Field> relatedFields) {
        this.relatedFields = relatedFields;
    }
}
