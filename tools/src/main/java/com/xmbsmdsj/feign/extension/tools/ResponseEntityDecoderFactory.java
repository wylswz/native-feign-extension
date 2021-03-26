package com.xmbsmdsj.feign.extension.tools;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

public interface ResponseEntityDecoderFactory {
	ResponseEntityDecoder getDecoder();

	ResponseEntityDecoderFactory DEFAULT = new SpringJacksonResponseDecoderFactory();

	class SpringJacksonResponseDecoderFactory implements ResponseEntityDecoderFactory {

		@Override
		public ResponseEntityDecoder getDecoder() {
			return new ResponseEntityDecoder(
					new SpringDecoder(
							() -> new HttpMessageConverters(
									new MappingJackson2HttpMessageConverter()
							)
					)
			);
		}
	}
}
