package gg.xp.xivgear.dataapi.persistence

import gg.xp.xivgear.dataapi.datamanager.FullData
import groovy.util.logging.Slf4j
import io.micronaut.core.annotation.Nullable
import io.micronaut.objectstorage.ObjectStorageEntry
import io.micronaut.objectstorage.ObjectStorageOperations
import io.micronaut.objectstorage.request.UploadRequest
import jakarta.inject.Singleton

@Slf4j
@Singleton
class DataPersistence {

	private static final String key = "stored-data-api-data-v" + FullData.serialVersionUID
	private final ObjectStorageOperations<?, ?, ?> storage

	DataPersistence(ObjectStorageOperations<?, ?, ?> storage) {
		this.storage = storage
	}

	@Nullable
	FullData getData() {
		Optional<ObjectStorageEntry<?>> raw = storage.retrieve key
		if (raw.present) {
			log.info "Stored data present"
			ObjectStorageEntry<?> entry = raw.get()
			ObjectInputStream stream = new ObjectInputStream(entry.inputStream)
			def object = stream.readObject()
			if (object instanceof FullData) {
				log.info "Stored data successfully deserialized"
				return object
			}
			else {
				log.info "Deserialization failed! Wrong type: ${object}"
			}
		}
		else {
			log.info "Stored data not present"
		}
		return null
	}

	void setData(FullData data) {
		if (data == null) {
			storage.delete key
		}
		else {
			byte[] bytes
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()
				 ObjectOutputStream oos = new ObjectOutputStream(baos)) {
				oos.writeObject(data)
				bytes = baos.toByteArray()
			}
			storage.upload UploadRequest.fromBytes(bytes, key)
		}
	}


}
