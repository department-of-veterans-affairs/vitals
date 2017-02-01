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


import gov.va.vinci.vitals.types.BMI_value;
import gov.va.vinci.vitals.types.Bmi_Term;
import gov.va.vinci.vitals.types.Relation;
import gov.va.vinci.vitals.types.Unit;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

public class ExtractBmi extends BaseVitalExtractorAE {
	public static String currentType = "BMI";
	public static double[][] typeRanges = { { 18, 45 } };
	public static String outputType = BMI_value.class.getCanonicalName();

	@Override
	public void annotate(JCas aJCas) throws AnalysisEngineProcessException {

		try {
			analyzePatterns(aJCas);
			createValueTypes(aJCas, currentType, outputType);
		} catch (CASException ex) {

			ex.printStackTrace();
		}

	}

	public void analyzePatterns(JCas aJCas) throws CASException {

		FSIterator<Annotation> iter = this.getAnnotationListForType(aJCas, Relation.class.getCanonicalName());
		while (iter.hasNext()) {
			Relation currRelation = (Relation) iter.next();
			if (currRelation.getTarget() != null) {
				Annotation value = currRelation.getTarget();

				Unit curUnit = null;
				if (currRelation.getAnchor() != null) {
					Annotation term = currRelation.getAnchor();
					// check type
					if (term instanceof Bmi_Term) {
						processValue(value, currentType, curUnit, true, typeRanges);
					} else {
						continue;
					}
				} else {
					continue;
				}
			}
		}

	}

}
