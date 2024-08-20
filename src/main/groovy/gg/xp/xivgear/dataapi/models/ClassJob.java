package gg.xp.xivgear.dataapi.models;

import gg.xp.xivapi.annotations.XivApiSheet;
import gg.xp.xivapi.clienttypes.XivApiObject;

@XivApiSheet
public interface ClassJob extends XivApiObject {

	String getAbbreviation();
	int getModifierDexterity();
	int getModifierHitPoints();
	int getModifierIntelligence();
	int getModifierMind();
	int getModifierPiety();
	int getModifierStrength();
	int getModifierVitality();

}
