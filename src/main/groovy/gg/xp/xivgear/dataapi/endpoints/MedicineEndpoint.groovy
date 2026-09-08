package gg.xp.xivgear.dataapi.endpoints

import gg.xp.xivgear.dataapi.datamanager.DataManager
import gg.xp.xivgear.dataapi.datamanager.FullData
import gg.xp.xivgear.dataapi.models.Food
import groovy.transform.TupleConstructor
import io.micronaut.context.annotation.Context
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.swagger.v3.oas.annotations.Operation

@Context
@Controller("/Medicine")
class MedicineEndpoint extends BaseDataEndpoint<Void, Response> {

	MedicineEndpoint(DataManager dm) {
		super(dm)
	}

	@TupleConstructor(includeFields = true)
	private static class Response {
		final List<Food> items
	}

	@SuppressWarnings(['GrMethodMayBeStatic', 'unused'])
	@Operation(summary = "Get medicine items")
	@Get("/")
	@Produces(MediaType.APPLICATION_JSON)
	HttpResponse<Response> foodItems(HttpRequest<?> request) {
		return makeResponse(request, null)
	}

	@Override
	protected Response getContent(FullData data, Void _) {
		return new Response(data.medicines)
	}
}
