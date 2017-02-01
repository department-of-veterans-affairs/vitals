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
import java.util.Iterator;

public class ExtractHeartRateAE extends BaseVitalExtractorAE {
	static String currentType = "Heart_Rate";
	public static String outputValue = Hr_value.class.getCanonicalName();
	public static double[][] typeRanges = { { 30, 150 } };

	@Override
	public void annotate(JCas aJCas) throws AnalysisEngineProcessException {

		try {
			analyzePatterns(aJCas);
			createValueTypes(aJCas, currentType, outputValue);

			analyzeHiPWindow(aJCas);
			createValueTypes(aJCas, currentType, outputValue);
		} catch (CASException ex) {
			ex.printStackTrace();
		}

	}

	public void analyzeHiPWindow(JCas aJCas) throws CASException {

		Iterator<Annotation> pbps = (Iterator<Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(aJCas,
		    IntegerNumber.class.getCanonicalName(), false).iterator();
		while (pbps.hasNext()) {
			IntegerNumber value = (IntegerNumber) pbps.next();

			ArrayList<Annotation> coverWindow = (ArrayList<Annotation>) AnnotationLibrarian.getAllContainingAnnotationsOfType(value,
			    HiPrecisionWindow.type, false);
			ArrayList<Annotation> potentialBps = (ArrayList<Annotation>) AnnotationLibrarian.getAllContainingAnnotationsOfType(value,
			    PotentialBp.type, false);

			// FIXME
			Unit curUnit = null;
			if (coverWindow.size() > 0 && potentialBps.size() == 0) {
				Annotation currWindow = (coverWindow.get(0));
				if (AnnotationLibrarian.getAllOverlappingAnnotationsOfType(currWindow, Numeric.type, true).size() > 4) {
					processValue(value, currentType, curUnit, false, typeRanges);
				}
			} // end of double number loop
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
					if (term instanceof Hr_Term) {
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
		}
	}

	public void processValue(Annotation a, String vital_type, Annotation u, boolean markIt, double[][] ranges) {
		if (a instanceof Numeric) {
			if (StringUtils.isBlank(((Numeric) a).getConcept())) {
				if (isInRange(((Numeric) a).getValue(), ranges)) {
					((Numeric) a).setConcept(vital_type);
					((Numeric) a).setUnit(u);
				} else {
					if (markIt) {
						((Numeric) a).setConcept("Did not match on value: " + vital_type);
					}
				}
			}

		} else if (a instanceof Range) {
			if (StringUtils.isBlank(((Numeric) ((Range) a).getValue1()).getConcept())) {
				if (isInRange(((Numeric) ((Range) a).getValue1()).getValue(), ranges)
				    && isInRange(((Numeric) ((Range) a).getValue2()).getValue(), ranges)) {
					((Numeric) ((Range) a).getValue1()).setConcept(vital_type);
					((Numeric) ((Range) a).getValue1()).setUnit(u);
					((Numeric) ((Range) a).getValue2()).setConcept(vital_type);
					((Numeric) ((Range) a).getValue2()).setUnit(u);
				} else {
					if (markIt) {
						((Numeric) ((Range) a).getValue1()).setConcept("Did not match on value: " + vital_type);
						((Numeric) ((Range) a).getValue2()).setConcept("Did not match on value: " + vital_type);
					}
				}
			}
		}
	}
}
