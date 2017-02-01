import gov.va.vinci.vitals.listeners.CsvListener

listener = new CsvListener(new File("c:\\my_dir\\output\\outputTable_1.csv"),
        ["VitalSignID", "-1", "int"],
        ["Sta3n", "4", "varchar(10)"],
        ["TIUDocumentSID", "0", "bigint"],
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
        ["SpanEnd", "-1", "int"]);