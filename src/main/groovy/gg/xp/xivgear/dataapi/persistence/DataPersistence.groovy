package gg.xp.xivgear.dataapi.persistence

import gg.xp.xivgear.dataapi.datamanager.FullData
import gg.xp.xivgear.dataapi.datamanager.FullDataHeader
import groovy.util.logging.Slf4j
import io.micronaut.core.annotation.Nullable
import io.micronaut.objectstorage.ObjectStorageEntry
import io.micronaut.objectstorage.ObjectStorageOperations
import io.micronaut.objectstorage.request.UploadRequest
import jakarta.inject.Singleton

import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * DataPersistence is responsible for storing and retrieving data packs into object storage.
 */
@Slf4j
@Singleton
class DataPersistence {

	private static final String key = "stored-data-api-data-v" + FullData.serialVersionUID
	private final ObjectStorageOperations<?, ?, ?> storage

	DataPersistence(ObjectStorageOperations<?, ?, ?> storage) {
		this.storage = storage
	}

	/**
	 * Get new data but only if it is newer than
	 * @param inMemoryHeader
	 * @return
	 */
	@Nullable
	FullData getDataIfNewer(@Nullable FullDataHeader inMemoryHeader) {
		Optional<ObjectStorageEntry<?>> raw = storage.retrieve key
		if (raw.present) {
			log.info "Stored data present"
			ObjectStorageEntry<?> entry = raw.get()
			try (
					GZIPInputStream gzipIn = new GZIPInputStream(entry.inputStream)
					ObjectInputStream stream = new ObjectInputStream(gzipIn)) {
				def storedObjectHeader = stream.readObject()
				if (!(storedObjectHeader instanceof FullDataHeader)) {
					log.info "Deserialization failed! Wrong header type: ${storedObjectHeader}"
					return null
				}
				if (inMemoryHeader != null && !inMemoryHeader.timestamp().isBefore(storedObjectHeader.timestamp())) {
					log.info "Stored data is not newer (In-Memory: ${inMemoryHeader.timestamp()}; Stored Object: ${storedObjectHeader.timestamp()})"
					return null
				}
				def storedData = stream.readObject()
				if (storedData instanceof FullData) {
					log.info "Stored data successfully deserialized"
					return storedData
				}
				else {
					log.info "Deserialization failed! Wrong type: ${storedData}"
				}
			}
		}
		else {
			log.info "Stored data not present"
		}
		return null
	}

	void setData(FullData data) {
		if (data == null) {
			log.info "Deleting data"
			storage.delete key
		}
		else {
			byte[] bytes
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()
				 GZIPOutputStream gzipOut = new GZIPOutputStream(baos)
				 ObjectOutputStream oos = new ObjectOutputStream(gzipOut)) {
				oos.writeObject(data.makeHeader())
				oos.writeObject(data)
				oos.flush()
				gzipOut.finish()
				bytes = baos.toByteArray()
			}
			log.info "Uploading data"
			storage.upload UploadRequest.fromBytes(bytes, key)
			log.info "Uploaded data"
		}
	}


}
