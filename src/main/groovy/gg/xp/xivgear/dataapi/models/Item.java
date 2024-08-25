package gg.xp.xivgear.dataapi.models;

import java.util.List;
import java.util.Map;

public interface Item extends ItemBase {
	Map<Integer, Integer> getBaseParamMap();

	Map<Integer, Integer> getBaseParamMapHQ();

	List<String> getClassJobs();

	int getDamageMagHQ();

	int getDamagePhysHQ();
}
