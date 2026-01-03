package gg.xp.xivgear.dataapi.endpoints

import gg.xp.xivgear.dataapi.datamanager.DataManager
import gg.xp.xivgear.dataapi.datamanager.FullData
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import io.micronaut.core.annotation.NonNull
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import jakarta.annotation.Nullable

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
 * @param <In>       A type that encompasses whatever necessary parameters.
 * @param <Out>      The response object type.
 */
@TupleConstructor(includeFields = true, defaults = false)
@CompileStatic
abstract class BaseDataEndpoint<In, Out> {

	protected final DataManager dm

	/**
	 * Turn a request into the content of the request. Will only be invoked if the DataManager is ready, and the
	 * client's last-modified header is not newer than the data.
	 *
	 * If you return a null from this method, then the endpoint will return a 404.
	 *
	 * @param data
	 * @param input
	 * @return
	 */
	protected abstract @Nullable Out getContent(FullData data, In input);

	protected HttpResponse<Out> makeResponse(@NonNull HttpRequest<?> request, In input) {
		// First check - is the DM ready?
		if (dm.ready) {
			FullData data = dm.dataFuture.get()
			// Check client If-Modified-Since header so that we don't send data that
			// has already been cached.
			String ifModifiedHeader = request.headers.get(HttpHeaders.IF_MODIFIED_SINCE)
			ZonedDateTime dataModified = data.timestamp.atZone(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
			if (ifModifiedHeader != null) {
				ZonedDateTime headerTimestamp = ZonedDateTime.parse(ifModifiedHeader, DateTimeFormatter.RFC_1123_DATE_TIME).truncatedTo(ChronoUnit.SECONDS)
				// If the header's value is non-null, and does not predate the DM timestamp, tell the client that they
				// already have the latest.
				if (!headerTimestamp.isBefore(dataModified)) {
					return HttpResponse.notModified()
				}
			}
			// Calls the abstract method to get the actual content for the response.
			// Note that this only happens if we have determined that we cannot send back a "not modified" response.
			Out content = getContent(data, input)
			if (content == null) {
				return HttpResponse.status(404, "Not found")
			}
			return HttpResponse.ok(content).with {
				// Add last modified header
				header HttpHeaders.LAST_MODIFIED, dataModified.format(DateTimeFormatter.RFC_1123_DATE_TIME)
				// TODO: add stale-if-error=3600*24 (or higher)
				header HttpHeaders.CACHE_CONTROL, "max-age=1200, public"
			}
		}
		else {
			return HttpResponse.status(503, "Not Ready")
		}
	}
}
