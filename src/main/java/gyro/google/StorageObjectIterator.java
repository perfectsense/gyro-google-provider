package gyro.google;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.Objects;
import com.google.api.services.storage.model.StorageObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class StorageObjectIterator implements Iterator<StorageObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageObjectIterator.class);

    private String bucket;
    private String prefix;
    private Storage client;

    private List<StorageObject> storageObjects = new ArrayList<>();
    private int index;
    private String nextPageToken;

    public StorageObjectIterator(String bucket, String prefix, Storage client) {
        this.bucket = bucket;
        this.prefix = prefix;
        this.client = client;
    }

    @Override
    public boolean hasNext() {
        if (index < storageObjects.size()) {
            return true;
        }

        if (index > 0 && nextPageToken == null) {
            return false;
        }

        try {
            Objects objects = client.objects()
                .list(bucket)
                .setPrefix(prefix)
                .setPageToken(nextPageToken)
                .setMaxResults(100L)
                .execute();

            if (objects.getItems() != null) {
                index = 0;
                storageObjects.clear();
                storageObjects.addAll(objects.getItems());
            }
            nextPageToken = objects.getNextPageToken();

        } catch (IOException e) {
            LOGGER.error("Failed to retrieve storage objects: {} {} {}", bucket, prefix, nextPageToken, e);
            return false;
        }

        return !storageObjects.isEmpty();

    }

    @Override
    public StorageObject next() {
        if (hasNext()) {
            return storageObjects.get(index++);
        }
        throw new NoSuchElementException();
    }
}
