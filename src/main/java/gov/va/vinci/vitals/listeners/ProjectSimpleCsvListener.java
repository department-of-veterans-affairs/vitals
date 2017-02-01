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

import gov.va.vinci.leo.listener.BaseCsvListener;
import gov.va.vinci.leo.tools.LeoUtils;
import org.apache.log4j.Logger;
import org.apache.uima.cas.*;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.jcas.tcas.Annotation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Note: This is a very simple gov.va.vinci.leo.listener to output one type of annotation and, optionally,
 * its features. It DOES NOT work well with features of Array type. If you need to
 * output features of Array type, this code simply calls toStringArray on the ArrayFS, and
 * outputs those values.
 *
 * @author vhaislcornir
 */
public class ProjectSimpleCsvListener extends BaseCsvListener {
	public static Logger log = Logger.getLogger(LeoUtils.getRuntimeClass().toString());
	static String del = "\t";
	/**
	 * The type name this annotation gov.va.vinci.leo.listener is limited to.
	 */
	protected List<String> typeNames;

	private final static Logger LOG = Logger.getLogger(LeoUtils.getRuntimeClass().toString());

	/**
	 * The output file to write to.
	 */
	protected File outputFile;

	/**
	 * Include features on the annotations or not.
	 */
	protected boolean includeFeatures;

	public ProjectSimpleCsvListener(File pw) throws FileNotFoundException {
		super(pw);
		log.info("CSV output to: " + pw.getAbsolutePath());
	}

	/**
	 * Constructor that sets the initial outputFile, annotation type name for output, and includeFeatures flag.
	 *
	 * @param outputFile      File to which the annotation information will be output.
	 * @param includeFeatures If true then include the features that need to be added.
	 * @param typeName        Name of the annotation type that will be output to the file. At least one type name is
	 *                        required.
	 * @throws FileNotFoundException  if the output file cannot be found or written to.
	 */
	public ProjectSimpleCsvListener(File outputFile, boolean includeFeatures,
	    String... typeName) throws FileNotFoundException {
		super(outputFile);
		if (typeName == null || typeName.length < 1) {
			throw new IllegalArgumentException("Type name is required.");
		}
		this.typeNames = Arrays.asList(typeName);
		this.includeFeatures = includeFeatures;
	}

	/**
	 * @see org.apache.uima.aae.client.UimaAsBaseCallbackListener#entityProcessComplete(org.apache.uima.cas.CAS, org.apache.uima.collection.EntityProcessStatus)
	 * @param aCas      the CAS containing the processed entity and the analysis results
	 * @param aStatus   the status of the processing. This object contains a record of any Exception that occurred, as well as timing information.
	 */
	@Override
	public void entityProcessComplete(CAS aCas, EntityProcessStatus aStatus) {
		super.entityProcessComplete(aCas, aStatus);
		for (String singleType : typeNames) {
			Type type = aCas.getTypeSystem().getType(singleType);
			FSIndex<?> index = aCas.getAnnotationIndex(type);
			FSIterator<?> iterator = index.iterator();

			String refLoc;
			try {
				refLoc = getReferenceLocation(aCas.getJCas());
			} catch (CASException e1) {
				throw new RuntimeException(e1);
			}

			while (iterator.hasNext()) {
				Annotation a = (Annotation) iterator.next();
				ArrayList<String> lineRow = new ArrayList<String>();
				lineRow.add(refLoc);
				lineRow.add("" + a.getBegin());
				lineRow.add("" + a.getEnd());
				lineRow.add(singleType);
				lineRow.add(a.getCoveredText().replaceAll("\\s+", " "));

				if (includeFeatures) {
					for (Feature f : type.getFeatures()) {
						String featureName = f.getName();
						if (featureName.startsWith(singleType + ":")) {
							try {
								if (f.getRange().isPrimitive()) {
									lineRow.add(f.getShortName());
								} else {
									String[] values = ((ArrayFS) a.getFeatureValue(f)).toStringArray();

									lineRow.add(f.getShortName());
									for (String val : values) {
										lineRow.add(val);
									}
								}
							} catch (Exception e) {
								LOG.error(e);
							}
						}
					}
				}
				writer.writeNext(lineRow.toArray(new String[lineRow.size()]));
			}
		}
	}

	/**
	 * Called once processing of the entire collection is complete.
	 *
	 * @see org.apache.uima.aae.client.UimaAsBaseCallbackListener#collectionProcessComplete(org.apache.uima.collection.EntityProcessStatus)
	 * @param aStatus   the status of the processing. This object contains a record of any Exception that occurred, as well as timing information.
	 */
	@Override
	public void collectionProcessComplete(EntityProcessStatus aStatus) {
		try {
			writer.flush();
			writer.close();
		} catch (Exception e) {
			LOG.warn("Error closing writer: " + e);
		}
	}

	@Override
	protected List<String[]> getRows(CAS cas) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String[] getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}
}