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
@Controller("/Item")
@CompileStatic
class ItemEndpoint extends BaseDataEndpoint<Integer, Item> {

	ItemEndpoint(DataManager dm) {
		super(dm)
	}

	@SuppressWarnings(['GrMethodMayBeStatic', 'unused'])
	@Operation(summary = "Get a single gear item")
	@Get("/{itemId}")
	@Produces(MediaType.APPLICATION_JSON)
	HttpResponse<Item> item(HttpRequest<?> request, Integer itemId) {
		return makeResponse(request, itemId)
	}

	@Override
	protected Item getContent(FullData fd, Integer itemId) {
		return fd.items.find {
			it.rowId == itemId
		}
	}
}
