package com.lma.core.task.api;

import com.lma.core.proto.entity.TaskLink;

import java.util.List;
import java.util.function.BiConsumer;

public interface Consumers extends BiConsumer {

    void setConsumers(List<BiConsumer> tasks, String option);
}
