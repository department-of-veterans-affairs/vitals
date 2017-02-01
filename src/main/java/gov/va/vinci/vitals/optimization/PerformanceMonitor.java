package gov.va.vinci.vitals.optimization;

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

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import gov.va.vinci.leo.ae.LeoBaseAnnotator;
import gov.va.vinci.leo.annotationpattern.AnnotationPatternService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Created by ryancornia on 12/6/16.
 */
@Aspect
public class PerformanceMonitor {
    /** Performance monitoring variables. **/
    private static final EtmMonitor etmMonitor = EtmManager.getEtmMonitor();

    @Around("call(* annotate(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        String name="";
        if (point.getTarget() instanceof LeoBaseAnnotator) {
            name=((LeoBaseAnnotator)point.getTarget()).getName();
        }
        EtmPoint etmPoint = etmMonitor.createPoint( name + ":" + point.getTarget().getClass().getCanonicalName());
        Object result = point.proceed();
        etmPoint.collect();
        return result;
    }

    @Around("call(* gov.va.vinci.leo.annotationpattern.ae.AnnotationPatternAnnotator.processPattern(..))")
    public Object apaAroundPattern(ProceedingJoinPoint point) throws Throwable {

        AnnotationPatternService service = (AnnotationPatternService) point.getArgs()[0];
        EtmPoint etmPoint = etmMonitor.createPoint(point.getTarget().getClass().getCanonicalName() + ":" + service.getAnnotationPattern().getPattern());
        Object result = point.proceed();
        etmPoint.collect();
        return result;
    }


}
