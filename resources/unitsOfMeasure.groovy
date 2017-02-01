/* An arbitrary name for this annotator. Used in the pipeline for the name of this annotation. */
// Blood_Pressure, Heart_Rate, Temperature, Height, Weight, SO2, BMI, Pain;
name = "UnitOfMeasureAnnotation"
configuration {
	/* All configuration for this annotator. */
	global_settings {
		performance_monitoring = true
	}
	
	defaults {
		/* Global for all configrations below if a property specified here is not overridden in a section below. */
		outputType = "gov.va.vinci.vitals.types.Unit"
		concept_feature_name = "concept"
		matchedPatternFeatureName = "pattern"
		groupFeatureName = "group"
		case_sensitive = false
	}


	/* An arbitrary name for this set of patterns/config. */
	"Temperature" {
		expressions = [
			'\\bc\\b',
			'\\bf\\b',
			'(?<=\\d)f\\b',
			'(?<=\\d)c\\b',
			'degrees Fahr\\w+',
			'centegrade',
			'centigrade',
			'celcius'
		]
		concept_feature_value = "Temperature"
	}

	"Blood_Pressure" {
		expressions = ['\\bmm ?(\\[)?hg(\\])?', '\\bmm\\/Hg', '\\bmmhg\\b']
		concept_feature_value = "Blood_Pressure"
	}

	"Heart_Rate" {
		expressions = ['beats per minute', '\\bbpm\\b', '/min\\b', '/mt\\b']
		concept_feature_value = "Heart_Rate"
	}
	"Height" {
		expressions = [
			'\\bin\\b',
			'\\"',
			'\\bm\\b',
			'\\bcm\\b',
			'\\binch\\b',
			'\\binches',
			'\\bft\\b',
			'\\bfeet\\b'
		]
		concept_feature_value = "Height"
	}
	"Weight" {
		expressions = ['\\blb\\b', '\\blbs\\b', '\\bkg\\b', '\\bpounds?\\b', '\\#']
		concept_feature_value = "Weight"
	}
	"SO2" {
		expressions = ['per *cent', '%']
		concept_feature_value = "SO2"
	}

	"NotIt_Term" {
		expressions = ["ms", "mcg/min",  "ml/hr", "c/o"]
		concept_feature_value = "NotIt_Term"
	}
}

