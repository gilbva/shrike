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

package com.github.gilbva.shrikeioc.container;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.github.gilbva.shrikeioc.annotations.Inject;
import com.github.gilbva.shrikeioc.annotations.InjectNext;
import com.github.gilbva.shrikeioc.annotations.ComponentInit;

class ClassCache {
    private final List<Field> injectFields;

    private final Constructor constructor;

    private final List<Method> postConstructs;

    ClassCache(Class<?> cls) {
        injectFields = createInjectFields(cls);
        constructor = findConstructor(cls);
        postConstructs = findPostConstructs(cls);
    }

    List<Field> getInjectFields() {
        return injectFields;
    }

    Constructor getConstructor() {
        return constructor;
    }

    List<Method> getPostConstructs() {
        return postConstructs;
    }

    private List<Field> createInjectFields(Class<?> cls) {
        var result = new ArrayList<Field>();
        var declaredFields = cls.getDeclaredFields();
        for (var field : declaredFields) {
            var annotation = field.getAnnotation(Inject.class);
            var annotationNext = field.getAnnotation(InjectNext.class);
            if (annotation != null) {
                field.trySetAccessible();
                result.add(field);
            } else if (annotationNext != null) {
                field.trySetAccessible();
                result.add(field);
            }
        }
        return result;
    }

    private Constructor findConstructor(Class<?> cls) {
        for (var cons : cls.getDeclaredConstructors()) {
            if (cons.getParameterTypes().length == 0) {
                cons.trySetAccessible();
                return cons;
            }
        }
        return null;
    }

    private List<Method> findPostConstructs(Class<?> cls) {
        var result = new ArrayList<Method>();
        var methods = cls.getDeclaredMethods();
        for (var method : methods) {
            var annotation = method.getAnnotation(ComponentInit.class);
            if (annotation != null) {
                method.trySetAccessible();
                result.add(method);
            }
        }
        return result;
    }

}
