package com.lma.core.proto.transformation;

import com.lma.core.proto.annotation.Entity;
import com.lma.core.proto.annotation.Relation;
import com.lma.core.proto.entity.Base;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractProtoDataTransformation implements ProtoDataTransformation {

    protected Set<Class<?>> entities;
    protected Reflections reflections;

    public AbstractProtoDataTransformation() {
    }

    public AbstractProtoDataTransformation(Set<String> packages) {
        initReflections(packages);
        initEntities();
    }

    private void initEntities() {
        entities = reflections.getTypesAnnotatedWith(Entity.class);
    }

    private void initReflections(Set<String> packages) {
        Collection<URL> urlPackages = packages.stream()
                .map(p -> ClasspathHelper.forPackage(p))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        reflections = new Reflections(new ConfigurationBuilder()
                .addUrls(urlPackages));
    }

    protected boolean validateObject(Object obj) {
        boolean isValidClass = Arrays.stream(obj.getClass().getAnnotations())
                .anyMatch(a -> a instanceof Entity
                        && StringUtils.isNotBlank(((Entity)a).value())
                        && Base.class.isAssignableFrom(obj.getClass()));

        if (!isValidClass) return false;

        Optional wrongFields = Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(field -> Arrays.stream(field.getDeclaredAnnotations()).anyMatch(a -> a instanceof Relation
                        && StringUtils.isBlank(((Relation)a).value())))
                .findFirst();
        if (wrongFields.isPresent()) return false;
        return true;
    }

    protected List<Field> findRelatedFields(Object obj) {
        return Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(field -> Arrays.stream(field.getDeclaredAnnotations()).anyMatch(a -> a instanceof Relation))
                .collect(Collectors.toList());
    }
}
