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

import gov.va.vinci.kttr.types.*;
import gov.va.vinci.vitals.types.*;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.tcas.Annotation;

import java.util.ArrayList;
import java.util.HashMap;

public class ListenerLogic {
	public static int recordID = 0;

	// Using HashMap instead of String[] to allow arbitrary ordering of columns
	public static ArrayList<HashMap<String, String>> getRows(CAS aCas) {
		ArrayList<HashMap<String, String>> allRows = new ArrayList<HashMap<String, String>>();

		///////////////////////////////////////////////////////////		
		//  This is project specific!!!
		///////////////////////////////////////////////////////////
		// Output all refst annotations
		String[] types = new String[] {
		    Bp_value.class.getCanonicalName(),
		    T_value.class.getCanonicalName()
		    , Hr_value.class.getCanonicalName()
		    , BMI_value.class.getCanonicalName()
		    , Height_value.class.getCanonicalName()
		    , Weight_value.class.getCanonicalName()
		    , Pain_value.class.getCanonicalName()
		    , Resp_value.class.getCanonicalName()
		    , So2_value.class.getCanonicalName()
		    , Bp_Diastolic_value.class.getCanonicalName()
		    , Bp_Systolic_value.class.getCanonicalName()
		};
		for (String singleType : types) {
			Type type = aCas.getTypeSystem().getType(singleType);
			FSIndex<?> index = aCas.getAnnotationIndex(type);
			FSIterator<?> iterator = index.iterator();

			while (iterator.hasNext()) {
				recordID++;
				HashMap<String, String> lineRow = new HashMap<String, String>();
				Annotation a = (Annotation) iterator.next();

				if (((Output_Value) a).getUnit() != null) {
					lineRow.put("Unit", ((Output_Value) a).getUnit().getCoveredText().replaceAll("\\s+", " "));
					;
				}
				if (((Output_Value) a).getTimestamp() != null) {
					lineRow
					    .put("Timestamp", ((Output_Value) a).getTimestamp().getCoveredText().replaceAll("\\s+", " ").trim());
				}
				if (a instanceof Bp_Systolic_value) {
					lineRow.put("Systolic", ((Output_Value) a).getValue());
				} else if (a instanceof Bp_Diastolic_value) {
					lineRow.put("Diastolic", ((Output_Value) a).getValue());
				}

				lineRow.put("Result", ((Output_Value) a).getValue());
				lineRow.put("VitalType", singleType);
				lineRow.put("ValueString", a.getCoveredText().replaceAll("\\s+", " ").trim());
				lineRow.put("SpanStart", "" + a.getBegin());
				lineRow.put("SpanEnd", "" + a.getEnd());
				lineRow.put("VitalSignID", "" + recordID);

				int windowSize = 30;
				int start = a.getBegin() - windowSize;
				int end = a.getEnd() + windowSize;
				if (start < 0) {
					start = 0;
				}
				if (end > aCas.getDocumentText().length() - 1) {
					end = aCas.getDocumentText().length() - 1;
				}
				lineRow.put("Snippets", aCas.getDocumentText().substring(start, end).replaceAll("\\s+", " ").trim());
				allRows.add(lineRow);
			}
		}

		///////////////////////////////////////////////////////////
		return allRows;
	}

	// Using HashMap instead of String[] to allow arbitrary ordering of columns
	public static ArrayList<HashMap<String, String>> getReferenceRows(CAS aCas) {
		ArrayList<HashMap<String, String>> allRows = new ArrayList<HashMap<String, String>>();

		///////////////////////////////////////////////////////////		
		//  This is project specific!!!
		///////////////////////////////////////////////////////////
		// Output all refst annotations
		String[] types = new String[] {
		    BPValue.class.getCanonicalName(),
		    TValue.class.getCanonicalName()
		    , HRValue.class.getCanonicalName()
		    , BMIValue.class.getCanonicalName()
		    , HeightValue.class.getCanonicalName()
		    , WeightValue.class.getCanonicalName()
		    , PainValue.class.getCanonicalName()
		    , RespValue.class.getCanonicalName()
		    , OxygenValue.class.getCanonicalName()
		    , BPDiasValue.class.getCanonicalName()
		    , BPSysValue.class.getCanonicalName()
		};
		for (String singleType : types) {
			Type type = aCas.getTypeSystem().getType(singleType);
			FSIndex<?> index = aCas.getAnnotationIndex(type);
			FSIterator<?> iterator = index.iterator();

			while (iterator.hasNext()) {
				recordID++;
				HashMap<String, String> lineRow = new HashMap<String, String>();
				Annotation a = (Annotation) iterator.next();
				RefValue refValue = (RefValue) a;

				String t = refValue.getCoveredText().trim().replaceAll("[a-z]","").replaceAll("[A-Z]","").replaceAll("(>|%|\'s)","").replaceAll("/$","").replaceAll("\\s+","");
				String u = refValue.getCoveredText().trim().replaceAll("\\d+","").replaceAll("\\.","").replaceAll("\\s+","");
				lineRow.put("Result", t);
				lineRow.put("Unit", u);
				lineRow.put("VitalType", singleType.replaceAll("gov.va.vinci.kttr.types.", ""));
				lineRow.put("ValueString", a.getCoveredText().replaceAll("\\s+", " ").trim());
				lineRow.put("SpanStart", "" + a.getBegin());
				lineRow.put("SpanEnd", "" + a.getEnd());
				lineRow.put("VitalSignID", "" + recordID);

				int windowSize = 30;
				int start = a.getBegin() - windowSize;
				int end = a.getEnd() + windowSize;
				if (start < 0) {
					start = 0;
				}
				if (end > aCas.getDocumentText().length() - 1) {
					end = aCas.getDocumentText().length() - 1;
				}
				lineRow.put("Snippets", aCas.getDocumentText().substring(start, end).replaceAll("\\s+", " ").trim());
				
				allRows.add(lineRow);
			}
		}

		///////////////////////////////////////////////////////////
		return allRows;
	}
}
