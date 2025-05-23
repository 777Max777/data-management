package com.lma.core.conroller;

import com.lma.core.builder.CommonBuilder;
import com.lma.core.proto.controller.ProtoDataCRUDController;
import com.lma.core.proto.entity.Entity;
import com.lma.core.proto.entity.Instance;
import com.lma.core.service.InstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/management/instance")
public class InstanceController extends ProtoDataCRUDController<Instance> {

    @Autowired
    private InstanceService instanceService;

    @Override
    protected void postConstructInit() {
        super.setProtoDataService(instanceService);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Instance> getInstanceById(@PathVariable("id") Long instanceId) {
        Optional<Instance> instance = instanceService.findById(instanceId);
        if (instance.isPresent()) {
            return new ResponseEntity(usuallyConvertDto(instance.get()), HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @Override
    protected Object convertPostDto(Object createdData) {
        return usuallyConvertDto((Instance) createdData);
    }

    @Override
    protected Object convertPutDto(Object updatedData) {
        return usuallyConvertDto((Instance) updatedData);
    }

    @Override
    protected Object defaultConvert(Object object) {
        return object;
    }

    private Object usuallyConvertDto(Instance data) {
        return CommonBuilder.of(Instance::new)
                .with(Instance::setId, data.getId())
                .with(Instance::setName, data.getName())
                .with(Instance::setEntity, CommonBuilder.of(Entity::new)
                        .with(Entity::setId, data.getEntity().getId())
                        .with(Entity::setName, data.getEntity().getName())
                        .build())
                .with(Instance::setParent, CommonBuilder.of(Instance::new)
                        .with(Instance::setId, data.getId())
                        .with(Instance::setName, data.getName())
                        .build())
                .build();
    }
}
