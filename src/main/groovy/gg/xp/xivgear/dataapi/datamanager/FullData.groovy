package gg.xp.xivgear.dataapi.datamanager

import gg.xp.xivapi.clienttypes.GameVersion
import gg.xp.xivapi.clienttypes.XivApiSchemaVersion
import gg.xp.xivgear.dataapi.gear.GearSource
import gg.xp.xivgear.dataapi.models.*
import groovy.transform.CompileStatic

import java.time.Instant

/**
 * FullData represents a full "data pack" from xivapi
 */
@CompileStatic
class FullData implements Serializable {

	// ALWAYS UPDATE THIS IF CHANGING THIS CLASS OR ANYTHING ELSE IN IT!
	// The persistence later avoids conflicts between concurrently-running versions by
	// using a different object storage key based on the serialVersionUID.
	// Also update that if any dependencies change in a way that breaks serialization.
	@Serial
	static final long serialVersionUID = 26

	final List<GameVersion> versions
	final List<BaseParam> baseParams
	final List<ItemBase> itemBases
	final List<ItemLevel> itemLevels
	final List<ClassJob> jobs
	final List<Materia> materia
	final List<Food> food
	final List<Food> medicines
	final Set<Integer> itemsWithRecipes
	final Instant timestamp = Instant.now()
	private transient int maxEquipLvl
	private transient List<Item> items

	FullData(List<GameVersion> versions,
			List<BaseParam> baseParams,
			List<ItemBase> itemBases,
			List<ItemLevel> itemLevels,
			List<ClassJob> jobs,
			List<Materia> materia,
			List<Food> food,
			List<Food> medicines,
			Set<Integer> itemsWithRecipes) {
		this.versions = Collections.unmodifiableList(versions)
		this.baseParams = Collections.unmodifiableList(baseParams)
		this.itemBases = Collections.unmodifiableList(itemBases)
		this.itemLevels = Collections.unmodifiableList(itemLevels)
		this.jobs = Collections.unmodifiableList(jobs)
		this.materia = Collections.unmodifiableList(materia)
		this.food = Collections.unmodifiableList(food)
		this.medicines = Collections.unmodifiableList(medicines)
		this.itemsWithRecipes = Collections.unmodifiableSet(itemsWithRecipes)
		finishItems()
	}

	/**
	 * This method is responsible for processing the raw item data from ItemBases into Items, since getAcquisitionS
	 */
	private void finishItems() {
		// Used to determine acq source for un-augmented tome items
		Set<String> itemNames = new HashSet<>()
		itemBases.each {
			itemNames.add it.name
		}
		int maxEqpLvl = 0
		List<Item> builtItems = itemBases.collect { base ->
			GearAcquisitionSource source = GearSource.getAcquisitionSource this, base, itemNames
			if (base.equipLevel > maxEqpLvl) {
				maxEqpLvl = base.equipLevel
			}
			return new ItemImpl(base, source) as Item
		}
		items = Collections.unmodifiableList(builtItems)
		this.maxEquipLvl = maxEqpLvl
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		// After deserialization, we need to initialize the transient field
		ois.defaultReadObject()
		finishItems()
	}

	XivApiSchemaVersion getSchemaVersion() {
		return baseParams[0].schemaVersion
	}

	List<Item> getItems() {
		return items
	}

	int getMaxEquipLvl() {
		return maxEquipLvl
	}

	FullDataHeader makeHeader() {
		return new FullDataHeader(timestamp)
	}
}
