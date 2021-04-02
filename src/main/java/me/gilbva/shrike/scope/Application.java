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

package me.gilbva.shrike.scope;

/**
 * This class represents the application scope witch is the root context of
 * Shrike IoC.
 *
 * @author Gilberto Vento
 */
public final class Application implements Scope {
    private static Application INSTANCE;

    /**
     * Gets the internal Application instance.
     *
     * @return The internal Application instance.
     */
    public static Application getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Application();
        }
        return INSTANCE;
    }

    /**
     * Default constructor protected, Only for package access.
     */
    private Application() {
        //Protected constructor
    }

    @Override
    public void preCreateComponent(Class<Object> clazz) {
        //Before creating an Application scoped component
    }

    @Override
    public void preInitComponent(Class<Object> clazz, Object instance) {
        //Before init an Application scoped component
    }

    @Override
    public void postInitComponent(Class<Object> clazz, Object instance) {
        //After init an Application scoped component
    }
}
