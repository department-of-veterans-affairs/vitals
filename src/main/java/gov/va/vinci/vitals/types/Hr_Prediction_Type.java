
/* First created by JCasGen Wed Dec 07 13:28:00 CST 2016 */
package gov.va.vinci.vitals.types;

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

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** Type used to output predictions
 * Updated by JCasGen Wed Dec 07 13:28:00 CST 2016
 * @generated */
public class Hr_Prediction_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Hr_Prediction_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Hr_Prediction_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Hr_Prediction(addr, Hr_Prediction_Type.this);
  			   Hr_Prediction_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Hr_Prediction(addr, Hr_Prediction_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Hr_Prediction.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("gov.va.vinci.vitals.types.Hr_Prediction");
 
  /** @generated */
  final Feature casFeat_srcFVFeature;
  /** @generated */
  final int     casFeatCode_srcFVFeature;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getSrcFVFeature(int addr) {
        if (featOkTst && casFeat_srcFVFeature == null)
      jcas.throwFeatMissing("srcFVFeature", "gov.va.vinci.vitals.types.Hr_Prediction");
    return ll_cas.ll_getRefValue(addr, casFeatCode_srcFVFeature);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSrcFVFeature(int addr, int v) {
        if (featOkTst && casFeat_srcFVFeature == null)
      jcas.throwFeatMissing("srcFVFeature", "gov.va.vinci.vitals.types.Hr_Prediction");
    ll_cas.ll_setRefValue(addr, casFeatCode_srcFVFeature, v);}
    
  
 
  /** @generated */
  final Feature casFeat_prediction;
  /** @generated */
  final int     casFeatCode_prediction;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPrediction(int addr) {
        if (featOkTst && casFeat_prediction == null)
      jcas.throwFeatMissing("prediction", "gov.va.vinci.vitals.types.Hr_Prediction");
    return ll_cas.ll_getStringValue(addr, casFeatCode_prediction);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPrediction(int addr, String v) {
        if (featOkTst && casFeat_prediction == null)
      jcas.throwFeatMissing("prediction", "gov.va.vinci.vitals.types.Hr_Prediction");
    ll_cas.ll_setStringValue(addr, casFeatCode_prediction, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Hr_Prediction_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_srcFVFeature = jcas.getRequiredFeatureDE(casType, "srcFVFeature", "uima.tcas.Annotation", featOkTst);
    casFeatCode_srcFVFeature  = (null == casFeat_srcFVFeature) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_srcFVFeature).getCode();

 
    casFeat_prediction = jcas.getRequiredFeatureDE(casType, "prediction", "uima.cas.String", featOkTst);
    casFeatCode_prediction  = (null == casFeat_prediction) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_prediction).getCode();

  }
}



    