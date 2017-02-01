package gov.va.vinci.vitals;

/*
 * #%L
 * Vitals extractor
 * %%
 * Copyright (C) 2010 - 2017 University of Utah / VA
 * %%
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
 * #L%
 */

import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

//import com.gentlyweb.utils.IOUtils;

/**
 * Utility methods
 *
 */
public class Utils {

    /**
     * Load the config file - From code by Ryan Cornia
     *
     * @param environment
     * @param filePaths
     * @return
     * @throws IOException
     */
    public static ConfigObject loadConfigFile(String environment, String... filePaths) throws IOException {
        ConfigSlurper slurper = new ConfigSlurper(environment);
        ConfigObject config = new ConfigObject();

        ClassLoader cl = ClassLoader.getSystemClassLoader();

        for (String filePath : filePaths) {
            InputStream in = null;
            in = cl.getResourceAsStream(filePath);
            System.out.println("Loading file:  " + new File(filePath).getAbsolutePath());
            
            String resourceAsString = IOUtils.toString(in);
            config.merge(slurper.parse(resourceAsString));
            System.out.println("Loaded file:  " + new File(filePath).getAbsolutePath());
        }
        return config;
    }

}
