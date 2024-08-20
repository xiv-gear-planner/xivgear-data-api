package gg.xp.xivgear.dataapi.endpoints


import gg.xp.xivgear.dataapi.datamanager.DataManager
import gg.xp.xivgear.dataapi.datamanager.FullData
import gg.xp.xivgear.dataapi.models.Item
import gg.xp.xivgear.dataapi.models.ItemImpl
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import io.micronaut.context.annotation.Context
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.swagger.v3.oas.annotations.Operation

@Context
@Controller("/Items")
//@TupleConstructor(includeFields = true, defaults = false)
class ItemsEndpoint {

	private final DataManager dm

	ItemsEndpoint(DataManager dm) {
		this.dm = dm
	}

	@TupleConstructor(includeFields = true)
	private static class Response {
		final List<Item> items
	}

	@SuppressWarnings(['GrMethodMayBeStatic', 'unused'])
	@Operation(summary = "Get applicable gear items")
	@Get("/")
	@Produces(MediaType.APPLICATION_JSON)
	HttpResponse<Response> items(String job) {
		// Ready check endpoint
		if (dm.ready) {
			FullData fd = dm.getDataFuture().get()

			List<Item> items = fd.itemBases
					.findAll { it.classJobCategory.jobs[job] }
					.collect { new ItemImpl(it) as Item }

			return HttpResponse.ok(new Response(items))
		}
		else {
			return HttpResponse.status(503, "Not Ready")
		}
	}

}
