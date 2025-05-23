package com.lma.core.proto.transformation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lma.core.proto.annotation.Entity;
import com.lma.core.proto.annotation.Relation;
import com.lma.core.proto.entity.Base;
import com.lma.core.proto.entity.FieldInstanceId;
import com.lma.core.proto.entity.FieldInstanceRelations;
import com.lma.core.proto.entity.Instance;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class InputProtoDataTransformation extends AbstractProtoDataTransformation {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputProtoDataTransformation.class);

    private ObjectMapper objectMapper;

    public InputProtoDataTransformation() {
        super();
        this.objectMapper = new ObjectMapper();
    }

    public InputProtoDataTransformation(Set<String> packages) {
        super(packages);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public <T, I extends Instance> void resolveEntity(T source, I result) {
        LOGGER.info("START Object transformation - serialize");
        if (source == null) {
            LOGGER.info("Source is null");
            LOGGER.info("END Object transformation - serialize");
            return;
        }
        if (!validateObject(source)) {
            LOGGER.error("Class is not valid {} \nThere is no Entity annotation, valueId or isn't subtype Base class", source.getClass());
            return;
        }

        String data = null;
        try {
            data = objectMapper.writeValueAsString(source);
        } catch(JsonProcessingException e) {
            LOGGER.error("Something went wrong");
            e.printStackTrace();
        }


        Optional<String> entityId = getEntityValueId(source);

        if (result == null) {
            result = (I) new Instance();
        }

        result.setId(((Base)source).getId());
        result.setName(((Base)source).getName());
        result.setData(data);

        if(entityId.isPresent()) {
            Base base = new com.lma.core.proto.entity.Entity();
            base.setId(Long.valueOf(entityId.get()));
            result.setEntity((com.lma.core.proto.entity.Entity)base);
        }

        resolveRelations(source, result);

        LOGGER.info("END Object transformation- serialize");
    }

    @Override
    public <T, I extends Instance> void resolveRelations(T source, I result) {
        LOGGER.info("START Relation transformation - serialize");
        List<Field> fields = findRelatedFields(source);

        List<FieldInstanceRelations> relations = new ArrayList<>();

        for (Field field : fields) {
            if (!Collection.class.isAssignableFrom(field.getType()) /*&& entities.contains(field.getType())*/) {
                field.setAccessible(true);
                Instance object = null;
                try {
                    resolveEntity(field.get(source), object);
                    collectRelation(object, result, field, relations);

                } catch (IllegalAccessException e) {
                    LOGGER.error("Can't get value for field {} an object {}", field, source);
                    e.printStackTrace();
                }
            } else {
//                LOGGER.error("There is no entity for field {} of type {}", field.getName(), source);
                field.setAccessible(true);
                com.lma.core.proto.entity.Field lmaField = configureField(field);
//                List<FieldInstanceRelations> relFields = new ArrayList<>();
                try {
                    Object collectionObj = field.get(source);
                    if (collectionObj != null) {
                        for (Object objEntity : (Collection) field.get(source)) {
                            Instance object = new Instance();
                            resolveEntity(objEntity, object);
                            FieldInstanceRelations relObject = initRelObject(lmaField, result, object);
                            relations.add(relObject);
//                            relFields.add(relObject);
                        }
//                        lmaField.setRelations(relFields);
                    }
                } catch (IllegalAccessException e) {
                    LOGGER.error("Can't get value for field {} an object {}", field, source);
                    e.printStackTrace();
                }
            }
        }

        result.setRelations(relations);
//        result.setFields(relations);
//        result.setBackRelations(relations);
        LOGGER.info("END Relation transformation - serialize");
    }

    private com.lma.core.proto.entity.Field configureField(Field field) {
        Optional<String> relId = getRelationValueId(field);
        com.lma.core.proto.entity.Field lmaField = null;
        if (relId.isPresent()) {
            lmaField = new com.lma.core.proto.entity.Field();
            lmaField.setId(Long.valueOf(relId.get()));

        } else {
            LOGGER.error("Relation ID is not present by field {} an object", field);
        }
        return lmaField;
    }

    private void collectRelation(Instance object, Instance result, Field field, List<FieldInstanceRelations> relations) {
        com.lma.core.proto.entity.Field lmaField = configureField(field);
        if (lmaField != null) {
            FieldInstanceRelations relObject = initRelObject(lmaField, result, object);
            relations.add(relObject);
        }
    }

    private void collectRelation(FieldInstanceRelations relObject, Instance object) {
//        Set<Instance> relations = relObject.getRelations();
//        if (CollectionUtils.isEmpty(relations)) {
//            relations = new HashSet<>();
//        }
//        relations.add(object);

    }

    private FieldInstanceRelations initRelObject(com.lma.core.proto.entity.Field field, Instance result, Instance object) {
        FieldInstanceRelations relObject = new FieldInstanceRelations();
//        relObject.setRelation(object);
//        relObject.setRelatedField(field);
//        relObject.setInstance(result);
        relObject.setId(new FieldInstanceId(field, result, object));
        return relObject;
    }

    private Optional<String> getEntityValueId(Object obj) {
        return Arrays.stream(obj.getClass().getAnnotations())
                .filter(a -> a instanceof Entity)
                .map(a -> ((Entity)a).value())
                .findFirst();
    }

    private Optional<String> getRelationValueId(Field field) {
        return Arrays.stream(field.getAnnotations())
                .filter(a -> a instanceof Relation)
                .map(a -> ((Relation)a).value())
                .findFirst();
    }

}
