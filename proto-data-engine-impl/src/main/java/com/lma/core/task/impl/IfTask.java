package com.lma.core.task.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.lma.core.task.api.Consumers;
import com.lma.core.task.api.ReMapField;
import com.lma.core.task.api.Task;
import com.lma.core.task.consumer.SwitchConsumers;
//import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = IfTask.Long.class, name = "Long"),
        @JsonSubTypes.Type(value = IfTask.Integer.class, name = "Integer"),
        @JsonSubTypes.Type(value = IfTask.Date.class, name = "Date"),
        @JsonSubTypes.Type(value = IfTask.Double.class, name = "Double"),
        @JsonSubTypes.Type(value = IfTask.BigDecimal.class, name = "BigDecimal"),
        @JsonSubTypes.Type(value = IfTask.IfString.class, name = "String")
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class IfTask<T extends Comparable & Serializable> extends SerializableTask implements Supplier<String> {

    protected T leftOperand;
    protected T rightOperand;
    protected OperandType type;

    // here must be Map<Boolean, String>
    @JsonIgnore
    private Map<Object, String> result = new HashMap<Object, String>() {{
        put(Boolean.TRUE, "YES");
        put(Boolean.FALSE, "NO");
    }};

    public IfTask() {
    }

    @JsonCreator
    public IfTask(@JsonProperty("resultNestedFields") List<String> resultNestedFields,
                          @JsonProperty("includeNestedField") Map<String, List<String>> includeNestedField,
                          @JsonProperty("removeNestedField") List<List<String>> removeNestedField,
                          @JsonProperty("reMapFields") List<ReMapField> reMapFields,
                          @JsonProperty("leftOperand") T leftOperand,
                          @JsonProperty("rightOperand") T rightOperand) {
        setResultNestedField(resultNestedFields);
        setIncludeNestedField(includeNestedField);
        setRemoveNestedField(removeNestedField);
        setReMapFields(reMapFields);
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

    public String calculate() {
        if (leftOperand != null && rightOperand != null) {

            int comparingValue = leftOperand.compareTo(rightOperand);
            switch (type) {
                case MORE: {
                    return comparingValue > 0
                            ? resolveTypedLink(Boolean.TRUE)
                            : resolveTypedLink(Boolean.FALSE);
                }
                case LESS: {
                    return comparingValue < 0
                            ? resolveTypedLink(Boolean.TRUE)
                            : resolveTypedLink(Boolean.FALSE);
                }
                case EQUAL: {
                    return comparingValue == 0
                            ? resolveTypedLink(Boolean.TRUE)
                            : resolveTypedLink(Boolean.FALSE);
                }
            }
        }
        return StringUtils.EMPTY;
    }

    private String resolveTypedLink(Boolean key) {
        String typedLink = result.get(key);
        return StringUtils.isNotBlank(typedLink) ? typedLink : StringUtils.EMPTY;
    }

    private void setValue(Field field, T value) {
        try {
            field.setAccessible(true);
            field.set(this, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void accept(Map<String, Object> masterData, Map<String, Object> attributes) {
        Consumers neighbors = this.output.getNeighbors();
        if (neighbors != null && neighbors instanceof SwitchConsumers) {
            if (((SwitchConsumers)neighbors).getResolver() == null) {
                ((SwitchConsumers) neighbors).setResolver(this);
            }
        }

        if (MapUtils.isNotEmpty(getInitValues())) {
            for (Field field : this.getClass().getSuperclass().getDeclaredFields()) {
                List<String> strValues = getInitValue(field.getName());
                if (CollectionUtils.isNotEmpty(strValues)) {
                    Optional<Comparable> value = find(Comparable.class, strValues.iterator(), attributes);
                    if (!value.isPresent()) {
                        value = find(Comparable.class, strValues.iterator(), masterData);
                    }
                    if (value.isPresent()) {
                        Class relevantClass = ((Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
                        if (relevantClass.equals(value.getClass())) {
                            setValue(field, (T)value.get());
                        }
                    }
                }
            }
        }

        super.accept(masterData, attributes);
    }

    @Override
    public String get() {
        return calculate();
    }

    public enum OperandType {
        MORE,
        LESS,
        EQUAL
    }

    @JsonTypeName("Long")
    static public class Long extends IfTask<java.lang.Long> {
    }
    @JsonTypeName("BigDecimal")
    static public class BigDecimal extends IfTask<java.math.BigDecimal> {
    }
    @JsonTypeName("String")
    static public class IfString extends IfTask<java.lang.String> {
    }
    @JsonTypeName("Date")
    static public class Date extends IfTask<OffsetDateTime> {
    }
    @JsonTypeName("Integer")
    static public class Integer extends IfTask<java.lang.Integer> {
    }
    @JsonTypeName("Double")
    static public class Double extends IfTask<java.lang.Double> {
    }

    public Object getLeftOperand() {
        return leftOperand;
    }

    public void setLeftOperand(T leftOperand) {
        this.leftOperand = leftOperand;
    }

    public T getRightOperand() {
        return rightOperand;
    }

    public void setRightOperand(T rightOperand) {
        this.rightOperand = rightOperand;
    }

    public OperandType getType() {
        return type;
    }

    public void setType(OperandType type) {
        this.type = type;
    }

    public Map<Object, String> getResult() {
        return result;
    }

    public void setResult(Map<Object, String> result) {
        this.result = result;
    }
}