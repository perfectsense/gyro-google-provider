package gyro.google.storage;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.Bucket;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.ValidStrings;
import gyro.core.validation.ValidationError;
import gyro.google.GoogleResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates a bucket within a specified region.
 *
 * Example
 * -------
 *
 * ..code-block:: gyro
 *
 *   google::bucket bucket-1
 *     name: 'example-one'
 *     location: 'us-central1'
 *     labels: {
 *         'foo': 'bar_1900'
 *     }
 *   end
 */
@Type("bucket")
public class BucketResource extends GoogleResource {

    private static final String LABEL_REGEX = "[^a-z0-9_-]";
    private static final Pattern LABEL_PATTERN = Pattern.compile(LABEL_REGEX);
    private static final String NAME_REGEX = "[^\\.a-z0-9_-]";
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);

    private String name;
    private Map<String, String> labels;
    private String location;

    /**
     * A unique name for the Bucket conforming to Google bucket naming guidelines.
     */
    @Id
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Optional set of up to 64 key:value metadata pairs. Each key:value must conform to Google guidelines.
     */
    @Updatable
    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    /**
     * The geographic region objects within the bucket will reside. Valid values are "northamerica-northeast1",
     * "us-central1", "us-east1", "us-east4", "us-west1", "us-west2", "southamerica-east1", "europe-north1",
     * "europe-west1", "europe-west2", "europe-west3", "europe-west4", "europe-west6", "asia-east1", "asia-east2",
     * "asia-northeast1", "asia-northeast2", "asia-south1", "asia-southeast1", "australia-southeast1", "asia",
     * "eu", "us", "eur4", "nam4", "us-central2"
     */
    @Updatable
    @ValidStrings({"northamerica-northeast1", "us-central1", "us-east1", "us-east4", "us-west1", "us-west2",
            "southamerica-east1", "europe-north1", "europe-west1", "europe-west2", "europe-west3", "europe-west4",
            "europe-west6", "asia-east1", "asia-east2", "asia-northeast1", "asia-northeast2", "asia-south1",
            "asia-southeast1", "australia-southeast1", "asia", "eu", "us", "eur4", "nam4", "us-central2"})
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean refresh() {
        Storage storage = creatClient(Storage.class);

        try {
            Bucket bucket = storage.buckets().get(getName()).execute();
            return (bucket != null);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void create(GyroUI ui, State state) throws Exception {
        Storage storage = creatClient(Storage.class);

        Bucket bucket = new Bucket();
        bucket.setName(getName());
        bucket.setLocation(getLocation());

        try {
            storage.buckets().insert(getProjectId(), bucket).execute();
        } catch (GoogleJsonResponseException e) {
            throw new GyroException(String.format("Unable to create, Google error: %s", e.getMessage()));
        }
    }

    @Override
    public void update(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Storage storage = creatClient(Storage.class);

        try {
            Bucket bucket = storage.buckets().get(getName()).execute();

            bucket.setLabels(getLabels());
            bucket.setLocation(getLocation());

            storage.buckets().update(getName(), bucket).execute();
        } catch (GoogleJsonResponseException e) {
            throw new GyroException(String.format("Unable to update, Google error: %s", e.getMessage()));
        }
    }

    @Override
    public void delete(GyroUI ui, State state) throws Exception {
        Storage storage = creatClient(Storage.class);
        storage.buckets().delete(getName()).execute();
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (getName() != null && NAME_PATTERN.matcher(getName()).find()) {
            errors.add(new ValidationError(this, "name", "Invalid name format."));
        }

        if (getLabels() != null) {
            for (Map.Entry<String, String> entry : getLabels().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Matcher keyMatcher = LABEL_PATTERN.matcher(key);
                Matcher valueMatcher = LABEL_PATTERN.matcher(value);
                if ((key.length() > 63) || (value.length() > 63) || keyMatcher.find() || valueMatcher.find()) {
                    errors.add(new ValidationError(
                            this,
                            "labels",
                            String.format("Invalid key/value => '%s:%s'", key, value)));
                }
            }
        }

        return errors;
    }
}
