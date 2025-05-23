package com.lma.core.conroller;

import com.lma.core.builder.CommonBuilder;
import com.lma.core.proto.controller.ProtoDataCRUDController;
import com.lma.core.proto.entity.Entity;
import com.lma.core.proto.entity.Field;
import com.lma.core.service.FieldService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/management/field")
public class FieldController extends ProtoDataCRUDController<Field> {

    @Autowired
    private FieldService fieldService;

    @PostConstruct
    @Override
    protected void postConstructInit() {
        super.setProtoDataService(fieldService);
    }

    @Override
    protected Object convertPostDto(Object createdData) {
        return usuallyConvertDto((Field) createdData);
    }

    @Override
    protected Object convertPutDto(Object updatedData) {
        return usuallyConvertDto((Field) updatedData);
    }

    @Override
    protected Object defaultConvert(Object object) {
        return object;
    }

    private Object usuallyConvertDto(Field createdData) {
        return CommonBuilder.of(Field::new)
                .with(Field::setId, createdData.getId())
                .with(Field::setName, createdData.getName())
                .with(Field::setFieldType, createdData.getFieldType())
                .with(Field::setEntities, createdData.getEntities().stream()
                        .map(entity -> CommonBuilder.of(Entity::new)
                                .with(Entity::setId, entity.getId())
                                .with(Entity::setName, entity.getName())
                                .with(Entity::setDescription, entity.getDescription())
                                .build())
                        .collect(Collectors.toList()))
                .build();

    }

    @RequestMapping(value = "/model/{modelId}", method = RequestMethod.GET)
    public ResponseEntity<List<Field>> getFieldsByEntityId(@PathVariable(name="modelId") Long modelId) {
        List<Field> fields = fieldService.getFieldsByEntityId(modelId);
        if (CollectionUtils.isNotEmpty(fields)) {
            return new ResponseEntity(fields.stream()
                    .map(f -> usuallyConvertDto(f))
                    .collect(Collectors.toList()), HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    /*@Autowired
    private FieldService fieldService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Field> createField(@RequestBody Field model) {
        if (model == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(fieldService.createField(model), HttpStatus.OK);
    }

    @RequestMapping(value = "/batch", method = RequestMethod.POST)
    public ResponseEntity<List<Field>> createFields(@RequestBody List<Field> models) {
        if (CollectionUtils.isNotEmpty(models)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(fieldService.createFields(models), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Field> updateField(@RequestBody Field model) {
        if (model == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(fieldService.updateField(model), HttpStatus.OK);
    }

    @RequestMapping(value = "/batch", method = RequestMethod.PUT)
    public ResponseEntity<List<Field>> updateFields(@RequestBody List<Field> models) {
        if (CollectionUtils.isNotEmpty(models)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(fieldService.updateFields(models), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Field> deleteField(@RequestBody Field model) {
        if (model == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        fieldService.deleteField(model);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/batch", method = RequestMethod.DELETE)
    public ResponseEntity<List<Field>> deleteFields(@RequestBody List<Field> models) {
        if (CollectionUtils.isNotEmpty(models)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        fieldService.deleteFields(models);
        return new ResponseEntity(HttpStatus.OK);
    }*/
}
