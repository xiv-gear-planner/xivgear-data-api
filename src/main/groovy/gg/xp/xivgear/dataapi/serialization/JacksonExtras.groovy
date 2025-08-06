package gg.xp.xivgear.dataapi.serialization

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Primary
import io.micronaut.context.annotation.Replaces
import io.micronaut.jackson.databind.JacksonDatabindMapper
import io.micronaut.json.JsonMapper
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Factory
@Context
class JacksonExtras {

	@Inject
	void customize(ObjectMapper mapper) {
		mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS)
		mapper.registerModule(new Jdk8Module())
	}

	@Singleton
	@Primary
	@Replaces(JsonMapper)
	JsonMapper jsonMapper(JacksonDatabindMapper mapper) {
		return mapper
	}
}
