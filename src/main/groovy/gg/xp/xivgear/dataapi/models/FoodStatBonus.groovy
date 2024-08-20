package gg.xp.xivgear.dataapi.models

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.TupleConstructor

@TupleConstructor(includeFields = true, defaults = false)
class FoodStatBonus {
	final int percentage
	final int max

	@JsonIgnore
	boolean isZero() {
		return percentage == 0 || max == 0
	}
}
