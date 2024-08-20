package gg.xp.xivgear.dataapi.models

import groovy.transform.TupleConstructor

@TupleConstructor(includeFields = true, defaults = false)
class ItemImpl implements Item {
//	@Delegate(interfaces = false, excludes = ['getBaseParam', 'getBaseParamSpecial', 'getBaseParamValue', 'getBaseParamValueSpecial'])
	@Delegate
	private final ItemBase base

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
		return out;
	};

	Map<Integer, Integer> getBaseParamMapHQ() {
		if (canBeHq) {
			Map<Integer, Integer> out = new HashMap(getBaseParamMap())
			base.baseParamSpecial.eachWithIndex { int entry, int i ->
				if (entry != 0) {
					int value = base.baseParamValueSpecial[i]
					if (value != 0) {
						out.compute(entry, { Integer k, Integer v ->
							if (k == null) {
								return value
							}
							else {
								return k + value
							}
						})
					}
				}
			}
			return out
		}
		else {
			return getBaseParamMap()
		}
	}

	@Override
	List<String> getClassJobs() {
		return base.classJobCategory.jobs
				.findAll { it.value }
				.collect { it.key }
	};


}
