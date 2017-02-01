package gov.va.vinci.vitals.listeners;

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
import gov.va.vinci.leo.listener.BaseCsvListener;
import gov.va.vinci.leo.tools.LeoUtils;
import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.jcas.tcas.Annotation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SimpleCompareListener extends BaseCsvListener {
	public static HashMap<String, Integer> tpCount_au = new HashMap<String, Integer>();;
	public static HashMap<String, Integer> tpCount_sys = new HashMap<String, Integer>();;
	public static HashMap<String, Integer> fpCount_sys = new HashMap<String, Integer>();;
	public static HashMap<String, Integer> fnCount_au = new HashMap<String, Integer>();
	public static HashMap<String, Integer> totalCount = new HashMap<String, Integer>();;
	/**
	 * The type name this annotation listener is limited to.
	 */
	protected HashMap<String, String> auSysMap;
	private static final Logger log = Logger.getLogger(LeoUtils.getRuntimeClass().toString());
	/**
	 * 
	 * @param goldSysMap
	 * @param file
	 * @throws FileNotFoundException 
	 */
	public SimpleCompareListener(HashMap<String, String> goldSysMap, File file) throws FileNotFoundException {
		super(file);
		this.auSysMap = goldSysMap;
		for (java.util.Map.Entry<String, String> a : auSysMap.entrySet()) {
			tpCount_au.put(a.getKey(), 0);
			tpCount_sys.put(a.getValue(), 0);
			fpCount_sys.put(a.getValue(), 0);
			fnCount_au.put(a.getKey(), 0);
			totalCount.put(a.getKey(), 0);
			totalCount.put(a.getValue(), 0);
		}
	}

	@Override
	public void collectionProcessComplete(EntityProcessStatus aStatus) {
		// TODO Auto-generated method stub
		super.collectionProcessComplete(aStatus);
		log.info(", TP:	" + outputMap(tpCount_au));
		log.info(", TP:	" + outputMap(tpCount_sys));
		log.info(", FP:	" + outputMap(fpCount_sys));
		log.info(", FN:	" + outputMap(fnCount_au));
		log.info(", Totals: " + outputMap(totalCount));
		
	}

	public static String outputMap(HashMap<String, Integer> map) {
		String line = "";
		ArrayList<String> keys = new ArrayList<String>(map.keySet());
		Collections.sort(keys);
		for (String k : keys) {
			line = line + "," + k.replaceAll("gov.va.vinci.kttr.types.", "").replaceAll("gov.va.vinci.vitals.types.", "") + "," + map.get(k);
		}
		return line;
	}

	public static String[] outTP(Annotation a, String referenceID, String type) {
		if (tpCount_au.containsKey(type)) {
			int i = tpCount_au.get(type);
			i++;
			tpCount_au.put(type, i);
		} else if (tpCount_sys.containsKey(type)) {
			int i = tpCount_sys.get(type);
			i++;
			tpCount_sys.put(type, i);
		} else {
			System.out.println("Processing error " + referenceID + " " + type + " TP map");
		}
		return new String[] { referenceID, type, " TP ", "" + a.getBegin(), "" + a.getEnd(), a.getCoveredText() };

	}

	public static String[] outFP(Annotation a, String referenceID, String type) {
		if (fpCount_sys.containsKey(type)) {
			int i = fpCount_sys.get(type);
			i++;
			fpCount_sys.put(type, i);
		} else {
			System.out.println("Processing error " + referenceID + " " + type + " FP map");
		}
		return new String[] { referenceID, type, " FP ", "" + a.getBegin(), "" + a.getEnd(), a.getCoveredText() };
	}

	public static String[] outFN(Annotation a, String referenceID, String type) {

		if (fnCount_au.containsKey(type)) {
			int i = fnCount_au.get(type);
			i++;
			fnCount_au.put(type, i);
		} else {
			System.out.println("Processing error " + referenceID + " " + type + " FN map");
		}
		return new String[] { referenceID, type, " FN ", "" + a.getBegin(), "" + a.getEnd(), a.getCoveredText() };
	}

	@Override
	protected List<String[]> getRows(CAS cas) {
		ArrayList<String[]> rows = new ArrayList<String[]>();
		for (String auType : auSysMap.keySet()) {
			String toolType = auSysMap.get(auType);

			try {
				ArrayList<Annotation> auAnns = (ArrayList<Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(cas.getJCas(), auType, false);
				ArrayList<Annotation> toolAnns = (ArrayList<Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(cas.getJCas(), toolType, false);
				if (auAnns.size() == 0 && toolAnns.size() == 0) {
					continue;
				}
				else {
					if (totalCount.containsKey(auType)) {
						int i = totalCount.get(auType);
						i = i + auAnns.size();
						totalCount.put(auType, i);
					} else {
						System.out.println("Processing error " + referenceID + " " + auType + " total map");
					}
					if (totalCount.containsKey(toolType)) {
						int i = totalCount.get(toolType);
						i = i + toolAnns.size();
						totalCount.put(toolType, i);
					} else {
						System.out.println("Processing error " + referenceID + " " + toolType + " total map");
					}

					//	System.out.println(referenceID + " count au:" + auType + " -- " + auAnns.size() + " tool: "					    + toolType + " -- " + toolAnns.size());
					if (auAnns.size() > 0) {
						if (toolAnns.size() > 0) {
							for (Annotation au : auAnns) {
								if (AnnotationLibrarian.getAllOverlappingAnnotationsOfType(au, toolType, false).size() > 0) {
									rows.add(outTP(au, referenceID, auType));
								} else {
									// FN case.
									rows.add(outFN(au, referenceID, auType));
								}
							}
							for (Annotation tool : toolAnns) {
								if (AnnotationLibrarian.getAllOverlappingAnnotationsOfType(tool, auType, false).size() > 0) {
									// TP case.
									rows.add(outTP(tool, referenceID, toolType));
								} else {
									// FP case.
									rows.add(outFP(tool, referenceID, toolType));
								}
							}
						} else {
							// auAnns > 0, but toolAnns == 0. All auAnns are FN
							for (Annotation au : auAnns) {
								rows.add(outFN(au, referenceID, auType));
							}
						} // end auAnns > 0
					} else { // auAnns == 0
						// toolAnns > 0, all toolAnns are FP
						for (Annotation tool : toolAnns) {
							rows.add(outFP(tool, referenceID, toolType));
						}
					}
				}
			} catch (CASException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
		return rows;
	}

	@Override
	protected String[] getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

}
