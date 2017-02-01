
name = "ConceptsAndTermsAnnotation"
configuration {
	global_settings {
		performance_monitoring = true
	}

	/* All configuration for this annotator. */
	defaults {
		/* Global for all configurations below if a property specified here is not overridden in a section below. */
		outputType = "gov.va.vinci.vitals.types.Term"
		concept_feature_name = "concept"
		matchedPatternFeatureName = "pattern"
		groupFeatureName = "group"
		case_sensitive = false }


	/* An arbitrary name for this set of patterns/config. */
	"Temperature" {
		expressions = [
			'\\btemp\\w*\\b',
			'fever\\b',
			'\\bt\\b',
			'\\bt(?=\\d)',
			'\\btm\\b',
			'\\btc\\b',
			'\\btm/c\\b',
			'\\btm and t?c\\b',
			'\\btmax\\b',
			'\\btcur\\b',
			'\\bt *current\\b',
			'\\bTemp +F *\\(C\\)',
			'febrile',
			'\\bafeb\\b'
		]
		concept_feature_value = "Temperature"
		outputType = "gov.va.vinci.vitals.types.T_Term" }

	"Systolic"{
		expressions = [
			'bp *systolic',
			'systolic',
			'systolic *bp',
			'sytolic',
			'\\bsys\\b',
			'\\bsyst\\b',
			'\\bsbp\\b',
			'\\bbps\\b'
		]
		concept_feature_value = "Systolic"
		outputType = "gov.va.vinci.vitals.types.Bp_Term"}

	"Diastolic" {
		expressions= [
			"(bp *)?diastolic( *bp)?",
			'diastolic',
			'dias\\w*' ,
			'\\bdbp\\b'
		]
		concept_feature_value = "Diastolic"
		outputType = "gov.va.vinci.vitals.types.Bp_Term"}


	"Blood_Pressure" {
		expressions = [
			'\\babp\\b',
			'\\bsys *dias',
			'sbp *dbp\\b',
			'\\bb(/|-|\\.)?p(\\.)?\\b',
			'blood\\s*pressures?\\b',
			'bp *lying',
			'bp *range',
			'bp *recheck',
			//	'bp *standing',
			'bp *today',
			'lying *bp',
			'manual *pressure',
			'\\bnbp\\b',
			'repeat *bp\\b',
			'rest *bp\\b',
			'sitting *blood *presure',
			'sitting *bp',
			'sitting p\\b',
			//	'standing *blood *presures?\\b',
			//	'standing(\\s*after\\s*\\d+\\sminutes?)?',
			//  standing after 3 minutes
			//	'lying\\b',
			'BP\\s+LEFT\\s+ARM\\s+SITTING',
			'\\bb\\.p\\.'
		]
		concept_feature_value = "Blood_Pressure"
		outputType = "gov.va.vinci.vitals.types.Bp_Term" }

	"BMI" {
		expressions = ['bmi', 'body\\s+mass\\s+index']
		concept_feature_value = "BMI"
		outputType = "gov.va.vinci.vitals.types.Bmi_Term" }

	"Heart_Rate" {
		expressions = [
			'\\bpulse\\b(\\srate)?',
			'(max|mean|min)\\s+pulse',
			'\\bhr(?=(\\b|\\d))',
			'\\bheart\\s*rate\\b',
			'\\bp[uls]+e\\b',
			'\\bp\\b',
			'\\bpl\\b',
			'\\bpr\\b',
			'apical pulse',
			'heart *rate',
			'hr before tx',
			'pulse pre\\b',
			'pulse rate',
			'pulses\\b',
			'radial pulse',
			'\\bp(?=\\d{2,3}\\b)',
			'pulse\\s+(dropped|raised|went\\s+up)\\s+to',
			'Ventricular\\s+Rate',
			'\\bAF\\b',
			'\\ba(.){0,3}fib\\b',
			'atrial fib\\w*\\b',
			'\\bap\\b',
			'\\bat fib\\b'
		]
		concept_feature_value = "Heart_Rate"
		outputType = "gov.va.vinci.vitals.types.Hr_Term" }

	"Height" {
		expressions = [
			'\\bht\\b',
			'\\bHt +in *(\\()?cm(\\))?',
			'Ht *in *\\( *cm *\\)',
			'\\bheight\\b'
		]
		concept_feature_value = "Height"
		outputType = "gov.va.vinci.vitals.types.Height_Term" }

	"Pain" {
		expressions = ['Pain', 'pain score', 'PAIN INTENSITY', 'LEVEL OF PAIN', 'severity']
		concept_feature_value = "Pain"
		outputType = "gov.va.vinci.vitals.types.Pain_Term" }

	"Respiratory" {
		expressions = [
			'\\brate\\b',
			'\\bresp\\b',
			'\\brespir\\b',
			'\\brr\\b',
			'(?<!\\w)rr(?=(\\b|\\d))',
			'\\br\\b',
			'respiration\\s*rate',
			'respirations',
			'respiration',
			'respiratory\\s*rate',
			'\\br(?=\\d{2,3}\\b)'
		]
		concept_feature_value = "Respiratory"
		outputType = "gov.va.vinci.vitals.types.Resp_Term"}

	"SO2" {
		expressions = [
			'SO2',
			'\\bsats?\\b',
			'\\bpo2',
			'SAO2',
			'saturations?',
			'saturation\\s*O2',
			'saturation\\s*o2\\s*stats',
			'O2 *SATS',
			'SpO2',
			'PO2 *\\(L/Min\\)\\(%\\)',
			'POx *\\( *L */ *Min *\\) *\\( *% *\\)',
			'PULSE OXIMETRY RESULTS',
			'PULSE\\s+OXIMETRY\\s+\\(MOST RECENT IN LAST YEAR\\)',
			'PULSE OXIMETRY',
			'Pulse Ox\\w*\\b',
			'\\bpox\\b',
			'saturating',
			'02 *sats?',
			'o2 *sats?',
			'\\bsat\\w*ing'
		]
		concept_feature_value = "SO2"
		outputType = "gov.va.vinci.vitals.types.So2_Term" }

	"Weight" {
		expressions = [
			'weight',
			'\\bwt\\b',
			'\\bw\\b',
			'\\bw(?<=\\d{2,3}\\b)',
			'Wt *lbs *\\(kg\\)'
		]
		concept_feature_value = "Weight"
		outputType = "gov.va.vinci.vitals.types.Weight_Term"}

	"NotIt_Term" {
		expressions = [
			"\\bage\\b",
			"\\bdob\\b",
			"\\ba.c *r\\b",
			"wbc",
			"simv(\\s*rate)?",
			"VENTILATOR\\s*rate" ,
			"\\balt\\b",
			"\\bast\\b",
			"\\bglucose\\b",
			"\\blipase\\b",
			"\\baf\\b",
			"\\buo\\b",
			"\\bi/o\\b",
			"cvp\\b",
			"\\bCl\\b",
			"\\bCO2\\b",
			"\\bBUN\\b",
			"\\bCr\\b",
			"\\bGlu\\b",
			"ZINC\\s*OXIDE",
			"labs\\b",
			"\\bA/P\\b",
			"\\bS/P\\b",
			"\\bml/hr\\b",
			"\\bunits/hr",
			'\\bpasp\\b',
			'FIO2',
			'Atrial Rate',
			'vac',
			'ileo',
			"uop",
			"d5ns",
			"\\buo\\b",
			"esmolol",
			"\\bmap\\b",
			"\\bng\\b"
		]
		concept_feature_value = "NotIt_Term"
		outputType = "gov.va.vinci.vitals.types.NotIt_Term" } }
