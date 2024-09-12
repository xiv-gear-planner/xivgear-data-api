package gg.xp.xivgear.dataapi.datamanager

import gg.xp.xivapi.clienttypes.XivApiSchemaVersion
import gg.xp.xivgear.dataapi.gear.GearSource
import gg.xp.xivgear.dataapi.models.*
import groovy.transform.CompileStatic
import groovy.transform.DefaultsMode
import groovy.transform.TupleConstructor

import java.time.Instant

@CompileStatic
@TupleConstructor(includeFields = true, defaultsMode = DefaultsMode.AUTO, post = {
	this.finishItems()
}, excludes = ['timestamp', 'items'])
class FullData implements Serializable {

	// ALWAYS UPDATE THIS IF CHANGING THIS CLASS OR ANYTHING ELSE IN IT
	// The persistence later avoids conflicts between concurrently-running versions by
	// using a different object storage key based on the serialVersionUID
	@Serial
	static final long serialVersionUID = 7

	final List<String> versions
	final List<BaseParam> baseParams
	final List<ItemBase> itemBases
	final List<ItemLevel> itemLevels
	final List<ClassJob> jobs
	final List<Materia> materia
	final List<Food> food
	final Set<Integer> itemsWithRecipes
	final Instant timestamp = Instant.now()
	transient List<Item> items

	void finishItems() {
		items = itemBases.collect { base ->
			GearAcquisitionSource source = GearSource.getAcquisitionSource(this, base)
			return new ItemImpl(base, source) as Item
		}
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject()
		finishItems()
	}

	XivApiSchemaVersion getSchemaVersion() {
		return baseParams[0].schemaVersion
	}

	@Override
	Object getProperty(String propertyName) {
		Object out = super.getProperty(propertyName)
		if (out instanceof List) {
			return Collections.unmodifiableList(out)
		}
		return out
	}
}
