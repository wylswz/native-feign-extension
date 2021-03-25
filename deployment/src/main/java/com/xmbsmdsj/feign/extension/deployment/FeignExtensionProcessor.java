package com.xmbsmdsj.feign.extension.deployment;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.inject.Inject;

import com.xmbsmdsj.feign.extension.deployment.utils.BuildItemTemplateUtils;
import feign.InvocationHandlerFactory;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Produce;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageConfigBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageProxyDefinitionBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.apache.commons.logging.impl.SimpleLog;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.core.annotation.SynthesizedAnnotation;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

class FeignExtensionProcessor {

    private static final String FEATURE = "feign-extension";
    private static final DotName FEIGN_ANNOTATION = DotName.createSimple("org.springframework.cloud.openfeign.FeignClient");
    private static final Set<Class<?>> SPRING_ANNOTATIONS = new HashSet<>();
    private static final Set<Class<?>> OTHER_REFLECTIVE_CLASSES = new HashSet<>();
    private final Logger logger = Logger.getLogger(FeignExtensionProcessor.class.getName());

    static {
        SPRING_ANNOTATIONS.add(PathVariable.class);
        SPRING_ANNOTATIONS.add(RequestMapping.class);
        SPRING_ANNOTATIONS.add(PostMapping.class);
        SPRING_ANNOTATIONS.add(GetMapping.class);
        SPRING_ANNOTATIONS.add(RequestBody.class);
        SPRING_ANNOTATIONS.add(RequestParam.class);
        SPRING_ANNOTATIONS.add(RequestHeader.class);

        OTHER_REFLECTIVE_CLASSES.add(LogFactory.class);
        OTHER_REFLECTIVE_CLASSES.add(LogFactoryImpl.class);
        OTHER_REFLECTIVE_CLASSES.add(feign.Logger.ErrorLogger.class);
        OTHER_REFLECTIVE_CLASSES.add(feign.Logger.JavaLogger.class);
        OTHER_REFLECTIVE_CLASSES.add(feign.Logger.NoOpLogger.class);
        OTHER_REFLECTIVE_CLASSES.add(SimpleLog.class);
    }

    @Inject
    CombinedIndexBuildItem combinedIndexBuildItem;

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }


    /**
     * Register interfaces used for dynamic proxies in feign
     * @see InvocationHandlerFactory.Default
     * @see SpringMvcContract
     * @param producer
     */
    @BuildStep
    @Produce(NativeImageProxyDefinitionBuildItem.class)
    void register(
            BuildProducer<NativeImageProxyDefinitionBuildItem> producer) {
        IndexView index = combinedIndexBuildItem.getIndex();
        Collection<AnnotationInstance> instances = index.getAnnotations(FEIGN_ANNOTATION);
        NativeImageConfigBuildItem.Builder builder = NativeImageConfigBuildItem.builder();
        for (AnnotationInstance instance : instances) {
            AnnotationTarget target = instance.target();
            DotName dotName = target.asClass().name();
            logger.info("Registering proxy interface: " + dotName.toString());
            producer.produce(new NativeImageProxyDefinitionBuildItem(dotName.toString()));
        }
        for (Class<?> c : SPRING_ANNOTATIONS) {
            producer.produce(
                    new NativeImageProxyDefinitionBuildItem(c.getName(), SynthesizedAnnotation.class.getName())
            );
        }

    }

    /**
     * Register classes for reflection in feign
     * @param producer
     */
    @BuildStep
    @Produce(ReflectiveClassBuildItem.class)
    void registerReflection(BuildProducer<ReflectiveClassBuildItem> producer) {
        IndexView index = combinedIndexBuildItem.getIndex();
        Set<Class<?>> candidates = new HashSet<>();
        candidates.addAll(SPRING_ANNOTATIONS);
        candidates.addAll(OTHER_REFLECTIVE_CLASSES);
        candidates.addAll(getAllImplementations(HttpMessageConverter.class, index));


        for (Class<?> c : candidates) {
            logger.info("Registering reflective class: " + c.getName());
            producer.produce(BuildItemTemplateUtils.allOpenReflection(c));
        }


    }

    private Collection<Class<?>> getAllImplementations(Class<?> ifce, IndexView index) {
        Set<Class<?>> candidates = new HashSet<>();
        Collection<ClassInfo> infos = index.getAllKnownImplementors(DotName.createSimple(HttpMessageConverter.class.getName()));
        for (ClassInfo info : infos) {
            try {
                candidates.add(Class.forName(info.name().toString()));
            }catch (ClassNotFoundException| NoClassDefFoundError e) {
                logger.warning("Implementation for HttpMessageConverter not found: " + info.name());
            }
        }
        return candidates;
    }
}
