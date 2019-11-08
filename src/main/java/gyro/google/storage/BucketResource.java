package gyro.google.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import gyro.core.validation.ValidationError;
import gyro.google.GoogleResource;

@Type("bucket")
public class BucketResource extends GoogleResource {

    private static final String LABEL_REGEX = "[^a-z0-9_-]";
    private static final Pattern LABEL_PATTERN = Pattern.compile(LABEL_REGEX);
    private static final String NAME_REGEX = "[^\\.a-z0-9_-]";
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);

    private String name;
    private Map<String, String> labels;

    @Id
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Updatable
    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
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

        validate();

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
            validate();

            bucket.setLabels(getLabels());
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
                    errors.add(new ValidationError(this, "labels", String.format("Invalid key/value => '%s:%s'", key, value)));
                }
            }
        }

        return errors;
    }
}
