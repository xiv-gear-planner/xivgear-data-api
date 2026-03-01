package gg.xp.xivgear.dataapi.gear

enum Expansion {

	Stormblood(290, 310, 340),
	Shadowbringers(430, 440, 470),
	Endwalker(560, 570, 600),
	Dawntrail(690, 700, 730)

	/**
	 * The level of the basic artifact armor.
	 */
	final int artifactLevel
	/**
	 * The level of the basic (non-capped) tome gear.
	 */
	final int basicTomeLevel
	/**
	 * The level of raid tier items. Assumed to be 30 ilvls apart.
	 */
	final List<Integer> raidTierLevels

	private Expansion(int artifactLevel, int basicTomeLevel, int firstRaidTierLevel) {
		this.artifactLevel = artifactLevel
		this.basicTomeLevel = basicTomeLevel
		this.raidTierLevels = [firstRaidTierLevel, firstRaidTierLevel + 30, firstRaidTierLevel + 60].asImmutable()
	}


}