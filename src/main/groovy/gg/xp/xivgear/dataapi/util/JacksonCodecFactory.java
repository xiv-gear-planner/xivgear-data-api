package gg.xp.xivgear.dataapi.util;

import io.micronaut.context.annotation.Factory;
import io.micronaut.core.annotation.Order;
import io.micronaut.http.codec.MediaTypeCodec;
import io.micronaut.jackson.codec.JsonMediaTypeCodec;
import jakarta.inject.Singleton;

@Factory
public class JacksonCodecFactory {

	@Singleton
	@Order(Integer.MIN_VALUE)
	public MediaTypeCodec jacksonCodec(JsonMediaTypeCodec jsonMediaTypeCodec) {
		return jsonMediaTypeCodec;
	}
}
