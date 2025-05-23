package com.lma.core.task.consumer;

import com.lma.core.task.api.Consumers;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.function.BiConsumer;

public class DefaultConsumers implements Consumers {
    protected List<BiConsumer> consumers;

    public DefaultConsumers() {
    }

    public DefaultConsumers(List<BiConsumer> consumers) {
        this.consumers = consumers;
    }

    @Override
    public void accept(Object masterData, Object attributes) {
        if (CollectionUtils.isNotEmpty(consumers)) {
            consumers.stream().forEach(x -> x.accept(masterData, attributes));
        }
    }

    public List<BiConsumer> getConsumers() {
        return consumers;
    }

    @Override
    public void setConsumers(List<BiConsumer> tasks, String option) {
        this.consumers = tasks;
    }
}
