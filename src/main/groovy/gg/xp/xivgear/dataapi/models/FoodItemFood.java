package gg.xp.xivgear.dataapi.models;

import gg.xp.xivapi.annotations.XivApiRaw;
import gg.xp.xivapi.annotations.XivApiSheet;
import gg.xp.xivapi.clienttypes.XivApiObject;

@XivApiSheet("ItemFood")
public interface FoodItemFood extends XivApiObject {
	@XivApiRaw
	Integer[] getBaseParam();

	Integer[] getMax();
	Integer[] getMaxHQ();
	Integer[] getValue();
	Integer[] getValueHQ();
}
