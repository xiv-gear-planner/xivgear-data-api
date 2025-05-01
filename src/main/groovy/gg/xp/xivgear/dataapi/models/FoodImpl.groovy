package gg.xp.xivgear.dataapi.models


import groovy.transform.TupleConstructor

/**
 * FoodImpl combines an 'Item' and its corresponding 'ItemFood' row into a single view which includes
 * the food bonuses.
 */
@TupleConstructor(includeFields = true, defaults = false)
class FoodImpl implements Food {

	@Delegate
	private final FoodItemBase base

	private final FoodItemFood foodData

	/**
	 * Map from BaseParam ID to food bonus amount
	 */
	Map<Integer, FoodStatBonus> getBonuses() {
		Map<Integer, FoodStatBonus> out = [:]
		foodData.baseParam.eachWithIndex { int entry, int i ->
			out[entry] = new FoodStatBonus(foodData.value[i], foodData.max[i])
		}
		return out
	}

	Map<Integer, FoodStatBonus> getBonusesHQ() {
		Map<Integer, FoodStatBonus> out = [:]
		foodData.baseParam.eachWithIndex { int entry, int i ->
			out[entry] = new FoodStatBonus(foodData.valueHQ[i], foodData.maxHQ[i])
		}
		return out
	}
}
