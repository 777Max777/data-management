package com.lma.core.proto.transformation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lma.core.proto.annotation.Relation;
import com.lma.core.proto.entity.Base;
import com.lma.core.proto.entity.Field;
import com.lma.core.proto.entity.FieldInstanceRelations;
import com.lma.core.proto.entity.Instance;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class OutputProtoDataTransformation extends AbstractProtoDataTransformation {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutputProtoDataTransformation.class);

    private ObjectMapper objectMapper;

    public OutputProtoDataTransformation() {
        super();
        this.objectMapper = new ObjectMapper();
    }

    public OutputProtoDataTransformation(Set<String> packages) {
        super(packages);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public <T, I extends Instance> void resolveEntity(T result, I source) {
        LOGGER.info("START Object transformation - deserialize");
        if (source == null) {
            LOGGER.info("Source is null");
            LOGGER.info("END Object transformation - deserialize");
            return;
        }
        if (!validateObject(result)) {
            LOGGER.error("Class is not valid {} \nThere is no Entity annotation, valueId or isn't subtype Base class", result.getClass());
            return;
        }

        try {
            T deserialize = (T) objectMapper.readValue(source.getData(), result.getClass());
            mergeFields(deserialize, result);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        ((Base)result).setId(source.getId());
        ((Base)result).setName(source.getName());

        resolveRelations(result, source);

        LOGGER.info("END Object transformation - deserialize");
    }

    @Override
    public <T, I extends Instance> void resolveRelations(T result, I source) {
        if (CollectionUtils.isNotEmpty(source.getRelations())) {
            LOGGER.info("START Relation transformation - deserialize");

            List<java.lang.reflect.Field> relatedFields = findRelatedFields(result);
            for (FieldInstanceRelations rel : source.getRelations()) {
                Optional<java.lang.reflect.Field> fieldIsPresent = findFieldByRelId(relatedFields, rel.getId().getRelation().getId().toString());

                if (fieldIsPresent.isPresent()) {
                    java.lang.reflect.Field field = fieldIsPresent.get();
                    field.setAccessible(true);
                    try {
                        Object relObj = field.getType().newInstance();
                        resolveEntity(relObj, rel.getId().getRelation());
                        field.set(result, relObj);
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            LOGGER.info("END Relation transformation - deserialize");
        }
    }

    private Optional<java.lang.reflect.Field> findFieldByRelId(List<java.lang.reflect.Field> fields, String id) {
        return fields.stream()
                .filter(field -> ((Relation)Arrays.stream(field.getDeclaredAnnotations())
                                    .filter(a -> a instanceof Relation)
                                    .findFirst()
                                    .get())
                        .value()
                        .equals(id))
                .findFirst();
    }

    private void mergeFields(Object source, Object result) {
        if (source.getClass() != result.getClass()) {
            LOGGER.error("Incompatible types {} and {}", result.getClass(), source.getClass());
            return;
        }

        for (java.lang.reflect.Field field : source.getClass().getDeclaredFields()) {
            if ((!Base.class.isAssignableFrom(field.getType()) && !Collection.class.isAssignableFrom(field.getType()))
                    || (Collection.class.isAssignableFrom(field.getType())
                        && !Base.class.isAssignableFrom((Class)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0]))) {
                field.setAccessible(true);
                try {
                    field.set(result, field.get(source));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
