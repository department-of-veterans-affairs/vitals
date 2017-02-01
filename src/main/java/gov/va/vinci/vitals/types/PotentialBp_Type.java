
/* First created by JCasGen Wed Dec 07 13:28:01 CST 2016 */
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

/** 
 * Updated by JCasGen Wed Dec 07 13:28:01 CST 2016
 * @generated */
public class PotentialBp_Type extends Pattern_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (PotentialBp_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = PotentialBp_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new PotentialBp(addr, PotentialBp_Type.this);
  			   PotentialBp_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new PotentialBp(addr, PotentialBp_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = PotentialBp.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("gov.va.vinci.vitals.types.PotentialBp");
 
  /** @generated */
  final Feature casFeat_value1;
  /** @generated */
  final int     casFeatCode_value1;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getValue1(int addr) {
        if (featOkTst && casFeat_value1 == null)
      jcas.throwFeatMissing("value1", "gov.va.vinci.vitals.types.PotentialBp");
    return ll_cas.ll_getRefValue(addr, casFeatCode_value1);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setValue1(int addr, int v) {
        if (featOkTst && casFeat_value1 == null)
      jcas.throwFeatMissing("value1", "gov.va.vinci.vitals.types.PotentialBp");
    ll_cas.ll_setRefValue(addr, casFeatCode_value1, v);}
    
  
 
  /** @generated */
  final Feature casFeat_value2;
  /** @generated */
  final int     casFeatCode_value2;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getValue2(int addr) {
        if (featOkTst && casFeat_value2 == null)
      jcas.throwFeatMissing("value2", "gov.va.vinci.vitals.types.PotentialBp");
    return ll_cas.ll_getRefValue(addr, casFeatCode_value2);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setValue2(int addr, int v) {
        if (featOkTst && casFeat_value2 == null)
      jcas.throwFeatMissing("value2", "gov.va.vinci.vitals.types.PotentialBp");
    ll_cas.ll_setRefValue(addr, casFeatCode_value2, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public PotentialBp_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_value1 = jcas.getRequiredFeatureDE(casType, "value1", "uima.tcas.Annotation", featOkTst);
    casFeatCode_value1  = (null == casFeat_value1) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_value1).getCode();

 
    casFeat_value2 = jcas.getRequiredFeatureDE(casType, "value2", "uima.tcas.Annotation", featOkTst);
    casFeatCode_value2  = (null == casFeat_value2) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_value2).getCode();

  }
}



    