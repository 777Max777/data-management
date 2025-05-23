package com.lma.core.task.impl;

import com.lma.core.task.api.Consumers;
import com.lma.core.task.consumer.SwitchConsumers;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SwitchTask extends SerializableTask implements Supplier<String> {

    private List<Object> comparingValues;
    private Map<Object, String> result;

    public String calculate() {
        if (MapUtils.isNotEmpty(result)) {
            outerloop:
            for (Object value : comparingValues) {
                for (Map.Entry<Object, String> entry : result.entrySet()) {
                    if (value.getClass().equals(entry.getKey().getClass())) {
                        if (value.equals(entry.getKey())) {
                            String typedLink = entry.getValue();
                            return StringUtils.isNotBlank(typedLink) ? typedLink : StringUtils.EMPTY;
                        }
                    } else break outerloop;
                }
            }
        }
        return StringUtils.EMPTY;
    }

    @Override
    public void accept(Map<String, Object> masterData, Map<String, Object> attributes) {
        Consumers neighbors = this.output.getNeighbors();
        if (neighbors != null && neighbors instanceof SwitchConsumers) {
            if (((SwitchConsumers)neighbors).getResolver() == null) {
                ((SwitchConsumers) neighbors).setResolver(this);
            }
        }
        super.accept(masterData, attributes);
    }

    @Override
    public String get() {
        return calculate();
    }

    public List<Object> getComparingValues() {
        return comparingValues;
    }

    public void setComparingValues(List<Object> comparingValues) {
        this.comparingValues = comparingValues;
    }

    public Map<Object, String> getResult() {
        return result;
    }

    public void setResult(Map<Object, String> result) {
        this.result = result;
    }
}
