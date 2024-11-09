package gg.xp.xivgear.dataapi.models;

import gg.xp.xivapi.annotations.XivApiField;
import gg.xp.xivapi.annotations.XivApiRaw;
import gg.xp.xivapi.clienttypes.XivApiLangValue;
import gg.xp.xivapi.clienttypes.XivApiObject;

public interface MateriaItem extends XivApiObject {

	String getName();

	@XivApiField("Name")
	XivApiLangValue<String> getNameTranslations();

	Icon getIcon();

	@XivApiRaw
	@XivApiField("LevelItem")
	int getIlvl();
}
