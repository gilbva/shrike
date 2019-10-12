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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.gilbva.shrikeioc.context.IocContext;
import com.github.gilbva.shrikeioc.context.IocContextFactory;
import com.github.gilbva.shrikeioc.scope.Application;

/**
 * Factory object to create the application context.
 *
 * @author Gilberto Vento
 */
public class IocContextFactoryImpl implements IocContextFactory {
    private static final Logger LOG = Logger.getLogger(IocContextFactoryImpl.class.getName());

    private static IocContextFactoryImpl INSTANCE;

    private IocContextFactoryImpl() {
    }

    public static IocContextFactory getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new IocContextFactoryImpl();
        }
        return INSTANCE;
    }

    @Override
    public IocContext<Application> createApplicationContext(Application application) {
        try {
            return new ContextImpl<>(application);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }
}
