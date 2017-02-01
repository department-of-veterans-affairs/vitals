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
import gov.va.vinci.vitals.types.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import java.util.ArrayList;
import java.util.Iterator;

public class ExtractBloodPressureAE extends BaseVitalExtractorAE {
	static String currentType = "Systolic";
	public static double[][] typeRangesSystolic = {{50, 100}};
	public static double[][] typeDiastolicRanges = {{50, 100}};

	@Override
	public void annotate(JCas aJCas) throws AnalysisEngineProcessException {

		try {
			analyzePatterns(aJCas);
			createValueTypes(aJCas);

			//processed potential bps within the lowerprecision
			analyzeLowerPWindow(aJCas);
			createValueTypes(aJCas);

		} catch (CASException ex) {

			ex.printStackTrace();
		}
	}

	public void analyzeLowerPWindow(JCas aJCas) throws CASException {

		Iterator<Annotation> pbps = (Iterator<Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(aJCas,
				PotentialBp.class.getCanonicalName(), false).iterator();
		while (pbps.hasNext()) {
			PotentialBp d = (PotentialBp) pbps.next();

			ArrayList<Annotation> coverWindow = (ArrayList<Annotation>) AnnotationLibrarian.getAllContainingAnnotationsOfType(d,
					LowerPrecisionWindow.type, false);

			// FIXME

			if (coverWindow.size() > 0) {

				processPotentialBp(d);
			}
		} // end of double number loop
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
					// if systolic - test for systolic
					// if diastolic -- test for diastolic
					// if bp pattern -- split left and right
					if (term instanceof Bp_Term) {
						String bpType = ((Bp_Term) term).getConcept();

						processValue(value, bpType, curUnit, true);
					} else {
						continue;
					}
				} else if (curUnit != null) {
					if ((curUnit.getConcept().equalsIgnoreCase(currentType))) {
						processValue(value, currentType, curUnit, true);
					} else {
						continue;
					}
				}
			}
		}

	}

	public void processValue(Annotation a, String vital_type, Annotation u) {
		try {
			processValue(a, vital_type, u, false);
		} catch (CASException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}

	/**
	 * @param a
	 * @param vital_type
	 * @param u
	 * @throws CASException
	 */
	public void processValue(Annotation a, String vital_type, Annotation u, boolean markIt) throws CASException {
		if (a instanceof Numeric) {
			if (AnnotationLibrarian.getAllContainingAnnotationsOfType(a, Range.type, false).size() > 0) {
				return;
			} else if (AnnotationLibrarian.getAllContainingAnnotationsOfType(a, PotentialBp.type, false).size() > 0) {
				return;
			}
			if (StringUtils.isBlank(((Numeric) a).getConcept())) {
				if (AnnotationLibrarian.getAllOverlappingAnnotationsOfType(a, PotentialBp.class.getCanonicalName(), false).size() > 0) {
					return;
				}
				if (CheckRange.isSystolicBp(((Numeric) a).getValue())) {
					((Numeric) a).setConcept(vital_type);
					((Numeric) a).setUnit(u);
				} else {
					if (markIt) {
						((Numeric) a).setConcept("Did not match on value: " + vital_type);
					}
				}
			}
		} else if (a instanceof Range) {
			if (AnnotationLibrarian.getAllOverlappingAnnotationsOfType(a, PotentialBp.class.getCanonicalName(), false).size() > 0) {
				return;
			}
			if (StringUtils.isBlank(((Numeric) ((Range) a).getValue1()).getConcept())) {
				if (CheckRange.isSystolicBp(((Numeric) ((Range) a).getValue1()).getValue())
						&& CheckRange.isSystolicBp(((Numeric) ((Range) a).getValue2()).getValue())) {
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
			processPotentialBp((PotentialBp) a);
		}
	}

	public static void discardSystolic(Annotation systolicAnnotation) {
		if (systolicAnnotation != null) {
			if (systolicAnnotation instanceof IntegerNumber) {
				((IntegerNumber) systolicAnnotation).setConcept("Mathched BP pattern but not value");
			} else if (systolicAnnotation instanceof Range) {
				Annotation rv1 = ((Range) systolicAnnotation).getValue1();
				Annotation rv2 = ((Range) systolicAnnotation).getValue2();
				if (rv1 instanceof IntegerNumber && rv2 instanceof IntegerNumber) {

					((IntegerNumber) rv1).setConcept("Mathched BP pattern but not value");
					((IntegerNumber) rv2).setConcept("Mathched BP pattern but not value");
				}

			}
		}
	}

	public static void processPotentialBp(PotentialBp currBp) {
		try {
			// Anchor is Systolic
			// Target is Diastolic
			// if diastolic fails then systolic should be changed as well.
			Annotation systolicAnnotation = null;
			Annotation diastolicAnnotation = null;

			boolean bothMatch = true;
			if (currBp.getAnchor() != null) {
				systolicAnnotation = currBp.getAnchor();
				// The first annotation can be either an integer or a range
				if (systolicAnnotation instanceof IntegerNumber) {
					if (StringUtils.isBlank(((Numeric) systolicAnnotation).getConcept())) {
						if (CheckRange.isSystolicBp(((IntegerNumber) systolicAnnotation).getValue())) {
							((IntegerNumber) systolicAnnotation).setConcept("Systolic");
						} else {
							bothMatch = false;
							((IntegerNumber) systolicAnnotation).setConcept("Mathched BP pattern but not value");
						}
					}
				} else if (systolicAnnotation instanceof Range) {
					Annotation rv1 = ((Range) systolicAnnotation).getValue1();
					Annotation rv2 = ((Range) systolicAnnotation).getValue2();
					if (rv1 instanceof IntegerNumber && rv2 instanceof IntegerNumber) {
						if (StringUtils.isBlank(((Numeric) rv1).getConcept())
								&& StringUtils.isBlank(((Numeric) rv2).getConcept())) {
							if (CheckRange.isSystolicBp(((IntegerNumber) rv1).getValue())
									&& CheckRange.isSystolicBp(((IntegerNumber) rv2).getValue())) {
								((IntegerNumber) rv1).setConcept("Systolic");
								((IntegerNumber) rv2).setConcept("Systolic");
							}
						}
					}
				} // end if Range
			} // end if Anchor
			/**/
			if (bothMatch) {
				if (currBp.getTarget() != null) {
					diastolicAnnotation = currBp.getTarget();

					if (diastolicAnnotation instanceof IntegerNumber) {
						if (StringUtils.isBlank(((Numeric) diastolicAnnotation).getConcept())) {
							if (CheckRange.isDiastolicBp(((IntegerNumber) diastolicAnnotation).getValue())) {
								((IntegerNumber) diastolicAnnotation).setConcept("Diastolic");
							} else {
								bothMatch = false;
								((IntegerNumber) diastolicAnnotation).setConcept("Mathched BP pattern but not value");
								// if the second number fails, discard the first number as well
								discardSystolic(systolicAnnotation);
							}
						}
					} else if (diastolicAnnotation instanceof Range) {
						Annotation diastolicRange1 = ((Range) diastolicAnnotation).getValue1();
						Annotation diastolicRange2 = ((Range) diastolicAnnotation).getValue2();
						if (diastolicRange1 instanceof IntegerNumber && diastolicRange2 instanceof IntegerNumber) {
							if (StringUtils.isBlank(((Numeric) diastolicRange1).getConcept())
									&& StringUtils.isBlank(((Numeric) diastolicRange2).getConcept())) {
								if (CheckRange.isDiastolicBp(((IntegerNumber) diastolicRange1).getValue())
										&& CheckRange.isDiastolicBp(((IntegerNumber) diastolicRange2).getValue())) {
									((IntegerNumber) diastolicRange1).setConcept("Diastolic");
									((IntegerNumber) diastolicRange2).setConcept("Diastolic");
								} else {
									bothMatch = false;
									((IntegerNumber) diastolicRange1).setConcept("Mathched BP pattern but not value");
									((IntegerNumber) diastolicRange2).setConcept("Mathched BP pattern but not value");
									discardSystolic(systolicAnnotation);
								}
							}
						}
					} // end if Range
				}
			} /**/

		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}

	public void createValueTypes(JCas aJCas) throws AnalysisEngineProcessException {
		createValueTypes(aJCas, "Systolic", Bp_Systolic_value.class.getCanonicalName());
		createValueTypes(aJCas, "Diastolic", Bp_Diastolic_value.class.getCanonicalName());
		createValueTypes(aJCas, "Blood_Pressure", Bp_value.class.getCanonicalName());

	}

	static class CheckRange {

		public static boolean isSystolicBp(Double num) {
			if (num > 29 && num < 220)
				return true;
			else
				return false;
		}

		public static boolean isDiastolicBp(Double num) {
			if (num > 14 && num < 200)
				return true;
			else
				return false;
		}
	}
}
