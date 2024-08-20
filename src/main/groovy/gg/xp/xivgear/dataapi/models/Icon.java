package gg.xp.xivgear.dataapi.models;

import gg.xp.xivapi.annotations.XivApiField;
import gg.xp.xivapi.clienttypes.XivApiStruct;

import java.net.URI;
import java.net.URISyntaxException;

public interface Icon extends XivApiStruct {
	@XivApiField("id")
	int getId();

	@XivApiField("path")
	String getPath();

	@XivApiField("path_hr1")
	String getPathHD();

	default URI getPngIconUrl() {
		try {
			return new URI("https://beta.xivapi.com/api/1/asset/" + getPath() + "?format=png");
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

}
