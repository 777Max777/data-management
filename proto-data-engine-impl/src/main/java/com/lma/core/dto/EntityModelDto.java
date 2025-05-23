package com.lma.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lma.core.proto.entity.Base;
import com.lma.core.proto.entity.Entity;
import com.lma.core.proto.entity.Field;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityModelDto extends Base {

    private Entity parent;
    private Set<Field> fields;
    private String description;

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

    public Set<Field> getFields() {
        return fields;
    }

    public void setFields(Set<Field> fields) {
        this.fields = fields;
    }
}
