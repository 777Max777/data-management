package com.lma.core.proto.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name="fields")
@TypeDef(name = "type", typeClass = PostgreSQLEnumType.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Field extends Base {

    @ManyToMany
    @JoinTable(name="mappings",
            joinColumns=@JoinColumn(name ="field_id"),
            inverseJoinColumns=@JoinColumn(name ="entity_id"))
    private List<com.lma.core.proto.entity.Entity> entities;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="field_group_id")
    private Group group;

    @Enumerated(EnumType.STRING)
    @Column(name="type")
    @Type(type = "type")
    private FieldType fieldType;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="rel_entity_id")
    private com.lma.core.proto.entity.Entity relEntity;

    public List<com.lma.core.proto.entity.Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<com.lma.core.proto.entity.Entity> entities) {
        this.entities = entities;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public com.lma.core.proto.entity.Entity getRelEntity() {
        return relEntity;
    }

    public void setRelEntity(com.lma.core.proto.entity.Entity relEntity) {
        this.relEntity = relEntity;
    }
}
