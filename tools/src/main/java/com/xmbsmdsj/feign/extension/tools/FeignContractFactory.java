package com.xmbsmdsj.feign.extension.tools;

import java.util.Arrays;

import feign.Contract;

import org.springframework.cloud.openfeign.annotation.PathVariableParameterProcessor;
import org.springframework.cloud.openfeign.annotation.RequestHeaderParameterProcessor;
import org.springframework.cloud.openfeign.annotation.RequestParamParameterProcessor;
import org.springframework.cloud.openfeign.support.SpringMvcContract;

public interface FeignContractFactory {
	Contract getContract();

	public static FeignContractFactory DEFAULT = new SpringContractFactory();

	class SpringContractFactory implements FeignContractFactory {

		@Override
		public Contract getContract() {
			return new SpringMvcContract(
					Arrays.asList(
							new PathVariableParameterProcessor(),
							new RequestParamParameterProcessor(),
							new RequestHeaderParameterProcessor()
					)
			);
		}
	}
}
