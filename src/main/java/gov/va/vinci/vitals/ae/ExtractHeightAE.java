package gov.va.vinci.vitals.ae;

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


import gov.va.vinci.leo.AnnotationLibrarian;
import gov.va.vinci.vitals.types.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import java.util.ArrayList;

/** 
 * @author vhaslcpatteo
 *
 *   the extractor does not handle complex representation such as 5'4" or 5ft 4in
 */
public class ExtractHeightAE extends BaseVitalExtractorAE {

	static String currentType = "Height";
	public static String outputValue = Height_value.class.getCanonicalName();
	public static double[][] typeRanges = { { 52.0, 84.0 }, { 132.0, 214.0 } };  // 4'6" - 7"

	@Override
	public void annotate(JCas aJCas) throws AnalysisEngineProcessException {

		try {
			analyzePatterns(aJCas);
			processPotentialHeight(aJCas);
		} catch (CASException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		createValueTypes(aJCas, currentType, outputValue);
	}

	public void processPotentialHeight(JCas aJCas) throws CASException {

		FSIterator<Annotation> iter = this.getAnnotationListForType(aJCas, PotentialHeight.class.getCanonicalName());
		while (iter.hasNext()) {
			PotentialHeight potentialHeight = (PotentialHeight) iter.next();
			Annotation feet = null;
			Annotation inch = null;
			double v = 0;
			if (potentialHeight.getAnchor() != null) {
				feet = potentialHeight.getAnchor();
				v = 12 * ((Numeric) feet).getValue();
			}

			if (potentialHeight.getTarget() != null) {
				inch = potentialHeight.getTarget();
				v = v + ((Numeric) inch).getValue();
			}

			Numeric newNumeric = new Numeric(aJCas);
			if (feet != null) {
				newNumeric.setBegin(feet.getBegin());
				newNumeric.setEnd(feet.getEnd());
			}
			if (inch != null) {
				newNumeric.setEnd(inch.getEnd());
			}

			if (StringUtils.isNotBlank(newNumeric.getCoveredText())) {
				newNumeric.addToIndexes(aJCas);
				newNumeric.setConcept(currentType);
				newNumeric.setValue(v);
				

			}
		}
	}

	/** 
	 * If a document contains pattern, process that pattern
	 * variables to change -- vital name
	 * 
	 * @param aJCas
	 * @throws CASException 
	 */
	public void analyzePatterns(JCas aJCas) throws CASException {

		FSIterator<Annotation> iter = this.getAnnotationListForType(aJCas, Relation.class.getCanonicalName());
		while (iter.hasNext()) {
			Relation currRelation = (Relation) iter.next();
			//		if (AnnotationLibrarian.getAllContainingAnnotationsOfType(currRelation, LowerPrecisionWindow.type).size() > 0) {
			if (currRelation.getTarget() != null) {
				Annotation value = currRelation.getTarget();
				Unit curUnit = null;
				if (AnnotationLibrarian.getAllOverlappingAnnotationsOfType(currRelation, Unit.type, false).size() > 0) {
					curUnit = (Unit) ((ArrayList<Annotation>) AnnotationLibrarian
					    .getAllOverlappingAnnotationsOfType(currRelation, Unit.type, false)).get(0); // get the first unit in the pattern
				}

				// Has term?
				if (currRelation.getAnchor() != null) {
					Annotation term = currRelation.getAnchor();
					// check type
					if (term instanceof Height_Term) {
						processValue(value, currentType, curUnit, true, typeRanges);
					} else {
						continue;
					}
				} else if (curUnit != null) {
					if ((curUnit.getConcept().equalsIgnoreCase(currentType))) {
						processValue(value, currentType, curUnit, true, typeRanges);
					} else {
						continue;
					}
				}
			}
			//}
		}

	}

}
