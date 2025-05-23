package com.lma.core.conroller;

import com.lma.core.dto.FieldValuesDto;
import com.lma.core.dto.InstanceDto;
import com.lma.core.proto.entity.Base;
import com.lma.core.proto.service.ExplorerService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/explorer")
public class ExplorerController {

    @Autowired
    private ExplorerService explorerService;

    @RequestMapping(value = {"/models/{modelId}", "/models"}, method = RequestMethod.GET)
    public ResponseEntity<List<Base>> getModelsByParentId(@PathVariable(name="modelId", required = false) Long modelId) {
        List<Base> entities = explorerService.getModelsByParentId(modelId);
        if (CollectionUtils.isEmpty(entities)) {
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(entities, HttpStatus.OK);
    }

    @RequestMapping(value = "/instance/{instanceId}", method = RequestMethod.GET)
    public ResponseEntity<List<FieldValuesDto>> getFieldsByInstanceId(@PathVariable(name="instanceId") Long instanceId) {
        try {
            List<FieldValuesDto> values = explorerService.getFieldsByInstanceId(instanceId);
            return new ResponseEntity(values, HttpStatus.OK);
        } catch(Exception ex) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/instances/{modelId}", method = RequestMethod.GET)
    public ResponseEntity<List<InstanceDto>> getInstancesByModelId(@PathVariable(name="modelId") Long modelId) {
        try {
            List<InstanceDto> values = explorerService.getAllInstancesByModelId(modelId);
            return new ResponseEntity(values, HttpStatus.OK);
        } catch(Exception ex) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/instances/{modelId}", method = RequestMethod.POST)
    public ResponseEntity<InstanceDto> createInstanceByModelId(@PathVariable(name="modelId") Long modelId, @RequestBody InstanceDto instanceDto) {
        try {
            InstanceDto createdInstance = explorerService.saveNewInstanceByModelId(modelId, instanceDto.getName(), instanceDto.getData(), instanceDto.getRelations());
            return new ResponseEntity(createdInstance, HttpStatus.OK);
        } catch(Exception ex) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/instances", method = RequestMethod.PUT)
    public ResponseEntity updateInstance(@RequestBody InstanceDto instanceDto) {
        try {
            explorerService.updateDataInstance(instanceDto);
            return new ResponseEntity(HttpStatus.OK);
        } catch(Exception ex) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/instances", method = RequestMethod.DELETE)
    public ResponseEntity deleteInstance(@RequestBody InstanceDto instanceDto) {
        if (instanceDto == null || instanceDto.getId() == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        explorerService.deleteInstance(instanceDto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/instances/relation/{fieldId}", method = RequestMethod.GET)
    public ResponseEntity<InstanceDto> relationsByFieldId(@PathVariable(name="fieldId") Long fieldId) {
        try {
            List<InstanceDto> values = explorerService.findRelInstancesByFieldId(fieldId);
            return new ResponseEntity(values, HttpStatus.OK);
        } catch(Exception ex) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/instances/relation/{fieldId}/{param}", method = RequestMethod.GET)
    public ResponseEntity<InstanceDto> relationsByFieldIdAndPartOfName(@PathVariable(name="fieldId") Long fieldId, @PathVariable(name="param") String paramName) {
        try {
            List<InstanceDto> values = explorerService.findRelInstancesByFieldId(fieldId, paramName);
            return new ResponseEntity(values, HttpStatus.OK);
        } catch(Exception ex) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
}
