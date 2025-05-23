package com.lma.core.proto.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lma.core.builder.CommonBuilder;
import com.lma.core.dto.FieldValuesDto;
import com.lma.core.dto.InstanceDto;
import com.lma.core.dto.RelationCRUD;
import com.lma.core.proto.entity.Base;
import com.lma.core.proto.entity.Entity;
import com.lma.core.proto.entity.Field;
import com.lma.core.proto.entity.FieldInstanceId;
import com.lma.core.proto.entity.FieldInstanceRelations;
import com.lma.core.proto.entity.FieldType;
import com.lma.core.proto.entity.Instance;
import com.lma.core.proto.repository.EntityRepository;
import com.lma.core.proto.repository.FieldRepository;
import com.lma.core.proto.repository.InstanceRepository;
import com.lma.core.proto.repository.RelationRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExplorerService {

    @Autowired
    private EntityRepository entityRepository;
    @Autowired
    private FieldRepository fieldRepository;
    @Autowired
    private InstanceRepository instanceRepository;
    @Autowired
    private RelationRepository relationRepository;

    private ObjectMapper mapper = new ObjectMapper();

    public List<Base> getModelsByParentId(Long modelId) {
        List<Entity> entities = entityRepository.findAllByParentId(modelId);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.stream().map(e -> CommonBuilder.of(Base::new)
                    .with(Base::setId, e.getId())
                    .with(Base::setName, e.getName())
                    .build()).collect(Collectors.toList());
        }
        return null;
    }

//    public List<InstanceDto> findRelationData() {
//
//    }

    public List<InstanceDto> getAllInstancesByModelId(Long modelId) {
        List<Instance> instances = instanceRepository.findAllByEntityId(modelId);
        if (CollectionUtils.isNotEmpty(instances)) {
            return instances.stream().map(i -> CommonBuilder.of(InstanceDto::new)
                    .with(InstanceDto::setId, i.getId())
                    .with(InstanceDto::setName, i.getName())
                    .with(InstanceDto::setData, i.getData())
                    .build()).collect(Collectors.toList());
        }
        return null;
    }

    public List<FieldValuesDto> getFieldsByInstanceId(Long instanceId) throws JsonProcessingException {
        Instance instance = instanceRepository.getOne(instanceId);

        if (instance != null) {
            List<FieldValuesDto> dtos = new ArrayList<>();
            List<Field> fields = fieldRepository.loadFieldsByEntityId(instance.getEntity().getId());
            List<Long> relFields = fields.stream().filter(r -> r.getFieldType() == FieldType.RELATION || r.getFieldType() == FieldType.MULTIPLE_RELATION)
                    .map(r -> r.getId())
                    .collect(Collectors.toList());
            Map<Long, String> data = null;
            if (StringUtils.isNotBlank(instance.getData())) {
                data = mapper.readValue(instance.getData(), new TypeReference<Map<Long, String>>() {
                });
            }
            Map<Field, List<FieldInstanceId>> groupingRelFields = null;
            if (CollectionUtils.isNotEmpty(relFields)) {
                List<FieldInstanceRelations> fieldInstanceRelations = relationRepository.findAllByInstanceRelFields(instance.getId(), relFields.toArray(new Long[relFields.size()]));
                groupingRelFields = fieldInstanceRelations.stream()
                        .map(f -> f.getId())
                        .collect(Collectors.groupingBy(FieldInstanceId::getRelatedField));
            }

            for (Field field : fields) {
                if (MapUtils.isNotEmpty(groupingRelFields) && groupingRelFields.containsKey(field)) {
                    groupingRelFields.entrySet()
                            .forEach(complexEntryId -> dtos.add(CommonBuilder.of(FieldValuesDto::new)
                                    .with(FieldValuesDto::setId, complexEntryId.getValue().get(0).getRelatedField().getId())
                                    .with(FieldValuesDto::setName, complexEntryId.getValue().get(0).getRelatedField().getName())
                                    .with(FieldValuesDto::setFieldType, complexEntryId.getValue().get(0).getRelatedField().getFieldType())
                                    .with(FieldValuesDto::setValue, complexEntryId.getValue().stream()
                                            .map(id -> CommonBuilder.of(Base::new)
                                                    .with(Base::setId, id.getRelation().getId())
                                                    .with(Base::setName, id.getRelation().getName())
                                                    .build())
                                            .collect(Collectors.toList()))
                                    .build())
                            );
                } else {
                    FieldValuesDto fieldValuesDto = CommonBuilder.of(FieldValuesDto::new)
                            .with(FieldValuesDto::setId, field.getId())
                            .with(FieldValuesDto::setName, field.getName())
                            .with(FieldValuesDto::setFieldType, field.getFieldType())
                            .build();
                    if (MapUtils.isNotEmpty(data) && data.containsKey(field.getId())) {
                        fieldValuesDto.setValue(data.get(field.getId()));
                    }
                    dtos.add(fieldValuesDto);
                }
            }
                    /*data.entrySet().forEach(entry -> {
                        Optional<Field> field = fields.stream().filter(f -> f.getId().equals(entry.getKey())).findFirst();
                        if (field.isPresent()) {
                            dtos.add(CommonBuilder.of(FieldValuesDto::new)
                                    .with(FieldValuesDto::setId, field.get().getId())
                                    .with(FieldValuesDto::setName, field.get().getName())
                                    .with(FieldValuesDto::setValue, entry.getValue())
                                    .with(FieldValuesDto::setFieldType, field.get().getFieldType())
                                    .build());
                        }
                    });
                }
                if (CollectionUtils.isNotEmpty(relFields)) {
                    List<FieldInstanceRelations> fieldInstanceRelations = relationRepository.findAllByInstanceRelFields(instance.getId(), relFields.toArray(new Long[relFields.size()]));
                    fieldInstanceRelations.stream()
                            .map(f -> f.getId())
                            .collect(Collectors.groupingBy(FieldInstanceId::getRelatedField))
                            .entrySet()
                            .forEach(complexEntryId -> dtos.add(CommonBuilder.of(FieldValuesDto::new)
                                    .with(FieldValuesDto::setId, complexEntryId.getValue().get(0).getRelatedField().getId())
                                    .with(FieldValuesDto::setName, complexEntryId.getValue().get(0).getRelatedField().getName())
                                    .with(FieldValuesDto::setFieldType, complexEntryId.getValue().get(0).getRelatedField().getFieldType())
                                    .with(FieldValuesDto::setValue, complexEntryId.getValue().stream()
                                            .map(id -> CommonBuilder.of(Base::new)
                                                    .with(Base::setId, id.getRelation().getId())
                                                    .with(Base::setName, id.getRelation().getName())
                                                    .build())
                                            .collect(Collectors.toList()))
                                    .build())
                            );*/

            return dtos;

        }
        return null;
    }

    public InstanceDto saveNewInstanceByModelId(Long modelId, String name, String data, Map<Long, List<Long>> relations) {
        Instance instance = new Instance();
        instance.setName(name);
        instance.setEntity(CommonBuilder.of(Entity::new)
                .with(Entity::setId, modelId)
                .build());
        if (StringUtils.isNotBlank(data)) {
            instance.setData(data);
        }
        Instance newInstance = instanceRepository.save(instance);
        if (MapUtils.isNotEmpty(relations)) {
            relationRepository.saveAll(convertDataToRelations(relations, newInstance.getId()));
        }
        return commonConvertInstanceDto(instance);
    }

    public void updateDataInstance(InstanceDto instanceDto) {
        if (instanceDto.getId() != null) {
            Optional<Instance> isThere = instanceRepository.findById(instanceDto.getId());
            if (isThere.isPresent()) {
                Instance instance = isThere.get();
                if (StringUtils.isNotBlank(instanceDto.getName())) {
                    instance.setName(instanceDto.getName());
                }
                if (StringUtils.isNotBlank(instanceDto.getData())) {
                    instance.setData(instanceDto.getData());
                }
                instanceRepository.save(instance);
            }
            if (MapUtils.isNotEmpty(instanceDto.getDeleteRelations())) {
                relationRepository.deleteAll(convertDataToRelations(instanceDto.getDeleteRelations(), instanceDto.getId()));
            }
            if (MapUtils.isNotEmpty(instanceDto.getRelations())) {
                relationRepository.saveAll(convertDataToRelations(instanceDto.getRelations(), instanceDto.getId()));
            }
        }
    }

    @Transactional
    public void deleteInstance(InstanceDto instanceDto) {
        relationRepository.deleteAllById_InstanceId(instanceDto.getId());
        instanceRepository.deleteById(instanceDto.getId());
    }

    public List<InstanceDto> findRelInstancesByFieldId(Long fieldId) {
        List<Instance> instances = instanceRepository.loadRelInstancesByFieldId(fieldId, 100L);
        if (CollectionUtils.isNotEmpty(instances)) {
            return instances.stream().map(i -> commonConvertInstanceDto(i))
                    .collect(Collectors.toList());
        }
        return null;
    }

    public List<InstanceDto> findRelInstancesByFieldId(Long fieldId, String paramName) {
        List<Instance> instances = instanceRepository.loadFilteredRelInstancesByFieldId(fieldId, paramName + "%");
        if (CollectionUtils.isNotEmpty(instances)) {
            return instances.stream().map(i -> commonConvertInstanceDto(i))
                    .collect(Collectors.toList());
        }
        return null;
    }

    private FieldInstanceRelations getRelation(Long instanceId, Long relationId, Long fieldId) {
        return CommonBuilder.of(FieldInstanceRelations::new)
                .with(FieldInstanceRelations::setId, CommonBuilder.of(FieldInstanceId::new)
                        .with(FieldInstanceId::setInstance, CommonBuilder.of(Instance::new)
                                .with(Instance::setId, instanceId)
                                .build())
                        .with(FieldInstanceId::setRelation, CommonBuilder.of(Instance::new)
                                .with(Instance::setId, relationId)
                                .build())
                        .with(FieldInstanceId::setRelatedField, CommonBuilder.of(Field::new)
                                .with(Field::setId, fieldId)
                                .build())
                        .build())
                .build();
    }

    private List<FieldInstanceRelations> convertDataToRelations(Map<Long, List<Long>> data, Long instanceId) {
        return data.entrySet().stream().map(entry ->
                entry.getValue().stream().map(rel -> getRelation(instanceId, rel, entry.getKey()))
                        .collect(Collectors.toList()))
                .flatMap(list -> list.stream())
                .collect(Collectors.toList());
    }

    private InstanceDto commonConvertInstanceDto(Instance instance) {
        return CommonBuilder.of(InstanceDto::new)
                .with(InstanceDto::setId, instance.getId())
                .with(InstanceDto::setName, instance.getName())
                .with(InstanceDto::setData, instance.getData())
                .build();
    }
}
