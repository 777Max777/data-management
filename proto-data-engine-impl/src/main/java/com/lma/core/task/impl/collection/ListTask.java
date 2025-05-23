package com.lma.core.task.impl.collection;

import com.lma.core.task.impl.SerializableTask;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class ListTask extends SerializableTask {

    private String method;
    private Object element;

    @Override
    public void accept(Map<String, Object> masterData, Map<String, Object> attributes) {
        /*if (CollectionUtils.isNotEmpty(nestedFields)) {
            Optional<Collection> collection = find(Collection.class, nestedFields, attributes);

            if (!collection.isPresent()) {
                collection = find(Collection.class, nestedFields, attributes);
            }

            if (MapUtils.isNotEmpty(getInitValues())) {
                Stream.of(this.getClass().getDeclaredFields())
                        .forEach(field -> {
                            String strValue = getInitValue(field.getName());
                            if (StringUtils.isNotBlank(strValue)) {
                                Object value = null;
                                if (attributes.containsKey(strValue)) {
                                    value = attributes.get(strValue);
                                } else if (masterData.containsKey(strValue)) {
                                    value = masterData.get(strValue);
                                }
                                if (value != null) {
                                    element = value;
                                }
                            }
                        });
            }

            if (StringUtils.isNotBlank(method)
                    && element != null
                    && collection.isPresent()) {
                List array = new ArrayList(collection.get());
                for (Method method : List.class.getMethods()) {
                    if (method.getName().equals(method)) {
                        try {
                            method.invoke(array, element);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
                Map result = mapWrapper(array);
                if (MapUtils.isNotEmpty(result)) {
                    this.attrs.putAll(result);
                }
            }

        }*/

        super.accept(masterData, attributes);
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getElement() {
        return element;
    }

    public void setElement(Object element) {
        this.element = element;
    }
}
