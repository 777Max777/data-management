package com.lma.core.task.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lma.core.task.api.ReMapField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExpressionTask extends SerializableTask {

    private String expression;
    private Map<String, String> arguments;

    @JsonCreator
    public ExpressionTask(@JsonProperty("resultNestedFields") List<String> resultNestedFields,
                          @JsonProperty("includeNestedField") Map<String, List<String>> includeNestedField,
                          @JsonProperty("removeNestedField") List<List<String>> removeNestedField,
                          @JsonProperty("expression") String expression,
                          @JsonProperty("arguments") Map<String, String> arguments,
                          @JsonProperty("reMapFields") List<ReMapField> reMapFields) {
        setResultNestedField(resultNestedFields);
        setIncludeNestedField(includeNestedField);
        setRemoveNestedField(removeNestedField);
        setReMapFields(reMapFields);
        this.expression = expression;
        this.arguments = arguments;
    }

    @Override
    public void accept(Map<String, Object> masterData, Map<String, Object> attributes) {
        List<Argument> args = new ArrayList<>();

        if (MapUtils.isNotEmpty(arguments)) {
            args.addAll(arguments.entrySet().stream()
                    .map(entry -> new Argument(entry.getKey() + " = " + entry.getValue()))
                    .collect(Collectors.toList()));
        }

        if (MapUtils.isNotEmpty(getInitValues())) {
            List<Argument> initArgs = getInitValues().entrySet().stream()
                    .map(entry -> {
                            if (CollectionUtils.isNotEmpty(entry.getValue())) {
                                Optional<Number> opt = find(Number.class, entry.getValue(), masterData, attributes);
/*
                                if (!opt.isPresent()) {
                                    opt = find(Number.class, entry.getValue().iterator(), masterData);
                                }*/
                                if (opt.isPresent()) {
                                    return new Argument(entry.getKey() + " = " + opt.get().toString());
                                }
                            }
                            return null;
                        })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(initArgs)) {
                args.addAll(initArgs);
            }
        }

        if (CollectionUtils.isNotEmpty(args)) {
            Expression expr = new Expression(expression, args.toArray(new Argument[args.size()]));
            Double res = Double.valueOf(expr.calculate());
            applyResult(res);
        }
        super.accept(masterData, attributes);
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Map<String, String> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, String> arguments) {
        this.arguments = arguments;
    }
}
