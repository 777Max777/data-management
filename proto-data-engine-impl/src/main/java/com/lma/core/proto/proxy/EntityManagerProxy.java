package com.lma.core.proto.proxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lma.core.proto.entity.Base;
import com.lma.core.proto.entity.Instance;
import com.lma.core.proto.transformation.BaseTransformation;
import com.lma.core.proto.transformation.InputProtoDataTransformation;
import com.lma.core.proto.transformation.OutputProtoDataTransformation;
import com.lma.core.proto.transformation.ProtoDataTransformation;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EntityManagerProxy implements InvocationHandler {

    private EntityManager entityManager;
    private BaseTransformation baseTransformation;

    protected ProtoDataTransformation inputTransformation;
    protected ProtoDataTransformation outputTransformation;

    public EntityManagerProxy(EntityManager entityManager, Set<String> packages) {
        this.entityManager = entityManager;
        this.baseTransformation = new BaseTransformation(new ObjectMapper());
        this.inputTransformation = new InputProtoDataTransformation(packages);
        this.outputTransformation = new OutputProtoDataTransformation(packages);
    }

    public static EntityManager proxiedEntityManager(EntityManager entityManager, Set<String> packages) {
        return (EntityManager) Proxy.newProxyInstance(entityManager.getClass().getClassLoader(),
                entityManager.getClass().getInterfaces(),
                new EntityManagerProxy(entityManager, packages));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        Parameter[] parameters = method.getParameters();

        if (parameters != null
                && !Arrays.asList(parameters).isEmpty()
                && args != null
                && args.length != 0) {

            Object[] protoArgs = Arrays.copyOf(args, args.length);
            if (parameters[0].getType() == Object.class) {
                resolveInputData(protoArgs);
                result = method.invoke(entityManager, protoArgs);
            } else {
                if (parameters[0].getType() == Class.class || method.getName().equals("merge")) {
                    protoArgs[0] = Instance.class;
                    Instance protoData = (Instance) method.invoke(entityManager, protoArgs);
                    resolveOutputData(protoData, result);
                    return result;
                } else {
                    result = method.invoke(entityManager, protoArgs);
                }
            }

            if (Base.class.isAssignableFrom(args[0].getClass())) {
                resolveOutputData((Instance) protoArgs[0], args[0]);
//                ((Base)args[0]).setId(((Base)protoArgs[0]).getId());
            }
        } else {
            result = method.invoke(entityManager, args);
        }

        return result;
    }

//    private boolean isSpecialMethod(Method m) {
//        if (specialMethods.contains(m)) {
//            return true;
//        }
//        return false;
//    }

    private void resolveInputData(Object[] args) {
        Instance instance = new Instance();
        inputTransformation.resolveEntity(args[0], instance);
        args[0] = instance;
    }

    private void resolveOutputData(Instance protoData, Object output) {
        outputTransformation.resolveEntity(output, protoData);
    }

}
