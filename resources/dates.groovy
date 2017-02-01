
name = "TimestampAnnotator"
configuration {
	global_settings {
		performance_monitoring = true
	}

	/* All configuration for this annotator. */
	defaults {
		/* Global for all configurations below if a property specified here is not overridden in a section below. */
		outputType = "gov.va.vinci.vitals.types.Timestamp"
		matchedPatternFeatureName = "pattern"
		case_sensitive = false
	}
/**
	"month" {
		expressions = [
				"[\\n\\r ](jan(uary)?|feb(ruary)?|mar(ch)?|apr(il)?|may|jun(e)?|jul(y)?|aug(ust)?|sep(tember)?|oct(ober)?|nov(ember)?|dec(ember)?) +"
		]
		outputType = "gov.va.vinci.vitals.types.Month"
	}
**/
	/* An arbitrary name for this set of patterns/config. */
	"Timestamps" {
		expressions = [
				// 11/21/2000 14:48
				"\\d{1,2}/\\d{1,2}/\\d{2,4} +\\d{1,2}:\\d{2}",

				// @0930
				"@\\s*\\d{4}",

				// at 0015
				"\\bat *\\d{4}",
				"(?<=\\n) *\\d{4} *(?=( |:|-|h\\b))",

				// JUL 21, 2009@08:31:41
				// NOV 10,2008@09:43:25
				// JUN 18, 2009@07:34:28
				// Mar 19,2007@08:00
				"(\\()?\\b\\w{2,4} *\\d+, *\\d+@\\d{2}:\\d{2}(:\\d{2})?(\\))?",

				//01:41
				"\\b\\d\\d:\\d\\d\\b",

				// 11/10/08
				// 9/25/08
				"\\b\\d{1,2}/\\d{1,2}/\\d\\d(\\d\\d)?",

				// 11/10/08 @ 1355
				"\\b\\d{1,2}/\\d{1,2}/\\d\\d(\\d\\d)? *@ *\\d\\d:?\\d\\d",


				// 10/19/2006 09:13 AM Thursday
				"\\b\\d{1,2}/\\d{1,2}/\\d\\d(\\d\\d)?( *@)? *\\d\\d:?\\d\\d *(am|pm)( ?(mon\\w*|tue\\w*|wed\\w*|thu\\w*|fri\\w*|sat\\w*|sun\\w*))",

				// 28 SEPT 2009
				"\\d+ *(jan\\w*|feb\\w*|mar\\w*|apr\\w*|may\\w*|jun\\w*|jul\\w*|aug\\w*|sep\\w*|oct\\w*|nov\\w*|dec\\w*),? *\\d{2,4}",



				"\\d+:\\d+:\\d+ ?(am|pm)",

				"\\d+\\/\\d+\\/\\d+\\s+\\d+:\\d+(:\\d+ ?(am|pm)?)?"

			]
	}

}
