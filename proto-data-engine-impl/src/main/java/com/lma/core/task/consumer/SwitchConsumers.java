package com.lma.core.task.consumer;

import com.lma.core.task.api.Consumers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SwitchConsumers implements Consumers {

    private Map<String, List<BiConsumer>> consumers = new HashMap<>();

    private Supplier<String> resolver;

    public SwitchConsumers() {
    }

    public SwitchConsumers(Map<String, List<BiConsumer>> consumers) {
        this.consumers = consumers;
    }

    @Override
    public void accept(Object masterData, Object attributes) {
        if (MapUtils.isNotEmpty(consumers) && resolver != null) {
            List<BiConsumer> typedConsumers = consumers.get(resolver.get());
            typedConsumers.stream().forEach(x -> x.accept(masterData, attributes));
        }
    }

    @Override
    public void setConsumers(List<BiConsumer> tasks, String option) {
        if (StringUtils.isNotBlank(option) && CollectionUtils.isNotEmpty(tasks)) {
            consumers.put(option, tasks);
        }
    }

    public void setResolver(Supplier<String> resolver) {
        this.resolver = resolver;
    }

    public Supplier<String> getResolver() {
        return resolver;
    }
}
