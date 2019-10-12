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

package com.github.gilbva.shrikeioc;

import com.github.gilbva.shrikeioc.container.IocContextFactoryImpl;
import com.github.gilbva.shrikeioc.context.IocContext;
import com.github.gilbva.shrikeioc.context.IocContextFactory;
import com.github.gilbva.shrikeioc.scope.Application;

/**
 * Facade for the Shrike IoC API.
 * <p>
 * This class provides the method context() which will deliver the IocContext
 * for the application scope.
 */
public class Shrike {
    private static IocContext<Application> appContext;

    private static IocContextFactory findFactory() {
        return IocContextFactoryImpl.getInstance();
    }

    /**
     * Private constructor so this object cannot be instantiated.
     */
    private Shrike() {
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
}
