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
import gov.va.vinci.vitals.types.Numeric;
import gov.va.vinci.vitals.types.PotentialBp;
import gov.va.vinci.vitals.types.Range;
import gov.va.vinci.vitals.types.Relation;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import java.util.ArrayList;

public class FilterRelationsAE extends LeoBaseAnnotator {
	public class InstanceType {
		boolean valueIsNumeric = false;
		boolean valueIsRange = false;
		boolean valueIsBp = false;

		public InstanceType(Annotation value) {
			if (value instanceof Numeric)
				valueIsNumeric = true;

			if (value instanceof Range)
				valueIsRange = true;

			if (value instanceof PotentialBp)
				valueIsBp = true;
		}

	}

	@Override
	public void annotate(JCas aJCas) throws AnalysisEngineProcessException {

		// Iterate through all Relations
		// if the first target overlaps with the other target do:
		// if the target is an instance of Numeric, check if it overlaps with another Relation that has target Range

		ArrayList<Annotation> annsToRemove = new ArrayList<Annotation>();
		FSIterator<Annotation> iter = this.getAnnotationListForType(aJCas, Relation.class.getCanonicalName());
		
		relationLoop:
		while (iter.hasNext()) {

			Relation currRelation = (Relation) iter.next();

			if (currRelation.getTarget() != null) {
				Annotation value = currRelation.getTarget();
				InstanceType valueInstanceType = new InstanceType(value);

				// if more than one relation overlaps with the annotation which is the target of the currRelation.
				try {
					if (AnnotationLibrarian.getAllOverlappingAnnotationsOfType(value, Relation.type, false).size() > 1) {
						ArrayList<Annotation> overlappingRelations = (ArrayList<Annotation>) AnnotationLibrarian
						    .getAllOverlappingAnnotationsOfType(value, Relation.type, false);
						
						overlapLoop:
						for (Annotation or : overlappingRelations) {
							Relation overRel = (Relation) or;
							if (overRel.getTarget() != null) { // that means there is a value associated with the relation. Should always be true
								Annotation valueOverRel = overRel.getTarget();
								InstanceType valueOverRelInstanceType = new InstanceType(valueOverRel);
								// FIXME: Finish annotator at this point
								if (valueInstanceType.valueIsNumeric && valueOverRelInstanceType.valueIsNumeric) {
									if (value == valueOverRel) {
										if (currRelation != overRel) {
											// remove either currRelation or overRel - whichever is shorter.  
											// case 1 : start at the same point, end at the same point -- delete either one
											// case 2 : start1 < start2 && end1 > end2 or other way around -- delete relation2
											// case 3 : start1 < start2 but end1 < end2 -- change annotation relation1 setEnd(end2) 
										} else { // move on to the next overlapping relationship.
											continue overlapLoop;
										}
									}
								}else { // move on to the next overlapping relationship because the two relations deal with a different numeric
									// FIXME: check if the numerics overlap
									continue overlapLoop;
								}
							}
						}

					} else {
						// the current pattern is the only pattern that overlaps with the numeric
						continue;
					}
				} catch (CASException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		} // end of relation loop
	}

}
