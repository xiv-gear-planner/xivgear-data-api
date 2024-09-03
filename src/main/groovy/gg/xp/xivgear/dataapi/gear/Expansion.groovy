package gg.xp.xivgear.dataapi.gear

enum Expansion {

	Stormblood(290, 310, 340),
	Shadowbringers(430, 440, 470),
	Endwalker(560, 570, 600),
	Dawntrail(690, 700, 730)

	final int artifactLevel
	final int basicTomeLevel
	final List<Integer> raidTierLevels

	private Expansion(int artifactLevel, int basicTomeLevel, int firstRaidTierLevel) {
		this.artifactLevel = artifactLevel
		this.basicTomeLevel = basicTomeLevel
		this.raidTierLevels = [firstRaidTierLevel, firstRaidTierLevel + 30, firstRaidTierLevel + 60].asImmutable()
	}


}