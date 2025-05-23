package com.lma.core.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lma.core.proto.annotation.Entity;
import com.lma.core.proto.entity.Base;

@Entity("2")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Document extends Base {

}