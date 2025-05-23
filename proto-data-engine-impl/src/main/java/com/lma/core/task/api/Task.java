package com.lma.core.task.api;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class Task extends AbstractTask implements BiConsumer<Map<String, Object>, Map<String, Object>> {
    protected TaskOutput output;
    protected List<String> mergeData;
    protected Map<String, Object> attrs;
    protected Map<String, Object> childData = new HashMap<>();
    protected List<String> removeData;  //It is rudiment

    /**
     * Arrange update data {@link #mergeData} by {@link #attrs} keys
     * @param masterData
     * @param attributes
     */
    @Override
    public void accept(Map<String, Object> masterData, Map<String, Object> attributes) {
        if (shouldStartTask()) {
            if (CollectionUtils.isNotEmpty(removeData)) {
                removeData.forEach(masterData::remove);
            }
            Map<String, Object> newAttributes = null;
            if (MapUtils.isNotEmpty(attrs)) {
                if (CollectionUtils.isNotEmpty(mergeData)) {
                    mergeData.forEach(x -> {
                        if (attrs.containsKey(x)) {
//                        masterData.put(x, attrs.get(x));
                            if (attrs.get(x) instanceof Map && masterData.get(x) instanceof Map) {
                                mergeCommonData((Map) masterData.get(x), (Map) attrs.get(x));
                            } else {
                                masterData.put(x, attrs.get(x));
                            }
                        } else if (attributes.containsKey(x)) {
//                        masterData.put(x, attrs.get(x));
                            if (attributes.get(x) instanceof Map && masterData.get(x) instanceof Map) {
                                mergeCommonData((Map) masterData.get(x), (Map) attributes.get(x));
                            } else {
                                masterData.put(x, attributes.get(x));
                            }
                        }
                    });
                }
                mergeCommonData(attributes, attrs);
                newAttributes = new HashMap<>(attributes);
                mergeCommonData(newAttributes, childData);
            }
            output.invoke(masterData, attributes, newAttributes == null ? new HashMap<>() : newAttributes);
        }
    }

    protected void mergeCommonData(Map<String, Object> masterNode, Map<String, Object> collectedMap) {
        for (Map.Entry<String, Object> entry : collectedMap.entrySet()) {
            String x = entry.getKey();
            if (masterNode.containsKey(entry.getKey())) {
                if (collectedMap.get(x) instanceof Map && masterNode.get(x) instanceof Map) {
                    mergeCommonData((Map)masterNode.get(x), (Map)collectedMap.get(x));
                } else {
                    masterNode.put(x, collectedMap.get(x));
                }
            } else {
                masterNode.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public TaskOutput getOutput() {
        return output;
    }

    public void setOutput(TaskOutput output) {
        this.output = output;
    }

    public List<String> getMergeData() {
        return mergeData;
    }

    public void setMergeData(List<String> mergeData) {
        this.mergeData = mergeData;
    }

    public Map<String, Object> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<String, Object> attrs) {
        this.attrs = attrs;
    }

    public List<String> getRemoveData() {
        return removeData;
    }

    public void setRemoveData(List<String> removeData) {
        this.removeData = removeData;
    }

    public Map<String, Object> getChildData() {
        return childData;
    }

    public void setChildData(Map<String, Object> childData) {
        this.childData = childData;
    }
}
