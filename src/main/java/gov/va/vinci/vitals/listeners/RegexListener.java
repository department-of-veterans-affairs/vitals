package gov.va.vinci.vitals.listeners;

/*
 * #%L
 * Leo
 * %%
 * Copyright (C) 2010 - 2014 Department of Veterans Affairs
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import gov.va.vinci.leo.listener.BaseListener;
import gov.va.vinci.vitals.types.NumericExclude;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.jcas.tcas.Annotation;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Note: This is a simple listener that helps debug regex pattern/counts/matches while doing performance optimization
 * on vitals. It is likely not useful except for performance tuning/verification that pattern changes are not changing
 * match counts.
 *
 * @author vhaislcornir
 */
public class RegexListener extends BaseListener {

	protected List<DetailRow> results = new ArrayList<DetailRow>();


	public void printSummary() {
		Map<String, Long> patternSummary =
				results.stream().collect(
						Collectors.groupingBy(item->item.getPattern(), Collectors.counting()
						)
				);

		System.out.println("Pattern Summary:" );
		for (String key : patternSummary.keySet()) {
			System.out.println(StringUtils.rightPad("'" + key + "'", 40, " ") + "\t\t" + patternSummary.get(key));
		}

		System.out.println("\n\n");
		System.out.println("Match Summary:");

		Map<String, Long> matchSummary =
				results.stream().collect(
						Collectors.groupingBy(item->item.getMatch(), Collectors.counting()
						)
				);
		List<String> matchList = new ArrayList<>(matchSummary.keySet());
		Collections.sort(matchList, (String s1, String s2) -> s1.compareToIgnoreCase(s2) );
		for (String key : matchList) {
			System.out.println(StringUtils.rightPad("'" + key +  "'", 40, " ") + "\t\t" + matchSummary.get(key));
		}

		System.out.println("\n\n");

	}
	public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
		List<T> list = new ArrayList<T>(c);
		Collections.sort(list);
		return list;
	}


	/**
	 * @see org.apache.uima.aae.client.UimaAsBaseCallbackListener#entityProcessComplete(CAS, EntityProcessStatus)
	 * @param aCas      the CAS containing the processed entity and the analysis results
	 * @param aStatus   the status of the processing. This object contains a record of any Exception that occurred, as well as timing information.
	 */
	@Override
	public void entityProcessComplete(CAS aCas, EntityProcessStatus aStatus) {

		super.entityProcessComplete(aCas, aStatus);

		Iterator<Type> typeInterator = aCas.getTypeSystem().getTypeIterator();

		while (typeInterator.hasNext()) {
			try {
				Type type = typeInterator.next();
				int typeCount = aCas.getAnnotationIndex(type).size();

				FSIterator<AnnotationFS> index = aCas.getAnnotationIndex(type).iterator();
				while (index.hasNext()) {
					Annotation a = (Annotation) index.next();

					/**
					 * Create window code if wanting context of a match.
					int start = a.getBegin() -10;
					int end = a.getEnd() + 10;
					if (start < 0) {
						start = 0;
					}
					if (end > aCas.getDocumentText().length()) {
						end = aCas.getDocumentText().length() - 1;
					} **/


					if (type.getName().endsWith(".NumericExclude")) {
						NumericExclude term = (NumericExclude) a;
						if (term.getPattern() == null) {
							System.out.println("Null pattern.");
							results.add(new DetailRow("ct:" + a.getCoveredText(), a.getCoveredText()));
						} else {
							results.add(new DetailRow(term.getPattern().replaceAll("\n", "<n>").replaceAll("\r", "<r>"), a.getCoveredText().replaceAll("\n", "<n>").replaceAll("\r", "<r>")));
						}

					}
				}

			} catch (Exception e) {
				// do op.
				//System.out.println(e);
			}
		}
	}

	@Override
	public void collectionProcessComplete(EntityProcessStatus aStatus) {
		this.printSummary();

	}

	class DetailRow {
		String pattern;
		String match;

		public DetailRow(String pattern, String match) {
			this.pattern = pattern;
			this.match = match;
		}

		public String getPattern() {
			return pattern;
		}

		public void setPattern(String pattern) {
			this.pattern = pattern;
		}

		public String getMatch() {
			return match;
		}

		public void setMatch(String match) {
			this.match = match;
		}

	}

}