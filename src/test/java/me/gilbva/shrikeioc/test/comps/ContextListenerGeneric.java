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

package me.gilbva.shrikeioc.test.comps;

import java.util.logging.Level;
import java.util.logging.Logger;

import me.gilbva.shrikeioc.context.IocContextListener;
import me.gilbva.shrikeioc.annotations.Component;

@Component
public class ContextListenerGeneric implements IocContextListener<Object> {
    private static final Logger LOG = Logger.getLogger(ContextListenerGeneric.class.getName());

    @Override
    public void preCreateComponent(Class<Object> clazz) {
        LOG.log(Level.INFO, "General method called for all the components preCreate: {0}", clazz.getName());
    }

    @Override
    public void preInitComponent(Class<Object> clazz, Object object) {
        LOG.log(Level.INFO, "General method called for all the components  preInit: {0}", clazz.getName());
    }

    @Override
    public void postInitComponent(Class<Object> clazz, Object object) {
        LOG.log(Level.INFO, "General method called for all the components  postInit: {0}", clazz.getName());
    }
}
