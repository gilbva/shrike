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

package me.gilbva.shrike.test;

import me.gilbva.shrike.Shrike;
import me.gilbva.shrike.test.chain.ChainTest;
import me.gilbva.shrike.test.comps.ComponentChild;
import me.gilbva.shrike.test.comps.SomeService;

import java.io.IOException;

import me.gilbva.shrike.test.comps.ComplexInjectComponent;
import me.gilbva.shrike.test.comps.ComponentBaseInterface;
import me.gilbva.shrike.test.comps.ConcreteComponent;
import me.gilbva.shrike.test.comps.DummyComponent;
import me.gilbva.shrike.test.comps.DummyServiceProvider;
import me.gilbva.shrike.test.comps.DummyServiceProvider2;
import me.gilbva.shrike.test.comps.GenericComponent;
import me.gilbva.shrike.test.comps.GenericInjectComponent;
import me.gilbva.shrike.test.context.ContextInject;
import me.gilbva.shrike.test.priority.PriorityComp1;
import me.gilbva.shrike.test.priority.PriorityComp2;
import me.gilbva.shrike.test.priority.PriorityComp3;
import me.gilbva.shrike.test.priority.PriorityComp4;
import me.gilbva.shrike.test.priority.PriorityService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class IocContextImplTest {
    @Test
    public void testFind() {
        var instance = Shrike.context();
        var result = instance.find(DummyComponent.class);
        assertNotNull(result);
    }

    @Test
    public void testFindByService() {
        var result = Shrike.find(SomeService.class);
        assertNotNull(result);
        assertTrue(result instanceof DummyServiceProvider);

        var resultArr = Shrike.findAll(SomeService.class);
        assertNotNull(resultArr);
        assertEquals(2, resultArr.length);
        assertTrue(resultArr[0] instanceof DummyServiceProvider);
        assertTrue(resultArr[1] instanceof DummyServiceProvider2);
    }

    @Test
    public void testInjectAndHerarchy() {
        var conComp = Shrike.find(ConcreteComponent.class);
        assertNotNull(conComp.getDummyComponent());
        assertNotNull(conComp.getServices());
        assertTrue(conComp.getServices()[0] instanceof DummyServiceProvider);
        assertTrue(conComp.getServices()[1] instanceof DummyServiceProvider2);
    }

    @Test
    public void testInjectGeneric() {
        var giComp = Shrike.find(GenericInjectComponent.class);
        assertNotNull(giComp);
        assertNotNull(giComp.getGsOfStr());
        assertNull(giComp.getGsOfObject());
        assertTrue(giComp.getGsOfStr() instanceof GenericComponent);
        assertNotNull(giComp.getComplexInject());
        assertTrue(giComp.getComplexInject() instanceof ComplexInjectComponent);
    }

    @Test
    public void testPriority() {
        var prorityArr = Shrike.findAll(PriorityService.class);
        assertTrue(prorityArr[0] instanceof PriorityComp3);
        assertTrue(prorityArr[1] instanceof PriorityComp1);
        assertTrue(prorityArr[2] instanceof PriorityComp4);
        assertTrue(prorityArr[3] instanceof PriorityComp2);

        assertTrue(Shrike.find(PriorityService.class) instanceof PriorityComp3);
    }

    @Test
    public void testChain() {
        var chainTest = Shrike.find(ChainTest.class);

        assertEquals("1 2 3", chainTest.execute(), "Wrong Chain");
    }

    @Test
    public void testComponentChild() throws IOException {
        var childTest = Shrike.find(ComponentBaseInterface.class);

        assertNotNull(childTest);
        assertTrue(childTest instanceof ComponentChild);
    }

    @Test
    public void testInjectContext() {
        var ctxInj = Shrike.find(ContextInject.class);
        assertNotNull(ctxInj.getAppCtx());
        assertEquals(Shrike.context(), ctxInj.getAppCtx());
    }
}
