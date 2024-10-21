package gg.xp.xivgear.dataapi.gear

import gg.xp.xivgear.dataapi.datamanager.FullData
import gg.xp.xivgear.dataapi.models.GearAcquisitionSource
import gg.xp.xivgear.dataapi.models.ItemBase
import groovy.transform.CompileStatic

import static gg.xp.xivgear.dataapi.models.GearAcquisitionSource.*

@CompileStatic
class GearSource {

	static GearAcquisitionSource getAcquisitionSource(FullData fd, ItemBase base, Set<String> allItemNames) {
		return base.with {
			switch (rarity) {
				case 1: {
					// Normal item
					// For gear items, this means it is crafted
					return Crafted
				}
				case 2: {
					// Green item
					// This can be crafted, augmented crafted, or dungeon
					if (name.contains("Augmented")) {
						return AugCrafted
					}
					else {
						boolean isCraftable = fd.itemsWithRecipes.contains(rowId)
						if (isCraftable) {
							return Crafted
						}
						else {
							return Dungeon
						}
					}
				}
				case 3: {
					// Blue item
					// This can be raid, tome, extreme, augmented tome, alliance raid, etc
					boolean isWeapon = equipSlotCategory.mainHand || equipSlotCategory.offHand
					int ilvl = ilvl
					// This is start-of-expac relics (e.g. Didact set)
					if (GearLevels.isArtifactLevel(ilvl)) {
						if (isWeapon) {
							// Ambiguous due to start-of-expac extreme trial weapons having the same level
							// TODO: these AF weapons probably have a shop that we can use.
							return Unknown
						}
						else {
							return Artifact
						}
					}
					// Start-of-expac tome gear (e.g. Ronkan)
					else if (GearLevels.isBasicTomeLevel(ilvl)) {
						return Tome
					}
					// Savage/Aug Tome
					else if (GearLevels.isSavageRaidLevel(ilvl)) {
						if (name.contains("Augmented")) {
							return AugTome
						}
						else {
							return SavageRaid
						}
					}
					// Savage/Ultimate weapon
					else if (GearLevels.isSavageRaidWeaponLevel(ilvl) && isWeapon) {
						if (name.contains("Ultimate")) {
							return Ultimate
						}
						else if (name.contains("Exquisite")) {
							return Criterion
						}
						else {
							return SavageRaid
						}
					}
					// Unaug tome, alliance raid
					else if (GearLevels.isUnAugmentedTomeLevel(ilvl)) {
						// Guess if it is tome or alliance by checking for the presence of an item
						// with the same name, prefixed with "Augmented".
						// The ideal way to do this would be to see if there is a shop that sells this
						// item, but SpecialShop is an atrocity and new xivapi can't deal with it very
						// well yet.
						if (allItemNames.contains("Augmented ${name}".toString())) {
							return Tome
						}
						else {
							return AllianceRaid
						}
					}
					// Normal raids, start-of-expac extreme accs
					else if (GearLevels.isNormalRaidLevel(ilvl)) {
						if (isWeapon) {
							return ExtremeTrial
						}
						else {
							// TODO: shops needed for this
							return Unknown
//							boolean hasShop = true
//							if (hasShop) {
//								// TODO: this logic is untested
//								// old logic just put these as 'other'
//								return NormalRaid
//							}
//							else {
//								return ExtremeTrial
//							}
						}
					}
					else if (GearLevels.isExTrialLevel(ilvl) && isWeapon) {
						return ExtremeTrial
					}
					break
				}
				case 4: {
					// Purple item
					// This is only relics
					return Relic
				}
			}
			// Fallback
			return Unknown
		}
	}
}
