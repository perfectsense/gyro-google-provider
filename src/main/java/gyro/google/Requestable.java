package gyro.google;

import com.google.api.client.json.GenericJson;

/**
 * Interface for converting Gyro data structure to Google data structure.
 */
public interface Requestable<T extends GenericJson> {

    T copyTo();
}
