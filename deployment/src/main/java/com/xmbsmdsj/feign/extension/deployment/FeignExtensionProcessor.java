package com.xmbsmdsj.feign.extension.deployment;

import java.util.Collection;
import java.util.logging.Logger;

import javax.inject.Inject;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Produce;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageConfigBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageProxyDefinitionBuildItem;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;

class FeignExtensionProcessor {

    private static final String FEATURE = "feign-extension";
    private static final DotName FEIGN_ANNOTATION = DotName.createSimple("org.springframework.cloud.openfeign.FeignClient");
    private final Logger logger = Logger.getLogger(FeignExtensionProcessor.class.getName());

    @Inject
    CombinedIndexBuildItem combinedIndexBuildItem;

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }


    @BuildStep
    @Produce(NativeImageConfigBuildItem.class)
    NativeImageConfigBuildItem register(
            BuildProducer<NativeImageProxyDefinitionBuildItem> producer) {
        IndexView index = combinedIndexBuildItem.getIndex();
        Collection<AnnotationInstance> instances = index.getAnnotations(FEIGN_ANNOTATION);
        NativeImageConfigBuildItem.Builder builder = NativeImageConfigBuildItem.builder();
        for (AnnotationInstance instance : instances) {
            AnnotationTarget target = instance.target();
            DotName dotName = target.asClass().name();
            logger.info("Registering proxy interface: " + dotName.toString());
            builder.addProxyClassDefinition(dotName.toString());
        }
        return builder.build();
    }

}
