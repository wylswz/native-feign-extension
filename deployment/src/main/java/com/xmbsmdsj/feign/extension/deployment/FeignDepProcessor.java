package com.xmbsmdsj.feign.extension.deployment;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.inject.Inject;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Produce;
import io.quarkus.deployment.builditem.AdditionalIndexedClassesBuildItem;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import org.springframework.cloud.openfeign.FeignClient;

public class FeignDepProcessor {

	public static final Class<FeignClient> FEIGN_ANNOTATION = FeignClient.class;
	private final Logger logger = Logger.getLogger(FeignExtensionProcessor.class.getName());

	@Inject
	List<IndexDependencyBuildItem> dependencyBuildItems;

	@BuildStep
	@Produce(AdditionalIndexedClassesBuildItem.class)
	void additionalIndexedClassesBuildItem(BuildProducer<AdditionalIndexedClassesBuildItem> producer) {

		for (IndexDependencyBuildItem dependencyBuildItem : dependencyBuildItems) {
			String groupId = dependencyBuildItem.getGroupId();
			String artifactId = dependencyBuildItem.getArtifactId();
			Reflections reflections = new Reflections(
					groupId, new TypeAnnotationsScanner(), new SubTypesScanner()
			);
			Set<Class<?>> classes = reflections.getTypesAnnotatedWith(FEIGN_ANNOTATION);
			for (Class<?> c : classes) {
				logger.info("Added annotated client class: " + c.getName());
				producer.produce(new AdditionalIndexedClassesBuildItem(
						c.getName()
				));
			}
		}
	}

}
