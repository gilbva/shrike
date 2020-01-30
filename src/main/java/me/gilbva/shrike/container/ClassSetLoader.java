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

package me.gilbva.shrike.container;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class stores and organize the clases of the register
 * components.
 *
 * @author Gilberto Vento
 */
class ClassSetLoader {
    private static final Logger LOG = Logger.getLogger(ClassSetLoader.class.getName());

    private static ClassSetLoader INSTANCE;

    /**
     * All ClassSets available by scope.
     */
    private final Map<Class<?>, ClassSet> clsCache = new ConcurrentHashMap<>();

    /**
     * Al the components declared in the components.properties files.
     */
    private Map<String, String> propFilesCache;

    public static ClassSetLoader instance() {
        if (INSTANCE == null) {
            INSTANCE = new ClassSetLoader();
        }
        return INSTANCE;
    }

    private ClassSetLoader() {
    }

    /**
     * Finds a ClassSet that contains all the classes in the specified scope.
     *
     * @param scope The scope of the classes to lookup.
     * @return A ClassSet containing all the classes in the specified scope.
     */
    ClassSet findByScope(Class<?> scope) {
        if (clsCache.containsKey(scope)) {
            return clsCache.get(scope);
        }
        return loadScope(scope);
    }

    /**
     * Load all classes of the specified scope from the class path.
     *
     * @param scope The scope to load.
     * @return A ClassSet containing all the classes in the scope, that where
     * found in the classpath.
     * @throws IOException If something when wrong.
     */
    private ClassSet loadFromClassPath(Class<?> scope) throws IOException {
        var clsList = new HashSet<Class<?>>();
        //An instance of IocContextImpl is always a component in every scope.
        clsList.add(ContextImpl.class);
        clsList.add(scope);
        if (propFilesCache == null) {
            propFilesCache = loadPropFilesCache();
        }
        propFilesCache.forEach((clsName, compScope) ->
        {
            if (compScope != null && scope.getName().equalsIgnoreCase(compScope)) {
                try {
                    clsList.add(Class.forName(clsName));
                } catch (ClassNotFoundException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        });
        if (clsList.isEmpty()) {
            return null;
        }
        return new ClassSet(clsList);
    }

    /**
     * Loads all of the components.properties files in the class path.
     *
     * @return A map containing the combination of all the components.properties
     * files present in the class path.
     * @throws IOException If a file cannot be read.
     */
    private Map<String, String> loadPropFilesCache() throws IOException {
        var result = new HashMap<String, String>();
        var files = findComponentsFiles();
        for (var file : files) {
            var resources = Thread.currentThread().getContextClassLoader().getResources(file);
            while (resources.hasMoreElements()) {
                var nextElement = resources.nextElement();
                var prop = new Properties();
                try (InputStream is = nextElement.openStream()) {
                    prop.load(is);
                }
                prop.forEach((key, value) ->
                {
                    String clsName = (String) key;
                    String compScope = (String) value;
                    result.put(clsName, compScope);
                });
            }
        }
        return result;
    }

    private synchronized ClassSet loadScope(Class<?> scope) {
        try {
            if (clsCache.containsKey(scope)) {
                return clsCache.get(scope);
            }
            var result = loadFromClassPath(scope);
            if (result != null) {
                clsCache.put(scope, result);
                return result;
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    private Set<String> findComponentsFiles() {
        var result = new HashSet<String>();
        try {
            var resources = getClass().getClassLoader().getResources("META-INF/shrike");
            while (resources.hasMoreElements()) {
                var dirURL = resources.nextElement();
                if (dirURL != null) {
                    if (dirURL.getProtocol().equals("file")) {
                        findComponentsFilesFromDir(dirURL, result);
                    } else if (dirURL.getProtocol().equals("jar")) {
                        findComponentsFilesFromJar(dirURL, result);
                    }
                }
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }

    private void findComponentsFilesFromDir(URL dirURL, Set<String> result) throws IOException {
        try {
            var f = new File(dirURL.toURI());
            if (f.isDirectory()) {
                Files.find(f.toPath(),
                        Integer.MAX_VALUE,
                        (t, u) -> t.getFileName().toString().endsWith("ioc-components.properties"))
                        .forEach(path ->
                        {
                            int lastIndex = path.toString().lastIndexOf("META-INF/shrike");
                            String name = path.toString().substring(lastIndex);
                            result.add(name);
                        });
            }
        } catch (URISyntaxException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private void findComponentsFilesFromJar(URL dirURL, Set<String> result) throws IOException {
        /*
         * A JAR path
         */
        var jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
        try {
            var jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
            var entries = jar.entries(); //gives ALL entries in jar
            while (entries.hasMoreElements()) {
                var jarEntry = entries.nextElement();
                var name = jarEntry.getName().trim();
                if (name.endsWith("/ioc-components.properties")) {
                    result.add(name);
                }
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

}
