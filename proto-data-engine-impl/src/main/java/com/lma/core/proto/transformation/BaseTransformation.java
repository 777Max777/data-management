package com.lma.core.proto.transformation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lma.core.proto.annotation.Entity;
import com.lma.core.proto.annotation.Relation;
import com.lma.core.proto.entity.Base;
import com.lma.core.proto.entity.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class BaseTransformation {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTransformation.class);

    private ObjectMapper objectMapper;

    public BaseTransformation(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private boolean validateObject(Object obj) {
        boolean isValidClass = Arrays.stream(obj.getClass().getAnnotations())
                .anyMatch(a -> a instanceof Entity
                                && StringUtils.isNotBlank(((Entity)a).value())
                                && Base.class.isAssignableFrom(obj.getClass()));

        if (!isValidClass) return false;

        Optional wrongFields = Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(field -> Arrays.stream(field.getDeclaredAnnotations()).anyMatch(a -> a instanceof Relation
                                    && StringUtils.isBlank(((Relation)a).value())))
                .findFirst();
        if (wrongFields.isPresent()) return false;
        return true;
    }

    private List<Field> findRelatedFields(Object obj) {
        return Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(field -> Arrays.stream(field.getDeclaredAnnotations()).anyMatch(a -> a instanceof Relation))
                .collect(Collectors.toList());
    }

    private Optional<String> getEntityValueId(Object obj) {
        return Arrays.stream(obj.getClass().getAnnotations())
                    .filter(a -> a instanceof Entity)
                    .map(a -> ((Entity)a).value())
                    .findFirst();
    }

    public Instance collectProtoData(Object source) {
        LOGGER.info("Object transformation started");
        if (!validateObject(source)) {
            LOGGER.error("Class is not valid {} \nThere is no Entity annotation or valueId", source.getClass());
        }

        List<Field> fields = findRelatedFields(source);
        fields.forEach(f -> {
            f.setAccessible(true);
            try {
                f.set(source, null);
            } catch (IllegalAccessException e) {
                LOGGER.error("Can't set null value for field {} an object {}", f, source);
                e.printStackTrace();
            }
        });

        String data = null;
        try {
            data = objectMapper.writeValueAsString(source);
        } catch(JsonProcessingException e) {
            LOGGER.error("Something went wrong");
            e.printStackTrace();
        }


        Optional<String> entityId = getEntityValueId(source);

        Instance instance = new Instance();

        instance.setId(((Base)source).getId());
        instance.setName(((Base)source).getName());
        instance.setData(data);

        if(entityId.isPresent()) {
            Base base = new com.lma.core.proto.entity.Entity();
            base.setId(Long.valueOf(entityId.get()));
            instance.setEntity((com.lma.core.proto.entity.Entity)base);
        }

        return instance;
    }

    public List<Instance> collectProtoData(List<Object> sources) {
        return null;
    }

}
