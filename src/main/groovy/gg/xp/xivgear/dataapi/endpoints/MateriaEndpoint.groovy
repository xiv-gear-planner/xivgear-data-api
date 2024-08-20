package gg.xp.xivgear.dataapi.endpoints

import gg.xp.xivgear.dataapi.datamanager.DataManager
import gg.xp.xivgear.dataapi.datamanager.FullData
import gg.xp.xivgear.dataapi.models.Materia
import groovy.transform.TupleConstructor
import io.micronaut.context.annotation.Context
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.swagger.v3.oas.annotations.Operation

@Context

@Controller("/Materia")
//@TupleConstructor(includeFields = true, defaults = false)
class MateriaEndpoint {

	private final DataManager dm

	MateriaEndpoint(DataManager dm) {
		this.dm = dm
	}

	@TupleConstructor(includeFields = true)
	private static class Response {
		final List<Materia> items
	}

	@SuppressWarnings(['GrMethodMayBeStatic', 'unused'])
	@Operation(summary = "Get Materia")
	@Get("/")
	@Produces(MediaType.APPLICATION_JSON)
	HttpResponse<Response> materia() {
		// Ready check endpoint
		if (dm.ready) {
			FullData fd = dm.getDataFuture().get()

			List<Materia> items = fd.materia

			return HttpResponse.ok(new Response(items))
		}
		else {
			return HttpResponse.status(503, "Not Ready")
		}
	}

}
