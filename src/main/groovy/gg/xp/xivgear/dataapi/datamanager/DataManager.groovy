package gg.xp.xivgear.dataapi.datamanager

import gg.xp.xivapi.XivApiClient
import gg.xp.xivapi.clienttypes.GameVersion
import gg.xp.xivapi.clienttypes.XivApiObject
import gg.xp.xivapi.clienttypes.XivApiSchemaVersion
import gg.xp.xivapi.clienttypes.XivApiSettings
import gg.xp.xivapi.filters.SearchFilter
import gg.xp.xivapi.pagination.ListOptions
import gg.xp.xivgear.dataapi.models.*
import gg.xp.xivgear.dataapi.persistence.DataPersistence
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton

import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

import static gg.xp.xivapi.filters.SearchFilters.*

/**
 * The main data holder class. Responsible for gathering data from xivapi, holding onto it, and occasionally refreshing
 * it.
 */
@Context
@Singleton
@Slf4j
@CompileStatic
class DataManager implements AutoCloseable {

	private CompletableFuture<FullData> dataFuture = new CompletableFuture<>()
	private final Thread xivApiUpdater
	private final Thread persistenceUpdater
	private final XivApiClient client
	private final DataPersistence pers
	private volatile boolean stop

	// TODO: make these app properties
	private static final int minIlvl = 290
	private static final int maxIlvl = 999
	private static final int minIlvlFood = 430
	private static final int maxIlvlFood = 999

	DataManager(DataPersistence pers, @Value('${xivapi.baseUri}') Optional<URI> xivApiUri) {
		this.pers = pers
		client = new XivApiClient(XivApiSettings.newBuilder().with {
			xivApiUri.ifPresent {
				baseUri = it
			}
			userAgent = "xivgear-data-api/1.0 (xivapi-java)"
			build()
		})
		xivApiUpdater = Thread.startVirtualThread this.&xivApiUpdateLoop
		persistenceUpdater = Thread.startVirtualThread this.&persistenceUpdateLoop
	}

	/**
	 * offerNewData is called when we have a potentially better data pack from any source (either xivapi
	 * directly or persistent storage).
	 *
	 * @param possibleNewData The new data
	 * @param tryPersist If true, will persist the data back into persistent storage. Should be false if the data
	 * originally came from object storage.
	 */
	private void offerNewData(FullData possibleNewData, boolean tryPersist) {
		// This branch means we already have data
		if (dataFuture.state() == Future.State.SUCCESS) {
			FullData existing = dataFuture.get()
			if (existing.timestamp.isBefore(possibleNewData.timestamp)) {
				dataFuture = CompletableFuture.completedFuture possibleNewData
				if (tryPersist) {
					persistData possibleNewData
				}
				log.info "New data timestamp: ${possibleNewData.timestamp}"
			}
			else {
				log.info "Data not newer"
			}
		}
		// We do not have data yet
		else if (dataFuture.state() == Future.State.RUNNING) {
			log.info "Initial data at ${possibleNewData.timestamp}"
			dataFuture.complete possibleNewData
			if (tryPersist) {
				persistData possibleNewData
			}
		}
		else {
			log.error "Invalid state ${dataFuture.state()}"
		}
	}

	/**
	 * Store a new data pack into persistent storage.
	 *
	 * @param newData The data to store
	 */
	private void persistData(FullData newData) {
		try {
			log.info "Persistence: Pushing Data"
			pers.data = newData
		}
		catch (Throwable t) {
			log.error "Error persisting data", t
		}
	}

	/**
	 * This updater loop runs in its own thread and is responsible for periodically querying Xivapi.
	 * <p>
	 * It will unconditionally pull the full data pack if we do not have any persisted data yet. Otherwise, it will
	 * "pre-check" by querying the versions and schema, and checking if it is different from the persisted data.
	 * It will only pull the full data pack if there is a mismatch.
	 * <p>
	 * Note that this introduces a useful quirk where, because the persistenceUpdateLoop most likely will not have
	 * pulled data prior to the first iteration of this loop, this will pull fresh data when the process starts,
	 * even if there is existing data.
	 */
	private void xivApiUpdateLoop() {
		while (!stop) {
			try {
				// TODO: do self-testing of the newly-acquired data to make sure that a schema mismatch didn't
				// horribly break things.
				boolean loadNew
				Future.State state = dataFuture.state()
				if (state == Future.State.SUCCESS) {
					// If there is existing data to compare to, check if there's an update available.
					// We consider data to be "different" if either the set of game versions, or the schema
					// version does not match.
					FullData oldData = dataFuture.get()
					List<GameVersion> oldVersions = oldData.versions
					List<GameVersion> newVersions = client.gameVersionsFull
					XivApiSchemaVersion oldSchema = oldData.schemaVersion
					XivApiSchemaVersion newSchema = getLiveSchemaVersion()
					boolean versionSame = oldVersions == newVersions
					boolean schemaSame = oldSchema.fullVersionString() == newSchema.fullVersionString()
					if (versionSame && schemaSame) {
						log.info "No update"
					}
					else {
						if (versionSame) {
							log.info "Update triggered (schema changed), going to reload data"
						}
						else if (schemaSame) {
							log.info "Update triggered (version changed), going to reload data"
						}
						else {
							log.info "Update triggered (both changed), going to reload data"
						}
						offerNewData makeData(), true
						log.info "Reloaded data"
					}
				}
				else {
					// If no existing data, then unconditionally pull and persist data.
					log.info "No existing data"
					offerNewData makeData(), true
				}
				// Since the process of checking data can be resource-intensive, set it to lower priority for future
				// iterations. The first iteration gets to run normally.
				Thread.currentThread().priority = Thread.MIN_PRIORITY
			}
			catch (Throwable t) {
				if (!stop) {
					log.error "Error in update loop", t
				}
			}
			// Add some random waiting so that the workers will naturally stagger
			Thread.sleep Duration.ofSeconds(120 + (Math.random() * 10) as int).toMillis()
		}
	}

	/**
	 * Updater loop that periodically pulls from persistent storage.
	 */
	private void persistenceUpdateLoop() {
		while (!stop) {
			try {
				log.info "Persistence: Getting Data"
				FullData fd = pers.data
				if (fd == null) {
					log.info "Persistence: No Data"
				}
				else {
					log.info "Persistence: Has Data"
					offerNewData fd, false
				}
			}
			catch (Throwable t) {
				if (!stop) {
					log.error "Error in pers update loop", t
				}
			}
			// Add some random waiting so that the workers will naturally stagger
			Thread.sleep Duration.ofSeconds(120 + (Math.random() * 10) as int).toMillis()
		}
	}

	private XivApiSchemaVersion getLiveSchemaVersion() {
		return client.getById(BaseParam, 1).schemaVersion
	}

	/**
	 * Pull current live data from Xivapi.
	 *
	 * @return The data pack.
	 */
	private FullData makeData() {
		try {

			ListOptions<XivApiObject> opts = ListOptions.<XivApiObject> newBuilder().with {
				perPage 500
				build()
			}
			client.defaultListOpts = opts

			log.info "Loading versions"
			List<GameVersion> versions = client.gameVersionsFull
			log.info "Loaded ${versions.size()} versions"
			GameVersion latest = versions.find { it.names().contains('latest') }
			if (latest != null) {
				log.info "'latest' version is ${latest.key()}: (${latest.names().join(', ')})"
			}
			else {
				log.warn("No 'latest' version found!")
			}

			log.info "Loading BaseParams"
			// TODO revert this
			List<BaseParam> baseParams = client.getListIterator(BaseParam).toBufferedStream(10).toList().toSorted { it.rowId }
			log.info "Loaded ${baseParams.size()} BaseParams"

			log.info "Loading ItemLevel"
			List<ItemLevel> itemLevels = client.getListIterator(ItemLevel).toList().toSorted { it.rowId }
			log.info "Loaded ${itemLevels.size()} ItemLevels"

			log.info "Loading ClassJob"
			SearchFilter combatJobsOnly = gt("PrimaryStat", 0)
			List<ClassJob> jobs = client.getSearchIterator(ClassJob, combatJobsOnly).toList().toSorted { it.rowId }
			log.info "Loaded ${jobs.size()} ClassJobs"

			log.info "Loading Items"
			SearchFilter itemFilter = and(
					gte("LevelItem", minIlvl),
					lte("LevelItem", maxIlvl),
					gt("EquipSlotCategory", 0),
					or(jobs
							.findAll { it.rowId > 0 }
							.collect {
								return eq("ClassJobCategory.${it.abbreviation}", 1)
							}
					)
			)
			List<ItemBase> itemBases = client.getSearchIterator(ItemBase, itemFilter).toList().toSorted { it.rowId }
			Set<Integer> itemIds = itemBases.collect { it.rowId }.toSet()
			log.info "Loaded ${itemBases.size()} Items"

			log.info "Loading Recipes"
			SearchFilter recipeFilter = and(
					gte("ItemResult.LevelItem", minIlvl),
					lte("ItemResult.LevelItem", maxIlvl),
					gt("ItemResult.EquipSlotCategory", 0),
//					or(jobs
//							.findAll { it.rowId > 0 }
//							.collect {
//								return eq("ItemResult.ClassJobCategory.${it.abbreviation}", 1)
//							}
//					)
			)
			List<Recipe> recipes = client.getSearchIterator(Recipe, recipeFilter).toList()
			// Only retain items that were in our original result set
			Set<Integer> itemsWithRecipes = recipes
					.collect { it.itemResult }
					.findAll { it in itemIds }
					.toSet()
			log.info "Loaded ${itemsWithRecipes.size()} Recipes"

			// There is currently no good way to do shops. SpecialShop items have a 60-item "Items" array which results
			// in a massive response. It is too slow and bloated to consume raw. Trying to filter also results in
			// unacceptable performance because xivapi has to do way too many joins.
//			log.info "Loading Shops"
//			Set<Integer> itemsWithShops = new HashSet<>()
			// Searching is currently too slow because it has to do a double-join
//			itemBases.collate(50).each { subList ->
//				SearchFilter shopsFilter = or(
//						subList.collect {
//							eq "Item[].Item[]", it.rowId
//						},
//				)
//				client.getSearchIterator(SpecialShop, shopsFilter).each {
//					itemsWithShops.addAll it.item.collectMany { it.item }
//				}
//			}
//			client.getListIterator(SpecialShop).each {
//				it.item.each {
//					itemsWithShops.addAll it.item
//				}
//			}
//			log.info "Loaded ${itemsWithShops} Shop->Item Mappings"

			log.info "Loading Materia"
			SearchFilter materiaFilter = and(
					gt(any("Item"), 0)
			)
			List<Materia> materia = client.getSearchIterator(Materia, materiaFilter).toList().<Materia> toSorted { it.rowId }
			log.info "Loaded ${materia.size()} Materia"

			log.info "Loading Food"
			Map<Integer, FoodItemFood> foodBonuses = [:]

			SearchFilter foodFilter = and(
					eq("ItemSearchCategory", 45),
					gte("LevelItem", minIlvlFood),
					lte("LevelItem", maxIlvlFood),
			)
			Iterator<FoodItemBase> bases = client.getSearchIterator(FoodItemBase, foodFilter)

			client.getListIterator(FoodItemFood).each {
				foodBonuses[it.rowId] = it
			}

			List<Food> food = bases.collectMany {
				int bonusId = it.foodItemId
				FoodItemFood itemFood = foodBonuses[bonusId]
				if (itemFood == null) {
					log.error "Food item ${it} did not have corresponding ItemFood ID ${bonusId}"
					return [] as List<Food>
				}
				else {
					return [new FoodImpl(it, itemFood)] as List<Food>
				}
			}
			food.<Food> sort { it.rowId }
			log.info "Loaded ${food.size()} Foods"

			def data = new FullData(versions, baseParams, itemBases, itemLevels, jobs, materia, food, itemsWithRecipes)
			return data
		}
		catch (Throwable t) {
			log.error "Error getting data", t
			throw t
		}
	}

	/**
	 * @return True if this DataManager has data from any source.
	 */
	boolean isReady() {
		if (!dataFuture.isDone()) {
			return false
		}
		try {
			FullData data = dataFuture.get()
			if (data.items.isEmpty()) {
				return false
			}
			return true
		}
		catch (Throwable t) {
			return false
		}
	}

	/**
	 * @return The data, from any source, in the form of a future. Note that this future will initially be in
	 * "RUNNING" state, and will transition to "SUCCESS" upon the first ingestion of data from any source. However,
	 * this future will not be updated with newer data. Thus, it should never be stored and queried later - you should
	 * always re-query this method.
	 */
	Future<FullData> getDataFuture() {
		return dataFuture
	}

	@Override
	void close() throws Exception {
		stop = true
		xivApiUpdater.interrupt()
		persistenceUpdater.interrupt()
		client.close()
	}
}
