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
import gov.va.vinci.vitals.types.Output_Value;
import gov.va.vinci.vitals.types.Timestamp;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import java.util.ArrayList;

public class FilterTimestampAE extends LeoBaseAnnotator {

	@Override
	public void annotate(JCas aJCas) throws AnalysisEngineProcessException {

		ArrayList<Annotation> timeList = (ArrayList<Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(aJCas, Timestamp.type, false);
		ArrayList<Annotation> timesToKeep = new ArrayList<Annotation>();
		if (timeList.size() > 0) {
			FSIterator<Annotation> iterNums = this.getAnnotationListForType(aJCas, Output_Value.class.getCanonicalName());

			while (iterNums.hasNext()) {
				// Need to delete all Timestamp annotations that are not included into OutputType feature Timestamp
				Output_Value currOut = (Output_Value) iterNums.next();
				if (currOut.getTimestamp() != null) {
					if (currOut.getTimestamp() instanceof Timestamp) {
						timesToKeep.add(currOut.getTimestamp());
					}
				}
			}
		}

		checkingLoop: for (Annotation t : timeList) {
			toKeep: for (Annotation k : timesToKeep) {
				if (t == k) {
					continue checkingLoop;
					// moving to the next timestamp
				}
			}
			t.removeFromIndexes(aJCas);
		}
	}

}
