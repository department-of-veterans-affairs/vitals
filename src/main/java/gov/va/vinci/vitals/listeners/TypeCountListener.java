package gov.va.vinci.vitals.listeners;

/*
 * #%L
 * Leo
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

import gov.va.vinci.leo.listener.BaseListener;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.jcas.tcas.Annotation;

import java.util.*;

/**
 * Note: This is a very simple gov.va.vinci.leo.listener to output one type of annotation and, optionally,
 * its features. It DOES NOT work well with features of Array type. If you need to
 * output features of Array type, this code simply calls toStringArray on the ArrayFS, and
 * outputs those values.
 *
 * @author vhaislcornir
 */
public class TypeCountListener extends BaseListener {

	protected Map<String, Integer> typeCountMap = new HashMap<String, Integer>();
//	File outputFile = new File("H:/git/derma/vitals-optim/new-output.txt");
//	FileOutputStream writer;

	public void printTypeMap() {
		Set<String> keys = typeCountMap.keySet();

		List<String> sorted = asSortedList(keys);
		for (String type: sorted) {
			System.out.println("\t\t" + type + "\t\t" + typeCountMap.get(type));
		}

	}
	public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
		List<T> list = new ArrayList<T>(c);
		java.util.Collections.sort(list);
		return list;
	}


	/**
	 * @see org.apache.uima.aae.client.UimaAsBaseCallbackListener#entityProcessComplete(CAS, EntityProcessStatus)
	 * @param aCas      the CAS containing the processed entity and the analysis results
	 * @param aStatus   the status of the processing. This object contains a record of any Exception that occurred, as well as timing information.
	 */
	@Override
	public void entityProcessComplete(CAS aCas, EntityProcessStatus aStatus) {

		super.entityProcessComplete(aCas, aStatus);
/**
		if (writer == null) {
			try {
				if (!outputFile.exists()) {
					outputFile.createNewFile();
				}
				writer = new FileOutputStream(outputFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	**/
		Iterator<Type> typeInterator = aCas.getTypeSystem().getTypeIterator();

		while (typeInterator.hasNext()) {
			try {
				Type type = typeInterator.next();
				int typeCount = aCas.getAnnotationIndex(type).size();

				FSIterator<AnnotationFS> index = aCas.getAnnotationIndex(type).iterator();
				while (index.hasNext()) {
					Annotation a = (Annotation) index.next();
					int start = a.getBegin() -10;
					int end = a.getEnd() + 10;
					if (start < 0) {
						start = 0;
					}
					if (end > aCas.getDocumentText().length()) {
						end = aCas.getDocumentText().length() - 1;
					}
					/**
					if (type.getName().endsWith("ExcludePrefix")) {
 						ExcludePrefix term = (ExcludePrefix)a;
						System.out.println("Pattern[" + term.getPattern() + "] ExcludePrefix[" + aCas.getDocumentText().substring(start, end).replaceAll("\n","").replaceAll("\r", "") + "]");
					}**/
				}



				if (typeCountMap.containsKey(type.getName())) {
					typeCount += typeCountMap.get(type.getName());
				}
				typeCountMap.put(type.getName(), typeCount);
			} catch (Exception e) {
				// do op.
			//	System.out.println(e);
			}
		}
	}

	@Override
	public void collectionProcessComplete(EntityProcessStatus aStatus) {
		this.printTypeMap();
		try {
		//	writer.flush();
		//	writer.close();
		} catch (Exception e) {
			LOG.warn("Error closing writer: " + e);
		}
	}

}