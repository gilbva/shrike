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

class GlobalCache {
    private static GlobalCache INSTANCE;

    private final Map<Class<?>, ScopeCache> scopeMap;

    public static GlobalCache instance() {
        if (INSTANCE == null) {
            INSTANCE = new GlobalCache();
        }
        return INSTANCE;
    }

    private GlobalCache() {
        scopeMap = new ConcurrentHashMap<>();
    }

    ScopeCache getScope(Class<?> scope) {
        var cache = scopeMap.get(scope);
        if (cache == null) {
            cache = new ScopeCache();
            scopeMap.put(scope, cache);
        }
        return cache;
    }

}
