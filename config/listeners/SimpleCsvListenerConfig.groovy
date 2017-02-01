import gov.va.vinci.vitals.listeners.SimpleListener
import gov.va.vinci.vitals.types.So2_Term
import gov.va.vinci.vitals.types.So2_value
import gov.va.vinci.vitals.types.Weight_Term
import gov.va.vinci.vitals.types.Weight_value

String csvDirPath = "output/";

String csvFile = "simple-output.csv";

if (!(new File(csvDirPath).exists()))
    new File(csvDirPath).mkdirs();

listener = new SimpleListener(new File(csvDirPath + "/" + csvFile), true,

        [

                So2_Term.canonicalName,
                So2_value.canonicalName,
                Weight_Term.canonicalName,
                Weight_value.canonicalName
        ] as String[]);