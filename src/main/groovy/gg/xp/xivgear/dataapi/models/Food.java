package gg.xp.xivgear.dataapi.models;

import java.util.Map;

public interface Food extends FoodItemBase {
	Map<Integer, FoodStatBonus> getBonuses();

	Map<Integer, FoodStatBonus> getBonusesHQ();
}
