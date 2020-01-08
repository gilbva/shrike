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

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.gilbva.shrikeioc.context.IocContext;

/**
 * An object to keep track of the services of the components.
 *
 * @author Gilberto Vento
 */
class ServiceMap {
    /**
     * A cache for the ServiceMap of all scopes.
     */
    private static final Map<Class<?>, ServiceMap> SERVICES_MAP = new ConcurrentHashMap<>();

    /**
     * The service map, who links a services to a list of components.
     */
    private final Map<Type, List<Class<?>>> map;

    /**
     * A map who links the components to a list of all it´s services.
     */
    private final Map<Class<?>, List<Type>> compMap;

    /**
     * Constructor for this class.
     *
     * @param clsSet The components to create this services map for.
     */
    ServiceMap(ClassSet clsSet) {
        var servMap = new HashMap<Type, List<Class<?>>>();
        var compsMap = new HashMap<Class<?>, List<Type>>();
        if (clsSet != null) {
            for (var component : clsSet) {
                var services = findServices(component);
                compsMap.put(component, services);
                services.stream()
                        .forEach(s -> addComponentToService(servMap, s, component));
            }
        }
        servMap.values()
                .stream()
                .forEach(ClassUtils::sort);
        this.map = servMap;
        this.compMap = compsMap;
    }

    /**
     * Finds the first component class by the given service type.
     *
     * @param service The type of the service.
     * @return The component class or null if none can be found.
     */
    Class<?> findOne(Type service) {
        return findOne(service, null);
    }

    /**
     * Finds the first component class by the given service type.
     *
     * @param service  The type of the service.
     * @param priority The priority
     * @return The component class or null if none can be found.
     */
    Class<?> findOne(Type service, Integer priority) {
        if (ClassUtils.rawClass(service).equals(IocContext.class)) {
            return map.get(ClassUtils.rawClass(service)).get(0);
        }
        var lst = map.get(service);
        if (lst == null || lst.isEmpty()) {
            return null;
        }
        if (priority == null) {
            return lst.get(0);
        } else {
            for (var cls : lst) {
                var v1 = ClassUtils.findPriority(cls);
                if (v1 > priority || v1 == Integer.MAX_VALUE) {
                    return cls;
                }
            }
        }
        return null;
    }

    /**
     * Determines whenever a service is provided by a least one component.
     *
     * @param service The serivce to look for.
     * @return true at least one component provides the given service.
     */
    boolean exists(Type service) {
        return map.containsKey(service);
    }

    /**
     * Finds all components classes by the given service type.
     *
     * @param service The service to lookup
     * @return The list of components that provides the given service if any.
     */
    List<Class<?>> findAll(Type service) {
        var realService = service;
        if (service instanceof WildcardType) {
            realService = ClassUtils.typeOf((WildcardType) service);
        }
        if (realService != null) {
            var result = map.get(realService);
            if (result == null) {
                return null;
            }
            return Collections.unmodifiableList(result);
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * Gets the service map by the given scope.
     *
     * @param scope The scope to look for.
     * @return A service map with all the service in the given scope or null if
     * the given scope has no components.
     */
    static ServiceMap findByScope(Class<?> scope) {
        if (!SERVICES_MAP.containsKey(scope)) {
            var classSet = ClassSet.findByScope(scope);
            if (classSet != null) {
                var result = new ServiceMap(classSet);
                SERVICES_MAP.put(scope, result);
                return result;
            }
            return null;
        }
        return SERVICES_MAP.get(scope);
    }

    /**
     * Gets all the services provided by a component.
     *
     * @param component The component to look for.
     * @return The list of service the especified component provides.
     */
    List<Type> getServices(Class<?> component) {
        return compMap.get(component);
    }

    /**
     * Creates a list of all services the especified component provides.
     *
     * @param component The component to search.
     * @return The list of all services the especified component provides.
     */
    private static List<Type> findServices(Class<?> component) {
        var result = new ArrayList<Type>();
        result.add(Object.class);
        result.add(component);
        fillServicesSuperClasses(component, result);
        fillServicesIntefaces(component, result);
        return result;
    }

    /**
     * Fills all the services provided by all supper classes of the component.
     *
     * @param component    The component to look for it´s services for.
     * @param servicesList The list to put all finded services.
     */
    private static void fillServicesSuperClasses(Class<?> component, List<Type> servicesList) {
        var supClass = component.getGenericSuperclass();
        while (supClass != null && supClass != Object.class) {
            if (!ClassUtils.hasGenericDeclaration(supClass)
                    && !servicesList.contains(supClass)) {
                servicesList.add(supClass);
            }
            var cls = ClassUtils.rawClass(supClass);
            servicesList.add(cls);
            fillServicesIntefaces(cls, servicesList);
            if (cls != null) {
                supClass = cls.getGenericSuperclass();
            } else {
                supClass = null;
            }
        }
    }

    /**
     * Fills all the services provided by all supper interfaces of the
     * component.
     *
     * @param servicesList The list to put all finded services.
     */
    private static void fillServicesIntefaces(Class<?> cls, List<Type> servicesList) {
        var interfaces = cls.getGenericInterfaces();
        for (var ifc : interfaces) {
            if (!ClassUtils.hasGenericDeclaration(ifc)
                    && !servicesList.contains(ifc)) {
                servicesList.add(ifc);
            }
            var icfCls = ClassUtils.rawClass(ifc);
            servicesList.add(icfCls);
            if (icfCls != null) {
                fillServicesIntefaces(icfCls, servicesList);
            }
        }
    }

    /**
     * Adds a component to a list of components that provides the given service.
     *
     * @param service   The service provided
     * @param component The component who provides the given services.
     */
    private static void addComponentToService(Map<Type, List<Class<?>>> servMap, Type service, Class<?> component) {
        var components = servMap.computeIfAbsent(service, k -> new ArrayList<>());
        if (!components.contains(component)) {
            components.add(component);
        }
    }

}
