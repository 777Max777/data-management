package com.lma.core.conroller;

import com.lma.core.builder.CommonBuilder;
import com.lma.core.dto.CaseDto;
import com.lma.core.proto.controller.ProtoDataCRUDController;
import com.lma.core.proto.entity.Case;
import com.lma.core.service.CaseService;
import com.lma.core.task.api.TaskLoader;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@RestController
@RequestMapping("/case")
public class CaseController extends ProtoDataCRUDController<CaseDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaseController.class);

    @Autowired
    private TaskLoader taskLoader;

    @Autowired
    private CaseService caseService;

    @Override
    @PostConstruct
    protected void postConstructInit() {
        super.setProtoDataService(caseService);
    }

    @Override
    protected Object convertPostDto(Object createdData) {
        return usuallyDtoConverter((Case) createdData);
    }

    @Override
    protected Object convertPutDto(Object updatedData) {
        return usuallyDtoConverter((Case) updatedData);
    }

    @Override
    protected Object defaultConvert(Object object) {
        CaseDto dto = (CaseDto)object;
        return CommonBuilder.of(Case::new)
                .with(Case::setId, dto.getId())
                .with(Case::setName, dto.getName())
                .with(Case::setDescription, dto.getDescription())
                .build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ResponseEntity executeTasksByCaseId(@PathVariable("id") Long caseId, @RequestBody(required=false) Map<String, Object> body) {
        LOGGER.info("Start load tasks");
        List<BiConsumer> tasks = taskLoader.load(caseId);
        LOGGER.info("End loading tasks");

        LOGGER.info("Start evaluating TASKS");
        Map<String, Object> data = new HashMap<>();
        if (MapUtils.isNotEmpty(body)) {
            data.putAll(body);
        }
        tasks.forEach(task -> task.accept(data, new HashMap<>()));

        LOGGER.info("Results executed tasks: {}", data);
        LOGGER.info("End evaluating TASKS");
        return new ResponseEntity(data, HttpStatus.OK);
    }

    private Object usuallyDtoConverter(Case c) {
        return CommonBuilder.of(CaseDto::new)
                .with(CaseDto::setId, c.getId())
                .with(CaseDto::setName, c.getName())
                .with(CaseDto::setDescription, c.getDescription())
                .build();
    }
}
