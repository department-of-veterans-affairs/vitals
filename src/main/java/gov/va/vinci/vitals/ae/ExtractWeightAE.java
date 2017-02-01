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
import gov.va.vinci.vitals.types.Relation;
import gov.va.vinci.vitals.types.Unit;
import gov.va.vinci.vitals.types.Weight_Term;
import gov.va.vinci.vitals.types.Weight_value;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import java.util.ArrayList;

public class ExtractWeightAE extends BaseVitalExtractorAE {
	static String currentType = "Weight";
	public static String outputValue = Weight_value.class.getCanonicalName();
	public static double[][] typeRanges = { { 40.0, 600.0 } }; // combined kg and lbs

	@Override
	public void annotate(JCas aJCas) throws AnalysisEngineProcessException {

		try {
			analyzePatterns(aJCas);
		} catch (CASException ex) {

			ex.printStackTrace();
		}
		createValueTypes(aJCas, currentType, outputValue);
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
					if (term instanceof Weight_Term) {
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

}
