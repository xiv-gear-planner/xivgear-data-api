package gg.xp.xivgear.dataapi.datamanager

import gg.xp.xivapi.XivApiClient
import gg.xp.xivapi.clienttypes.XivApiObject
import gg.xp.xivapi.clienttypes.XivApiSchemaVersion
import gg.xp.xivapi.filters.SearchFilter
import gg.xp.xivapi.pagination.ListOptions
import gg.xp.xivgear.dataapi.models.*
import gg.xp.xivgear.dataapi.persistence.DataPersistence
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.micronaut.context.annotation.Context
import jakarta.inject.Singleton

import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
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

	private static final ExecutorService exs = Executors.newVirtualThreadPerTaskExecutor()

	private CompletableFuture<FullData> dataFuture = new CompletableFuture<>()
	private final Thread xivApiUpdater
	private final Thread persistenceUpdater
	private final XivApiClient client
	private final DataPersistence pers
	private volatile boolean stop

	private static final int minIlvl = 290
//	private static final int minIlvl = 570
//	private static final int maxIlvl = 310
//	private static final int minIlvl = 680
	private static final int maxIlvl = 999
	private static final int minIlvlFood = 430
	private static final int maxIlvlFood = 999


	DataManager(DataPersistence pers) {
		this.pers = pers
		client = new XivApiClient()
		xivApiUpdater = Thread.startVirtualThread this.&xivApiUpdateLoop
		persistenceUpdater = Thread.startVirtualThread this.&persistenceUpdateLoop
	}

	private void offerNewData(FullData possibleNewData, boolean tryPersist) {
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

	void persistData(FullData newData) {
		try {
			log.info "Persistence: Pushing Data"
			pers.data = newData
		}
		catch (Throwable t) {
			log.error "Error persisting data", t
		}
	}

	void xivApiUpdateLoop() {
		while (!stop) {
			try {
				// TODO: do self-testing of the newly-acquired data to make sure that a schema mismatch didn't
				// horribly break things.
				// If no existing data, retrieve new data unconditionally
				boolean loadNew
				Future.State state = dataFuture.state()
				if (state == Future.State.SUCCESS) {
					// If there is existing data to compare to, check if there's an update available
					FullData oldData = dataFuture.get()
					Set<String> oldVersions = oldData.versions.toSet()
					Set<String> newVersions = client.gameVersions.toSet()
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
					// If no existing data, then unconditionally update
					log.info "No existing data"
					offerNewData makeData(), true
				}
			}
			catch (Throwable t) {
				if (!stop) {
					log.error "Error in update loop", t
				}
			}
			// Add some random waiting so that the workers will naturally stagger
			Thread.sleep Duration.ofSeconds(60 + (Math.random() * 10) as int).toMillis()
		}
	}

	void persistenceUpdateLoop() {
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
			Thread.sleep Duration.ofSeconds(60 + (Math.random() * 10) as int).toMillis()
		}
	}

	private XivApiSchemaVersion getLiveSchemaVersion() {
		return client.getById(BaseParam, 1).schemaVersion
	}

	private FullData retrievePersistentData() {
		return pers.data
	}

	private FullData makeData() {
		try {

			ListOptions<XivApiObject> opts = ListOptions.<XivApiObject> newBuilder().with {
				perPage 500
				build()
			}
			client.defaultListOpts = opts

			log.info "Loading versions"
			List<String> versions = client.gameVersions
			log.info "Loaded ${versions.size()} versions"

			log.info "Loading BaseParams"
			List<BaseParam> baseParams = client.getListIterator(BaseParam).toList().toSorted { it.rowId }
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
			Set<Integer> itemsWithRecipes = recipes.collect { it.itemResult }.toSet()
			log.info "Loaded ${recipes.size()} Recipes"

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

	boolean isReady() {
		if (!dataFuture.isDone()) {
			return false
		}
		try {
			dataFuture.get()
		}
		catch (Throwable t) {
			return false
		}
	}

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
