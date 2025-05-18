package gg.xp.xivgear.dataapi.models;

import gg.xp.xivapi.annotations.XivApiField;
import gg.xp.xivapi.annotations.XivApiSheet;
import gg.xp.xivapi.clienttypes.XivApiLangValue;
import gg.xp.xivapi.clienttypes.XivApiObject;

@XivApiSheet
public interface ClassJob extends XivApiObject {

	String getAbbreviation();

	@XivApiField("Abbreviation")
	XivApiLangValue<String> getAbbreviationTranslations();

	@XivApiField("Name")
	XivApiLangValue<String> getNameTranslations();

	int getModifierDexterity();
	int getModifierHitPoints();
	int getModifierIntelligence();
	int getModifierMind();
	int getModifierPiety();
	int getModifierStrength();
	int getModifierVitality();

}
