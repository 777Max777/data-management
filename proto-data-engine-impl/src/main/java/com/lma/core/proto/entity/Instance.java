package com.lma.core.proto.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name="instances")
@TypeDef(
        name="jsonb",
        typeClass=JsonBinaryType.class
)
public class Instance extends Base{

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="instance_parent_id")
    private Instance parent;

    @OneToMany(mappedBy="parent")
    private List<Instance> children;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="entity_id")
    private com.lma.core.proto.entity.Entity entity;

    @Type(type="jsonb")
    @Column(name="data")
    private String data;

//    @OneToMany(mappedBy = "id.instance")
//    @MapKeyJoinColumn(name="field_id")
//    private Map<Field, FieldInstanceRelations> fields = new HashMap<>();

    @OneToMany(mappedBy = "id.relation", cascade=CascadeType.ALL, orphanRemoval = true)
//    @JoinTable(name="relations",
//            joinColumns=@JoinColumn(name="instance_id", referencedColumnName = "id"),
//            inverseJoinColumns=@JoinColumn(name="relation_id", referencedColumnName = "id"))
//    @MapKeyJoinColumn(name="field_id")
    private List<FieldInstanceRelations> relations;

//    @OneToMany(mappedBy = "relatedField", cascade=CascadeType.ALL, orphanRemoval = true)
//    private List<FieldInstanceRelations> fields;
//
//    @OneToMany(mappedBy = "instance", cascade=CascadeType.ALL, orphanRemoval = true)
//    private List<FieldInstanceRelations> backRelations;

//    public List<FieldInstanceRelations> getFields() {
//        return fields;
//    }
//
//    public void setFields(List<FieldInstanceRelations> fields) {
//        this.fields = fields;
//    }
//
//    public List<FieldInstanceRelations> getBackRelations() {
//        return backRelations;
//    }
//
//    public void setBackRelations(List<FieldInstanceRelations> backRelations) {
//        this.backRelations = backRelations;
//    }

    public Instance getParent() {
        return parent;
    }

    public void setParent(Instance parent) {
        this.parent = parent;
    }

    public List<Instance> getChildren() {
        return children;
    }

    public void setChildren(List<Instance> children) {
        this.children = children;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<FieldInstanceRelations> getRelations() {
        return relations;
    }

    public void setRelations(List<FieldInstanceRelations> relations) {
        this.relations = relations;
    }

    public com.lma.core.proto.entity.Entity getEntity() {
        return entity;
    }

    public void setEntity(com.lma.core.proto.entity.Entity entity) {
        this.entity = entity;
    }

    //    public Map<Field, FieldInstanceRelations> getFields() {
//        return fields;
//    }
//
//    public void setFields(Map<Field, FieldInstanceRelations> fields) {
//        this.fields = fields;
//    }
}
