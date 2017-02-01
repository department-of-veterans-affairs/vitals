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


import gov.va.vinci.vitals.types.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

public class ExtractPainAE extends BaseVitalExtractorAE {
	static String currentType = "Pain";
	public static double[][] typeRanges = { { 0, 10 } };
	public static double[][] typeRangesMax = { { 10, 10 } };
	public static String outputValue = Pain_value.class.getCanonicalName();

	@Override
	public void annotate(JCas aJCas) throws AnalysisEngineProcessException {

		analyzePatterns(aJCas);
		createValueTypes(aJCas, currentType, outputValue);
	}

	/** 
	 * If a document contains pattern, process that pattern
	 * variables to change -- vital name
	 * 
	 * @param aJCas
	 */
	public void analyzePatterns(JCas aJCas) {

		FSIterator<Annotation> iter = this.getAnnotationListForType(aJCas, Relation.class.getCanonicalName());
		while (iter.hasNext()) {
			Relation currRelation = (Relation) iter.next();
			if (currRelation.getTarget() != null) {
				Annotation value = currRelation.getTarget();

				Unit curUnit = null;

				// Has term?
				if (currRelation.getAnchor() != null) {
					Annotation term = currRelation.getAnchor();
					// check type
					if (term instanceof Pain_Term) {
						processValue(value, currentType, curUnit, true, typeRanges);
					} else {
						continue;
					}
				}
			}
		}

	}

	@Override
	public void processValue(Annotation a, String vital_type, Annotation u, double[][] range) {
		processValue(a, vital_type, u, false, range);
	}

	/**
	 * 
	 * @param a
	 * @param vital_type
	 * @param u
	 */
	@Override
	public void processValue(Annotation a, String vital_type, Annotation u, boolean markIt, double[][] range) {
		if (a instanceof Numeric) {
			if (StringUtils.isBlank(((Numeric) a).getConcept())) {
				if (isInRange((((Numeric) a).getValue()), range)) {
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
				if (isInRange((((Numeric) ((Range) a).getValue1()).getValue()), range)
				    && isInRange((((Numeric) ((Range) a).getValue2()).getValue()), range)) {
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
		} else if (a instanceof PotentialBp) { // FIXME: come up with a way to deal with it
			Annotation pb1 = ((PotentialBp) a).getAnchor();
			Annotation pb2 = ((PotentialBp) a).getTarget();
			if (pb2 instanceof Numeric) {
				if (((Numeric) pb2).getValue() == 10) {
					if (pb1 instanceof Numeric) {
						if (StringUtils.isBlank(((Numeric) pb1).getConcept())) {
							if (isInRange(((Numeric) pb1).getValue(), range)) {
								((Numeric) pb1).setConcept(vital_type);
								((Numeric) pb1).setUnit(u);

							}
							if (markIt) {
								((Numeric) pb1).setConcept("Did not match on value: " + vital_type);
							}
						}
					}
				}
				//remove the denominator from index so that it is not confused with a value
				Annotation ten = pb2;
				((PotentialBp) a).setTarget(null);
				ten.removeFromIndexes();
			} else if (pb1 instanceof Range) {
				log.error("Unhandled condition in line 107 in ExtractPainAE.java");
			}
		}
	}
}
