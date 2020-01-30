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

package me.gilbva.shrike.processor;

import java.io.IOException;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.type.MirroredTypeException;

import me.gilbva.shrike.utils.ClassListPropertyFile;
import me.gilbva.shrike.annotations.Component;

/**
 * Annotations processor for the {@link Component} annotation.
 *
 * @author Gilberto Vento
 */
@SupportedAnnotationTypes("me.gilbva.shrike.annotations.Component")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ComponentProcessor extends ClassListPropertyFile {
    /**
     * IOC Components declaration file.
     */
    static final String COMPONENTS_RESOURCE_FILE = "META-INF/shrike/ioc-components.properties";

    @Override
    public String getFileName() {
        return COMPONENTS_RESOURCE_FILE;
    }

    @Override
    public void processElement(Element element) throws IOException {
        //Get the @Component annotation for the current element.
        var annot = element.getAnnotation(Component.class);
        String clsName = element.toString();
        String scope = findScope(annot);
        appendProperty(clsName, scope);
    }

    private String findScope(Component annot) {
        try {
            annot.scope();
        } catch (MirroredTypeException e) {
            return e.getTypeMirror().toString();
        }
        return "";
    }

}
