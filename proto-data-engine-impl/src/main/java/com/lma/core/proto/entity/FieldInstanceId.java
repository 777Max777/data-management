package com.lma.core.proto.entity;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FieldInstanceId implements Serializable {

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "instance_id")
    private Instance instance;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "field_id")
    private Field relatedField;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="relation_id")
    private Instance relation;


    public FieldInstanceId() {
    }

    public FieldInstanceId(Field field, Instance instance, Instance relation) {
        this.relatedField = field;
        this.instance = instance;
        this.relation = relation;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FieldInstanceId that = (FieldInstanceId) obj;
        return Objects.equals(relatedField, that.relatedField) &&
                Objects.equals(instance, that.instance);
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }


    public Field getRelatedField() {
        return relatedField;
    }

    public void setRelatedField(Field relatedField) {
        this.relatedField = relatedField;
    }

    public Instance getRelation() {
        return relation;
    }

    public void setRelation(Instance relation) {
        this.relation = relation;
    }
}
