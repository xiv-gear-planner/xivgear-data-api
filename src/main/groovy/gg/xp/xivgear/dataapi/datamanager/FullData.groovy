package gg.xp.xivgear.dataapi.datamanager

import gg.xp.xivapi.clienttypes.XivApiSchemaVersion
import gg.xp.xivgear.dataapi.models.*
import groovy.transform.TupleConstructor

@TupleConstructor(includeFields = true, defaults = false)
class FullData implements Serializable {

	@Serial
	static final long serialVersionUID = 1

	final List<String> versions
	final List<BaseParam> baseParams
	final List<ItemBase> itemBases
	final List<ItemLevel> itemLevels
	final List<ClassJob> jobs
	final List<Materia> materia
	final List<Food> food

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
