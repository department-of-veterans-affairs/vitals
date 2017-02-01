package gov.va.vinci.vitals;

/*
 * #%L
 * Vitals
 * %%
 * Copyright (C) 2010 - 2014 Department of Veterans Affairs
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

import gov.va.vinci.leo.CommandLineClient;
import org.kohsuke.args4j.CmdLineException;

import java.io.File;


/**
 * An example UIMA AS Client that takes command line arguments for its configuration.
 */
public class VitalsClient extends CommandLineClient {
	/**
	 * @param args
	 */
	public static void main(String[] args) throws CmdLineException {
		VitalsClient bean = new VitalsClient(args);
		bean.runClient();
	}

	public VitalsClient(String[] args) {
		super(args);
	}

	public VitalsClient(File clientConfigFile, File readerConfig, File[] listenerConfigFiles) {
		super(clientConfigFile, readerConfig, listenerConfigFiles);
	}

	public String defaultClientConfig() {
		return "config/ClientConfig.groovy";
	}

	public String defaultCollectionReaderConfig() {
		return "config/readers/FileCollectionReaderConfig.groovy";
	}

	public String[] defaultListenerConfig() {
		return  new String[] {"config/listeners/DatabaseListenerConfig.groovy"};
	}
}
