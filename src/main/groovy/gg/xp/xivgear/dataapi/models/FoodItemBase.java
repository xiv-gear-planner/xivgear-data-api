package gg.xp.xivgear.dataapi.models;

import gg.xp.xivapi.annotations.XivApiField;
import gg.xp.xivapi.annotations.XivApiRaw;
import gg.xp.xivapi.annotations.XivApiSheet;
import gg.xp.xivapi.clienttypes.XivApiLangValue;
import gg.xp.xivapi.clienttypes.XivApiObject;

@XivApiSheet("Item")
public interface FoodItemBase extends XivApiObject {

	String getName();

	@XivApiField("Name")
	XivApiLangValue<String> getNameTranslations();

	Icon getIcon();

	@XivApiRaw
	int getLevelItem();

	FoodItemAction getItemAction();

	default int getFoodItemId() {
		return getItemAction().getData()[1];
	}
}
