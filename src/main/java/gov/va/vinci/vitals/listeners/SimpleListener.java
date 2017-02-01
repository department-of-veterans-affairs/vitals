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
import org.apache.uima.cas.*;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.jcas.tcas.Annotation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
public class SimpleListener extends BaseListener {

	/**
	 * The type name this annotation gov.va.vinci.leo.listener is limited to.
	 */
	protected List<String> typeNames;

	/**
	 * The output file to write to.
	 */
	protected File outputFile;

	/**
	 * The writer used to write to the output file.
	 */
	protected PrintWriter writer;

	/**
	 * Include features on the annotations or not.
	 */
	protected boolean includeFeatures;

	/**
	 * Constructor that sets the initial outputFile, annotation type name for output, and includeFeatures flag.
	 *
	 * @param outputFile      File to which the annotation information will be output.
	 * @param includeFeatures If true then include the features that need to be added.
	 * @param typeName        Name of the annotation type that will be output to the file. At least one type name is
	 *                        required.
	 * @throws FileNotFoundException  if the output file cannot be found or written to.
	 */
	public SimpleListener(File outputFile, boolean includeFeatures, String... typeName)
	    throws FileNotFoundException {
		if (typeName == null || typeName.length < 1) {
			throw new IllegalArgumentException("Type name is required.");
		}
		this.typeNames = Arrays.asList(typeName);
		this.outputFile = outputFile;
		this.includeFeatures = includeFeatures;
		writer = new PrintWriter(outputFile);
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

				writer.write(refLoc + " ~ " + a.getBegin() + " ~ " + a.getEnd() + " ~ " + singleType + " ~ "
				    + a.getCoveredText().replaceAll("\\s+", " ") + "~");

				if (includeFeatures) {
					for (Feature f : type.getFeatures()) {
						if (f.getName().startsWith(typeNames + ":")) {
							try {
								if (a.getFeatureValue(f) instanceof ArrayFS) {
									String[] values = ((ArrayFS) a.getFeatureValue(f)).toStringArray();
									writer.write("[ " + f.getShortName() + " ~ ");
									for (String val : values) {
										writer.write(val + ",");
									}
									writer.write("] ");
								} else {
									writer.write("[ " + f.getShortName() + " ~ " + a.getFeatureValueAsString(f) + "]");
								}
							} catch (Exception e) {
								LOG.error(e);
							}
						}
					}
				}
				writer.write("\n");
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
		super.collectionProcessComplete(aStatus);
		try {
			writer.close();
		} catch (Exception e) {
			LOG.warn("Error closing writer: " + e);
		}
	}
}