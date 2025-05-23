package com.lma.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lma.core.proto.entity.Base;
import com.lma.core.proto.entity.FieldType;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldValuesDto extends Base {

    private Object value;
    private List<Object> values;
    private FieldType fieldType;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }
}
