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
import gov.va.vinci.leo.ae.LeoBaseAnnotator;
import gov.va.vinci.vitals.types.Hr_Prediction;
import gov.va.vinci.vitals.types.Hr_value;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import java.util.ArrayList;

public class FilterHeartRateAnnotator extends LeoBaseAnnotator {

	@Override
	public void annotate(JCas aJCas) throws AnalysisEngineProcessException {

		ArrayList<Annotation> predictionList = (ArrayList<Annotation>) AnnotationLibrarian
		    .getAllAnnotationsOfType(aJCas, Hr_Prediction.type, false);
		for (Annotation a : predictionList) {
			Hr_Prediction p = (Hr_Prediction) a;
			try {
				if ("0.0".equalsIgnoreCase(p.getPrediction())) {
					ArrayList<Annotation> hrAnnotations;

					hrAnnotations = (ArrayList<Annotation>) AnnotationLibrarian.getAllOverlappingAnnotationsOfType(p, Hr_value.type, false);

					for (Annotation hr : hrAnnotations) {
						hr.removeFromIndexes(aJCas);
					}
				}
			} catch (CASException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
	}

}
