package gg.xp.xivgear.dataapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gg.xp.xivapi.annotations.XivApiAssetPath;
import gg.xp.xivapi.annotations.XivApiField;
import gg.xp.xivapi.assets.ImageFormat;
import gg.xp.xivapi.clienttypes.XivApiAsset;
import gg.xp.xivapi.clienttypes.XivApiStruct;

import java.net.URI;
import java.net.URISyntaxException;

public interface Icon extends XivApiStruct {
	// TODO: remove these extra fields - they take up memory for no benefit
	@XivApiField("id")
	int getId();

	@XivApiField("path")
	String getPath();

	@XivApiField("path_hr1")
	String getPathHD();

	// For later - reduces mem

//	@JsonIgnore
//	@XivApiField("path_hr1")
//	XivApiAsset<ImageFormat> getAssetPathHD();
//
//	default URI getPngIconUrl() {
//		return getAssetPathHD().getURI(ImageFormat.PNG);
//	}

	default URI getPngIconUrl() {
		try {
			return new URI("https://beta.xivapi.com/api/1/asset/" + getPathHD() + "?format=png");
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
