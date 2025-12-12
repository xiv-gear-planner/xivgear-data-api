package gg.xp.xivgear.dataapi.endpoints

import gg.xp.xivapi.clienttypes.GameVersion
import gg.xp.xivgear.dataapi.datamanager.DataManager
import gg.xp.xivgear.dataapi.datamanager.FullData
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
@Controller("/Versions/Full")
class VersionsFullEndpoint extends BaseDataEndpoint<Void, Response> {

	VersionsFullEndpoint(DataManager dm) {
		super(dm)
	}

	@TupleConstructor(includeFields = true)
	private static class Response {
		final List<GameVersion> versions
	}

	@SuppressWarnings(['GrMethodMayBeStatic', 'unused'])
	@Operation(summary = "Get versions available via Xivapi at the time the data was pulled. Returns the same shape as Xivapi's verion endpoint would.")
	@Get("/")
	@Produces(MediaType.APPLICATION_JSON)
	HttpResponse<Response> versions(HttpRequest<?> request) {
		return makeResponse(request, null)
	}

	@Override
	protected Response getContent(FullData data, Void _) {
		return new Response(data.versions)
	}
}
