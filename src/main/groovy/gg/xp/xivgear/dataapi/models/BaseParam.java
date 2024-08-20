package gg.xp.xivgear.dataapi.models;

import gg.xp.xivapi.annotations.XivApiSheet;
import gg.xp.xivapi.clienttypes.XivApiObject;

@XivApiSheet
public interface BaseParam extends XivApiObject {

	String getName();

	int getTwoHandWeaponPercent();
	int getOneHandWeaponPercent();
	int getOffHandPercent();

	int getHeadPercent();
	int getChestPercent();
	int getHandsPercent();
	int getLegsPercent();
	int getFeetPercent();

	int getEarringPercent();
	int getNecklacePercent();
	int getBraceletPercent();
	int getRingPercent();


}
