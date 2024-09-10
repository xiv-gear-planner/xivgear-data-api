package gg.xp.xivgear.dataapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

	// Ignored because of another method providing this
	@JsonIgnore
	ClassJobCategory getClassJobCategory();

	EquipSlotCategory getEquipSlotCategory();

	// These are ignored because there is another method that provides these as a map
	@JsonIgnore
	@XivApiRaw
	List<Integer> getBaseParam();

	@XivApiRaw
	@JsonIgnore
	List<Integer> getBaseParamSpecial();

	@JsonIgnore
	List<Integer> getBaseParamValue();

	@JsonIgnore
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
