package gg.xp.xivgear.dataapi.endpoints

import gg.xp.xivgear.dataapi.datamanager.DataManager
import gg.xp.xivgear.dataapi.datamanager.FullData
import gg.xp.xivgear.dataapi.gear.GearLevels
import gg.xp.xivgear.dataapi.models.GearAcquisitionSource
import gg.xp.xivgear.dataapi.models.Item
import gg.xp.xivgear.dataapi.models.ItemBase
import gg.xp.xivgear.dataapi.models.ItemImpl
import groovy.transform.TupleConstructor
import io.micronaut.context.annotation.Context
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.swagger.v3.oas.annotations.Operation

import static gg.xp.xivgear.dataapi.models.GearAcquisitionSource.*


@Context
@Controller("/Items")
//@TupleConstructor(includeFields = true, defaults = false)
class ItemsEndpoint extends BaseDataEndpoint<String, Response> {

	ItemsEndpoint(DataManager dm) {
		super(dm)
	}

	@TupleConstructor(includeFields = true)
	private static class Response {
		final List<Item> items
	}

	@SuppressWarnings(['GrMethodMayBeStatic', 'unused'])
	@Operation(summary = "Get applicable gear items")
	@Get("/")
	@Produces(MediaType.APPLICATION_JSON)
	HttpResponse<Response> items(HttpRequest<?> request, String job) {
		return makeResponse(request, job)
	}

	private GearAcquisitionSource getAcquisitionSource(ItemBase base) {
		return base.with {
			switch (rarity) {
				case 1: {
					// Normal item
					// For gear items, this means it is crafted
					return Crafted
				}
				case 2: {
					// Green item
					// This can be crafted, augmented crafted, or dungeon
					if (name.contains("Augmented")) {
						return AugCrafted
					}
					else {
						return Unknown
						boolean isCraftable = false // TODO
						if (isCraftable) {
							return Crafted
						}
						else {
							return Dungeon
						}
					}
				}
				case 3: {
					// Blue item
					// This can be raid, tome, extreme, augmented tome, alliance raid, etc
					boolean isWeapon = equipSlotCategory.mainHand || equipSlotCategory.offHand
					int ilvl = ilvl
					// This is start-of-expac relics (e.g. Didact set)
					if (GearLevels.isArtifactLevel(ilvl)) {
						if (isWeapon) {
							// Ambiguous due to start-of-expac extreme trial weapons having the same level
							// TODO: do these AF weapons have a shop that we can use?
							return Unknown
						}
						else {
							return Artifact
						}
					}
					// Start-of-expac tome gear (e.g. Ronkan)
					else if (GearLevels.isBasicTomeLevel(ilvl)) {
						return Tome
					}
					// Savage/Aug Tome
					else if (GearLevels.isSavageRaidLevel(ilvl)) {
						if (name.contains("Augmented")) {
							return AugTome
						}
						else {
							return SavageRaid
						}
					}
					// Savage/Ultimate weapon
					else if (GearLevels.isSavageRaidWeaponLevel(ilvl) && isWeapon) {
						// TODO: Aloalo has some weapons, doesn't it?
						if (name.contains("Ultimate")) {
							return Ultimate
						}
						else {
							return SavageRaid
						}
					}
					// Unaug tome, alliance raid
					else if (GearLevels.isUnAugmentedTomeLevel(ilvl)) {
						// TODO
						return Unknown
						boolean hasShop = true
						if (hasShop) {
							return Tome
						}
						else {
							return AllianceRaid
						}
					}
					// Normal raids, start-of-expac extreme accs
					else if (GearLevels.isNormalRaidLevel(ilvl)) {
						if (isWeapon) {
							return ExtremeTrial
						}
						else {
							// TODO
							return Unknown
							boolean hasShop = true
							if (hasShop) {
								// TODO: this logic is untested
								// old logic just put these as 'other'
								return NormalRaid
							}
							else {
								return ExtremeTrial
							}
						}
					}
					else if (GearLevels.isExTrialLevel(ilvl) && isWeapon) {
						return ExtremeTrial
					}
				}
				case 4: {
					// Purple item
					// This is only relics
					return Relic
				}
			}
			// Fallback
			return Unknown
		}
	}

	@Override
	protected Response getContent(FullData fd, String job) {
		List<Item> items = fd.itemBases
				.findAll { it.classJobCategory.jobs[job] }
				.collect { base ->
					// TODO: is this heavy to recompute every time?
					// Can BaseDataEndpoint be augmented to cache this internally?
					GearAcquisitionSource source = getAcquisitionSource(base)
					return new ItemImpl(base, source) as Item
				}
		return new Response(items)
	}
}
