package gg.xp.xivgear.dataapi.endpoints


import gg.xp.xivgear.dataapi.datamanager.DataManager
import gg.xp.xivgear.dataapi.datamanager.FullData
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import io.micronaut.core.annotation.NonNull
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.jackson.codec.JsonMediaTypeCodec
import jakarta.inject.Inject

import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Base class for XIV data endpoints.
 *
 * This handles:
 * <ul>
 *     <li>Returning 503 Not Ready when the {@link DataManager} does not have data yet.</li>
 *     <li>Attaching cache control and last-modified headers.</li>
 *     <li>Returning 304 Not Modified when the client already has the newest data.</li>
 * </ul>
 *
 * @param <In>    A type that encompasses whatever necessary parameters.
 * @param <Out>   The response object type.
 */
@TupleConstructor(includeFields = true, defaults = false)
@CompileStatic
abstract class BaseDataEndpoint<In, Out> {

	protected final DataManager dm

	/**
	 * Turn a request into the content of the request. Will only be invoked if the DataManager is ready, and the
	 * client's last-modified header is not newer than the data.
	 *
	 * @param data
	 * @param input
	 * @return
	 */
	protected abstract Out getContent(FullData data, In input);

	protected HttpResponse<Out> makeResponse(@NonNull HttpRequest<?> request, In input) {
		if (dm.ready) {
			FullData data = dm.dataFuture.get()
			String ifModifiedHeader = request.headers.get(HttpHeaders.IF_MODIFIED_SINCE)
			ZonedDateTime dataModified = data.timestamp.atZone(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
			if (ifModifiedHeader != null) {
				ZonedDateTime headerTimestamp = ZonedDateTime.parse(ifModifiedHeader, DateTimeFormatter.RFC_1123_DATE_TIME).truncatedTo(ChronoUnit.SECONDS)
				if (!headerTimestamp.isBefore(dataModified)) {
					return HttpResponse.notModified()
				}
			}
			Out content = getContent(data, input)
			// TODO: decide appropriate cache duration
			return HttpResponse.ok(content).with {
				header HttpHeaders.LAST_MODIFIED, dataModified.format(DateTimeFormatter.RFC_1123_DATE_TIME)
				// TODO: add stale-if-error=3600*24 (or higher)
				header HttpHeaders.CACHE_CONTROL, "max-age=300, public"
			}
		}
		else {
			return HttpResponse.status(503, "Not Ready")
		}
	}
}
