package gg.xp.xivgear.dataapi.models

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.TupleConstructor
import io.micronaut.core.annotation.NonNull

@TupleConstructor(includeFields = true, defaults = false)
class FoodStatBonus {
	@NonNull
	final int percentage
	@NonNull
	final int max

	@JsonIgnore
	boolean isZero() {
		return percentage == 0 || max == 0
	}
}
