package com.lma.core.proto.controller;

import com.lma.core.proto.entity.Base;
import com.lma.core.proto.service.ProtoDataService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public abstract class ProtoDataCRUDController<S extends Base> {

    private ProtoDataService protoDataService;

    protected abstract void postConstructInit();
    protected abstract Object convertPostDto(Object createdData);
    protected abstract Object convertPutDto(Object updatedData);
    protected abstract Object defaultConvert(Object object);

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<S> createProtoData(@RequestBody S newProtoData) {
        if (newProtoData == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        Base object = (Base) defaultConvert(newProtoData);
        S newData = (S) protoDataService.createProtoData(object);
        Object dto = convertPostDto(newData);
        return new ResponseEntity(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "/batch", method = RequestMethod.POST)
    public ResponseEntity<List<S>> createBatchProtoData(@RequestBody List<S> newBatchProtoData) {
        if (CollectionUtils.isNotEmpty(newBatchProtoData)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        List<Object> objects = newBatchProtoData.stream().map(this::defaultConvert).collect(Collectors.toList());
        List<S> createdData = protoDataService.createBatchProtoData(objects);
        List<Object> dataDtos = new ArrayList<>();
        for (S data: createdData) {
            dataDtos.add(convertPostDto(data));
        }

        return new ResponseEntity(dataDtos, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<S> updateProtoData(@RequestBody S updatingProtoData) {
        if (updatingProtoData == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        Base object = (Base) defaultConvert(updatingProtoData);
        S updatedData = (S) protoDataService.updateProtoData(object);
        Object dto = convertPutDto(updatedData);
        return new ResponseEntity(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "/batch", method = RequestMethod.PUT)
    public ResponseEntity<List<S>> updateBatchProtoData(@RequestBody List<S> updatingBatchProtoData) {
        if (CollectionUtils.isNotEmpty(updatingBatchProtoData)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        List<Object> objects = updatingBatchProtoData.stream().map(this::defaultConvert).collect(Collectors.toList());
        List<S> updatedData = protoDataService.updateBatchProtoData(objects);
        List<Object> dataDtos = new ArrayList<>();
        for (S data: updatedData) {
            dataDtos.add(convertPutDto(data));
        }
        return new ResponseEntity(dataDtos, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity deleteProtoData(@RequestBody S protoData) {
        if (protoData == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        protoDataService.deleteProtoData(protoData);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/batch", method = RequestMethod.DELETE)
    public ResponseEntity deleteBatchProtoData(@RequestBody List<S> batchProtoData) {
        if (CollectionUtils.isNotEmpty(batchProtoData)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        protoDataService.deleteBatchProtoData(batchProtoData);
        return new ResponseEntity(HttpStatus.OK);
    }

    public ProtoDataService getProtoDataService() {
        return protoDataService;
    }

    public void setProtoDataService(ProtoDataService protoDataService) {
        this.protoDataService = protoDataService;
    }
}
