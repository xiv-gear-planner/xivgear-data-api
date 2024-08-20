package test

import gg.xp.xivgear.dataapi.datamanager.DataManager
import gg.xp.xivgear.dataapi.datamanager.FullData
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.junit.jupiter.api.Test


@Slf4j
class DatamanagerTest {

	@Test
	void testDm() {
		DataManager dm = new DataManager()
		FullData fd = dm.getDataFuture().get()
		log.info "items size: ${fd.itemBases.size()}"
		log.info "baseparam size: ${fd.baseParams.size()}"
	}

}
