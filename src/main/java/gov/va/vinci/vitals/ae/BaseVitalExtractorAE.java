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
import gov.va.vinci.vitals.types.Numeric;
import gov.va.vinci.vitals.types.Output_Value;
import gov.va.vinci.vitals.types.PotentialBp;
import gov.va.vinci.vitals.types.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

public abstract class BaseVitalExtractorAE extends LeoBaseAnnotator {
	public static final Logger log = Logger.getLogger(LeoUtils.getRuntimeClass().toString());

	public abstract void analyzePatterns(JCas aJCas) throws CASException;

	public boolean isInRange(double num, double[][] ranges) {

		for (double[] range : ranges) {
			if ((num >= range[0] && num <= range[1]))
				return true;
		}
		return false;
	}

	public void processValue(Annotation a, String vital_type, Annotation u, double[][] ranges) throws CASException {
		processValue(a, vital_type, u, false, ranges);
	}

	/**
	 * 
	 * @param a
	 * @param vital_type
	 * @param u
	 * @throws CASException 
	 */
	public void processValue(Annotation a, String vital_type, Annotation u, boolean markIt, double[][] ranges)
	    throws CASException {
		if (a instanceof Numeric) {
			if (AnnotationLibrarian.getAllContainingAnnotationsOfType(a, Range.type, false).size() > 0) {
				return;
			}
			if (AnnotationLibrarian.getAllContainingAnnotationsOfType(a, PotentialBp.type, false).size() > 0) {
				return;
			}
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
			if (AnnotationLibrarian.getAllContainingAnnotationsOfType(a, PotentialBp.type, false).size() > 0) {
				return;
			}
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
		} else if (a instanceof PotentialBp) {
			// FIXME: add logic when value in the pattern is PotentialBp

		}
	}

	

	/**
	 * 
	 * @param aJCas
	 * @param currentType  -- such as Temperature, or Resp_value.class.getCanonicalName()
	 * @param outType
	 * @throws AnalysisEngineProcessException
	 */
	public void createValueTypes(JCas aJCas, String currentType, String outType) throws AnalysisEngineProcessException {
		FSIterator<Annotation> iterNums = this.getAnnotationListForType(aJCas, Numeric.class.getCanonicalName());
		while (iterNums.hasNext()) {
			Numeric curNum = (Numeric) iterNums.next();
			if (StringUtils.isNotBlank(curNum.getConcept())) {
				if (curNum.getConcept().equalsIgnoreCase(currentType)) {
					Output_Value newAnn = (Output_Value) this.addOutputAnnotation(outType,
					    aJCas, curNum.getBegin(), curNum.getEnd());
					newAnn.setSource(curNum.getSource());
					newAnn.setUnit(curNum.getUnit());
					newAnn.setTimestamp(curNum.getTimestamp());
					newAnn.setValue("" + curNum.getValue());
					newAnn.setValueAnnotation(curNum);
				}
			}
		}
	}
}
