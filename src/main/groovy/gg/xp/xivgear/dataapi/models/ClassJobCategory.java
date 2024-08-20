package gg.xp.xivgear.dataapi.models;

import gg.xp.xivapi.annotations.XivApiMapKeys;
import gg.xp.xivapi.annotations.XivApiSheet;
import gg.xp.xivapi.annotations.XivApiThis;
import gg.xp.xivapi.clienttypes.XivApiObject;

import java.util.Map;

@XivApiSheet("ClassJobCategory")
public interface ClassJobCategory extends XivApiObject {
	// Should filter out "Name" field
	@XivApiThis
	@XivApiMapKeys("[A-Z]{3}")
	Map<String, Boolean> getJobs();
}
