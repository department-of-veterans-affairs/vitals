import gov.va.vinci.leo.model.DatabaseConnectionInformation
import gov.va.vinci.vitals.listeners.DbsListener

DatabaseConnectionInformation databaseConnectionInformation = new DatabaseConnectionInformation(
        "com.mysql.jdbc.Driver",
        "jdbc:mysql://localhost:3306/vitals",
        "vitals", "vitals");

ArrayList<ArrayList<String>> fields = new ArrayList<>();
fields.add(["VitalSignID", "-1", "int"])


listener = DbsListener.createNewListener(databaseConnectionInformation, "", "output_table", 1000,
        [
                ["VitalSignID", "-1", "int"],
                ["Sta3n", "4", "varchar(10)"],
                //["TIUDocumentSID", "0", "varchar(30)"],//
                ["TIUDocumentSID", "0", "bigint"],
                ["ReferenceDateTime", "3", "datetime"],
                ["Term", "-1", "varchar(1000)"],
                ["VitalType", "-1", "varchar(1000)"],
                ["Result", "-1", "varchar(1000)"],
                ["Systolic", "-1", "varchar(1000)"],
                ["Diastolic", "-1", "varchar(1000)"],
                ["ValueString", "-1", "varchar(1000)"],
                ["Assessment", "-1", "varchar(1000)"],
                ["Unit", "-1", "varchar(1000)"],
                ["Timestamp", "-1", "varchar(1000)"],
                ["Snippets", "-1", "varchar(2000)"],
                ["SpanStart", "-1", "int"],
                ["SpanEnd", "-1", "int"]
        ]);


listener.createTable(databaseConnectionInformation,
        '''create table output_table (
                VitalSignID int,
                Sta3n varchar(10),
                TIUDocumentSID int,
                ReferenceDateTime varchar(100),
                Term varchar(1000),
                VitalType varchar(1000),
                Result varchar(1000),
                Systolic varchar(1000),
                Diastolic varchar(1000),
                ValueString varchar(1000),
                Assessment varchar(1000),
                Unit varchar(1000),
                Timestamp varchar(1000),
                Snippets varchar(2000),
                SpanStart int,
                SpanEnd int
            )''', false, "output_table")