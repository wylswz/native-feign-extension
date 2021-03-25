package com.xmbsmdsj.feign.extension.deployment.utils;

import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

public class BuildItemTemplateUtils {

	/**
	 * Open constructors, fields and methods
	 * @param clazz
	 * @return
	 */
	public static ReflectiveClassBuildItem allOpenReflection(Class<?> clazz) {
		ReflectiveClassBuildItem.Builder builder = ReflectiveClassBuildItem.builder(clazz);
		return builder.constructors(true).fields(true).methods(true).build();
	}
}
