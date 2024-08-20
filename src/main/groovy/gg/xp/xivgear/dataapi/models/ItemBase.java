package gg.xp.xivgear.dataapi.models;

import gg.xp.xivapi.annotations.XivApiField;
import gg.xp.xivapi.annotations.XivApiRaw;
import gg.xp.xivapi.annotations.XivApiSheet;
import gg.xp.xivapi.clienttypes.XivApiObject;

import java.util.List;

@XivApiSheet("Item")
public interface ItemBase extends XivApiObject {

	@XivApiRaw
	@XivApiField("LevelItem")
	int getIlvl();

	String getName();
	Icon getIcon();


	// TODO: need to be able to deserialize "this" to a method
	ClassJobCategory getClassJobCategory();
	EquipSlotCategory getEquipSlotCategory();


	// TODO: does raw work here?
	@XivApiRaw
	List<Integer> getBaseParam();
	@XivApiRaw
	List<Integer> getBaseParamSpecial();
	List<Integer> getBaseParamValue();
	List<Integer> getBaseParamValueSpecial();

	int getDamageMag();
	int getDamagePhys();
	@XivApiField("Delayms")
	int getDelayMs();

	int getMateriaSlotCount();

	@XivApiField("IsAdvancedMeldingPermitted")
	boolean isAdvancedMeldingPermitted();

	boolean isCanBeHq();

	@XivApiField("IsUnique")
	boolean isUnique();

	int getRarity();


}
