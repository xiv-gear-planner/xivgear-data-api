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

/**
 * Endpoint for items. Supports querying items for multiple jobs. The key type is defined as a Set of job names, so that
 * order does not matter. i.e. job=WHM,AST will cache the same as job=AST,WHM.
 */
@Context
@Controller("/Items")
@CompileStatic
class ItemsEndpoint extends BaseDataEndpoint<Set<String>, Response> {

	ItemsEndpoint(DataManager dm) {
		super(dm)
	}

	@TupleConstructor(includeFields = true)
	static class Response {
		@SuppressWarnings('unused')
		final List<Item> items
	}

	@SuppressWarnings(['GrMethodMayBeStatic', 'unused'])
	@Operation(summary = "Get applicable gear items")
	@Get("/")
	@Produces(MediaType.APPLICATION_JSON)
	HttpResponse<Response> items(HttpRequest<?> request, List<String> job) {
		return makeResponse(request, job as Set<String>)
	}

	@Override
	protected Response getContent(FullData fd, Set<String> jobs) {
		List<Item> items = fd.items.findAll {
			jobs.any { job ->
				it.classJobCategory.jobs[job]
			}
		}
		return new Response(items)
	}
}
