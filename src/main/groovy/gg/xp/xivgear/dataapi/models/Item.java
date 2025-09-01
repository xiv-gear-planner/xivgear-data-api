package gg.xp.xivgear.dataapi.models;

import io.micronaut.core.annotation.Nullable;

import java.util.List;
import java.util.Map;

public interface Item extends ItemBase {
	/**
	 * @return The map of BaseParam id number to the value. For items without NQ/HQ variants, this returns
	 * the same as {@link #getBaseParamMapHQ()}. For items with NQ/HQ, this returns the NQ values.
	 */
	Map<Integer, Integer> getBaseParamMap();

	/**
	 * @return The map of BaseParam id number to the value. For items without NQ/HQ variants, this returns
	 * the same as {@link #getBaseParamMap()}. For items with NQ/HQ, this returns the HQ values.
	 */
	Map<Integer, Integer> getBaseParamMapHQ();

	/**
	 * @return The special stat values for items such as Occult Crescent gear. Not used for simple HQ/NQ
	 * differentiation - use {@link #getBaseParamMap()} and {@link #getBaseParamMapHQ()} for that.
	 */
	@Nullable
	Map<Integer, Integer> getBaseParamMapSpecial();

	/**
	 * @return What type of special stats are indicated by {@link #getBaseParamMapSpecial()}, if any, otherwise null.
	 */
	@Nullable
	SpecialStatType getSpecialStatType();

	/**
	 * @return The list of jobs that may equip this item.
	 */
	List<String> getClassJobs();

	/**
	 * @return Magic weapon damage for the HQ variant, if it exists. If it does not exist,
	 * returns the same as {@link #getDamageMag()}. Returns 0 if not a weapon.
	 */
	int getDamageMagHQ();

	/**
	 * @return Physical weapon damage for the HQ variant, if it exists. If it does not exist,
	 * returns the same as {@link #getDamageMag()}. Returns 0 if not a weapon.
	 */
	int getDamagePhysHQ();

	/**
	 * @return The (probable) acquisition source for this gear. Accuracy not guaranteed, as doing this
	 * correctly requires SpecialShop.
	 */
	GearAcquisitionSource getAcquisitionSource();

	/**
	 * @return Magic Defense for the HQ variant, if it exists. If it does not exist,
	 * returns the same as {@link #getDefenseMag()}.
	 */
	int getDefenseMagHQ();

	/**
	 * @return Physical Defense (just "Defense" in-game) for the HQ variant, if it exists. If it does not exist,
	 * returns the same as {@link #getDefensePhys()} ()}.
	 */
	int getDefensePhysHQ();
}
