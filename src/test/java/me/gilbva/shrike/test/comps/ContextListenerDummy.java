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

package me.gilbva.shrike.test.comps;

import java.util.logging.Logger;

import me.gilbva.shrike.context.IocContextListener;
import me.gilbva.shrike.annotations.Component;

@Component
public class ContextListenerDummy implements IocContextListener<DummyComponent> {
    private static final Logger LOG = Logger.getLogger(ContextListenerDummy.class.getName());

    @Override
    public void preCreateComponent(Class<DummyComponent> clazz) {
        LOG.info("This method is called only when DummyComponent is preCreate");
    }

    @Override
    public void preInitComponent(Class<DummyComponent> clazz, DummyComponent instance) {
        LOG.info("This method is called only when DummyComponent is preInit");
    }

    @Override
    public void postInitComponent(Class<DummyComponent> clazz, DummyComponent instance) {
        LOG.info("This method is called only when DummyComponent is postInit");
    }
}
