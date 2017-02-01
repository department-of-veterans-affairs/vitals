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
import gov.va.vinci.vitals.types.*;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import java.util.ArrayList;

public class AssignUnitAndTimeAE extends LeoBaseAnnotator {
	public static final String NEW_LINE = System.getProperty("line.separator");

	@Override
	public void annotate(JCas aJCas) throws AnalysisEngineProcessException {

		processTimestamp(aJCas);
		processUnits(aJCas);
	}

	public void processUnits(JCas aJCas) {
		ArrayList<Annotation> unitList = (ArrayList<Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(aJCas, Unit.type, false);

		if (unitList.size() > 0) {
			FSIterator<Annotation> iterNums = this.getAnnotationListForType(aJCas, Numeric.class.getCanonicalName());
			try {
				while (iterNums.hasNext()) {
					Numeric currNum = (Numeric) iterNums.next();
					if (AnnotationLibrarian.getAllContainingAnnotationsOfType(currNum, Relation.type, false).size() > 0) {
						Relation currRelation = (Relation) ((ArrayList) AnnotationLibrarian.getAllContainingAnnotationsOfType(currNum,
						    Relation.type, false)).get(0);
						if (AnnotationLibrarian.getNextClosestAnnotations(currNum, unitList).size() > 0) {
							Unit units = (Unit) ((ArrayList) AnnotationLibrarian.getNextClosestAnnotations(currNum, unitList)).get(0);
							currNum.setUnit(units);
						}
					}

					if (currNum.getUnit() == null) {
						if (AnnotationLibrarian.getAllContainingAnnotationsOfType(currNum, Relation_Time.type, false).size() > 0) {
							Relation_Time currRelation = (Relation_Time) ((ArrayList) AnnotationLibrarian.getAllContainingAnnotationsOfType(currNum, Relation_Time.type, false)).get(0);
							if (AnnotationLibrarian.getAllCoveredAnnotationsOfType(currRelation, Unit.type, false).size() > 0) {
								Unit units = (Unit) ((ArrayList) AnnotationLibrarian.getAllCoveredAnnotationsOfType(currRelation, Unit.type, false)).get(0);
								currNum.setUnit(units);
							}
						}
					}
				}
			} catch (CASException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}

	}

	public void processTimestamp(JCas aJCas) {
		ArrayList<Annotation> timeList = (ArrayList<Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(aJCas, Timestamp.type, false);
		ArrayList<Annotation> timesToKeep = new ArrayList<Annotation>();
		if (timeList.size() > 0) {
			FSIterator<Annotation> iterNums = this.getAnnotationListForType(aJCas, Numeric.class.getCanonicalName());
			try {
				while (iterNums.hasNext()) {
					Numeric currNum = (Numeric) iterNums.next();

					if (AnnotationLibrarian.getAllContainingAnnotationsOfType(currNum, Relation.type, false).size() > 0) {
						Relation currRelation = (Relation) ((ArrayList) AnnotationLibrarian.getAllContainingAnnotationsOfType(
						    currNum, Relation.type, false)).get(0);
						if (AnnotationLibrarian.getAllCoveredAnnotationsOfType(currRelation, Timestamp.type, false).size() > 0) {
							Timestamp stamp = (Timestamp) ((ArrayList) AnnotationLibrarian.getAllCoveredAnnotationsOfType(
							    currRelation, Timestamp.type, false)).get(0);
							currNum.setTimestamp(stamp);
							timesToKeep.add(stamp);
						}
					}

					if (currNum.getTimestamp() == null) {
						if (AnnotationLibrarian.getAllContainingAnnotationsOfType(currNum, Relation_Time.type, false).size() > 0) {
							Relation_Time currRelation = (Relation_Time) ((ArrayList) AnnotationLibrarian.getAllContainingAnnotationsOfType(
							    currNum, Relation_Time.type, false)).get(0);
							if (AnnotationLibrarian.getAllCoveredAnnotationsOfType(currRelation, Timestamp.type, false).size() > 0) {
								Timestamp stamp = (Timestamp) ((ArrayList) AnnotationLibrarian.getAllCoveredAnnotationsOfType(
								    currRelation, Timestamp.type, false)).get(0);
								currNum.setTimestamp(stamp);
								timesToKeep.add(stamp);
							}
						}
					}
					if (currNum.getTimestamp() == null) {
						if (AnnotationLibrarian.getPreviousClosestAnnotations(currNum, timeList).size() > 0) {
							Timestamp stamp = (Timestamp) ((ArrayList) AnnotationLibrarian.getPreviousClosestAnnotations(currNum, timeList))
							    .get(0);
							int len = aJCas.getDocumentText().substring(stamp.getEnd(), currNum.getBegin()).split(NEW_LINE).length;
							if (len < 5) {
								//if (!aJCas.getDocumentText().substring(stamp.getEnd(), currNum.getBegin()).contains(NEW_LINE)) {
								currNum.setTimestamp(stamp);
								timesToKeep.add(stamp);
							}
						}
					}

					if (currNum.getTimestamp() == null) {
						if (AnnotationLibrarian.getNextClosestAnnotations(currNum, timeList).size() > 0) {
							Timestamp stamp = (Timestamp) ((ArrayList) AnnotationLibrarian.getNextClosestAnnotations(currNum, timeList))
							    .get(0);
							if (!aJCas.getDocumentText().substring(currNum.getEnd(), stamp.getBegin()).contains(NEW_LINE)) {
								currNum.setTimestamp(stamp);
								timesToKeep.add(stamp);
							}
						}
					}
				}
			} catch (CASException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}

		}
		checkingLoop: for (Annotation t : timeList) {
			toKeep: for (Annotation k : timesToKeep) {
				if (t == k) {
					continue checkingLoop;
					// moving to the next timestamp
				}
			}
		//	t.removeFromIndexes(aJCas);
		}
	}

}
