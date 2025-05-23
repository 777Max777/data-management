package com.lma.core.task.api;

import java.util.List;
import java.util.Map;

public abstract class AbstractTask {
    private int numberOfUsage = 1;
    private int counter = 0;
    private Map<String, List<String>> initValues;

    public void incrementUsage() {
        numberOfUsage++;
    }

    protected boolean shouldStartTask() {
        counter++;
        if (counter == numberOfUsage) {
            counter = 0;
            return true;
        }
        return false;
    }

    protected List<String> getInitValue(String key) {
        return initValues.get(key);
    }

    public Map<String, List<String>> getInitValues() {
        return initValues;
    }

    public void setInitValues(Map<String, List<String>> initValues) {
        this.initValues = initValues;
    }
}
