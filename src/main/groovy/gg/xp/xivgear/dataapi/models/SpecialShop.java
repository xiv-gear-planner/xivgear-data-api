package gg.xp.xivgear.dataapi.models;

import gg.xp.xivapi.annotations.XivApiSheet;
import gg.xp.xivapi.clienttypes.XivApiObject;

import java.util.List;

@XivApiSheet
public interface SpecialShop extends XivApiObject {
	List<SpecialShopItem> getItem();
}
