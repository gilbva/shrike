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

import java.util.List;
import java.util.Map;

import me.gilbva.shrikeioc.annotations.Component;
import me.gilbva.shrikeioc.annotations.Inject;

@Component
public class GenericInjectComponent {
    private GenericService<String>[] gsOfStrArr;

    @Inject
    private GenericService<String> gsOfStr;

    @Inject
    private GenericService<Object> gsOfObject;

    @Inject
    private GenericService<Map<String, List<Integer>>> complexInject;

    public GenericService<String> getGsOfStr() {
        return gsOfStr;
    }

    public GenericService<Object> getGsOfObject() {
        return gsOfObject;
    }

    public GenericService<Map<String, List<Integer>>> getComplexInject() {
        return complexInject;
    }

    public GenericService<String>[] getGsOfStrArr() {
        return gsOfStrArr;
    }
}
