package com.lma.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lma.core.proto.entity.Base;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RelationCRUD extends Base {

    private Map<Long, List<Long>> newData;
    private Map<Long, List<Long>> deleteData;

    public Map<Long, List<Long>> getNewData() {
        return newData;
    }

    public void setNewData(Map<Long, List<Long>> newData) {
        this.newData = newData;
    }

    public Map<Long, List<Long>> getDeleteData() {
        return deleteData;
    }

    public void setDeleteData(Map<Long, List<Long>> deleteData) {
        this.deleteData = deleteData;
    }
}
