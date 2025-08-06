package gg.xp.xivgear.dataapi.models;

import gg.xp.xivapi.annotations.XivApiSheet;
import gg.xp.xivapi.clienttypes.XivApiObject;
import io.swagger.v3.oas.annotations.media.Schema;

@XivApiSheet
public interface BaseParam extends XivApiObject {

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	String getName();

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	int getTwoHandWeaponPercent();

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	int getOneHandWeaponPercent();

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	int getOffHandPercent();

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	int getHeadPercent();

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	int getChestPercent();

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	int getHandsPercent();

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	int getLegsPercent();

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	int getFeetPercent();

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	int getEarringPercent();

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	int getNecklacePercent();

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	int getBraceletPercent();

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	int getRingPercent();


}
