package gg.xp.xivgear.dataapi.models;

import gg.xp.xivapi.clienttypes.XivApiObject;

public interface EquipSlotCategory extends XivApiObject {
	int getMainHand();
	int getOffHand();

	int getHead();
	int getBody();
	int getGloves();
	int getLegs();
	int getFeet();

	int getEars();
	int getNeck();
	int getWrists();
	int getFingerL();
	int getFingerR();
}
