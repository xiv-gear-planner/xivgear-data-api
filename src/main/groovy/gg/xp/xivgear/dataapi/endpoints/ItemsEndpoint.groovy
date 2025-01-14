package gg.xp.xivgear.dataapi.endpoints

import gg.xp.xivgear.dataapi.datamanager.DataManager
import gg.xp.xivgear.dataapi.datamanager.FullData
import gg.xp.xivgear.dataapi.models.Item
import groovy.transform.CompileStatic
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
@Controller("/Items")
@CompileStatic
class ItemsEndpoint extends BaseDataEndpoint<String, Response> {

	ItemsEndpoint(DataManager dm) {
		super(dm)
	}

	@TupleConstructor(includeFields = true)
	private static class Response {
		@SuppressWarnings('unused')
		final List<Item> items
	}

	@SuppressWarnings(['GrMethodMayBeStatic', 'unused'])
	@Operation(summary = "Get applicable gear items")
	@Get("/")
	@Produces(MediaType.APPLICATION_JSON)
	HttpResponse<Response> items(HttpRequest<?> request, String job) {
		return makeResponse(request, job)
	}

	@Override
	protected Response getContent(FullData fd, String job) {
		List<Item> items = fd.items.findAll { it.classJobCategory.jobs[job] }
		return new Response(items)
	}
}
