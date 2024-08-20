package gg.xp.xivgear.dataapi.models;

import gg.xp.xivapi.annotations.XivApiThis;
import gg.xp.xivapi.clienttypes.XivApiObject;

import java.util.Map;

public interface ItemLevel extends XivApiObject {

	@XivApiThis
	Map<String, Object> getData();

}
