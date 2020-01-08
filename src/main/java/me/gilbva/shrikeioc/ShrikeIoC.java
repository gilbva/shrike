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

package me.gilbva.shrikeioc;

import me.gilbva.shrikeioc.container.IocContextFactoryImpl;
import me.gilbva.shrikeioc.context.IocContext;
import me.gilbva.shrikeioc.context.IocContextFactory;
import me.gilbva.shrikeioc.navigation.ClassRepository;
import me.gilbva.shrikeioc.scope.Application;
import me.gilbva.shrikeioc.scope.Scope;

import java.io.PrintWriter;
import java.lang.reflect.Type;

/**
 * Facade for the Shrike IoC API.
 * <p>
 * This class provides the method context() which will deliver the IocContext
 * for the application scope.
 *
 * @author Gilberto Vento
 */
public class ShrikeIoC {
    private static IocContext<Application> appContext;

    private static IocContextFactory findFactory() {
        return IocContextFactoryImpl.getInstance();
    }

    /**
     * Private constructor so this object cannot be instantiated.
     */
    private ShrikeIoC() {
    }

    /**
     * This method returns the {@link IocContext} for the APPLICATION scoped
     * {@link IocContext}.
     * <p>
     *
     * @return The APPLICATION scoped {@link IocContext} instance for this
     * application.
     */
    public static IocContext<Application> context() {
        if (appContext == null) {
            var factory = findFactory();
            if (factory == null) {
                String message = "IoC container provider service was not found on the class path.";
                message += " You must include an IoC container dependency like 'shrike-ioc-container' in your class path.";
                throw new IllegalStateException(message);
            }
            appContext = factory.createApplicationContext(Application.getInstance());
        }
        return appContext;
    }

    /**
     * This method finds the highest priority component that provides the given
     * service.
     * <p>
     *
     * @param <T>     The generic type of the class of the service that this method
     *                should find.
     * @param service The class that represents the service that this method
     *                must find.
     *                <p>
     * @return An object that extends or implement the class of the service
     * provided, or null if no component provides this services in the context.
     */
    public static <T> T find(Class<T> service) {
        return context().find(service);
    }

    /**
     * This method finds the component that provides the given service with less
     * priority than the priority parameter.
     * <p>
     *
     * @param <T>      The generic type of the class of the service that this method
     *                 should find.
     * @param service  The class that represents the service that this method
     *                 must find.
     *                 <p>
     * @param priority The given component must have a priority value greater
     *                 than this parameter.
     *                 <p>
     * @return An object that extends or implement the class of the service
     * provided, or null if no component provides this services in the context.
     */
    public static <T> T findNext(Class<T> service, int priority) {
        return context().findNext(service, priority);
    }

    /**
     * This method finds all the components that provides the given service.
     * <p>
     *
     * @param <T>     The generic type of the class of the service that this method
     *                should find.
     * @param service The class that represents the service that this method
     *                must find.
     *                <p>
     * @return An array of objects who extends or implement the class of the
     * service provided, or an empty array if no component provides this
     * services in the context.
     */
    public static <T> T[] findAll(Class<T> service) {
        return context().findAll(service);
    }

    /**
     * This method finds the highest priority component that provides the given
     * generic service.
     * <p>
     *
     * @param service The {@link java.lang.reflect.Type} that represents the
     *                service that this method must find.
     *                <p>
     * @return An object that extends or implement the service provided, or null
     * if no component provides this services in the context.
     */
    public static Object findGeneric(Type service) {
        return context().findGeneric(service);
    }

    /**
     * This method finds the component that provides the given generic service
     * with less priority than the priority parameter.
     * <p>
     *
     * @param service  The {@link java.lang.reflect.Type} that represents the
     *                 service that this method must find.
     *                 <p>
     * @param priority The given component must have a priority value greater
     *                 than this parameter.
     *                 <p>
     * @return An object that extends or implement the service provided, or null
     * if no component provides this services in the context.
     */
    public static Object findNextGeneric(Type service, int priority) {
        return context().findNextGeneric(service, priority);
    }

    /**
     * This method finds if a service is provided by a least one component in
     * the context.
     * <p>
     *
     * @param service The type of the service to look for.
     *                <p>
     * @return {@literal true} If at least one component provides this service,
     * {@literal false} otherwise.
     */
    public static boolean exists(Type service) {
        return context().exists(service);
    }

    /**
     * This method finds if the given class is a component of the context.
     * <p>
     *
     * @param component The class of the component to look for.
     *                  <p>
     * @return {@literal true} If this class represents a component of the
     * context, {@literal false} otherwise.
     */
    boolean existsComponent(Class<?> component) {
        return context().existsComponent(component);
    }

    /**
     * The parent of this context.
     * <p>
     *
     * @return The IocContext instance representing the parent of this context,
     * or null if this context has no parent.
     */
    public static IocContext<?> getParent() {
        return context().getParent();
    }

    /**
     * Create a child IocContext of this context.
     * <p>
     *
     * @param <T>   The type of the scope.
     * @param scope The scope of the new context.
     * @return The new IocContext instance created as child of this context.
     */
    public static <T extends Scope> IocContext<T> createChild(T scope) {
        return context().createChild(scope);
    }

    /**
     * Obtains the class repository associated with this context. that allows to
     * find classes, fields and methods of the components in this context.
     * <p>
     *
     * @return A ClassRepository instance
     */
    public static ClassRepository getClassRepository() {
        return context().getClassRepository();
    }

    /**
     * Prints all the implementations of the services with its priorities.
     *
     * @param service The service to lookup.
     * @param writer  The writer to print the result.
     */
    void printPriorities(Class<?> service, PrintWriter writer) {
        context().printPriorities(service, writer);
    }
}
