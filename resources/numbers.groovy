
name = "NumbersAnnotation"
configuration {
	/* All configuration for this annotator. */

	global_settings {
		performance_monitoring = true
	}

	defaults {
		/* Global for all configurations below if a property specified here is not overridden in a section below. */
		outputType = "gov.va.vinci.vitals.types.Numeric"
		case_sensitive = false }


	/* An arbitrary name for this set of patterns/config. */
	"Whole_number" {
		expressions = [
			"(?<!\\.)\\b\\d{1,3}\\b(?!\\.\\d)" ,
			"(?<!\\.)\\b\\d{1,2}(?=(i|f))"	 ,
			"(?<=[rptb])\\d{2,3}\\b",
			"(?<!\\.)\\b\\d{2,3}(?=('?s\\b|\\b|b))"
		]
		concept_feature_value = "pressure, rates"
		concept_feature_name = "comment"
		matchedPatternFeatureName = "pattern"
		groupFeatureName="group"

		outputType = "gov.va.vinci.vitals.types.IntegerNumber" }
	/**
	 "ZeroDigit_number" {
	 expressions = ['\\b\\d{2,3}\\.0\\b']
	 concept_feature_value = "Pressure, rates"
	 outputType = "gov.va.vinci.vitals.types.ZeroDecimalNumber" }
	 /**/
	"Decimal_number"{
		expressions = [
			'\\b\\d{2,3}\\.\\d{1,2}\\b',
			"(?<=t)\\d{2,3}\\.\\d+\\b",
			"\\b\\d{2,3}\\.\\d+(?=f\\b)",
			"\\b\\d{2,3}\\.\\d+(?=c\\b)"
		]
		concept_feature_value = "temperature, weight"
		concept_feature_name = "comment"
		matchedPatternFeatureName = "pattern"
		groupFeatureName="group"
		outputType = "gov.va.vinci.vitals.types.DoubleNumber"
	}


	/** A prefix to numbers we annotate for exclusion in the numericValuesExclude.pattern file.
	"ExcludePrefix" {
		matchedPatternFeatureName = "pattern"
		expressions = [
		        "(reading|#|CVP|MAP|\\bi.o)\\s*"
		]
		outputType = "gov.va.vinci.vitals.types.ExcludePrefix"
	}
**/

}