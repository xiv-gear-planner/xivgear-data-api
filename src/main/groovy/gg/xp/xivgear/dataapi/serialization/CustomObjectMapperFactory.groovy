package gg.xp.xivgear.dataapi.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import groovy.transform.CompileStatic
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Primary
import io.micronaut.context.annotation.Replaces
import jakarta.inject.Singleton

@Factory

class CustomObjectMapperFactory {

	@Primary
	@Replaces(ObjectMapper)
	@Singleton
	ObjectMapper customObjectMapper() {
		var out = new ObjectMapper()
		out.setPropertyNamingStrategy(new CapitalizedPropertyNamingStrategy())
		out.registerModule(new Jdk8Module())
		return out
	}

}
