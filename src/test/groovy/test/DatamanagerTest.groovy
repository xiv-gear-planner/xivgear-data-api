package test

import gg.xp.xivgear.dataapi.datamanager.DataManager
import gg.xp.xivgear.dataapi.datamanager.FullData
import gg.xp.xivgear.dataapi.models.GearAcquisitionSource
import gg.xp.xivgear.dataapi.models.Item
import gg.xp.xivgear.dataapi.models.ItemImpl
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@Slf4j
@CompileStatic
@MicronautTest
class DatamanagerTest {

	@Test
	void testDm(DataManager dm) {
//		DataManager dm = new DataManager()
		FullData fd = dm.getDataFuture().get()
		log.info "items size: ${fd.itemBases.size()}"
		log.info "baseparam size: ${fd.baseParams.size()}"

		Item archeoBroadSword = new ItemImpl(fd.itemBases.find { it.rowId == 42870 }, GearAcquisitionSource.Unknown)
		int str = 1
		int vit = 3
		int det = 44
		int crt = 27
		// https://na.finalfantasyxiv.com/lodestone/playguide/db/item/91f11c2f0cd/
		Assertions.assertEquals(354, archeoBroadSword.baseParamMap[str])
		Assertions.assertEquals(393, archeoBroadSword.baseParamMapHQ[str])

		Assertions.assertEquals(366, archeoBroadSword.baseParamMap[vit])
		Assertions.assertEquals(407, archeoBroadSword.baseParamMapHQ[vit])

		Assertions.assertEquals(166, archeoBroadSword.baseParamMap[crt])
		Assertions.assertEquals(185, archeoBroadSword.baseParamMapHQ[crt])

		Assertions.assertEquals(238, archeoBroadSword.baseParamMap[det])
		Assertions.assertEquals(264, archeoBroadSword.baseParamMapHQ[det])

		Assertions.assertEquals(127, archeoBroadSword.damagePhys)
		Assertions.assertEquals(64, archeoBroadSword.damageMag)

		Assertions.assertEquals(141, archeoBroadSword.damagePhysHQ)
		Assertions.assertEquals(71, archeoBroadSword.damageMagHQ)

		Item quetzalliCane = fd.items.find { it.rowId == 42952 }

		Assertions.assertEquals(GearAcquisitionSource.Tome, quetzalliCane.acquisitionSource)
	}

}
