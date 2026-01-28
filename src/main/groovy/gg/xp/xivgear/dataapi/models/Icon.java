package gg.xp.xivgear.dataapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gg.xp.xivapi.annotations.XivApiField;
import gg.xp.xivapi.assets.ImageFormat;
import gg.xp.xivapi.clienttypes.XivApiAsset;
import gg.xp.xivapi.clienttypes.XivApiStruct;

import java.net.URI;

public interface Icon extends XivApiStruct {

	@JsonIgnore
	@XivApiField("path_hr1")
	XivApiAsset<ImageFormat> getAssetPathHD();

	@Deprecated
	default URI getPngIconUrl() {
		return getAssetPathHD().getURI(ImageFormat.PNG);
	}

	default URI getUrl() {
		return getAssetPathHD().getURI(ImageFormat.WEBP);
	}

}
