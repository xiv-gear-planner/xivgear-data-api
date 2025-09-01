package gg.xp.xivgear.dataapi.models

import groovy.transform.TupleConstructor
import io.micronaut.core.annotation.Nullable

/**
 * ItemImpl combines the 'Item' sheet row with the gear acquisition source, and also provides helper methods
 * to calculate the item's effective bonuses.
 */
@TupleConstructor(includeFields = true, defaults = false)
class ItemImpl implements Item {
	@Delegate
	private final ItemBase base

	private final GearAcquisitionSource acquisitionSource

	@Override
	Map<Integer, Integer> getBaseParamMap() {
		Map<Integer, Integer> out = [:]
		base.baseParam.eachWithIndex { int entry, int i ->
			if (entry != 0) {
				int value = base.baseParamValue[i]
				if (value != 0) {
					out[entry] = value
				}
			}
		}
		return out
	}

	@Override
	Map<Integer, Integer> getBaseParamMapHQ() {
		if (canBeHq) {
			return baseParamPlusSpecial
		}
		else {
			return baseParamMap
		}
	}

	@Override
	@Nullable
	Map<Integer, Integer> getBaseParamMapSpecial() {
		if (specialStatType == null) {
			return null
		}
		Map<Integer, Integer> out = [:]
		base.baseParamSpecial.eachWithIndex { int entry, int i ->
			if (entry != 0) {
				int value = base.baseParamValueSpecial[i]
				if (value != 0) {
					out[entry] = value
				}
			}
		}
		return out
	}

	/**
	 * @return A map of BaseParam id number to stat value, but with BaseParam and BaseParamSpecial summed together.
	 * i.e. represents the HQ stats of an item with HQ/NQ variants (HQ stats = BaseParam (NQ stats) + BaseParamSpecial).
	 * Also used for things like Occult Crescent items.
	 */
	private Map<Integer, Integer> getBaseParamPlusSpecial() {
		Map<Integer, Integer> out = new HashMap(baseParamMap)
		base.baseParamSpecial.eachWithIndex { int entry, int i ->
			if (entry != 0) {
				int value = base.baseParamValueSpecial[i]
				if (value != 0) {
					out.compute(entry, { Integer k, Integer v ->
						if (v == null) {
							return value
						}
						else {
							return v + value
						}
					})
				}
			}
		}
		return out
	}

	@Override
	List<String> getClassJobs() {
		return base.classJobCategory.jobs
				.findAll { it.value }
				.collect { it.key }
	}

	@Override
	int getDamageMagHQ() {
		return getDamageMag() + (getBaseParamMapHQ()[13] ?: 0)
	}

	@Override
	int getDamagePhysHQ() {
		return getDamagePhys() + (getBaseParamMapHQ()[12] ?: 0)
	}

	@Override
	int getDefenseMagHQ() {
		return getDefenseMag() + (getBaseParamMapHQ()[24] ?: 0)
	}

	@Override
	int getDefensePhysHQ() {
		return getDefensePhys() + (getBaseParamMapHQ()[21] ?: 0)
	}

	@Override
	GearAcquisitionSource getAcquisitionSource() {
		return acquisitionSource
	}

	@Nullable
	SpecialStatType getSpecialStatType() {
		// Occult Crescent items are i745 and have a BaseParamSpecial for BaseParam 54 "Special Attribute"
		if (ilvl == 745 && baseParamPlusSpecial.containsKey(54)) {
			return SpecialStatType.OccultCrescent
		}
		return null
	}
}
