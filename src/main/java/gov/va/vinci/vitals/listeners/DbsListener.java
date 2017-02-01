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

import gov.va.vinci.leo.listener.BaseDatabaseListener;
import gov.va.vinci.leo.model.DatabaseConnectionInformation;
import gov.va.vinci.leo.tools.LeoUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class DbsListener extends BaseDatabaseListener {

	private static final Logger log = Logger.getLogger(LeoUtils
	    .getRuntimeClass().toString());
	protected HashMap<String, Integer> fields = new HashMap<String, Integer>();
	protected ArrayList<String> headers = new ArrayList<String>();

	public String createStatement;

	/**
	 * This method creates a DbsListener object and initializes createStatement
	 * 
	 * @param databaseConnectionInformation
	 * @param dbsName
	 * @param tableName
	 * @param batchSize
	 * @param fieldList
	 * @return
	 */
	public static DbsListener createNewListener(
	    DatabaseConnectionInformation databaseConnectionInformation,
	    String dbsName, String tableName, int batchSize,
	    ArrayList<ArrayList<String>> fieldList) {

		String createStatement = createCreateStatement(dbsName, tableName,
		    fieldList);
		String insertStatement = createInsertStatement(dbsName, tableName,
		    fieldList);
		boolean validateConnectionEachBatch = true;
		return new DbsListener(databaseConnectionInformation, insertStatement,
		    batchSize, validateConnectionEachBatch, createStatement,
		    fieldList);
	}

	/**
	 * Main constructor
	 * 
	 * @param databaseConnectionInformation
	 * @param preparedStatementSQL
	 * @param batchSize
	 * @param validateConnectionEachBatch
	 * @param createStatement
	 */
	public DbsListener(
	    DatabaseConnectionInformation databaseConnectionInformation,
	    String preparedStatementSQL, int batchSize,
	    boolean validateConnectionEachBatch, String createStatement,
	    ArrayList<ArrayList<String>> fieldList) {
		super(databaseConnectionInformation, preparedStatementSQL);
		this.setBatchSize(batchSize);
		this.setValidateConnectionEachBatch(validateConnectionEachBatch);
		this.createStatement = createStatement;
		this.setHeaders(fieldList);
	}

	/**
	 * Creates the database table. Accepts the create statement and executes it
	 * in the target database
	 * 
	 * @param dbConnectionInfo
	 * @param createStatement
	 * @param dropFirst
	 * @param tableName
	 * @throws Exception
	 */
	public void createTable(DatabaseConnectionInformation dbConnectionInfo,
	    String createStatement, boolean dropFirst, String tableName)
	    throws Exception {
		Class.forName(dbConnectionInfo.getDriver()).newInstance();
		log.info("Creating a table for output \r\n" + createStatement);
		Connection conn = DriverManager.getConnection(
		    dbConnectionInfo.getUrl(), dbConnectionInfo.getUsername(),
		    dbConnectionInfo.getPassword());
		if (dropFirst && StringUtils.isNotBlank(tableName)) {
			conn.createStatement().execute("DROP TABLE " + tableName);
		}// if

		conn.createStatement().execute(createStatement);
	}// createTable method

	/**
	 * Static method to create insert statement based on the database and table
	 * name
	 * 
	 * @param dbsName
	 * @param tableName
	 * @param fieldList
	 * @return
	 */
	public static String createInsertStatement(String dbsName,
	    String tableName, ArrayList<ArrayList<String>> fieldList) {
		String statement = "INSERT INTO " + dbsName + "." + tableName + " ( ";
		String values = "";
		for (ArrayList<String> entry : fieldList) {
			statement = statement + entry.get(0) + ", ";
			values = values + " ?,";
		}
		statement = statement.substring(0, statement.length() - 2)
		    + " ) VALUES ( " + values.substring(0, values.length() - 1)
		    + " ) ;";
		log.info(statement);
		return statement;
	}

	/**
	 * Static method to create a table create statement based on the database
	 * and table name and list of fields
	 * 
	 * @param dbsName
	 * @param tableName
	 * @param fieldList
	 * @return
	 */
	public static String createCreateStatement(String dbsName,
	    String tableName, ArrayList<ArrayList<String>> fieldList) {

		String statement = "Drop table  " + dbsName + "." + tableName + "; CREATE TABLE " + dbsName + "." + tableName + " ( ";
		for (ArrayList<String> entry : fieldList) {
			statement = statement + entry.get(0) + " " + entry.get(2) + ", ";
		}
		statement = statement.substring(0, (statement.length() - 2)) + " ) ;";
		return statement;
	}

	@Override
	protected List<Object[]> getRows(CAS aCas) {
		ArrayList<Object[]> rows = new ArrayList<Object[]>();
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

		ArrayList<HashMap<String, String>> rowsMap = ListenerLogic.getRows(aCas);
		//.getReferenceRows(aCas);

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
	protected void setHeaders(ArrayList<ArrayList<String>> fieldList) {
		fields = new HashMap<String, Integer>();
		for (ArrayList<String> entry : fieldList) {
			headers.add(entry.get(0));
			fields.put(entry.get(0), Integer.parseInt(entry.get(1)));
		}
	}

}// DBListener

