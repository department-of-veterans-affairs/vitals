
name = "TermExcludePatternAnnotator"
configuration {
	global_settings {
		performance_monitoring = true
	}

	/* All configuration for this annotator. */
	defaults {
		/* Global for all configurations below if a property specified here is not overridden in a section below. */
		outputType = "gov.va.vinci.vitals.types.TermExclude"
		matchedPatternFeatureName = "pattern"
		case_sensitive = false }


	/* An arbitrary name for this set of patterns/config. */
	"excludeSectionHeader" {
		expressions = [
				"labs",
				"chemistry",
				"laboratory",
				"HEMODYNAMICS",
				"ANGIOGRAPHY",
				"goal",
				"Glucose",
				"Electrolytes",
				"\\bi/o\\b",
				"Orthostats",
				"\\nABG"
			]
	}

}
