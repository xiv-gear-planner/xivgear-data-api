package gg.xp.xivgear.dataapi.endpoints

import gg.xp.xivgear.dataapi.datamanager.DataManager
import gg.xp.xivgear.dataapi.datamanager.FullData
import gg.xp.xivgear.dataapi.models.BaseParam
import groovy.transform.TupleConstructor
import io.micronaut.context.annotation.Context
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.swagger.v3.oas.annotations.Operation

@Context

@Controller("/BaseParams")
//@TupleConstructor(includeFields = true, defaults = false)
class BaseParamEndpoint {

	private final DataManager dm

	BaseParamEndpoint(DataManager dm) {
		this.dm = dm
	}

	@TupleConstructor(includeFields = true)
	private static class Response {
		final List<BaseParam> items
	}

	@SuppressWarnings(['GrMethodMayBeStatic', 'unused'])
	@Operation(summary = "Get BaseParams")
	@Get("/")
	@Produces(MediaType.APPLICATION_JSON)
	HttpResponse<Response> baseParams() {
		// Ready check endpoint
		if (dm.ready) {
			FullData fd = dm.getDataFuture().get()

			List<BaseParam> items = fd.baseParams

			return HttpResponse.ok(new Response(items))
		}
		else {
			return HttpResponse.status(503, "Not Ready")
		}
	}

}
