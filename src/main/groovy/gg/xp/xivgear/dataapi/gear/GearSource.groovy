package gg.xp.xivgear.dataapi.gear

import gg.xp.xivgear.dataapi.datamanager.FullData
import gg.xp.xivgear.dataapi.models.GearAcquisitionSource
import gg.xp.xivgear.dataapi.models.ItemBase

import static gg.xp.xivgear.dataapi.models.GearAcquisitionSource.*

class GearSource {

	static GearAcquisitionSource getAcquisitionSource(FullData fd, ItemBase base) {
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
//						return Unknown
						boolean isCraftable = fd.itemsWithRecipes.contains(rowId) // TODO
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
							// TODO: do these AF weapons have a shop that we can use?
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
						// TODO: Aloalo has some weapons, doesn't it?
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
						// TODO
						return Unknown
						boolean hasShop = true
						if (hasShop) {
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
							// TODO
							return Unknown
							boolean hasShop = true
							if (hasShop) {
								// TODO: this logic is untested
								// old logic just put these as 'other'
								return NormalRaid
							}
							else {
								return ExtremeTrial
							}
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
