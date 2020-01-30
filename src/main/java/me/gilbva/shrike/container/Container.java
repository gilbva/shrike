/*
 * Copyright 2019 Shrike Framework.
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

package me.gilbva.shrike.container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is responsable for holding references to the instantiated
 * components in a ioc context.
 *
 * @author Gilberto Vento
 */
class Container {
    /**
     * A map containing the class of the component and it´s instance for all
     * instantiated components in the context.
     */
    private final Map<Class, Object> components;

    /**
     * The guy responsable for instantiate a component.
     */
    private final Instanciator instanciator;

    /**
     * The constuctor for this container.
     *
     * @param creator   The instance of the object whose responsibility will be to
     *                  create the components.
     * @param instances The components that are already instantiated.
     */
    Container(Instanciator creator, Object... instances) {
        this.components = new ConcurrentHashMap<>();
        this.instanciator = creator;
        for (var instance : instances) {
            components.put(instance.getClass(), instance);
        }
    }

    /**
     * Determines if a component is already instantiated in this container.
     *
     * @param cls The class of the component.
     * @return true the component is already instantiated, false otherwise.
     */
    public boolean contains(Class cls) {
        return components.containsKey(cls);
    }

    /**
     * Gets a component´s instance by it´s class.
     *
     * @param <T> The type of the component.
     * @param cls The class of the component.
     * @return The component´s instance, or null if it does not exists.
     */
    public <T> T get(Class<T> cls) {
        return (T) components.get(cls);
    }

    /**
     * Creates a component using the internal instantiator, and put it in the
     * internal map for future use.
     *
     * @param <T> The type of the component.
     * @param cls The class of the component.
     * @return The new create component of null if the component cannot be
     * created.
     */
    <T> T create(Class<T> cls) {
        if (components.containsKey(cls)) {
            return (T) components.get(cls);
        } else {
            instanciator.invokePreCreateListener(cls);
            var obj = instanciator.instantiate(cls);
            if (obj == null) {
                return null;
            }
            components.put(cls, obj);

            instanciator.invokePreInitListener(cls, obj);
            instanciator.injectDependencies(cls, obj);

            instanciator.invokePostInitListener(cls, obj);
            instanciator.callPostConstruct(cls, obj);
            return obj;
        }
    }

}
