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


import gov.va.vinci.leo.ae.LeoBaseAnnotator;
import gov.va.vinci.leo.tools.LeoUtils;
import gov.va.vinci.vitals.types.DoubleNumber;
import gov.va.vinci.vitals.types.IntegerNumber;
import gov.va.vinci.vitals.types.Numeric;
import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

public class AnalyzeNumbersAE extends LeoBaseAnnotator {
	private static final Logger log = Logger.getLogger(LeoUtils.getRuntimeClass().toString());

	@Override
	public void annotate(JCas aJCas) throws AnalysisEngineProcessException {

		FSIterator<Annotation> iter = this.getAnnotationListForType(aJCas, Numeric.class.getCanonicalName());

		while (iter.hasNext()) {

			Numeric currNum = (Numeric) iter.next();
			String numberString = currNum.getCoveredText().trim().toLowerCase();

			if (currNum instanceof IntegerNumber) {
				currNum.setZero_decimal(false);
				currNum.setDecimal(false);
				currNum.setInteger(true);
				currNum.setValue(Integer.parseInt(numberString));
			}
			else if (currNum instanceof DoubleNumber) {
				currNum.setZero_decimal(false);
				currNum.setDecimal(true);
				currNum.setInteger(false);
				currNum.setValue(Double.parseDouble(numberString));

			}
		}

	}


}
