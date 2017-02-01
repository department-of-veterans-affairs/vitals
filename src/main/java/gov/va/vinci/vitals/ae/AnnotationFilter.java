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
import gov.va.vinci.leo.descriptors.LeoAEDescriptor;
import gov.va.vinci.leo.descriptors.LeoConfigurationParameter;
import gov.va.vinci.leo.tools.LeoUtils;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * The purpose of the AnnotationFilter is to remove overannotated instances.
 * The parameters include
 * typesToKeep - is a string array of anchor types
 * typesToDelete - is a string array of types to remove
 * removeOverlapping is set to true if overlap typesToDelete need to be overlapping with
 *     instead of completely covered by the instances of typesToKeep.
 *
 * @author Olga Patterson
 **/
public class AnnotationFilter extends LeoBaseAnnotator {
	private static final Logger log = Logger.getLogger(LeoUtils.getRuntimeClass().toString());
	@LeoConfigurationParameter
	protected String[] typesToKeep = null;

	@LeoConfigurationParameter
	protected String[] typesToDelete = null;

	@LeoConfigurationParameter
	protected Boolean removeOverlapping = false;


	public AnnotationFilter() {
		
	}

	public AnnotationFilter(String[] typesToKeep, String[] typesToDelete, Boolean removeOverlapping) {
		this.typesToDelete = typesToDelete;
		this.typesToKeep = typesToKeep;
		this.removeOverlapping = removeOverlapping;
	}

	/**     *      * @param a     * @param punc     */
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
	}

	@Override
	public void annotate(JCas aJCas) throws AnalysisEngineProcessException {
		for (String type1 : typesToKeep) {
			if (typesToDelete == null || typesToDelete.length == 0) {
				AnnotationLibrarian.removeCoveredAnnotations(aJCas, type1, false, null);
			}
			else {
				for (String type2 : typesToDelete) {
					if (!type1.equalsIgnoreCase(type2)) {
						if (removeOverlapping) {
							AnnotationLibrarian.removeOverlappingAnnotations(aJCas, type1, type2, true, false, null);
						}
						else {
							AnnotationLibrarian.removeCoveredAnnotations(aJCas, type1, type2, true, false, null);
						}
					} else {
						AnnotationLibrarian.removeCoveredAnnotations(aJCas, type1, false, null);
					}
				}
			}
		}
	}

    @Override
    public LeoAEDescriptor getLeoAEDescriptor() throws Exception {
        return super.getLeoAEDescriptor();
    }
}