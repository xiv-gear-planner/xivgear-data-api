package gg.xp.xivgear.dataapi.gear

/**
 * Utility class for information about specific levels of gear.
 */
final class GearLevels {

	private GearLevels() {
	}

	/**
	 * Check whether an ilvl matches the basic start-of-expac artifact level
	 *
	 * @param ilvl
	 * @return
	 */
	static boolean isArtifactLevel(int ilvl) {
		return Expansion.values().any {
			it.artifactLevel == ilvl
		}
	}

	/**
	 * Check whether an ilvl matches the basic start-of-expac tome ilvl (e.g. Ronkan)
	 *
	 * @param ilvl
	 * @return
	 */
	static boolean isBasicTomeLevel(int ilvl) {
		return Expansion.values().any {
			it.basicTomeLevel == ilvl
		}
	}

	private static boolean checkRelativeToRaid(int ilvl, int offset) {
		return Expansion.values().any {
			it.raidTierLevels.any { raidIlvl ->
				raidIlvl + offset == ilvl
			}
		}
	}

	static boolean isSavageRaidLevel(int ilvl) {
		return checkRelativeToRaid(ilvl, 0)
	}

	static boolean isSavageRaidWeaponLevel(int ilvl) {
		return checkRelativeToRaid(ilvl, 5)
	}

	static boolean isNormalRaidLevel(int ilvl) {
		return checkRelativeToRaid(ilvl, -20)
	}

	static boolean isUnAugmentedTomeLevel(int ilvl) {
		return checkRelativeToRaid(ilvl, -10)
	}

	/**
	 * Check whether an ilvl matches the non-start-of-expac extreme trial ilvls
	 *
	 * @param ilvl
	 * @return
	 */
	static boolean isExTrialLevel(int ilvl) {
		return checkRelativeToRaid(ilvl, -5) || checkRelativeToRaid(ilvl, -15)
	}
}
