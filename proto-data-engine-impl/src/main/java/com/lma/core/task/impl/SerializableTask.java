package com.lma.core.task.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.lma.core.task.api.InitData;
import com.lma.core.task.api.ReMapField;
import com.lma.core.task.api.Task;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SerializableTask extends Task implements Serializable {

    protected List<List<String>> removeNestedField;
    /**
     * Key of map contain name field for "local variables" (attributes parameter in {@value accept} method)
     * and value is hierarchical level nested fields inside "masterData" map (parameter in {@value accept} method also)
     */
    protected Map<String, List<String>> includeNestedField;
    /**
     * The Task can have only one result
     * List mean hierarchical level nested fields inside map
     */
    protected List<String> resultNestedField;
    /**
     * targetPath contain path to data and rewrite it by rewritePath value map
     * then it will remap data
     * priority:
     * 1) attributes
     * 2) masterData
     */
    protected List<ReMapField> reMapFields;

    protected List<InitData> initData;

    private Optional findByKey(String key, Map<String, Object> attributes) {
        if (attributes.containsKey(key)) {
            return Optional.of(attributes.get(key));
        }
        return Optional.empty();
    }

    protected <T> Optional<T> find(Class<T> clasS, Iterator<String> iterField, Map<String, Object> attributes) {
        Optional found = findByKey(iterField.next(), attributes);
        if (found.isPresent()) {
            if (found.get() instanceof Map && iterField.hasNext())
                return find(clasS, iterField, (Map<String, Object>) found.get());
            else if (clasS.isAssignableFrom(found.get().getClass()))
                return found;
            else
                return Optional.empty();
        }
        return Optional.empty();
    }
/*
    protected Optional find(List<String> fields, Map<String, Object> attributes) {
        Map<String, Object> temporaryMap = new HashMap<>(attributes);
        if (MapUtils.isNotEmpty(attributes)) {
            int i = 0;
            for (; i < fields.size()-1; i++) {
                Optional found = findByKey(fields.get(i), temporaryMap);
                if (found.isPresent()) {
                    if (found.get() instanceof Map)
                        temporaryMap = (Map<String, Object>) found.get();
                    else if (i+1 == fields.size())
                        return found;
                    else 
                        return Optional.empty();
                } else
                    break;
            }
        }
        return Optional.empty();
    }
*/

    protected <T> void find(
            Class<T> clasS,
            Collection<String> fields,
            Map<String, Object> masterData,
            Map<String, Object> attributes,
            Consumer<T> consumer) {
        Optional<T> option = find(clasS, fields.iterator(), attributes);
        if (option.isPresent()) {
            consumer.accept(option.get());
        } else {
            option = find(clasS, fields.iterator(), masterData);
            if (option.isPresent()) {
                consumer.accept(option.get());
            }
        }
    }

    protected <T> Optional<T> find(
            Class<T> clasS,
            Collection<String> fields,
            Map<String, Object> masterData,
            Map<String, Object> attributes) {
        Optional<T> option = find(clasS, fields.iterator(), attributes);
        if (!option.isPresent()) {
            option = find(clasS, fields.iterator(), masterData);
        }
        return option;
    }

    private Map<String, Object> mapWrapper(Object value, List<String> nestedFields) {
        if (CollectionUtils.isNotEmpty(nestedFields)) {
            Map<String, Object> map = new HashMap<>();
            map.put(nestedFields.get(nestedFields.size() - 1), value);

            for (int i = nestedFields.size() - 2; i >= 0; i--) {
                Map<String, Object> temporaryMap = new HashMap<>();
                temporaryMap.put(nestedFields.get(i), map);
                map = temporaryMap;
            }
            return map;
        }
        return null;
    }

    protected void applyResult(Object result) {
        if (MapUtils.isEmpty(this.attrs)) this.attrs = new HashMap<>();
        if (result != null) {
            Map resultMap = mapWrapper(result, resultNestedField);
            if (MapUtils.isNotEmpty(resultMap)) mergeCommonData(this.attrs, resultMap);
        }
    }

    @Override
    public void accept(Map<String, Object> masterData, Map<String, Object> attributes) {

        if (CollectionUtils.isNotEmpty(reMapFields)) {
            for (ReMapField reMapField : reMapFields) {
                if (CollectionUtils.isNotEmpty(reMapField.getTargetPath())
                        && CollectionUtils.isNotEmpty(reMapField.getRewritePath())) {
                    List<String> targetPath = reMapField.getTargetPath();
                    List<String> rewritePath = reMapField.getRewritePath();
                    Consumer<Map> consumer = map -> {
                        String targetKey = targetPath.get(targetPath.size()-1);
                        if (StringUtils.isNotBlank(targetKey)) {
                            Object value = map.get(targetKey);
                            if (value != null) {
                                Optional<Map> rewriteOption = Optional.empty();
                                if (rewritePath.size() > 1) {
                                    rewriteOption = find(Map.class, rewritePath.subList(0, rewritePath.size() - 1), masterData, attributes);
                                } else if (attributes.containsKey(rewritePath.get(0))) {
                                    rewriteOption = Optional.of(attributes);
                                } else if (masterData.containsKey((rewritePath.get(0)))) {
                                    rewriteOption = Optional.of(masterData);
                                }
                                rewriteOption.ifPresent(rewriteMap -> {
                                    String rewriteKey = rewritePath.get(rewritePath.size() - 1);
                                    if (StringUtils.isNotBlank(rewriteKey)) {
                                        rewriteMap.put(rewriteKey, value);
                                    }
                                });
                            }
                        }
                    };
                    if (targetPath.size() > 1) {
                        find(Map.class, targetPath.subList(0, targetPath.size() - 1), masterData, attributes, consumer);
                    } else if (targetPath.size() > 0) {
                        if (attributes.containsKey(targetPath.get(0))) {
                            consumer.accept(attributes);
                        } else if (masterData.containsKey(targetPath.get(0))) {
                            consumer.accept(masterData);
                        }
                    }
                }
            }
        }

        if (MapUtils.isNotEmpty(includeNestedField)) {
            for (Map.Entry<String, List<String>> entry : includeNestedField.entrySet()) {
                if (CollectionUtils.isNotEmpty(entry.getValue())) {
                    List<String> path = entry.getValue();
                    if (path.size() > 1) {
                        Optional<Map> option = find(Map.class, path.subList(0, path.size() - 1).iterator(), masterData);
                        option.ifPresent(map -> {
                            String key = path.get(path.size() - 1);
                            if (StringUtils.isNotBlank(key)) {
                                Object value = map.get(key);
                                if (StringUtils.isNotBlank(entry.getKey()) && value != null) {
                                    this.childData.put(entry.getKey(), value);
                                }
                            }
                        });
                    } else if (path.size() > 0) {
                        this.childData.put(entry.getKey(), masterData.get(0));
                    }
                }
            }
        }

        if (CollectionUtils.isNotEmpty(initData)) {
            if (attrs == null) {
                attrs = new HashMap<>();
            }
            for (InitData initData : initData) {
                if (CollectionUtils.isNotEmpty(initData.getPath())
                        && initData != null) {
                    Map<String, Object> initMap = mapWrapper(initData, initData.getPath());
                    mergeCommonData(attrs, initMap);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(removeNestedField)) {
            for (List<String> nestedFields : removeNestedField) {
                if (CollectionUtils.isNotEmpty(nestedFields)) {
                    if (nestedFields.size() > 1) {
                        Optional<Map> option = find(Map.class, nestedFields.subList(0, nestedFields.size() - 1).iterator(), masterData);
                        option.ifPresent(map -> map.remove(nestedFields.get(nestedFields.size() - 1)));
                    } else if (nestedFields.size() > 0) {
                        masterData.remove(nestedFields.get(0));
                    }
                }
            }
        }

        super.accept(masterData, attributes);
    }

    public List<List<String>> getRemoveNestedField() {
        return removeNestedField;
    }

    public void setRemoveNestedField(List<List<String>> removeNestedField) {
        this.removeNestedField = removeNestedField;
    }

    public Map<String, List<String>> getIncludeNestedField() {
        return includeNestedField;
    }

    public void setIncludeNestedField(Map<String, List<String>> includeNestedField) {
        this.includeNestedField = includeNestedField;
    }

    public List<String> getResultNestedField() {
        return resultNestedField;
    }

    public void setResultNestedField(List<String> resultNestedField) {
        this.resultNestedField = resultNestedField;
    }

    public List<ReMapField> getReMapFields() {
        return reMapFields;
    }

    public void setReMapFields(List<ReMapField> reMapFields) {
        this.reMapFields = reMapFields;
    }

    public List<InitData> getInitData() {
        return initData;
    }

    public void setInitData(List<InitData> initData) {
        this.initData = initData;
    }
}
