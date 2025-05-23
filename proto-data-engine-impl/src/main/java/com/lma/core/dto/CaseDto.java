package com.lma.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lma.core.proto.entity.Base;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaseDto extends Base {

    public String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
