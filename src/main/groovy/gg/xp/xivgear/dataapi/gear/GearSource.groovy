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
					// For most gear items, this means it is crafted. However, things like pre-order earrings are an exception.
					boolean isCraftable = fd.itemsWithRecipes.contains(rowId)
					if (isCraftable) {
						return Crafted
					}
					return Other
				}
				case 2: {
					// Green item
					// This can be crafted, augmented crafted, or dungeon
					if (name.contains("Augmented")) {
						return AugCrafted
					}
					else if (name.contains("Ornate")) {
						// TODO: is this really "Crated"? Or should there be a separate category for "Ornate"?
						return Crafted
					}
					else {
						boolean isCraftable = fd.itemsWithRecipes.contains(rowId)
						if (isCraftable) {
							return Crafted
						}
						else {
							// Bozjan Earring
							if (rowId == 31393) {
								return FieldOperation
							}
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
						else if (ilvl == 375 && name.endsWith("Ultima")) {
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
							// Memoria Miseria Extreme
							if (ilvl == 480 && name.startsWith("Idealized")) {
								// TODO: Should this be "Relic" or "Extreme"?
								return ExtremeTrial
							}
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
						if (ilvl == 365 && name.startsWith("Empyrean")) {
							return DeepDungeon
						}
						else if (ilvl == 625 && name.startsWith("Enaretos")) {
							return DeepDungeon
						}
						else if (ilvl == 755 && name.startsWith("Sacramental")) {
							return DeepDungeon
						}
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
