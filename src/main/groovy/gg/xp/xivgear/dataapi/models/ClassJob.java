package gg.xp.xivgear.dataapi.models;

import gg.xp.xivapi.annotations.XivApiField;
import gg.xp.xivapi.annotations.XivApiSheet;
import gg.xp.xivapi.clienttypes.XivApiLangValue;
import gg.xp.xivapi.clienttypes.XivApiObject;
import io.swagger.v3.oas.annotations.media.Schema;

@XivApiSheet
public interface ClassJob extends XivApiObject {

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	String getAbbreviation();

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	@XivApiField("Abbreviation")
	XivApiLangValue<String> getAbbreviationTranslations();

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	@XivApiField("Name")
	XivApiLangValue<String> getNameTranslations();

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	int getModifierDexterity();
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	int getModifierHitPoints();
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	int getModifierIntelligence();
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	int getModifierMind();
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	int getModifierPiety();
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	int getModifierStrength();
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	int getModifierVitality();

}
