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

import gov.va.vinci.kttr.types.HRValue;
import gov.va.vinci.leo.annotationpattern.ae.AnnotationPatternAnnotator;
import gov.va.vinci.leo.automatonRegex.ae.AutomatonRegexAnnotator;
import gov.va.vinci.leo.descriptors.LeoAEDescriptor;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.descriptors.TypeDescriptionBuilder;
import gov.va.vinci.leo.pipeline.PipelineInterface;
import gov.va.vinci.leo.regex.ae.RegexAnnotator;
import gov.va.vinci.leo.sherlock.ae.LearningAnnotator;
import gov.va.vinci.leo.types.TypeLibrarian;
import gov.va.vinci.leo.window.ae.WindowAnnotator;
import gov.va.vinci.svmlib.ml.SvmVectorTranslator;
import gov.va.vinci.vitals.ae.*;
import gov.va.vinci.vitals.types.Hr_value;
import org.apache.uima.resource.metadata.TypeDescription;
import org.apache.uima.resource.metadata.impl.TypeDescription_impl;
import org.jvnet.jaxb2_commons.lang.HashCode;
import org.jvnet.jaxb2_commons.lang.HashCodeStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ryancornia on 1/4/17.
 */
public class VitalsPipeline implements PipelineInterface, Serializable, HashCode {
    @Override
    public LeoAEDescriptor getPipeline() throws Exception {
        LeoTypeSystemDescription types = getLeoTypeSystemDescription();
        LeoAEDescriptor aggregate = new LeoAEDescriptor();

        aggregate.addDelegate(createNumericPipeline(types));
        aggregate.addDelegate(createTermAndIndicatorPipeline(types));
        aggregate.addDelegate(createWindowsPipeline(types));
        aggregate.addDelegate(createPatternsPipeline(types));
        aggregate.addDelegate(createVitalRulesPipeline(types));

        return aggregate;
    }

    @Override
    public LeoTypeSystemDescription getLeoTypeSystemDescription() throws Exception {
        LeoTypeSystemDescription types = new LeoTypeSystemDescription();
        types.addType(TypeLibrarian.getCSITypeSystemDescription());

        // Adding all knowtator annotations to the type list
        boolean addExtra = true;
        TypeDescription kttrType;
        String kttrStrType = "gov.va.vinci.kttr.types.RefValue";
        kttrType = new TypeDescription_impl(kttrStrType, "", "uima.tcas.Annotation");
        types.addType(kttrType);
        for (String type : KnowtatorVariables.uimaTypeFeatureMap.keySet()) {
            TypeDescription newType;
            newType = new TypeDescription_impl(type, "", kttrStrType);
            for (String feature : KnowtatorVariables.uimaTypeFeatureMap.get(type)) {
                newType.addFeature(feature, "", "uima.cas.String");
            }
            addExtra = false;
            types.addType(newType);
        }
        if (addExtra)
            types.addType("gov.va.vinci.kttr.types.HRValue", "", kttrStrType);


        // Regex default type
        types.addTypeSystemDescription(new RegexAnnotator().getLeoTypeSystemDescription());

        TypeDescription numType = new TypeDescription_impl(PipelineVariables.TYPE_NUMERIC, "",
                PipelineVariables.RegexType);
        numType.addFeature("comment", "", "uima.cas.String");
        numType.addFeature("value", "", "uima.cas.Double");
        numType.addFeature("decimal", "", "uima.cas.Boolean");
        numType.addFeature("integer", "", "uima.cas.Boolean");
        numType.addFeature("zero_decimal", "", "uima.cas.Boolean");
        numType.addFeature("unit", "", "uima.tcas.Annotation");
        numType.addFeature("source", "", "uima.cas.String");
        numType.addFeature("timestamp", "", "uima.tcas.Annotation");
        types.addType(numType);

        for (String a : PipelineVariables.TYPES_NUMERIC) {
            types.addType(a, "", PipelineVariables.TYPE_NUMERIC);
        }

        //////////////////
        types.addType(PipelineVariables.TYPE_UNIT, "", PipelineVariables.RegexType);
        types.addType(PipelineVariables.TYPE_TERM, "", PipelineVariables.RegexType);
        for (String a : PipelineVariables.TYPES_TERM) {
            types.addType(a, "", PipelineVariables.TYPE_TERM);
        }
        types.addType(PipelineVariables.TYPE_TIMESTAMP, "", PipelineVariables.RegexType);

        // APA default type
        TypeDescription newType = new TypeDescription_impl(PipelineVariables.PatternType, "",
                "uima.tcas.Annotation");
        newType.addFeature("pattern", "", "uima.cas.String");
        newType.addFeature("anchor", "", "uima.tcas.Annotation");
        newType.addFeature("target", "", "uima.tcas.Annotation");
        newType.addFeature("anchorPattern", "", "uima.cas.String");
        newType.addFeature("targetPattern", "", "uima.cas.String");
        types.addType(newType);

        types.addType(PipelineVariables.TYPE_INDICATOR, "", PipelineVariables.PatternType);
        types.addType(PipelineVariables.TYPE_TERMEXCLUDE, "", PipelineVariables.PatternType);
        types.addType(PipelineVariables.TYPE_NUMEXCLUDE, "", PipelineVariables.PatternType);

        types.addType(TypeDescriptionBuilder.create(PipelineVariables.TYPE_RANGE, "", PipelineVariables.PatternType)
                .addFeature("value1", "", "uima.tcas.Annotation")
                .addFeature("value2", "", "uima.tcas.Annotation")
                .getTypeDescription());

        types.addType(TypeDescriptionBuilder.create(PipelineVariables.TYPE_POTENTIAL_BP, "", PipelineVariables.PatternType)
                .addFeature("value1", "", "uima.tcas.Annotation")
                .addFeature("value2", "", "uima.tcas.Annotation")
                .getTypeDescription());
        types.addType(TypeDescriptionBuilder.create(PipelineVariables.TYPE_EX_POTENTIAL_BP, "", PipelineVariables.PatternType)
                .getTypeDescription());

        types.addType(TypeDescriptionBuilder.create(PipelineVariables.TYPE_POTENTIAL_HEIGHT, "", PipelineVariables.PatternType)
                .addFeature("value1", "", "uima.tcas.Annotation")
                .addFeature("value2", "", "uima.tcas.Annotation")
                .getTypeDescription());

        types.addType(TypeDescriptionBuilder.create(PipelineVariables.TYPE_EX_POTENTIAL_HEIGHT, "", "uima.tcas.Annotation")
                .getTypeDescription());

        types.addType(PipelineVariables.TYPE_RELATION, "", PipelineVariables.PatternType);
        types.addType(PipelineVariables.TYPE_RELATION_TIMESTAMP, "", PipelineVariables.PatternType);

        types.addType(TypeDescriptionBuilder.create(LearningVariables.TYPE_FeatureVector,
                "Type used to store the fearures and values", "uima.tcas.Annotation")
                .addFeature("keys", "", "uima.cas.StringArray")
                .addFeature("values", "", "uima.cas.StringArray")
                .addFeature("context", "", "uima.tcas.Annotation")
                .getTypeDescription());

        types.addType(TypeDescriptionBuilder
                .create(LearningVariables.TYPE_Prediction, "Type used to output predictions", "uima.tcas.Annotation")
                .addFeature("srcFVFeature", "Feature vector annotation", "uima.tcas.Annotation")
                .addFeature("prediction", "", "uima.cas.String")
                .getTypeDescription());

		/* Additional annotations for specific values */

        TypeDescription outType = new TypeDescription_impl(PipelineVariables.TYPE_OUTPUT, "",
                "uima.tcas.Annotation");
        outType.addFeature("value", "", "uima.cas.String");
        outType.addFeature("valueAnnotation", "", "uima.tcas.Annotation");
        outType.addFeature("concept", "", "uima.cas.String");
        outType.addFeature("unit", "", "uima.tcas.Annotation");
        outType.addFeature("source", "", "uima.cas.String");
        outType.addFeature("timestamp", "", "uima.tcas.Annotation");
        types.addType(outType);

        for (String a : PipelineVariables.valueTypes) {
            types.addType(new TypeDescription_impl(a, "", PipelineVariables.TYPE_OUTPUT));
        }
        types.addType(new TypeDescription_impl(PipelineVariables.TYPE_Bp_value, "", PipelineVariables.TYPE_OUTPUT));

        for (String a : PipelineVariables.valueBPTypes) {
            types.addType(new TypeDescription_impl(a, "", PipelineVariables.TYPE_Bp_value));
        }

        types.addTypeSystemDescription(new WindowAnnotator().getLeoTypeSystemDescription());
        for (String a : PipelineVariables.TYPES_WINDOW) {
            types.addType(new TypeDescription_impl(a, "", PipelineVariables.TYPE_WINDOW));
        }

        /*****/
        types.addType(new TypeDescription_impl("gov.va.vinci.vitals.types.Month", "", PipelineVariables.RegexType));

        types.addType(new TypeDescription_impl("gov.va.vinci.vitals.types.ExcludePrefix", "", PipelineVariables.RegexType));
        return types;
    }

    @Override
    public int hashCode(ObjectLocator objectLocator, HashCodeStrategy hashCodeStrategy) {
        return 0;
    }

    public static class PipelineVariables {

        static String RESOURCE_PATH = "resources/";
        static String PatternType = "gov.va.vinci.vitals.types.Pattern";
        static String RegexType = "gov.va.vinci.leo.regex.types.RegularExpressionType";

        static String TYPE_NUMERIC = "gov.va.vinci.vitals.types.Numeric";
        static String[] TYPES_NUMERIC = new String[] {
                "gov.va.vinci.vitals.types.IntegerNumber",
                "gov.va.vinci.vitals.types.DoubleNumber"
        };

        static String resourceNumbers = "numbers.groovy";

        static String TYPE_UNIT = "gov.va.vinci.vitals.types.Unit";
        static String resourceUnit = "unitsOfMeasure.groovy";

        static String TYPE_TERM = "gov.va.vinci.vitals.types.Term";
        static String[] TYPES_TERM = new String[] {
                "gov.va.vinci.vitals.types.Bp_Term",
                "gov.va.vinci.vitals.types.Bp_Systolic_Term",
                "gov.va.vinci.vitals.types.Bp_Diastolic_Term",
                "gov.va.vinci.vitals.types.Resp_Term",
                "gov.va.vinci.vitals.types.Hr_Term",
                "gov.va.vinci.vitals.types.Pain_Term",
                "gov.va.vinci.vitals.types.T_Term",
                "gov.va.vinci.vitals.types.Weight_Term",
                "gov.va.vinci.vitals.types.Height_Term",
                "gov.va.vinci.vitals.types.So2_Term",
                "gov.va.vinci.vitals.types.Bmi_Term",
                "gov.va.vinci.vitals.types.Age_Term",
                "gov.va.vinci.vitals.types.NotIt_Term"

        };
        static String resourceTerm = "concepts.groovy";

        static String TYPE_INDICATOR = "gov.va.vinci.vitals.types.Indicator";
        static String resourceIndicator = "indicator.pattern";
        static String resourceIndicatorRegex = "indicator.groovy";

        static String TYPE_TIMESTAMP = "gov.va.vinci.vitals.types.Timestamp";
        static String resourceTimestamp = "dates.groovy";

        static String TYPE_NUMEXCLUDE = "gov.va.vinci.vitals.types.NumericExclude";
        static String resourceNumExclude = "numericValuesExclude.pattern";
        static String resourceNumExcludeRegex = "numericValuesExclude.groovy";

        static String TYPE_RANGE = "gov.va.vinci.vitals.types.Range";
        static String resourceRange = "range.pattern";

        static String TYPE_POTENTIAL_BP = "gov.va.vinci.vitals.types.PotentialBp";
        static String TYPE_EX_POTENTIAL_BP = "gov.va.vinci.vitals.types.ExcludePotentialBp";
        static String resourceBp = "bp.pattern";
        static String resourceExBp = "bp_exclude.pattern";

        static String TYPE_POTENTIAL_HEIGHT = "gov.va.vinci.vitals.types.PotentialHeight";
        static String TYPE_EX_POTENTIAL_HEIGHT = "gov.va.vinci.vitals.types.ExcludePotentialHeight";
        static String resourceHeight = "height.pattern";
        static String resourceExHeight = "height_exclude.pattern";

        static String TYPE_TERMEXCLUDE = "gov.va.vinci.vitals.types.TermExclude";
        static String resourceSectionExclude = "excludeSectionHeader.pattern";
        static String resourceSectionExcludeRegex = "excludeSectionHeader.groovy";

        static String TYPE_RELATION = "gov.va.vinci.vitals.types.Relation";
        static String RESOURCE_RELATION = "relation.pattern";

        static String TYPE_RELATION_TIMESTAMP = "gov.va.vinci.vitals.types.Relation_Time";
        static String RESOURCE_RELATION_TIMESTAMP = "relation_time.pattern";

        static String TYPE_OUTPUT = "gov.va.vinci.vitals.types.Output_Value";
        static String TYPE_Bp_value = "gov.va.vinci.vitals.types.Bp_value";
        static String[] valueTypes = new String[] {
                "gov.va.vinci.vitals.types.Hr_value",
                //   TYPE_Bp_value,
                "gov.va.vinci.vitals.types.T_value",
                "gov.va.vinci.vitals.types.Weight_value",
                "gov.va.vinci.vitals.types.Height_value",
                "gov.va.vinci.vitals.types.So2_value",
                "gov.va.vinci.vitals.types.Resp_value",
                "gov.va.vinci.vitals.types.Pain_value",
                "gov.va.vinci.vitals.types.BMI_value"
        };
        static String[] valueBPTypes = new String[] {
                "gov.va.vinci.vitals.types.Bp_Systolic_value",
                "gov.va.vinci.vitals.types.Bp_Diastolic_value" };

        static String TYPE_WINDOW = "gov.va.vinci.leo.window.types.Window";
        static String[] TYPES_WINDOW = new String[] {
                "gov.va.vinci.vitals.types.HiPrecisionWindow",
                "gov.va.vinci.vitals.types.LowerPrecisionWindow",
                "gov.va.vinci.vitals.types.FVWindow",
                "gov.va.vinci.vitals.types.ExcludeAllWindow"
        };
    }

    public static class LearningVariables {
        static String TYPE_FeatureVector = "gov.va.vinci.vitals.types.Hr_Vector";
        static String TYPE_Prediction = "gov.va.vinci.vitals.types.Hr_Prediction";
        static String Hr_SvmModelPath = PipelineVariables.RESOURCE_PATH + "/hr_model.svm";
    }


    protected LeoAEDescriptor createNumericPipeline(LeoTypeSystemDescription types) throws Exception {
        LeoAEDescriptor aggregate = new LeoAEDescriptor();

        aggregate.addDelegate(
                new RegexAnnotator().setGroovyConfigFile(PipelineVariables.RESOURCE_PATH + PipelineVariables.resourceNumbers)
                        .getLeoAEDescriptor()
                        .addTypeSystemDescription(types)
        );

        // remove all numeric types ( Integer or DoubleNumber) if covered by another numeric
        aggregate.addDelegate(new AnnotationFilter(PipelineVariables.TYPES_NUMERIC, PipelineVariables.TYPES_NUMERIC, false)
                .getLeoAEDescriptor().setName("AnnotationFilterKeepTypesNumberRemoveTypesNumber")
                .addTypeSystemDescription(types));
        aggregate.addDelegate(
                new AutomatonRegexAnnotator()
                        .setGroovyConfigFile(PipelineVariables.RESOURCE_PATH + "automaton.groovy")
                        .getLeoAEDescriptor()
                        .addTypeSystemDescription(types));

        aggregate.addDelegate(
                new RegexAnnotator()
                        .setGroovyConfigFile(PipelineVariables.RESOURCE_PATH + PipelineVariables.resourceTimestamp)
                        .getLeoAEDescriptor()
                        .addTypeSystemDescription(types));

        aggregate.addDelegate(new AnnotationPatternAnnotator()
                .setIncludeChildAnnotations(true)
                .setResource(PipelineVariables.RESOURCE_PATH + PipelineVariables.resourceNumExclude)
                .setOutputType(PipelineVariables.TYPE_NUMEXCLUDE)
                .getLeoAEDescriptor().setName("ExcludeNumberPattern")
                .addTypeSystemDescription(types));

        aggregate.addDelegate(new RegexAnnotator()
                .setGroovyConfigFile(PipelineVariables.RESOURCE_PATH + PipelineVariables.resourceNumExcludeRegex)
                .getLeoAEDescriptor()
                .setName("ExcludeNumberPattern")
                .addTypeSystemDescription(types));

        aggregate.addDelegate(new AnnotationFilter(new String[] { PipelineVariables.TYPE_NUMEXCLUDE }, PipelineVariables.TYPES_NUMERIC, false)
                .getLeoAEDescriptor().setName("AnnotationFilterKeepNumExcludeRemoveNumeric")
                .addTypeSystemDescription(types));

        aggregate.addDelegate(new AnalyzeNumbersAE().getLeoAEDescriptor().setName("AnalyzeNumbersAE")
                .addTypeSystemDescription(types));

        return aggregate;
    }

    protected LeoAEDescriptor createTermAndIndicatorPipeline(LeoTypeSystemDescription types) throws Exception {
        LeoAEDescriptor aggregate = new LeoAEDescriptor();
        aggregate.addDelegate( new RegexAnnotator()
                .setGroovyConfigFile(PipelineVariables.RESOURCE_PATH + PipelineVariables.resourceUnit)
                .getLeoAEDescriptor()
                .setName("UnitsAnnotator")
                .addTypeSystemDescription(types));

        aggregate.addDelegate(new RegexAnnotator()
                .setGroovyConfigFile(PipelineVariables.RESOURCE_PATH + PipelineVariables.resourceTerm)
                .getLeoAEDescriptor()
                .setName("TermAnnotator")
                .addTypeSystemDescription(types));

        /** INFO: Filter unneeded annotations*/

        aggregate.addDelegate(new AnnotationFilter(PipelineVariables.TYPES_TERM, PipelineVariables.TYPES_TERM, false)
                .getLeoAEDescriptor()
                .setName("AnnotationFilterKeepTermRemoveTerm")
                .addTypeSystemDescription(types));

        aggregate.addDelegate(new AnnotationFilter(new String[] { PipelineVariables.TYPE_UNIT }, null, false)
                .getLeoAEDescriptor()
                .setName("AnnotationFilterKeepUnit")
                .addTypeSystemDescription(types));

        // delete terms that are covered by units -- FIXME: exception "BPS"
        aggregate.addDelegate(new AnnotationFilter( new String[] { PipelineVariables.TYPE_UNIT }, PipelineVariables.TYPES_TERM, false)
                .getLeoAEDescriptor()
                .setName("AnnotationFilterKeepUnitRemoveTerm")
                .addTypeSystemDescription(types));

        aggregate.addDelegate(new AnnotationPatternAnnotator()
                .setIncludeChildAnnotations(true)
                .setResource(PipelineVariables.RESOURCE_PATH + PipelineVariables.resourceIndicator)
                .setOutputType(PipelineVariables.TYPE_INDICATOR)
                .getLeoAEDescriptor().setName("IndicatorPatternAnnotator")
                .addTypeSystemDescription(types));


        /**
         *
         aggregate.addDelegate(new LeoAEDescriptor().setName("IndicatorPatternAnnotator")
         .setImplementationName(RegexAnnotator.class.getCanonicalName())
         .addParameterSetting(RegexAnnotator.Param.RESOURCE.getName(), true, false, "String",
         PipelineVariables.RESOURCE_PATH + PipelineVariables.resourceIndicatorRegex)
         .addParameterSetting(RegexAnnotator.Param.OUTPUT_TYPE.getName(), true, false, "String", PipelineVariables.TYPE_INDICATOR)
         .addTypeSystemDescription(types));

         */
        aggregate.addDelegate(new RegexAnnotator()
                .setGroovyConfigFile(PipelineVariables.RESOURCE_PATH + PipelineVariables.resourceIndicatorRegex)

                .getLeoAEDescriptor()
                .setName("IndicatorRegexAnnotator")
                .addTypeSystemDescription(types));

        aggregate.addDelegate(new AnnotationFilter(new String[] { PipelineVariables.TYPE_INDICATOR }, null, false)
                .getLeoAEDescriptor()
                .setName("AnnotationFilterKeepTypeIndication")
                .addTypeSystemDescription(types));

        aggregate.addDelegate(new AnnotationPatternAnnotator()
                .setIncludeChildAnnotations(true)
                .setResource(PipelineVariables.RESOURCE_PATH + PipelineVariables.resourceSectionExclude)
                .setOutputType(PipelineVariables.TYPE_TERMEXCLUDE)
                .getLeoAEDescriptor()
                .setName("TermExcludePatternAnnotator")
                .addTypeSystemDescription(types));

        aggregate.addDelegate(new RegexAnnotator()
                .setGroovyConfigFile(PipelineVariables.RESOURCE_PATH + PipelineVariables.resourceSectionExcludeRegex)

                .getLeoAEDescriptor()
                .setName("TermExcludePatternAnnotator")
                .addTypeSystemDescription(types));

        return aggregate;
    }

    protected LeoAEDescriptor createWindowsPipeline(LeoTypeSystemDescription types) throws Exception {
        LeoAEDescriptor aggregate = new LeoAEDescriptor();

        aggregate.addDelegate(new WindowAnnotator("gov.va.vinci.vitals.types.HiPrecisionWindow", PipelineVariables.TYPE_INDICATOR)
                .setAnchorFeature("Anchor")
                .setRtWindowSize(new Integer(20))
                .getLeoAEDescriptor()
                .setTypeSystemDescription(types));
        aggregate.addDelegate(new WindowAnnotator("gov.va.vinci.vitals.types.LowerPrecisionWindow",PipelineVariables.TYPE_INDICATOR )
                .setAnchorFeature("Anchor")
                .setRtWindowSize(new Integer(50))
                .getLeoAEDescriptor()
                .setTypeSystemDescription(types));

        aggregate.addDelegate(new WindowAnnotator("gov.va.vinci.vitals.types.ExcludeAllWindow", PipelineVariables.TYPE_TERMEXCLUDE)
                .setAnchorFeature("Anchor")
                .setRtWindowSize(new Integer(10))
                .getLeoAEDescriptor()
                .setTypeSystemDescription(types));

        aggregate.addDelegate(new AnnotationFilter(new String[] { "gov.va.vinci.vitals.types.ExcludeAllWindow" }, new String[] { PipelineVariables.TYPE_NUMERIC }, true)
                .getLeoAEDescriptor()
                .setName("AnnotationFilterKeepExcludeAllWindowRemoveNumeric")
                .addTypeSystemDescription(types));

        return aggregate;
    }

    protected LeoAEDescriptor createPatternsPipeline(LeoTypeSystemDescription types) throws Exception {
        LeoAEDescriptor aggregate = new LeoAEDescriptor();
        ///////////// INFO: Creating patterns
		/**/
        aggregate.addDelegate(new AnnotationPatternAnnotator()
                .setIncludeChildAnnotations(true)
                .setResource(PipelineVariables.RESOURCE_PATH + PipelineVariables.resourceRange)
                .setOutputType(PipelineVariables.TYPE_RANGE)
                .getLeoAEDescriptor()
                .setName("RangePattern")
                .addTypeSystemDescription(types));
		/**/

        aggregate.addDelegate(new AdjustRangeAnnotator().getLeoAEDescriptor().setName("AdjustRangeAnnotator")
                .addTypeSystemDescription(types));

        aggregate.addDelegate(new AnnotationPatternAnnotator()
                .setIncludeChildAnnotations(true)
                .setResource(PipelineVariables.RESOURCE_PATH + PipelineVariables.resourceBp)
                .setOutputType(PipelineVariables.TYPE_POTENTIAL_BP)
                .getLeoAEDescriptor()
                .setName("PotentialBpPattern")
                .addTypeSystemDescription(types));

        aggregate.addDelegate(new AnnotationPatternAnnotator()
                .setIncludeChildAnnotations(true)
                .setResource(PipelineVariables.RESOURCE_PATH + PipelineVariables.resourceHeight)
                .setOutputType(PipelineVariables.TYPE_POTENTIAL_HEIGHT)
                .getLeoAEDescriptor()
                .setName("PotentialHeightPattern")
                .addTypeSystemDescription(types));

        aggregate.addDelegate(new AnnotationPatternAnnotator()
                .setIncludeChildAnnotations(true)
                .setResource(PipelineVariables.RESOURCE_PATH + PipelineVariables.resourceExBp)
                .setOutputType(PipelineVariables.TYPE_EX_POTENTIAL_BP)
                .getLeoAEDescriptor()
                .setName("PotentialBpPattern")
                .addTypeSystemDescription(types));
        aggregate.addDelegate(new AnnotationFilter(new String[] { PipelineVariables.TYPE_EX_POTENTIAL_BP }, new String[] { PipelineVariables.TYPE_POTENTIAL_BP }, false)
                .getLeoAEDescriptor()
                .setName("AnnotationFilterKeepExPotentialBPRemovePotentialBP")
                .addTypeSystemDescription(types));
        aggregate.addDelegate(new AdjustPotentialBpAE()
                .getLeoAEDescriptor()
                .setName("AdjustPotentialBpAE")
                .addTypeSystemDescription(types));
		/**/
        aggregate.addDelegate(new AnnotationPatternAnnotator()
                .setIncludeChildAnnotations(true)
                .setResource(PipelineVariables.RESOURCE_PATH + PipelineVariables.RESOURCE_RELATION)
                .setOutputType(PipelineVariables.TYPE_RELATION)
                .getLeoAEDescriptor()
                .setName("RelationPatternAnnotator")
                .addTypeSystemDescription(types));

        aggregate.addDelegate(new AnnotationPatternAnnotator()
                .setIncludeChildAnnotations(true)
                .setResource(PipelineVariables.RESOURCE_PATH + PipelineVariables.RESOURCE_RELATION_TIMESTAMP)
                .setOutputType(PipelineVariables.TYPE_RELATION_TIMESTAMP)
                .getLeoAEDescriptor()
                .setName("RelationWithTimePatternAnnotator")
                .addTypeSystemDescription(types));

        // cannot filter duplicate Patterns because some patterns overlap by design.
        // FIXME: create a special AE that filters out overlapping patterns that have the same Numeric as target.
        //  create annotator that removes Relation if the target is a part of Range or PotentialBp
        // This has to be done because overannotations cause multiple problems
        //	aggregate.addDelegate(new FilterRelationsAE().getLeoAEDescriptor().setName("FilterRelationsAE").addTypeSystemDescription(types));

        return aggregate;
    }

    protected LeoAEDescriptor createVitalRulesPipeline(LeoTypeSystemDescription types) throws Exception {
        LeoAEDescriptor aggregate = new LeoAEDescriptor();
        aggregate.addDelegate(new AssignUnitAndTimeAE().getLeoAEDescriptor().addTypeSystemDescription(types));
        aggregate.addDelegate(new MarkNotItAE().getLeoAEDescriptor().addTypeSystemDescription(types));
        aggregate.addDelegate(new ExtractTemperatureAE().getLeoAEDescriptor().addTypeSystemDescription(types));
        aggregate.addDelegate(new ExtractSo2AE().getLeoAEDescriptor().addTypeSystemDescription(types));
        aggregate.addDelegate(new ExtractBmi().getLeoAEDescriptor().addTypeSystemDescription(types));
        aggregate.addDelegate(new ExtractBloodPressureAE().getLeoAEDescriptor().addTypeSystemDescription(types));
        aggregate.addDelegate(new ExtractRespiratoryAE().getLeoAEDescriptor().addTypeSystemDescription(types));
        aggregate.addDelegate(new ExtractHeightAE().getLeoAEDescriptor().addTypeSystemDescription(types));
        aggregate.addDelegate(new ExtractWeightAE().getLeoAEDescriptor().addTypeSystemDescription(types));
        aggregate.addDelegate(new ExtractPainAE().getLeoAEDescriptor().addTypeSystemDescription(types));
        aggregate.addDelegate(new ExtractHeartRateAE().getLeoAEDescriptor().addTypeSystemDescription(types));

        // Remove overannotated

        aggregate.addDelegate(new AnnotationFilter(PipelineVariables.valueTypes, new String[] {}, false)
                .getLeoAEDescriptor().setName("AnnotationFilterKeepValueTypes")
                .addTypeSystemDescription(types));

        aggregate.addDelegate(new AnnotationFilter(PipelineVariables.valueBPTypes, new String[]{}, false)
                .getLeoAEDescriptor().setName("AnnotationFilterKeepValueBPTypes")
                .addTypeSystemDescription(types));
        aggregate.addDelegate(new FilterTimestampAE().getLeoAEDescriptor().addTypeSystemDescription(types));

        return aggregate;
    }

    protected LeoAEDescriptor createML_Pipeline(LeoTypeSystemDescription types, String environment) throws Exception {
        LeoAEDescriptor aggregate = new LeoAEDescriptor();
        if ("predict".equalsIgnoreCase(environment) || "train".equalsIgnoreCase(environment)) {
            aggregate.addDelegate(new HrVectorAnnotator()
                    .setKeysFeature("keys")
                    .setValuesFeature("values")
                    .setOutputType(LearningVariables.TYPE_FeatureVector)
                    .setInputTypes( new String[] { Hr_value.class.getCanonicalName(), HRValue.class.getCanonicalName() })
                    .getLeoAEDescriptor().setName("HrVectorAnnotator")
                    .addTypeSystemDescription(types));

            if ("predict".equalsIgnoreCase(environment)) {
                aggregate.addDelegate(LearningAnnotator.getLeoAEDescriptor(
                        SvmVectorTranslator.class.getCanonicalName(),
                        LearningVariables.TYPE_Prediction, "srcFVFeature", "prediction",
                        LearningVariables.TYPE_FeatureVector,
                        "keys", "values", LearningVariables.Hr_SvmModelPath).addTypeSystemDescription(types));
                ;

                aggregate.addDelegate(new FilterHeartRateAnnotator().getLeoAEDescriptor()
                        .setName("FilterHeartRateAnnotator")
                        .addTypeSystemDescription(types));
            }
        }
        return aggregate;
    }

    public static class KnowtatorVariables {
        public static HashMap<String, ArrayList<String>> uimaTypeFeatureMap = new HashMap<String, ArrayList<String>>();

    }
}
