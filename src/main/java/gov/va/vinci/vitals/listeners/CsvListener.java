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


import gov.va.vinci.leo.listener.BaseCsvListener;
import gov.va.vinci.leo.tools.LeoUtils;
import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author OVP
 */
public class CsvListener extends BaseCsvListener {
	public static Logger log = Logger.getLogger(LeoUtils.getRuntimeClass().toString());
	protected HashMap<String, Integer> fields = new HashMap<String, Integer>();
	protected ArrayList<String> headers = new ArrayList<String>();

	/**
	 * Creating a new listener using the String path to the new file 
	 * and the ArrayList with field names
	 * @param fileName
	 * @param fieldList
	 * @return
	 * @throws FileNotFoundException
	 */
	public static CsvListener createNewListener(String fileName,
	    ArrayList<ArrayList<String>> fieldList) throws FileNotFoundException {
		return new CsvListener(new File(fileName), fieldList);

	}

	/**
	 * 
	 * @param pw
	 * @throws FileNotFoundException
	 */
	public CsvListener(File pw) throws FileNotFoundException {
		super(pw);
		log.info("CSV output to: " + pw.getAbsolutePath());
	}

	/**
	 * 
	 * @param file
	 * @param fieldList
	 * @throws FileNotFoundException
	 */
	public CsvListener(File file, ArrayList<ArrayList<String>> fieldList) throws FileNotFoundException {
		super(file);
		this.setHeaders(fieldList);
	}

	@Override
	/**
	 *  The current project outputs values for the context annotator
	 */
	protected List<String[]> getRows(CAS cas) {
		// Common fields from the incoming row data
		HashMap<String, String> commonFields = new HashMap<String, String>();

		if (docInfo.getRowData() == null) {
			commonFields.put("TIUDocumentSID", ((docInfo.getID()).split("_"))[0]);
		} else {
			for (Entry<String, Integer> header : fields.entrySet()) {
				if (header.getValue() >= 0)
					commonFields.put(header.getKey(),
					    docInfo.getRowData(header.getValue()));
			}
		}

		// Specific fields - possible multiple rows
		ArrayList<String[]> rows = new ArrayList<String[]>();

		ArrayList<HashMap<String, String>> rowsMap = ListenerLogic.getRows(cas);

		for (HashMap<String, String> rowMap : rowsMap) {
			//Add common fields
			rowMap.putAll(commonFields);
			// populate an ordered list of values for each column
			ArrayList<String> rowList = new ArrayList<String>();
			for (String column : headers) {
				if (rowMap.containsKey(column)) {
					rowList.add(rowMap.get(column));
				} else {
					rowList.add("");
				}
			}
			rows.add(rowList.toArray(new String[rowList.size()]));
		}
		
		return rows;
	}

	/**
	 * 
	 * @param fieldList
	 */
	public void setHeaders(ArrayList<ArrayList<String>> fieldList) {
		fields = new HashMap<String, Integer>();
		for (ArrayList<String> entry : fieldList) {
			headers.add(entry.get(0));
			fields.put(entry.get(0), Integer.parseInt(entry.get(1)));
		}
	}

	@Override
	protected String[] getHeaders() {
		return headers.toArray(new String[headers.size()]);
	}

}// EliteCsvListener class