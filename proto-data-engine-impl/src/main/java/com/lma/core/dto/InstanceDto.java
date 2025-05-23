package com.lma.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lma.core.proto.entity.Base;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstanceDto extends Base {

    private Map<Long, List<Long>> relations;
    private Map<Long, List<Long>> deleteRelations;

    @JsonRawValue
    private String data;

    public Map<Long, List<Long>> getRelations() {
        return relations;
    }

    public void setRelations(Map<Long, List<Long>> relations) {
        this.relations = relations;
    }

    public Map<Long, List<Long>> getDeleteRelations() {
        return deleteRelations;
    }

    public void setDeleteRelations(Map<Long, List<Long>> deleteRelations) {
        this.deleteRelations = deleteRelations;
    }

    public String getData() {
        return data;
    }

    @JsonSetter("data")
    public void setData(JsonNode data) {
        this.data = data.toString();
    }

    public void setData(String data) {
        this.data = data;
    }
}
