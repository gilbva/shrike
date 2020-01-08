/*
 * Copyright 2019 ShrikeIoC Framework.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.gilbva.shrikeioc.container;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.gilbva.shrikeioc.context.IocContextListener;
import me.gilbva.shrikeioc.annotations.Inject;
import me.gilbva.shrikeioc.annotations.InjectNext;

/**
 * This class is responsable of the creation of the components.
 *
 * @author Gilberto Vento
 */
class Instanciator {
    private static final Logger LOG = Logger.getLogger(Instanciator.class.getName());

    private final ContextImpl<?> context;

    private final ServiceMap serviceMap;

    private IocContextListener[] contextListeners;

    Instanciator(ContextImpl context, ServiceMap serviceMap) {
        this.context = context;
        this.serviceMap = serviceMap;
    }

    @SuppressWarnings("UseSpecificCatch")
    <T> T instantiate(Class<T> cls) {
        try {
            var constructor = context.findCache(cls).getConstructor();
            if (constructor == null) {
                return null;
            }
            constructor.trySetAccessible();
            return (T) constructor.newInstance();
        } catch (InstantiationException | IllegalArgumentException | InvocationTargetException | IllegalAccessException ex) {
            LOG.warning(ex.getMessage());
        }
        return null;
    }

    void callPostConstruct(Class cls, Object obj) {
        var currentClass = cls;
        while (!currentClass.equals(Object.class)) {
            var methods = context.findCache(currentClass).getPostConstructs();
            for (var method : methods) {
                try {
                    method.invoke(obj);
                } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
    }

    void injectDependencies(Class cls, Object obj) {
        var fields = context.findCache(cls).getInjectFields();
        for (var field : fields) {
            var annotation = field.getAnnotation(Inject.class);
            if (annotation != null) {
                injectDependency(cls, obj, field, null);
            }
            var annotationNext = field.getAnnotation(InjectNext.class);
            if (annotationNext != null) {
                injectDependency(cls, obj, field, ClassUtils.findPriority(cls));
            }
        }

        var supClass = cls.getSuperclass();
        if (supClass != null && supClass != Object.class) {
            injectDependencies(supClass, obj);
        }
    }

    private void injectDependency(Class cls, Object obj, Field field, Integer priority) {
        try {
            var service = field.getGenericType();
            Object componentObj;
            if (priority == null) {
                componentObj = context.findGeneric(service);
            } else {
                componentObj = context.findNextGeneric(service, priority);
            }

            field.set(obj, componentObj);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private void initContextListeners() {
        if (null == contextListeners) {
            contextListeners = context.findAll(IocContextListener.class);
        }
    }

    <T> void invokePreCreateListener(Class<T> cls) {
        if (IocContextListener.class.isAssignableFrom(cls)) {
            return;
        }

        initContextListeners();
        if (null != contextListeners) {
            for (var contextListener : contextListeners) {
                //find the generic parameter type of ContextListener, 
                //example ContexListener<Integer> -> type = java.lang.Integer
                var type = findGenericType(contextListener.getClass());
                if (type.equals(Object.class)) {
                    contextListener.preCreateComponent(cls);
                } else {
                    var services = serviceMap.getServices(cls);
                    if (services != null) {
                        for (var service : services) {
                            if (type.equals(service)) {
                                contextListener.preCreateComponent(cls);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    <T> void invokePreInitListener(Class<T> cls, Object instance) {
        if (IocContextListener.class.isAssignableFrom(cls)) {
            return;
        }

        initContextListeners();
        if (null != contextListeners) {
            for (var contextListener : contextListeners) {
                //find the generic parameter type of ContextListener, 
                //example ContexListener<Integer> -> type = java.lang.Integer
                var type = findGenericType(contextListener.getClass());
                if (type.equals(Object.class)) {
                    contextListener.preInitComponent(cls, instance);
                } else {
                    var services = serviceMap.getServices(cls);
                    if (services != null) {
                        for (var service : services) {
                            if (type.equals(service)) {
                                contextListener.preInitComponent(cls, instance);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    <T> void invokePostInitListener(Class<T> cls, Object instance) {
        if (IocContextListener.class.isAssignableFrom(cls)) {
            return;
        }

        initContextListeners();
        if (null != contextListeners) {
            for (var contextListener : contextListeners) {
                //find the generic parameter type of ContextListener, 
                //example ContexListener<Integer> -> type = java.lang.Integer
                var type = findGenericType(contextListener.getClass());
                if (type.equals(Object.class)) {
                    contextListener.postInitComponent(cls, instance);
                } else {
                    var services = serviceMap.getServices(cls);
                    if (services != null) {
                        for (var service : services) {
                            if (type.equals(service)) {
                                contextListener.postInitComponent(cls, instance);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private Type findGenericType(Class<? extends IocContextListener> clazz) {
        var ifcs = clazz.getGenericInterfaces();
        for (var ifc : ifcs) {
            if (ifc instanceof ParameterizedType
                    && ClassUtils.rawClass(ifc).equals(IocContextListener.class)) {
                return (((ParameterizedType) ifc).getActualTypeArguments())[0];
            }
        }
        return Object.class;
    }

}
