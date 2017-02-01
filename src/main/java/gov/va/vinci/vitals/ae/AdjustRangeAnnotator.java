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
import gov.va.vinci.vitals.types.Range;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import java.util.Collection;

public class AdjustRangeAnnotator extends LeoBaseAnnotator {


	@Override
	public void annotate(JCas aJCas) throws AnalysisEngineProcessException {

		AnnotationLibrarian.removeCoveredAnnotations(aJCas, Range.class.getCanonicalName(), false, null);

		Collection<Annotation> rangeIter = null;
		try {
			rangeIter = AnnotationLibrarian.getAllAnnotationsOfType(aJCas, Range.class.getCanonicalName(), false);
		} catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}


		for (Annotation a: rangeIter) {
			Range r = (Range) a;
			Annotation v1 = r.getAnchor();
			Annotation v2 = r.getTarget();
			if (v1 != null) {
				r.setBegin(v1.getBegin());
				r.setValue1(v1);
			}
			if (v2 != null) {
				r.setEnd(v2.getEnd());
				r.setValue2(v2);
			}
		}

	}

}
