package com.xmbsmdsj.feign.extension.deployment;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Produce;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;

public class DepEmittingProcessor {

	/**
	 * Emit some known dependencies to be indexed
	 * @param producer
	 */
	@BuildStep
	@Produce(IndexDependencyBuildItem.class)
	void process(BuildProducer<IndexDependencyBuildItem> producer) {
		producer.produce(new IndexDependencyBuildItem("org.springframework", "spring-web"));
	}
}
