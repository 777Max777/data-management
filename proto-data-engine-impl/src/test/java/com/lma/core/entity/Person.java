package com.lma.core.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lma.core.proto.annotation.Entity;
import com.lma.core.proto.annotation.Relation;
import com.lma.core.proto.entity.Base;

import java.math.BigDecimal;
import java.util.List;

@Entity("1")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Person extends Base {

    private BigDecimal weight;
    private BigDecimal height;
    private String sex;

    @Relation("1")
    private List<Document> docs;

    public List<Document> getDocs() {
        return docs;
    }

    public void setDocs(List<Document> docs) {
        this.docs = docs;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
