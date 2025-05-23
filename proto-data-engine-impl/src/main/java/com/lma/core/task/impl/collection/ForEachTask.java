package com.lma.core.task.impl.collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lma.core.task.api.ReMapField;
import com.lma.core.task.api.TaskOutput;
import com.lma.core.task.impl.SerializableTask;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ForEachTask extends SerializableTask {

    /*public enum Type {
        ARRAY, OBJECT
    }*/
//    private Type type;
//    protected String itemName;


    public ForEachTask() {
    }

    @JsonCreator
    public ForEachTask(
            @JsonProperty("resultNestedFields") List<String> resultNestedFields,
            @JsonProperty("includeNestedField") Map<String, List<String>> includeNestedField,
            @JsonProperty("removeNestedField") List<List<String>> removeNestedField,
            @JsonProperty("reMapFields") List<ReMapField> reMapFields) {
        setResultNestedField(resultNestedFields);
        setIncludeNestedField(includeNestedField);
        setRemoveNestedField(removeNestedField);
        setReMapFields(reMapFields);
    }

    @Override
    public void accept(Map<String, Object> masterData, Map<String, Object> attributes) {

        if (MapUtils.isNotEmpty(getInitValues())) {
            Map.Entry<String, List<String>> entry = getInitValues().entrySet().iterator().next();
            String itemName = entry.getKey();
            List<String> nestedFields = entry.getValue();
            Optional<Collection> collection = find(Collection.class, nestedFields, masterData, attributes);
/*
            if (!collection.isPresent()) {
                 collection = find(Collection.class, nestedFields.iterator(), masterData);
            }
*/
            if (MapUtils.isEmpty(this.attrs)) {
                this.attrs = new HashMap<>();
            }

            List array = new ArrayList();
            this.output.setType(TaskOutput.ExecutingType.CHILDREN);
            collection.ifPresent(c -> c.forEach(map -> {
                this.attrs.put(itemName, map);
                super.accept(masterData, attributes);

                if (masterData.containsKey(itemName)) {
                    Object value = masterData.get(itemName);
                    if (value != null) {
                        if (value instanceof Collection) {
                            array.addAll((Collection) value);
                        } else {
                            array.add(value);
                        }
                    }
                }
            }));

            if (masterData.containsKey(itemName)) {
                masterData.remove(itemName);
            }

            if (CollectionUtils.isNotEmpty(array)) {
                applyResult(array);
            }

            this.output.setType(TaskOutput.ExecutingType.NEIGHBORS);
/*
            List<Optional> mapped = new ArrayList();

            Class relevantClass = type.equals(Type.ARRAY)
                    ? Collection.class
                    : Map.class;

            if (CollectionUtils.isNotEmpty(mapFields)) {
                collection.ifPresent(c -> c.stream()
                        .map(item -> find(Map.class, mapFields, (Map<String, Object>) item))
                        .forEach(optional -> mapped.add((Optional) optional)));
            } 
*/
        }
        super.accept(masterData, attributes);
    }

    /*
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }*/
}
