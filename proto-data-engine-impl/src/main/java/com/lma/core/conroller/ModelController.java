package com.lma.core.conroller;

import com.lma.core.dto.EntityModelDto;
import com.lma.core.proto.controller.ProtoDataCRUDController;
import com.lma.core.proto.entity.Base;
import com.lma.core.proto.entity.Entity;
import com.lma.core.proto.entity.Field;
import com.lma.core.service.ModelService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.lma.core.builder.CommonBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/management/model")
public class ModelController extends ProtoDataCRUDController<Entity> {

    @Autowired
    private ModelService modelService;

    @PostConstruct
    @Override
    protected void postConstructInit() {
        super.setProtoDataService(modelService);
    }

    @Override
    protected Object convertPostDto(Object createdData) {
        return usuallyConvertDto((Entity) createdData);
    }

    @Override
    protected Object convertPutDto(Object updatedData) {
        return usuallyConvertDto((Entity) updatedData);
    }

    @Override
    protected Object defaultConvert(Object object) {
        return object;
    }

    @RequestMapping(method = RequestMethod.GET)
    private ResponseEntity getAllModels() {
        List<EntityModelDto> models = modelService.getAllModels().stream()
                .map(m -> (EntityModelDto) usuallyConvertDto(m))
                .collect(Collectors.toList());
        return new ResponseEntity(models, HttpStatus.OK);
    }

    private Object usuallyConvertDto(Entity createdData) {
        EntityModelDto entityModelDto = new EntityModelDto();
        entityModelDto.setId(createdData.getId());
        entityModelDto.setName(createdData.getName());
        entityModelDto.setDescription(createdData.getDescription());

        if (createdData.getParent() != null) {
            entityModelDto.setParent(CommonBuilder.of(Entity::new)
                    .with(Entity::setId, createdData.getParent().getId())
                    .with(Entity::setName, createdData.getParent().getName())
                    .with(Entity::setDescription, createdData.getParent().getDescription())
                    .build());
        }
        if (CollectionUtils.isNotEmpty(createdData.getFields())) {
            entityModelDto.setFields(createdData.getFields().stream()
                    .map(x -> CommonBuilder.of(Field::new)
                            .with(Field::setId, x.getId())
                            .with(Field::setName, x.getName())
                            .with(Field::setFieldType, x.getFieldType())
                            .with(Field::setGroup, x.getGroup())
                            .build())
                    .collect(Collectors.toSet()));
        }
        return entityModelDto;
    }

}
