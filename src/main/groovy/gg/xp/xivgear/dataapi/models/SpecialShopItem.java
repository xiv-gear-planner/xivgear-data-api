package gg.xp.xivgear.dataapi.models;

import gg.xp.xivapi.annotations.XivApiRaw;
import gg.xp.xivapi.clienttypes.XivApiStruct;

import java.util.List;

public interface SpecialShopItem extends XivApiStruct {
	@XivApiRaw
	List<Integer> getItem();
}
