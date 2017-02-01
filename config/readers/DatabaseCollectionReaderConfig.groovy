import gov.va.vinci.leo.cr.BatchDatabaseCollectionReader

String url = "jdbc:sqlserver://myserver:1433;databasename=myDB;integratedSecurity=true";
String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
String username = "";
String password = "";
String query = "SELECT documentId, patientId, reportText FROM mydb where reportText is not null ;"
String idColumn = "documentId";
String noteColumn = "reportText";
int minRecordNumber = 1
int maxRecordNumber = 100;
int batchSize = 1000;

reader = new BatchDatabaseCollectionReader(driver, url, username, password,
                                           query, idColumn, noteColumn, minRecordNumber,
                                           maxRecordNumber, batchSize).produceCollectionReader();