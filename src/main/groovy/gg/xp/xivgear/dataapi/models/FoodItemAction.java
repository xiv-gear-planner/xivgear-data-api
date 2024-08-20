package gg.xp.xivgear.dataapi.models;

import gg.xp.xivapi.annotations.XivApiSheet;
import gg.xp.xivapi.clienttypes.XivApiObject;

@XivApiSheet("ItemAction")
public interface FoodItemAction extends XivApiObject {

	Integer[] getData();
	// DataHQ seems to be unnecessary since the action itself is the same

}
