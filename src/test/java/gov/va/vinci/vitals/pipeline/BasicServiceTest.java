package gov.va.vinci.vitals.pipeline;

/*
 * #%L
 * Vitals extractor
 * %%
 * Copyright (C) 2010 - 2017 University of Utah / VA
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

import gov.va.vinci.leo.AnnotationLibrarian;
import gov.va.vinci.leo.descriptors.LeoAEDescriptor;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.types.CSI;
import gov.va.vinci.vitals.listeners.ListenerLogic;
import gov.va.vinci.vitals.types.*;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.tools.AnnotationViewerMain;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

/**
 * This test is designed to always check if the correct number of numeric values is present
 * @author vhaslcpatteo
 *
 */
public class BasicServiceTest {

	protected LeoAEDescriptor aggregate = null;
	protected LeoTypeSystemDescription types = null;
	protected String outputDir = "output/xmi";
	protected String aggXmi = "aggregateDesc";

	@Before
	public void setup() throws Exception {
		VitalsPipeline pipeline = new VitalsPipeline();
		LeoTypeSystemDescription types = pipeline.getLeoTypeSystemDescription();

		aggregate = pipeline.createNumericPipeline(types);
		aggregate.addDelegate(pipeline.createTermAndIndicatorPipeline(types));
		aggregate.addDelegate(pipeline.createPatternsPipeline(types));
		aggregate.addDelegate(pipeline.createVitalRulesPipeline(types));

		File o = new File(outputDir);
		if (!o.exists()) {
			o.mkdirs();
		}// if
	}// setup

	public String getDocText1() throws IOException {
		return "\n" +
		    "Neuro:\n" +
		    "Intubated, follows commands, denies pain\n" +
		    "meds:  dilaudid 0.2mg /ativan 1mg prn\n" +
		    "\n" +
		    "CV:\n" +
		    "HR 40-110 (55 current)\n" +
		    "BP 90-130/30-60 (98/51)\n" +
		    "CVP 15-24\n" +
		    "\n" +
		    "\n" +
		    "Vital Signs: BP- 110/65 HR-75 RR-18 Temp of 97.8 \n" +
		    "\n\n\n\n" +
		    "VITAL SIGNS:\n" + 
		    "Temp:  98.6 F [37.0 C] (12/10/2005 08:35)\n" + 
		    "HR:  101 (12/10/2005 08:35)\n" + 
		    "RESP:  20 (12/10/2005 08:35)\n" + 
		    "BP:  100/58 (12/10/2005 08:35)\n" + 
		    "Pain:  5 (12/10/2005 08:35)\n" + 
		    "SpO2:  PULSE OXIMETRY (MOST RECENT IN LAST YEAR): \n" + 
		    "  96 (DEC 10, 2005@08:35:21)\n" + 
		    "\n\n" +
		    "TF at 85/hr\n" + 
		    "IVF at 150/hr\n" + 
		    "Tm 37.4 c37.2 hr 80-100s  bp 80-140/40-70\n" + 
		    "gen: intubated, sedated not following commands\n" + 
		    "lungs: cta on vent\n" + 
		    "";

	}

	@Test
	public void testSingleFile() throws ResourceInitializationException, IOException,
	    AnalysisEngineProcessException {
		AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(aggregate.getAnalysisEngineDescription());

		String docText = "";
		JCas jcas = null;
		/**/
		String filename = "fileFromString";
		docText = getDocText1();
		jcas = createJCas(ae, docText, filename);
		ae.process(jcas);
		outputXmi(filename, jcas);

		justPrint(jcas, filename);
	}

	private void assertNumerics(JCas jcas) {
		Assert.assertEquals(31, ((ArrayList<Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(jcas, Numeric.type, false)).size());
		Assert.assertEquals(27, ((ArrayList<Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(jcas, IntegerNumber.type, false)).size());
		Assert.assertEquals(4, ((ArrayList<Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(jcas, DoubleNumber.type, false)).size());
	}
	
	private void assertNumericPatterns(JCas jcas) {
		Assert.assertEquals(3, ((ArrayList<Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(jcas, Range.type, false)).size());
		Assert.assertEquals(4, ((ArrayList<Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(jcas, PotentialBp.type, false)).size());
		Assert.assertEquals(13, ((ArrayList<Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(jcas, Relation.type, false)).size());
	}

	private void assertTerms(JCas jcas) {
		Assert.assertEquals(3, ((ArrayList<Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(jcas, Bp_Term.type, false)).size());
		Assert.assertEquals(3, ((ArrayList<Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(jcas, Hr_Term.type, false)).size());
		Assert.assertEquals(2, ((ArrayList<Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(jcas, Resp_Term.type, false)).size());
		Assert.assertEquals(2, ((ArrayList<Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(jcas, T_Term.type, false)).size());
		Assert.assertEquals(2, ((ArrayList<Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(jcas, So2_Term.type, false)).size());
	}

	public void outputXmi(String filename, JCas jcas) {
		try {

			File xmio = new File(outputDir, filename + ".xmi");
			XmiCasSerializer.serialize(jcas.getCas(), new FileOutputStream(xmio));
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	@SuppressWarnings("unchecked")
	public void justPrint(JCas jcas, String filename) {

		System.out.println(ListenerLogic.getRows(jcas.getCas()));
		ArrayList<Annotation> list = (ArrayList<Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(jcas,
		    Bp_value.type, false);

		System.out.println("BP count : " + list.size());
		for (Annotation a : list) {
			System.out.println("BP:" + a.getCoveredText());
		}

		list = (ArrayList<Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(jcas, T_value.type, false);
		System.out.println("T count : " + list.size());
		for (Annotation a : list) {
			System.out.println("T :" + a.getCoveredText());
		}

		list = (ArrayList<Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(jcas, Hr_value.type, false);
		System.out.println("HR count : " + list.size());
		for (Annotation a : list) {
			System.out.println("HR:" + a.getCoveredText());
		}

	}

	@SuppressWarnings("unchecked")
	public void assertAndPrint(JCas jcas, int bpCount, int tCount, int hrCount, String filename) {
		System.out.println(filename);
		System.out.println(ListenerLogic.getRows(jcas.getCas()));
		ArrayList<Annotation> list = (ArrayList<Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(jcas,
		    Bp_value.type, false);

		System.out.println("BP count : " + bpCount + " vs " + list.size());
		for (Annotation a : list) {
			System.out.println("BP:" + a.getCoveredText());
		}
		System.out.println("Assering " + filename + " BP refst=" + bpCount + " , but was sys=" + list.size());
		Assert.assertTrue(list.size() == bpCount);

		list = (ArrayList<Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(jcas, T_value.type, false);
		System.out.println("T count : " + list.size() + " vs " + tCount);
		for (Annotation a : list) {
			System.out.println("T :" + a.getCoveredText());
		}
		Assert.assertTrue(list.size() == tCount);

		list = (ArrayList<Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(jcas, Hr_value.type, false);
		System.out.println("HR count : " + list.size() + " vs " + hrCount);
		for (Annotation a : list) {
			System.out.println("HR:" + a.getCoveredText());
		}

		Assert.assertTrue(list.size() == hrCount);
	}

	/**
	 * 
	 * @param ae
	 * @param docText
	 * @param id
	 * @return
	 * @throws ResourceInitializationException
	 */
	protected JCas createJCas(AnalysisEngine ae, String docText, String id)
	    throws ResourceInitializationException {
		JCas jcas = ae.newJCas();
		jcas.setDocumentText(docText);
		CSI csi = new CSI(jcas);
		csi.setBegin(0);
		csi.setEnd(docText.length());
		csi.setID(id);
		csi.addToIndexes();
		return jcas;
	}

	protected void launchViewer() throws Exception {
		if (aggregate == null) {
			throw new RuntimeException(
			    "Aggregate is null, unable to generate descriptor for viewing xmi");
		}
		aggregate.toXML(aggXmi);
		String aggLoc = aggregate.getDescriptorLocator();
		Preferences prefs = Preferences.userRoot().node(
		    "org/apache/uima/tools/AnnotationViewer");
		if (aggLoc != null) {
			prefs.put("taeDescriptorFile", aggLoc);
		}// if mAggDesc != null
		if (outputDir != null) {
			prefs.put("inDir", outputDir);
		}// if mOutputDir != null
		AnnotationViewerMain avm = new AnnotationViewerMain();
		avm.setBounds(0, 0, 1000, 225);
		avm.setVisible(true);
	}// launchViewer method

	@After
	public void cleanup() throws Exception {

	}// cleanup method

}
