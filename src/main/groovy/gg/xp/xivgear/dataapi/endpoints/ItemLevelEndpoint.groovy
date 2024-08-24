package gg.xp.xivgear.dataapi.endpoints

import gg.xp.xivgear.dataapi.datamanager.DataManager
import gg.xp.xivgear.dataapi.datamanager.FullData
import gg.xp.xivgear.dataapi.models.ItemLevel
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
@Controller("/ItemLevel")
//@TupleConstructor(includeFields = true, defaults = false)
class ItemLevelEndpoint extends BaseDataEndpoint<Void, Response> {

	ItemLevelEndpoint(DataManager dm) {
		super(dm)
	}

	@TupleConstructor(includeFields = true)
	private static class Response {
		final List<ItemLevel> items
	}

	@SuppressWarnings(['GrMethodMayBeStatic', 'unused'])
	@Operation(summary = "Get ItemLevel data")
	@Get("/")
	@Produces(MediaType.APPLICATION_JSON)
	HttpResponse<Response> itemLevels(HttpRequest<?> request) {
		return makeResponse(request, null)
	}

	@Override
	protected Response getContent(FullData data, Void _) {
		new Response(data.itemLevels)
	}

}
