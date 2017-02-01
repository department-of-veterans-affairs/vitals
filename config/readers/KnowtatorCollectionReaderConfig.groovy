
import gov.va.vinci.knowtator.cr.KnowtatorCollectionReader
import gov.va.vinci.knowtator.model.KnowtatorToUimaTypeMap;

knowtatorCorpusPath = "c:\\my-dir\\corpus\\";
knowtatorXmlPath = "c:\\my-dir\\saved\\"

KnowtatorToUimaTypeMap map = new KnowtatorToUimaTypeMap();
map.addAnnotationTypeMap("blood_pressure_term","gov.va.vinci.kttr.types.BPTerm");
map.addAnnotationTypeMap("blood_pressure_value","gov.va.vinci.kttr.types.BPValue");
map.addAnnotationTypeMap("BP_systolic","gov.va.vinci.kttr.types.BPSysValue");
map.addAnnotationTypeMap("BP_diastolic","gov.va.vinci.kttr.types.BPDiasValue");
map.addAnnotationTypeMap("Indicator","gov.va.vinci.kttr.types.Indicator");
map.addAnnotationTypeMap("pulse_term","gov.va.vinci.kttr.types.HRTerm");
map.addAnnotationTypeMap("pulse_value","gov.va.vinci.kttr.types.HRValue");
map.addAnnotationTypeMap("temperature_term","gov.va.vinci.kttr.types.TTerm");
map.addAnnotationTypeMap("temperature_value","gov.va.vinci.kttr.types.TValue");
map.addAnnotationTypeMap("Document_reviewed","gov.va.vinci.kttr.types.Other");
map.addAnnotationTypeMap("bmi_value","gov.va.vinci.kttr.types.BMIValue");
map.addAnnotationTypeMap("height_value","gov.va.vinci.kttr.types.HeightValue");
map.addAnnotationTypeMap("weight_value","gov.va.vinci.kttr.types.WeightValue");
map.addAnnotationTypeMap("oxygen_value","gov.va.vinci.kttr.types.OxygenValue");
map.addAnnotationTypeMap("pain_value","gov.va.vinci.kttr.types.PainValue");
map.addAnnotationTypeMap("respiration_value","gov.va.vinci.kttr.types.RespValue");
map.addAnnotationTypeMap("time_value","gov.va.vinci.kttr.types.TimeValue");
map.addAnnotationTypeMap("pain_term","gov.va.vinci.kttr.types.Other");
map.addAnnotationTypeMap("bmi_term","gov.va.vinci.kttr.types.Other");
map.addAnnotationTypeMap("height_term","gov.va.vinci.kttr.types.Other");
map.addAnnotationTypeMap("weight_term","gov.va.vinci.kttr.types.Other");
map.addAnnotationTypeMap("oxygen_term","gov.va.vinci.kttr.types.Other");
map.addAnnotationTypeMap("respiration_term","gov.va.vinci.kttr.types.Other");
map.addAnnotationTypeMap("time_term","gov.va.vinci.kttr.types.Other");

reader = new KnowtatorCollectionReader(new File(knowtatorCorpusPath),
                                       new File(knowtatorXmlPath),
                                       map, true).produceCollectionReader()