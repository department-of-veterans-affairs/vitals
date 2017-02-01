
/* First created by JCasGen Mon Mar 31 19:30:41 CDT 2014 */
package gov.va.vinci.leo.types;

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

/** Validation Annotations
 * Updated by JCasGen Mon Mar 31 19:30:41 CDT 2014
 * @generated */
public class ValidationAnnotation_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (ValidationAnnotation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ValidationAnnotation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ValidationAnnotation(addr, ValidationAnnotation_Type.this);
  			   ValidationAnnotation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ValidationAnnotation(addr, ValidationAnnotation_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = ValidationAnnotation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("gov.va.vinci.leo.types.ValidationAnnotation");
 
  /** @generated */
  final Feature casFeat_ReferenceAnnotationGuid;
  /** @generated */
  final int     casFeatCode_ReferenceAnnotationGuid;
  /** @generated */ 
  public String getReferenceAnnotationGuid(int addr) {
        if (featOkTst && casFeat_ReferenceAnnotationGuid == null)
      jcas.throwFeatMissing("ReferenceAnnotationGuid", "gov.va.vinci.leo.types.ValidationAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_ReferenceAnnotationGuid);
  }
  /** @generated */    
  public void setReferenceAnnotationGuid(int addr, String v) {
        if (featOkTst && casFeat_ReferenceAnnotationGuid == null)
      jcas.throwFeatMissing("ReferenceAnnotationGuid", "gov.va.vinci.leo.types.ValidationAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_ReferenceAnnotationGuid, v);}
    
  
 
  /** @generated */
  final Feature casFeat_ValidationValue;
  /** @generated */
  final int     casFeatCode_ValidationValue;
  /** @generated */ 
  public String getValidationValue(int addr) {
        if (featOkTst && casFeat_ValidationValue == null)
      jcas.throwFeatMissing("ValidationValue", "gov.va.vinci.leo.types.ValidationAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_ValidationValue);
  }
  /** @generated */    
  public void setValidationValue(int addr, String v) {
        if (featOkTst && casFeat_ValidationValue == null)
      jcas.throwFeatMissing("ValidationValue", "gov.va.vinci.leo.types.ValidationAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_ValidationValue, v);}
    
  
 
  /** @generated */
  final Feature casFeat_ValidationComment;
  /** @generated */
  final int     casFeatCode_ValidationComment;
  /** @generated */ 
  public String getValidationComment(int addr) {
        if (featOkTst && casFeat_ValidationComment == null)
      jcas.throwFeatMissing("ValidationComment", "gov.va.vinci.leo.types.ValidationAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_ValidationComment);
  }
  /** @generated */    
  public void setValidationComment(int addr, String v) {
        if (featOkTst && casFeat_ValidationComment == null)
      jcas.throwFeatMissing("ValidationComment", "gov.va.vinci.leo.types.ValidationAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_ValidationComment, v);}
    
  
 
  /** @generated */
  final Feature casFeat_CreatedBy;
  /** @generated */
  final int     casFeatCode_CreatedBy;
  /** @generated */ 
  public String getCreatedBy(int addr) {
        if (featOkTst && casFeat_CreatedBy == null)
      jcas.throwFeatMissing("CreatedBy", "gov.va.vinci.leo.types.ValidationAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_CreatedBy);
  }
  /** @generated */    
  public void setCreatedBy(int addr, String v) {
        if (featOkTst && casFeat_CreatedBy == null)
      jcas.throwFeatMissing("CreatedBy", "gov.va.vinci.leo.types.ValidationAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_CreatedBy, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public ValidationAnnotation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_ReferenceAnnotationGuid = jcas.getRequiredFeatureDE(casType, "ReferenceAnnotationGuid", "uima.cas.String", featOkTst);
    casFeatCode_ReferenceAnnotationGuid  = (null == casFeat_ReferenceAnnotationGuid) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ReferenceAnnotationGuid).getCode();

 
    casFeat_ValidationValue = jcas.getRequiredFeatureDE(casType, "ValidationValue", "uima.cas.String", featOkTst);
    casFeatCode_ValidationValue  = (null == casFeat_ValidationValue) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ValidationValue).getCode();

 
    casFeat_ValidationComment = jcas.getRequiredFeatureDE(casType, "ValidationComment", "uima.cas.String", featOkTst);
    casFeatCode_ValidationComment  = (null == casFeat_ValidationComment) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ValidationComment).getCode();

 
    casFeat_CreatedBy = jcas.getRequiredFeatureDE(casType, "CreatedBy", "uima.cas.String", featOkTst);
    casFeatCode_CreatedBy  = (null == casFeat_CreatedBy) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_CreatedBy).getCode();

  }
}



    