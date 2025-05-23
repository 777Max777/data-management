package com.lma.core.task.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.lma.core.task.api.ReMapField;

import java.util.List;
import java.util.Map;
import java.util.Random;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RandomTask.Long.class, name = "Long"),
        @JsonSubTypes.Type(value = RandomTask.Integer.class, name = "Integer"),
        @JsonSubTypes.Type(value = RandomTask.Double.class, name = "Double"),
        @JsonSubTypes.Type(value = RandomTask.Gaussian.class, name = "Gaussian")
})
public abstract class RandomTask extends SerializableTask {

    protected Double left;
    protected Double right;

    protected abstract Object nextValue(Random random);

    @JsonCreator
    public RandomTask(@JsonProperty("resultNestedFields") List<String> resultNestedFields,
                  @JsonProperty("includeNestedField") Map<String, List<String>> includeNestedField,
                  @JsonProperty("removeNestedField") List<List<String>> removeNestedField,
                  @JsonProperty("reMapFields") List<ReMapField> reMapFields,
                  @JsonProperty("leftOperand") Double left,
                  @JsonProperty("rightOperand") Double right) {
        setResultNestedField(resultNestedFields);
        setIncludeNestedField(includeNestedField);
        setRemoveNestedField(removeNestedField);
        setReMapFields(reMapFields);
        this.left = left;
        this.right = right;
    }

    public RandomTask() {
    }

    @Override
    public void accept(Map<String, Object> masterData, Map<String, Object> attributes) {
        Random random = new Random();

        Object value = nextValue(random);

        applyResult(value);
        super.accept(masterData, attributes);
    }

    @JsonTypeName("Long")
    static public class Long extends RandomTask {
        @Override
        protected java.lang.Long nextValue(Random random) {
            return java.lang.Long.valueOf(random.nextLong());
        }
    }

    @JsonTypeName("Double")
    static public class Double extends RandomTask {
        @Override
        protected java.lang.Double nextValue(Random random) {
            return java.lang.Double.valueOf(random.nextDouble());
        }
    }

    @JsonTypeName("Integer")
    static public class Integer extends RandomTask {
        @Override
        protected java.lang.Integer nextValue(Random random) {
            return java.lang.Integer.valueOf(random.nextInt());
        }
    }

    @JsonTypeName("Gaussian")
    static public class Gaussian extends RandomTask {
        @Override
        protected java.lang.Double nextValue(Random random) {
            return java.lang.Double.valueOf(random.nextGaussian());
        }
    }

    public Double getLeft() {
        return left;
    }

    public void setLeft(Double left) {
        this.left = left;
    }

    public Double getRight() {
        return right;
    }

    public void setRight(Double right) {
        this.right = right;
    }
}
