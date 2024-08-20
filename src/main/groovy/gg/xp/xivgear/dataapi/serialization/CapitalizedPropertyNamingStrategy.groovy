package gg.xp.xivgear.dataapi.serialization

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import groovy.transform.CompileStatic


class CapitalizedPropertyNamingStrategy extends PropertyNamingStrategies.NamingBase {
	@Override
	String translate(String propertyName) {
		if (propertyName == null || propertyName.isEmpty()) {
			return propertyName
		}
		return propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1)
	}
}
