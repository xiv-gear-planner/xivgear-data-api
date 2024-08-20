package gg.xp.xivgear.dataapi.models;

import gg.xp.xivapi.annotations.XivApiRaw;
import gg.xp.xivapi.clienttypes.XivApiObject;

import java.util.List;

public interface Materia extends XivApiObject {

	List<MateriaItem> getItem();
	List<Integer> getValue();

	@XivApiRaw
	int getBaseParam();

}
