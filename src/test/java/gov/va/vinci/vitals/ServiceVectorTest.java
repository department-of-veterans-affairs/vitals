package gov.va.vinci.vitals;

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
import gov.va.vinci.vitals.pipeline.VitalsPipeline;
import gov.va.vinci.vitals.types.Bp_value;
import gov.va.vinci.vitals.types.Hr_value;
import gov.va.vinci.vitals.types.T_value;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.tools.AnnotationViewerMain;
import org.apache.uima.util.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class ServiceVectorTest {

	protected LeoAEDescriptor aggregate = null;
	protected LeoTypeSystemDescription types = null;
	protected String outputDir = "output/xmi";
	// "src/test/resources/output/xmi";

	protected String inputDir = "testCases/input/";
	//"src/test/resources/input/";

	protected boolean launchView = true;
	protected String aggXmi = "aggregateDesc";

	@Before
	public void setup() throws Exception {
		Service ds = new Service();
		// aggregate = ds.createPipeline(false);

		VitalsPipeline pipeline = new VitalsPipeline();
		LeoTypeSystemDescription types = pipeline.getLeoTypeSystemDescription();
		aggregate = pipeline.getPipeline();

		File o = new File(outputDir);
		if (!o.exists()) {
			o.mkdirs();
		}// if
	}// setup

	public String getDocText(String filename) throws IOException {
		return FileUtils.file2String(new File(inputDir + filename));
	}

	//@Test
	public void testWithoutAssert() throws ResourceInitializationException, IOException,
	    AnalysisEngineProcessException {
		AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(aggregate.getAnalysisEngineDescription());

		String docText = "";
		JCas jcas = null;
		/**/
		String filename = "vector1.txt";
		docText = getDocText(filename);
		jcas = createJCas(ae, docText, filename);
		ae.process(jcas);
		outputXmi(filename, jcas);
		justPrint(jcas, filename);

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
		System.out.println(filename);
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
