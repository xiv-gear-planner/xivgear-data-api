package gg.xp.xivgear.dataapi.datamanager

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
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
		@JsonProperty
		List<ItemDummy> items
	}

	private static final class ItemDummy {
		@JsonProperty
		List<String> classJobs
	}

	@Test
	@Timeout(120)
	void testSingleJobItems() {
		dm.dataFuture.get()
		HttpRequest<?> req = HttpRequest.GET(server.URL.toString() + "/Items?job=WHM")
		String singleJobResponseRaw = client.toBlocking().exchange(req, String).body()
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
	@Timeout(120)
	void testMultiJobItems() {
		dm.dataFuture.get()
		HttpRequest<?> req = HttpRequest.GET(server.URI.resolve("/Items?job=WHM,PLD,MNK"))
		ItemResponseDummy multiJobResponse = client.toBlocking().exchange(req, ItemResponseDummy).body()
		Assertions.assertAll(
				"should only contain WHM items",
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
}
