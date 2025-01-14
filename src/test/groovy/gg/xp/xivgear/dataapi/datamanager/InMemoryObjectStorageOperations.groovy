package gg.xp.xivgear.dataapi.datamanager

import groovy.transform.CompileStatic
import io.micronaut.context.annotation.Replaces
import io.micronaut.core.annotation.NonNull
import io.micronaut.objectstorage.ObjectStorageEntry
import io.micronaut.objectstorage.ObjectStorageOperations
import io.micronaut.objectstorage.request.UploadRequest
import io.micronaut.objectstorage.response.UploadResponse
import jakarta.inject.Singleton

import java.util.function.Consumer

@Singleton
@Replaces(ObjectStorageOperations.class)
@CompileStatic
class InMemoryObjectStorageOperations implements ObjectStorageOperations<Object, Object, Object> {
	private final Map<String, byte[]> storage = [:]

	@Override
	UploadResponse<Object> upload(@NonNull UploadRequest request) {
		storage[request.key] = request.inputStream.readAllBytes()
		return null
	}

	@Override
	UploadResponse<Object> upload(@NonNull UploadRequest request, @NonNull Consumer<Object> requestConsumer) {
		throw new UnsupportedOperationException()
	}

	@Override
	<E extends ObjectStorageEntry<?>> Optional<E> retrieve(@NonNull String key) {
		byte[] bytes = storage[key]
		if (bytes == null) {
			return Optional.empty()
		}
		return Optional.of(new ObjectStorageEntry<Object>() {

			@Override
			String getKey() {
				return key
			}

			@Override
			InputStream getInputStream() {
				return new ByteArrayInputStream(bytes)
			}

			@Override
			Object getNativeEntry() {
				return null
			}
		} as E)

	}

	@Override
	Object delete(@NonNull String key) {
		storage.remove(key)
		return null
	}
}
