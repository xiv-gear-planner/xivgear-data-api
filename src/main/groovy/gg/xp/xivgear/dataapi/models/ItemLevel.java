package gg.xp.xivgear.dataapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import gg.xp.xivapi.annotations.XivApiThis;
import gg.xp.xivapi.clienttypes.XivApiObject;

import java.util.Map;

public interface ItemLevel extends XivApiObject {

//	int getAdditionalEffect();
//	int getAttackMagicPotency();
//	int getAttackPower();
//	int getAttackSpeed();
//	int getBindResistance();
//	int getBlindResistance();
//	int getBlockRate();
//	int getBlockStrength();
//	int getBluntResistance();
//	int getCP();
//	int getCarefulDesynthesis();
//	int getControl();
//	int getCraftsmanship();
	int getCriticalHit();
//	int getCriticalHitEvasion();
//	int getCriticalHitPower();
//	int getCriticalHitResilience();
	int getDefense();
	int getDelay();
	int getDetermination();
	int getDexterity();
	int getDirectHitRate();
//	int getDoomResistance();
//	int getEXPBonus();
//	int getEarthResistance();
//	int getEnfeeblingMagicPotency();
//	int getEnhancementMagicPotency();
//	int getEnmity();
//	int getEnmityReduction();
//	int getEvasion();
//	int getFireResistance();
//	int getGP();
//	int getGathering();
	@JsonProperty("HP")
	int getHP();
//	int getHaste();
//	int getHealingMagicPotency();
//	int getHeavyResistance();
//	int getIceResistance();
//	int getIncreasedSpiritbondGain();
	int getIntelligence();
//	int getLightningResistance();
//	int getMP();
	int getMagicDefense();
//	int getMagicResistance();
	int getMagicalDamage();
	int getMind();
//	int getMorale();
//	int getMovementSpeed();
//	int getParalysisResistance();
//	int getPerception();
//	int getPetrificationResistance();
	int getPhysicalDamage();
//	int getPiercingResistance();
	int getPiety();
//	int getPoisonResistance();
//	int getProjectileResistance();
//	int getReducedDurabilityLoss();
//	int getRefresh();
//	int getRegen();
//	int getSilenceResistance();
	int getSkillSpeed();
//	int getSlashingResistance();
//	int getSleepResistance();
//	int getSlowResistance();
	int getSpellSpeed();
//	int getSpikes();
	int getStrength();
//	int getStunResistance();
//	int getTP();
	int getTenacity();
//	int getUnknown0();
	int getVitality();
//	int getWaterResistance();
//	int getWindResistance();
}
