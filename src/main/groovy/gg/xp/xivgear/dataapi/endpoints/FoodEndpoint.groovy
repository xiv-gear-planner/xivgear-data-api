package gg.xp.xivgear.dataapi.endpoints

import gg.xp.xivgear.dataapi.datamanager.DataManager
import gg.xp.xivgear.dataapi.datamanager.FullData
import gg.xp.xivgear.dataapi.models.Food
import gg.xp.xivgear.dataapi.models.FoodImpl
import groovy.transform.TupleConstructor
import io.micronaut.context.annotation.Context
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.swagger.v3.oas.annotations.Operation

@Context
@Controller("/Food")
class FoodEndpoint {

	private final DataManager dm

	FoodEndpoint(DataManager dm) {
		this.dm = dm
	}

	@TupleConstructor(includeFields = true)
	private static class Response {
		final List<Food> items
	}

	@SuppressWarnings(['GrMethodMayBeStatic', 'unused'])
	@Operation(summary = "Get food items")
	@Get("/")
	@Produces(MediaType.APPLICATION_JSON)
	HttpResponse<Response> foodItems() {
		// Ready check endpoint
		if (dm.ready) {

			FullData fd = dm.getDataFuture().get()

			var items = fd.food as List<Food>

			return HttpResponse.ok(new Response(items))
		}
		else {
			return HttpResponse.status(503, "Not Ready")
		}
	}

}
