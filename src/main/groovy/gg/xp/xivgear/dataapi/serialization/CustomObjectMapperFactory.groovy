package gg.xp.xivgear.dataapi.serialization

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import groovy.transform.CompileStatic
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Primary
import io.micronaut.context.annotation.Replaces
import io.micronaut.jackson.JacksonConfiguration
import io.swagger.v3.core.jackson.ModelResolver
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Factory
@Context
class CustomObjectMapperFactory {

//	@Primary
//	@Replaces(ObjectMapper)
//	@Singleton
//	ObjectMapper customObjectMapper() {
//		var out = new ObjectMapper()
////		out.setPropertyNamingStrategy(new CapitalizedPropertyNamingStrategy())
//		out.registerModule(new Jdk8Module())
//		return out
//	}
//
//	@Primary
//	@Singleton
//	@Replaces(JacksonConfiguration)
//	JacksonConfiguration jacksonConfiguration() {
//		var config = new JacksonConfiguration()
//		config.setPropertyNamingStrategy(new CapitalizedPropertyNamingStrategy())
//		return config
//	}
////
//	@Primary
//	@Singleton
//	@Replaces(ModelResolver)
//	ModelResolver modelResolver(ObjectMapper mapper) {
//		mapper.setPropertyNamingStrategy(new CapitalizedPropertyNamingStrategy())
//		return new ModelResolver(mapper)
//	}

	@Inject
	void customize(ObjectMapper mapper) {
//		mapper.setPropertyNamingStrategy(new CapitalizedPropertyNamingStrategy())
		mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS)
		mapper.registerModule(new Jdk8Module())
	}

}
