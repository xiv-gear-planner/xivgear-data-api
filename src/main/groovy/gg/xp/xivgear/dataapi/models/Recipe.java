package gg.xp.xivgear.dataapi.models;

import gg.xp.xivapi.annotations.XivApiRaw;
import gg.xp.xivapi.annotations.XivApiSheet;
import gg.xp.xivapi.clienttypes.XivApiObject;

@XivApiSheet
public interface Recipe extends XivApiObject {
	@XivApiRaw
	int getItemResult();
}
