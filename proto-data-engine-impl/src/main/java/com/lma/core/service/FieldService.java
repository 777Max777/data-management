package com.lma.core.service;

import com.lma.core.proto.entity.Field;
import com.lma.core.proto.repository.FieldRepository;
import com.lma.core.proto.service.ProtoDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class FieldService extends ProtoDataService<Field> {

    @Autowired
    private FieldRepository fieldRepository;

    @PostConstruct
    @Override
    protected void postConstructInit() {
        super.setJpaRepository(fieldRepository);
    }

    public List<Field> getFieldsByEntityId(Long modelId) {
        return fieldRepository.loadFieldsByEntityId(modelId);
    }

//    public Field createField(Field newField) {
//        return saveField(newField);
//    }
//
//    public List<Field> createFields(List<Field> newFields) {
//        return saveFields(newFields);
//    }
//
//    public Field updateField(Field updatingField) {
//        return saveField(updatingField);
//    }
//
//    public List<Field> updateFields(List<Field> updatingFields) {
//        return saveFields(updatingFields);
//    }
//
//    private Field saveField(Field Field) {
//        return fieldRepository.save(Field);
//    }
//
//    private List<Field> saveFields(List<Field> fields) {
//        return fieldRepository.saveAll(fields);
//    }
//
//    public void deleteField(Field Field) {
//        fieldRepository.delete(Field);
//    }
//
//    public void deleteFields(List<Field> fields) {
//        fieldRepository.deleteInBatch(fields);
//    }
}
