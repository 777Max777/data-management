package com.lma.core.proto.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name="relations")
public class FieldInstanceRelations {

    @EmbeddedId
    private FieldInstanceId id;

    public FieldInstanceId getId() {
        return id;
    }

    public void setId(FieldInstanceId id) {
        this.id = id;
    }
}
