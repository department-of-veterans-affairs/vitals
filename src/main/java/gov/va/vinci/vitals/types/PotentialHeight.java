

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

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Wed Dec 07 13:28:01 CST 2016
 * XML source: C:/Users/VHASLC~1/AppData/Local/Temp/2/leoTypeDescription_a7e39e42-6fbf-4217-9d38-e8c5b6b2154f4103637063506643695.xml
 * @generated */
public class PotentialHeight extends Pattern {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(PotentialHeight.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected PotentialHeight() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public PotentialHeight(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public PotentialHeight(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public PotentialHeight(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: value1

  /** getter for value1 - gets 
   * @generated
   * @return value of the feature 
   */
  public Annotation getValue1() {
    if (PotentialHeight_Type.featOkTst && ((PotentialHeight_Type)jcasType).casFeat_value1 == null)
      jcasType.jcas.throwFeatMissing("value1", "gov.va.vinci.vitals.types.PotentialHeight");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((PotentialHeight_Type)jcasType).casFeatCode_value1)));}
    
  /** setter for value1 - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setValue1(Annotation v) {
    if (PotentialHeight_Type.featOkTst && ((PotentialHeight_Type)jcasType).casFeat_value1 == null)
      jcasType.jcas.throwFeatMissing("value1", "gov.va.vinci.vitals.types.PotentialHeight");
    jcasType.ll_cas.ll_setRefValue(addr, ((PotentialHeight_Type)jcasType).casFeatCode_value1, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: value2

  /** getter for value2 - gets 
   * @generated
   * @return value of the feature 
   */
  public Annotation getValue2() {
    if (PotentialHeight_Type.featOkTst && ((PotentialHeight_Type)jcasType).casFeat_value2 == null)
      jcasType.jcas.throwFeatMissing("value2", "gov.va.vinci.vitals.types.PotentialHeight");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((PotentialHeight_Type)jcasType).casFeatCode_value2)));}
    
  /** setter for value2 - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setValue2(Annotation v) {
    if (PotentialHeight_Type.featOkTst && ((PotentialHeight_Type)jcasType).casFeat_value2 == null)
      jcasType.jcas.throwFeatMissing("value2", "gov.va.vinci.vitals.types.PotentialHeight");
    jcasType.ll_cas.ll_setRefValue(addr, ((PotentialHeight_Type)jcasType).casFeatCode_value2, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    