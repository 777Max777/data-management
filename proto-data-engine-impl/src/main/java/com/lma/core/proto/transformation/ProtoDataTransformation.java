package com.lma.core.proto.transformation;

import com.lma.core.proto.entity.Instance;

public interface ProtoDataTransformation {

    <T, I extends Instance> void resolveEntity(T obj1, I obj2);

    <T, I extends Instance> void resolveRelations(T obj1, I obj2);
}
