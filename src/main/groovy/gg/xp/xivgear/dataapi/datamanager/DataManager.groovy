package gg.xp.xivgear.dataapi.datamanager

import gg.xp.xivapi.XivApiClient
import gg.xp.xivapi.clienttypes.XivApiObject
import gg.xp.xivapi.filters.SearchFilter
import gg.xp.xivapi.filters.SearchFilters
import gg.xp.xivapi.pagination.ListOptions
import gg.xp.xivgear.dataapi.models.*
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.micronaut.context.annotation.Context
import jakarta.inject.Singleton

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

import static gg.xp.xivapi.filters.SearchFilters.*

@Context
@Singleton

@Slf4j
class DataManager {

	private static final ExecutorService exs = Executors.newVirtualThreadPerTaskExecutor()

	private final Future<FullData> dataFuture
	private final XivApiClient client

	private static final int minIlvl = 600
	private static final int maxIlvl = 999
	private static final int minIlvlFood = 600
	private static final int maxIlvlFood = 999


	DataManager() {
		client = new XivApiClient()
		dataFuture = exs.submit(this.&makeData as Callable<FullData>)
	}

	private FullData makeData() {
		try {

			ListOptions<XivApiObject> opts = ListOptions.newBuilder().with {
				perPage 500
				build()
			}
			client.defaultListOpts = opts

			log.info "Loading BaseParams"
			List<BaseParam> baseParams = client.getListIterator(BaseParam).toList()
			log.info "Loaded ${baseParams.size()} BaseParams"

			log.info "Loading ItemLevel"
			List<ItemLevel> itemLevels = client.getListIterator(ItemLevel).toList()
			log.info "Loaded ${itemLevels.size()} ItemLevels"

			log.info "Loading ClassJob"
			SearchFilter combatJobsOnly = isTrue("CanQueueForDuty")
			List<ClassJob> jobs = client.getSearchIterator(ClassJob, combatJobsOnly).toList()
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
			List<ItemBase> itemBases = client.getSearchIterator(ItemBase, itemFilter).toList()
			log.info "Loaded ${itemBases.size()} Items"

			log.info "Loading Materia"
			List<Materia> materia = client.getListIterator(Materia).toList()
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

			List<FoodItem> food = bases.collectMany {
				int bonusId = it.foodItemId
				FoodItemFood itemFood = foodBonuses[bonusId]
				if (itemFood == null) {
					log.error "Food item ${it} did not have corresponding ItemFood ID ${bonusId}"
				}
				else {
					return [new FoodItem(it, itemFood)]
				}
			}
			log.info "Loaded ${food.size()} Foods"

			return new FullData(baseParams, itemBases, itemLevels, jobs, materia, food)
		}
		catch (Throwable t) {
			log.error("Error getting data", t)
			throw t;
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
}
