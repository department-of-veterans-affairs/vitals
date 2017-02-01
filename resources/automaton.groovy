
name = "AutomatonAnnotator"
configuration {
	global_settings {
		performance_monitoring = true
	}

	defaults {
		case_sensitive = false
	}

	"month" {
		expressions = [
		        "(^|[\n\r ])(jan(uary)?|feb(ruary)?|mar(ch)?|apr(il)?|may|jun(e)?|jul(y)?|aug(ust)?|sep(tember)?|oct(ober)?|nov(ember)?|dec(ember)?) +"
		]
		outputType = "gov.va.vinci.vitals.types.Month"
	}
	/** A prefix to numbers we annotate for exclusion in the numericValuesExclude.pattern file.
	 **/
	"ExcludePrefix" {
		 trim_left = true
		 trim_right = false
		matchedPatternFeatureName = "pattern"
		expressions = [
				"(reading|\\#|cvp|map|(^|\t|\n|\r| )i.o)[ \t\r\n]*"
		]
		outputType = "gov.va.vinci.vitals.types.ExcludePrefix"
	}


}
