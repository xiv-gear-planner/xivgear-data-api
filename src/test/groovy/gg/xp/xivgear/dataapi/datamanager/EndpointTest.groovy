package gg.xp.xivgear.dataapi.datamanager

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.context.annotation.Property
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.junit.jupiter.api.function.Executable

@CompileStatic
@MicronautTest
@Property(name = "micronaut.http.client.max-content-length", value = "26214400")
class EndpointTest {

	@Singleton
	@Inject
	EmbeddedServer server

	@Inject
	@Client
	HttpClient client

	@Inject
	DataManager dm

	private static final class ItemResponseDummy {
		// Explicitly specify expected field name to test property naming
		@JsonProperty("items")
		List<ItemDummy> items
		@JsonProperty("maxIlvlPerLvl")
		int[] maxIlvlPerLvl
		@JsonProperty("maxIlvlPerLvlWpn")
		int[] maxIlvlPerLvlWpn
	}

	private static final class ItemDummy {
		// Explicitly specify expected field name to test property naming
		@JsonProperty("classJobs")
		List<String> classJobs
		@JsonProperty("rowId")
		int rowId
		@JsonProperty("ilvl")
		int ilvl
		@JsonProperty("equipLevel")
		int equipLevel
		@JsonProperty("equipSlotCategory")
		EquipSlotCategoryDummy equipSlotCategory
	}

	private static final class EquipSlotCategoryDummy {
		@JsonProperty("mainHand")
		int mainHand
	}

	@Test
	@Timeout(180)
	void testSingleJobItems() {
		dm.dataFuture.get()
		HttpRequest<?> req = HttpRequest.GET(server.URI.resolve("/Items?job=WHM"))
//		String singleJobResponseRaw = client.toBlocking().exchange(req, String).body()
		ItemResponseDummy singleJobResponse = client.toBlocking().exchange(req, ItemResponseDummy).body()
		Assertions.assertAll(
				"should only contain WHM items",
				singleJobResponse.items.collect { item ->
					return (Executable) { ->
						Assertions.assertTrue(item.classJobs.contains("WHM"))
					}
				}
		)
		Assertions.assertFalse singleJobResponse.items.empty
	}

	@Test
	@Timeout(180)
	void testMultiJobItems() {
		dm.dataFuture.get()
		HttpRequest<?> req = HttpRequest.GET(server.URI.resolve("/Items?job=WHM,PLD,MNK"))
		ItemResponseDummy multiJobResponse = client.toBlocking().exchange(req, ItemResponseDummy).body()
		Assertions.assertAll(
				"should only contain WHM/PLD/MNK items",
				multiJobResponse.items.collect { item ->
					return (Executable) { ->
						Assertions.assertTrue(
								item.classJobs.contains('WHM')
										|| item.classJobs.contains('PLD')
										|| item.classJobs.contains('MNK')
						)
					}
				}
		)
		Assertions.assertFalse multiJobResponse.items.empty
		// Make sure that every job has its items here
		Assertions.assertFalse multiJobResponse.items.findAll { it.classJobs.contains('WHM') }.empty
		Assertions.assertFalse multiJobResponse.items.findAll { it.classJobs.contains('PLD') }.empty
		Assertions.assertFalse multiJobResponse.items.findAll { it.classJobs.contains('MNK') }.empty
	}

	@Test
	@Timeout(180)
	void testSpecificItemIdsAreAndedWithJobFilter() {
		dm.dataFuture.get()
		HttpRequest<?> req = HttpRequest.GET(server.URI.resolve("/Items?job=WHM&itemIds=42952,34431"))
		ItemResponseDummy response = client.toBlocking().exchange(req, ItemResponseDummy).body()

		// 42952 is a WHM cane; 34431 is a casting chest that WHM cannot equip, therefore it should not be in the response. Item IDs do not override the job filter - items must match both criteria.
		Assertions.assertEquals([42952], response.items*.rowId)
	}

	@Test
	@Timeout(180)
	void testMaxIlvlArraysAreReturnedForTheMatchingItems() {
		dm.dataFuture.get()
		HttpRequest<?> req = HttpRequest.GET(server.URI.resolve("/Items?job=WHM"))
		ItemResponseDummy response = client.toBlocking().exchange(req, ItemResponseDummy).body()

		int[] expectedMaxIlvlPerLvl = new int[response.maxIlvlPerLvl.length]
		int[] expectedMaxIlvlPerLvlWpn = new int[response.maxIlvlPerLvlWpn.length]
		response.items.each { item ->
			if (item.equipSlotCategory.mainHand != 0) {
				expectedMaxIlvlPerLvlWpn[item.equipLevel] = Math.max(expectedMaxIlvlPerLvlWpn[item.equipLevel], item.ilvl)
			}
			else {
				expectedMaxIlvlPerLvl[item.equipLevel] = Math.max(expectedMaxIlvlPerLvl[item.equipLevel], item.ilvl)
			}
		}

		Assertions.assertArrayEquals(expectedMaxIlvlPerLvl, response.maxIlvlPerLvl)
		Assertions.assertArrayEquals(expectedMaxIlvlPerLvlWpn, response.maxIlvlPerLvlWpn)
		Assertions.assertTrue(response.maxIlvlPerLvl.any { int value -> value > 0 })
		Assertions.assertTrue(response.maxIlvlPerLvlWpn.any { int value -> value > 0 })
		// Test a few specific levels
		assert response.maxIlvlPerLvl[70] == 400
		assert response.maxIlvlPerLvlWpn[70] == 405
		assert response.maxIlvlPerLvl[80] == 530
		assert response.maxIlvlPerLvlWpn[80] == 535
		assert response.maxIlvlPerLvl[90] == 660
		assert response.maxIlvlPerLvlWpn[90] == 665
	}
}
