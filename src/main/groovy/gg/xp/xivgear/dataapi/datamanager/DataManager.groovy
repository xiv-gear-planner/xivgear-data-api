package gg.xp.xivgear.dataapi.datamanager

import gg.xp.xivapi.XivApiClient
import gg.xp.xivapi.clienttypes.XivApiObject
import gg.xp.xivapi.clienttypes.XivApiSchemaVersion
import gg.xp.xivapi.filters.SearchFilter
import gg.xp.xivapi.pagination.ListOptions
import gg.xp.xivgear.dataapi.models.*
import groovy.util.logging.Slf4j
import io.micronaut.context.annotation.Context
import jakarta.inject.Singleton

import java.time.Duration
import java.util.concurrent.*

import static gg.xp.xivapi.filters.SearchFilters.*

/**
 * The main data holder class. Responsible for gathering data from xivapi, holding onto it, and occasionally refreshing
 * it.
 */
@Context
@Singleton
@Slf4j
class DataManager implements AutoCloseable {

	private static final ExecutorService exs = Executors.newVirtualThreadPerTaskExecutor()

	private Future<FullData> dataFuture
	private final Thread updater
	private final XivApiClient client
	private volatile boolean stop

	private static final int minIlvl = 290
//	private static final int minIlvl = 570
//	private static final int maxIlvl = 310
//	private static final int minIlvl = 680
	private static final int maxIlvl = 999
	private static final int minIlvlFood = 430
	private static final int maxIlvlFood = 999


	DataManager() {
		client = new XivApiClient()
		dataFuture = exs.submit(this.&makeData as Callable<FullData>)
		updater = Thread.startVirtualThread this.&updateLoop
	}

	Runnable updateLoop() {
		while (!stop) {
			try {
				// TODO: do self-testing of the newly-acquired data to make sure that a schema mismatch didn't
				// horribly break things.
				Thread.sleep(Duration.ofSeconds(60).toMillis())
				// If the initial load failed, save new data unconditionally

				Future.State state = dataFuture.state()
				if (state == Future.State.FAILED || state == Future.State.CANCELLED) {
					log.info("Initial load failed, updater trying again")
					FullData data = makeData()
					dataFuture = CompletableFuture.completedFuture(data)
				}
				else if (state == Future.State.RUNNING) {
					log.info("Skipping update because initial load is still in progress")
				}
				else {
					FullData oldData = dataFuture.get()
					// First, check if there's actually an update
					Set<String> oldVersions = oldData.versions.toSet()
					Set<String> newVersions = client.gameVersions.toSet()
					XivApiSchemaVersion oldSchema = oldData.schemaVersion
					XivApiSchemaVersion newSchema = getLiveSchemaVersion()
					boolean versionSame = oldVersions == newVersions
					boolean schemaSame = oldSchema.fullVersionString() == newSchema.fullVersionString()
					if (versionSame && schemaSame) {
						log.info("No update")
					}
					else {
						if (versionSame) {
							log.info("Update triggered (schema changed), going to reload data")
						}
						else if (schemaSame) {
							log.info("Update triggered (version changed), going to reload data")
						}
						else {
							log.info("Update triggered (both changed), going to reload data")
						}
						FullData data = makeData()
						dataFuture = CompletableFuture.completedFuture(data)
						log.info("Reloaded data")
					}
				}
			}
			catch (Throwable t) {
				if (!stop) {
					log.error("Error in update loop", t)
					Thread.sleep(10_000)
				}
			}
		}
	}

	private XivApiSchemaVersion getLiveSchemaVersion() {
		return client.getById(BaseParam, 1).schemaVersion
	}

	private FullData makeData() {
		try {

			ListOptions<XivApiObject> opts = ListOptions.newBuilder().with {
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
					return []
				}
				else {
					return [new FoodImpl(it, itemFood)]
				}
			}.<Food> toSorted { it.rowId }
			log.info "Loaded ${food.size()} Foods"

			return new FullData(versions, baseParams, itemBases, itemLevels, jobs, materia, food)
		}
		catch (Throwable t) {
			log.error("Error getting data", t)
			throw t
		}
	}

	boolean isReady() {
		if (!dataFuture.isDone()) {
			return false
		}
		try {
			FullData result = dataFuture.get()
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
		updater.interrupt()
		client.close()
	}
}
