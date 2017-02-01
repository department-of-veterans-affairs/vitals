
name = "ExcludeNumberRegexAnnotator"
configuration {
	global_settings {
		performance_monitoring = true
	}

	/* All configuration for this annotator. */
	defaults {
		/* Global for all configurations below if a property specified here is not overridden in a section below. */
		outputType = "gov.va.vinci.vitals.types.NumericExclude"
		case_sensitive = false }


	/* An arbitrary name for this set of patterns/config. */
	"NumericPatterns" {
		expressions = [
				//# (801) 647-9999
				"\\( ?\\d+ ?\\) ?((\\d+|\\-|\\*) ?)+",


				// single digit number is ok for pain. Will have to reconsider when pain is included in the extraction
				// [^\\.]\\b\\d\\b

				// 09/09/1990
				"\\b\\d+/\\d+/\\d+\\b",
				"\\b\\d+:\\d+:\\d+\\b",

				// 09-09-09
				// \\b\\d+-\\d+-\\d+\\b


				"\\w+ *'\\d\\d\\b",
				"\\n *\\d+\\. ",
				"\\n *\\d+\\)",



				"\\b\\d/\\d+\\b",
				// The idea was to disregard dates and socials. but this is too common of a pattern for vital signs
				//\\d\\d\\d+-\\d\\d+-\\d\\d\\d+

				"mofh\\s+0-72"
			]
	}

}
