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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache for scope classes.
 */
class ScopeCache {
    private final Map<Class<?>, ClassCache> classMap;

    /**
     * Default constructor.
     */
    ScopeCache() {
        classMap = new ConcurrentHashMap<>();
    }

    /**
     * Gets the class cache for the given scope class.
     *
     * @param cls The scope class.
     * @return The class cache.
     */
    ClassCache getCache(Class<?> cls) {
        var cache = classMap.get(cls);
        if (cache == null) {
            cache = new ClassCache(cls);
            classMap.put(cls, cache);
        }
        return cache;
    }
}
