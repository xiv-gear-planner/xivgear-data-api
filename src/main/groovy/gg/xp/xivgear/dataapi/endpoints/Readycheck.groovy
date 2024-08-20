package gg.xp.xivgear.dataapi.endpoints

import gg.xp.xivgear.dataapi.datamanager.DataManager
import groovy.transform.CompileStatic
import io.micronaut.context.annotation.Context
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.swagger.v3.oas.annotations.Operation

@Context

@Controller("/readyz")
//@TupleConstructor(includeFields = true, defaults = false)
class Readycheck {

	private final DataManager dm

	Readycheck(DataManager dm) {
		this.dm = dm
	}

	@SuppressWarnings(['GrMethodMayBeStatic', 'unused'])
	@Operation(summary = "Ready Check")
	@Get("/")
	@Produces(MediaType.TEXT_PLAIN)
	HttpResponse<String> readyCheck() {
		// Ready check endpoint
		if (dm.ready) {
			return HttpResponse.ok("Ready")
		}
		else {
			return HttpResponse.status(503, "Not Ready")
		}
	}

}
