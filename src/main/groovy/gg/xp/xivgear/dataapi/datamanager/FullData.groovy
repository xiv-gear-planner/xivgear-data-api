package gg.xp.xivgear.dataapi.datamanager

import gg.xp.xivgear.dataapi.models.*
import groovy.transform.TupleConstructor

@TupleConstructor(includeFields = true, defaults = false)
class FullData implements Serializable {

	@Serial
	static final long serialVersionUID = 1

	final List<BaseParam> baseParams
	final List<ItemBase> itemBases
	final List<ItemLevel> itemLevels
	final List<ClassJob> jobs
	final List<Materia> materia
	final List<FoodItem> food


}
