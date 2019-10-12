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

package com.github.gilbva.shrikeioc.test;

import com.github.gilbva.shrikeioc.ShrikeIoC;
import com.github.gilbva.shrikeioc.test.chain.ChainTest;
import com.github.gilbva.shrikeioc.test.comps.ComponentChild;
import com.github.gilbva.shrikeioc.test.comps.SomeService;

import java.io.IOException;

import com.github.gilbva.shrikeioc.test.comps.ComplexInjectComponent;
import com.github.gilbva.shrikeioc.test.comps.ComponentBaseInterface;
import com.github.gilbva.shrikeioc.test.comps.ConcreteComponent;
import com.github.gilbva.shrikeioc.test.comps.DummyComponent;
import com.github.gilbva.shrikeioc.test.comps.DummyServiceProvider;
import com.github.gilbva.shrikeioc.test.comps.DummyServiceProvider2;
import com.github.gilbva.shrikeioc.test.comps.GenericComponent;
import com.github.gilbva.shrikeioc.test.comps.GenericInjectComponent;
import com.github.gilbva.shrikeioc.test.context.ContextInject;
import com.github.gilbva.shrikeioc.test.priority.PriorityComp1;
import com.github.gilbva.shrikeioc.test.priority.PriorityComp2;
import com.github.gilbva.shrikeioc.test.priority.PriorityComp3;
import com.github.gilbva.shrikeioc.test.priority.PriorityComp4;
import com.github.gilbva.shrikeioc.test.priority.PriorityService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class IocContextImplTest {
    @Test
    public void testFind() {
        var instance = ShrikeIoC.context();
        var result = instance.find(DummyComponent.class);
        assertNotNull(result);
    }

    @Test
    public void testFindByService() {
        var result = ShrikeIoC.find(SomeService.class);
        assertNotNull(result);
        assertTrue(result instanceof DummyServiceProvider);

        var resultArr = ShrikeIoC.findAll(SomeService.class);
        assertNotNull(resultArr);
        assertEquals(2, resultArr.length);
        assertTrue(resultArr[0] instanceof DummyServiceProvider);
        assertTrue(resultArr[1] instanceof DummyServiceProvider2);
    }

    @Test
    public void testInjectAndHerarchy() {
        var conComp = ShrikeIoC.find(ConcreteComponent.class);
        assertNotNull(conComp.getDummyComponent());
        assertNotNull(conComp.getServices());
        assertTrue(conComp.getServices()[0] instanceof DummyServiceProvider);
        assertTrue(conComp.getServices()[1] instanceof DummyServiceProvider2);
    }

    @Test
    public void testInjectGeneric() {
        var giComp = ShrikeIoC.find(GenericInjectComponent.class);
        assertNotNull(giComp);
        assertNotNull(giComp.getGsOfStr());
        assertNull(giComp.getGsOfObject());
        assertTrue(giComp.getGsOfStr() instanceof GenericComponent);
        assertNotNull(giComp.getComplexInject());
        assertTrue(giComp.getComplexInject() instanceof ComplexInjectComponent);
    }

    @Test
    public void testPriority() {
        var prorityArr = ShrikeIoC.findAll(PriorityService.class);
        assertTrue(prorityArr[0] instanceof PriorityComp3);
        assertTrue(prorityArr[1] instanceof PriorityComp1);
        assertTrue(prorityArr[2] instanceof PriorityComp4);
        assertTrue(prorityArr[3] instanceof PriorityComp2);

        assertTrue(ShrikeIoC.find(PriorityService.class) instanceof PriorityComp3);
    }

    @Test
    public void testChain() {
        var chainTest = ShrikeIoC.find(ChainTest.class);

        assertEquals("1 2 3", chainTest.execute(), "Wrong Chain");
    }

    @Test
    public void testComponentChild() throws IOException {
        var childTest = ShrikeIoC.find(ComponentBaseInterface.class);

        assertNotNull(childTest);
        assertTrue(childTest instanceof ComponentChild);
    }

    @Test
    public void testInjectContext() {
        var ctxInj = ShrikeIoC.find(ContextInject.class);
        assertNotNull(ctxInj.getAppCtx());
        assertEquals(ShrikeIoC.context(), ctxInj.getAppCtx());
    }
}
