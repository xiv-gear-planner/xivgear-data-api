package gg.xp.xivgear.dataapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gg.xp.xivapi.annotations.XivApiMapKeys;
import gg.xp.xivapi.annotations.XivApiSheet;
import gg.xp.xivapi.annotations.XivApiThis;
import gg.xp.xivapi.clienttypes.XivApiObject;

import java.util.HashMap;
import java.util.Map;

@XivApiSheet("ClassJobCategory")
public interface ClassJobCategory extends XivApiObject {
	// Should filter out "Name" field
	@XivApiThis
	@XivApiMapKeys("[A-Z]{3}")
	@JsonIgnore
	Map<String, Boolean> getJobsInternal();

	String getName();

	@JsonIgnore
	boolean getUnknown0();

	// Temporary hack
	default Map<String, Boolean> getJobs() {
		Map<String, Boolean> jobsOriginal = getJobsInternal();
		if (jobsOriginal.containsKey("BST")) {
			return jobsOriginal;
		}
		else {
			Map<String, Boolean> jobs = new HashMap<>(jobsOriginal);
			jobs.put("BST", getUnknown0());
			return jobs;
		}
	}
}
