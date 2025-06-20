package gg.xp.xivgear.dataapi.endpoints

import groovy.transform.CompileStatic
import io.micronaut.context.annotation.Context
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.swagger.v3.oas.annotations.Operation

@Context
@CompileStatic
@Controller("/healthz")
class Healthcheck {

	@SuppressWarnings(['GrMethodMayBeStatic', 'unused'])
	@Operation(summary = "Health Check")
	@Get("/")
	@Produces(MediaType.TEXT_PLAIN)
	String healthCheck() {
		return "Success"
	}

}
