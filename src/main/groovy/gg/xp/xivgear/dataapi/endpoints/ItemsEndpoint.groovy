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
import jakarta.annotation.Nullable

/**
 * Endpoint for items. Supports querying items for multiple jobs. The key type is defined as a Set of job names, so that
 * order does not matter. i.e. job=WHM,AST will cache the same as job=AST,WHM.
 */
@Context
@Controller("/Items")
@CompileStatic
class ItemsEndpoint extends BaseDataEndpoint<ItemsRequest, Response> {

	ItemsEndpoint(DataManager dm) {
		super(dm)
	}

	@TupleConstructor(includeFields = true, defaults = false)
	@SuppressWarnings('unused')
	static class Response {
		/**
		 * The list of items
		 */
		final List<Item> items
		/**
		 * The max ilvl per equip level. Indices represent equip levels, values represent ilvls.
		 * e.g. maxIlvlPerLvl[100] == 790.
		 * <p>
		 * Only indices with at least one matching item in the response will be populated. Values for equip levels
		 * for which no item in this response exists should be treated as undefined behavior.
		 */
		final int[] maxIlvlPerLvl
		/**
		 * The max ilvl per equip level, for weapons. Indices represent equip levels, values represent ilvls.
		 * e.g. maxIlvlPerLvlWpn[100] == 795.
		 * <p>
		 * Only indices with at least one matching item in the response will be populated. Values for equip levels
		 * for which no item in this response exists should be treated as undefined behavior.
		 */
		final int[] maxIlvlPerLvlWpn
	}

	@TupleConstructor(includeFields = true, defaults = false)
	static class ItemsRequest {
		final Set<String> jobs
		final @Nullable
		Set<Integer> itemIds
	}

	@SuppressWarnings(['GrMethodMayBeStatic', 'unused'])
	@Operation(summary = "Get applicable gear items")
	@Get("/")
	@Produces(MediaType.APPLICATION_JSON)
	HttpResponse<Response> items(HttpRequest<?> request, List<String> job, @Nullable List<Integer> itemIds) {
		return makeResponse(request, new ItemsRequest(job as Set<String>, itemIds == null ? null : (itemIds as Set<Integer>)))
	}

	@Override
	protected Response getContent(FullData fd, ItemsRequest req) {
		var jobs = req.jobs
		var itemIds = req.itemIds
		// TODO: can we use the indices as pseudo-keys?
		List<Item> items = []
		int[] maxIlvlPerLvl = new int[fd.maxEquipLvl + 1]
		int[] maxIlvlPerLvlWpn = new int[fd.maxEquipLvl + 1]
		fd.items.forEach {
			// Filter by jobs first
			if (!jobs.any{ job -> it.classJobCategory.jobs[job] }) {
				return
			}
			int level = it.equipLevel
			if (it.equipSlotCategory.mainHand) {
				maxIlvlPerLvlWpn[level] = Math.max(maxIlvlPerLvlWpn[level], it.ilvl)
			}
			else {
				maxIlvlPerLvl[level] = Math.max(maxIlvlPerLvl[level], it.ilvl)
			}
			if (itemIds == null || itemIds.contains(it.rowId)) {
				items << it
			}
		}
		return new Response(items, maxIlvlPerLvl, maxIlvlPerLvlWpn)
	}
}
