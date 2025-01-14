package gg.xp.xivgear.dataapi.datamanager

import gg.xp.xivgear.dataapi.models.GearAcquisitionSource
import gg.xp.xivgear.dataapi.models.Item
import gg.xp.xivgear.dataapi.persistence.DataPersistence
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.micronaut.objectstorage.ObjectStorageOperations
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@Slf4j
@CompileStatic
@MicronautTest
class DatamanagerTest {

	@Test
	void testDm(DataManager dm, DataPersistence pers, ObjectStorageOperations<?, ?, ?> storage) {
		log.info "Checking persistence"
		Assertions.assertNull pers.data
		log.info "Checking object storage"
		String storageKey = "stored-data-api-data-v" + FullData.serialVersionUID
		Assertions.assertNull storage.retrieve(storageKey).orElse(null)
		log.info "Max mem: ${Runtime.runtime.maxMemory()}"
//		DataManager dm = new DataManager()
		FullData fd = dm.getDataFuture().get()
		log.info "items size: ${fd.itemBases.size()}"
		log.info "baseparam size: ${fd.baseParams.size()}";
		//
		{
			Item archeoBroadSword = fd.items.find { it.rowId == 42870 }
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
		}

		{
			Item quetzalliCane = fd.items.find { it.rowId == 42952 }
			Assertions.assertEquals(GearAcquisitionSource.Tome, quetzalliCane.acquisitionSource)
		}

		// Now serialize and de-serialize
		byte[] dataSerial
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()
			 ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject fd
			oos.flush()
			dataSerial = baos.toByteArray()
			log.info "Serialized"
		}
		FullData deserialized
		try (ByteArrayInputStream bais = new ByteArrayInputStream(dataSerial)
			 ObjectInputStream ois = new ObjectInputStream(bais)) {
			deserialized = (FullData) ois.readObject()
			log.info "Deserialized"
		}

		{
			Item archeoBroadSword = deserialized.items.find { it.rowId == 42870 }
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
		}

		{
			Item quetzalliCane = deserialized.items.find { it.rowId == 42952 }
			Assertions.assertEquals(GearAcquisitionSource.Tome, quetzalliCane.acquisitionSource)
		}

		log.info "Checking persistence"
		Assertions.assertNotNull pers.data
		log.info "Checking object storage"
		Assertions.assertNotNull storage.retrieve(storageKey).orElse(null)
		log.info "Done"

		// New instance that only uses persisted data
		var dp = new DataPersistence(storage)
		var fromPers = dp.data;

		{
			Item archeoBroadSword = fromPers.items.find { it.rowId == 42870 }
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
		}

		{
			Item quetzalliCane = fromPers.items.find { it.rowId == 42952 }
			Assertions.assertEquals(GearAcquisitionSource.Tome, quetzalliCane.acquisitionSource)
		}


	}
}
