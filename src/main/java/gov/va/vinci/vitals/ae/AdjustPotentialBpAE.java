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
import gov.va.vinci.leo.tools.LeoUtils;
import gov.va.vinci.vitals.types.PotentialBp;
import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import java.util.Collection;

public class AdjustPotentialBpAE extends LeoBaseAnnotator {
	private static final Logger log = Logger.getLogger(LeoUtils.getRuntimeClass().toString());

	@Override
	public void annotate(JCas aJCas) throws AnalysisEngineProcessException {
		try {

			AnnotationLibrarian.removeCoveredAnnotations(aJCas, PotentialBp.class.getCanonicalName(), false, null);

			Collection<Annotation> iter = null;
			try {
				iter = AnnotationLibrarian.getAllAnnotationsOfType(aJCas, PotentialBp.class.getCanonicalName(), false);
			} catch (CASException e) {
				throw new AnalysisEngineProcessException(e);
			}

			for (Annotation a: iter) {
				PotentialBp pbp = (PotentialBp)a;
				if (pbp.getAnchor() != null) {
					pbp.setBegin(pbp.getAnchor().getBegin());
				}
				if (pbp.getTarget() != null) {
					pbp.setEnd(pbp.getTarget().getEnd());
				}
			}
		} catch (AnalysisEngineProcessException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

	}

}
