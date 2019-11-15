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
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;
import gyro.google.GoogleResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Creates a Bucket within a specified region.
 *
 * ========
 * Examples
 * ========
 *
 * Basic Bucket
 * ------------
 *
 * ..code-block:: gyro
 *
 *    google::bucket bucket-1
 *        name: 'example-one'
 *        location: 'us-central1'
 *        labels: {
 *            'foo': 'bar_1900'
 *        }
 *    end
 *
 * Adding CORS rule
 * ----------------
 *
 * ..code-block:: gyro
 *
 *     google::bucket bucket-1
 *         name: 'example-one'
 *         location: 'us-central1'
 *         labels: {
 *             foo: 'bar_1901'
 *         }
 *         cors-rule
 *             max-age-seconds: 3600
 *             method: ['GET', 'POST']
 *             origin: ['*']
 *             response-header: ['application-x-test']
 *         end
 *     end
 */
@Type("bucket")
public class BucketResource extends GoogleResource implements Copyable<Bucket> {

    private static final String LABEL_REGEX = "[^a-z0-9_-]";
    private static final Pattern LABEL_PATTERN = Pattern.compile(LABEL_REGEX);
    private static final String NAME_REGEX = "[^\\.a-z0-9_-]";
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);

    private List<BucketAccessControlConfiguration> acl;
    private String name;
    private Map<String, String> labels;
    private String location;
    private List<BucketCors> cors;
    private BucketBilling billing;
    private Boolean defaultEventBasedHold;
    private BucketEncryption encryption;
    private BucketIamConfiguration iamConfiguration;
    private BucketLifecycle lifecycle;
    private BucketLogging logging;
    private BucketRetentionPolicy retentionPolicy;
    private String storageClass;
    private BucketVersioning versioning;
    private BucketWebsite website;

    /**
     * Access controls on the bucket. See `Bucket Access Controls <https://cloud.google.com/storage/docs/json_api/v1/bucketAccessControls/>`_.
     *
     * @subresource gyro.google.storage.BucketAccessControlConfiguration
     */
    @Updatable
    public List<BucketAccessControlConfiguration> getAcl() {
        if (acl == null) {
            return new ArrayList<>();
        }
        return acl;
    }

    public void setAcl(List<BucketAccessControlConfiguration> acl) {
        this.acl = acl;
    }

    /**
     * A unique name for the Bucket conforming to the `bucket naming guidelines <https://cloud.google.com/storage/docs/naming?authuser=1#requirements/>`_.
     */
    @Id
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Optional set of up to 64 key:value metadata pairs. Each key:value must conform to `Label guidelines <https://cloud.google.com/storage/docs/key-terms?#bucket-labels/>`_.
     */
    @Updatable
    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    /**
     * The geographic region objects within the bucket will reside. See `Bucket locations <https://cloud.google.com/storage/docs/locations/>`_.
     */
    @Updatable
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Configure the cross origin request policies (CORS) for the bucket.
     *
     * @subresoure gyro.google.storage.BucketCors
     */
    @Updatable
    public List<BucketCors> getCors() {
        if (cors == null) {
            return new ArrayList<>();
        }
        return cors;
    }

    public void setCors(List<BucketCors> cors) {
        this.cors = cors;
    }

    /**
     * Configure the billing for the Bucket.
     *
     * @subresource gyro.google.storage.BucketBilling
     */
    @Updatable
    public BucketBilling getBilling() {
        return billing;
    }

    public void setBilling(BucketBilling billing) {
        this.billing = billing;
    }

    /**
     * When ``true`` automatically apply an GCP "eventBasedHold", or object hold, to new objects added to the bucket.
     */
    public Boolean getDefaultEventBasedHold() {
        return defaultEventBasedHold;
    }

    public void setDefaultEventBasedHold(Boolean defaultEventBasedHold) {
        this.defaultEventBasedHold = defaultEventBasedHold;
    }

    /**
     * The buckets encryption configuration.
     *
     * @subresource gyro.google.storage.BucketEncryption
     */
    @Updatable
    public BucketEncryption getEncryption() {
        return encryption;
    }

    public void setEncryption(BucketEncryption encryption) {
        this.encryption = encryption;
    }

    /**
     * The bucket's IAM configuration. See also `Cloud Identity and Access Management <https://cloud.google.com/storage/docs/access-control/iam/>`_.
     *
     * @subresource gyro.google.storage.BucketIamConfiguration
     */
    @Updatable
    public BucketIamConfiguration getIamConfiguration() {
        return iamConfiguration;
    }

    public void setIamConfiguration(BucketIamConfiguration iamConfiguration) {
        this.iamConfiguration = iamConfiguration;
    }

    /**
     * The bucket's lifecycle configuration. See also `Object Lifecycle Management <https://cloud.google.com/storage/docs/lifecycle/>`_.
     *
     * @subresource gyro.google.storage.BucketLifecycle
     */
    @Updatable
    public BucketLifecycle getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(BucketLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    /**
     * The bucket's logging configuration.
     *
     *  @subresource gyro.google.storage.BucketLogging
     */
    @Updatable
    public BucketLogging getLogging() {
        return logging;
    }

    public void setLogging(BucketLogging logging) {
        this.logging = logging;
    }

    /**
     * Minimum age an object in the bucket must reach before it can be deleted or overwritten. See also `Retention policies <https://cloud.google.com/storage/docs/bucket-lock?#retention-policy/>`_.
     *
     * @subresource gyro.google.storage.BucketRetentionPolicy
     */
    @Updatable
    public BucketRetentionPolicy getRetentionPolicy() {
        return retentionPolicy;
    }

    public void setRetentionPolicy(BucketRetentionPolicy retentionPolicy) {
        this.retentionPolicy = retentionPolicy;
    }

    /**
     * Bucket's default storage class used whenever no ``storageClass`` is specified for a newly-created object. Valid values are ``STANDARD``, ``NEARLINE``, ``COLDLINE``, ``MULTI-REGIONAL``, ``REGIONAL`` and ``DURABLE_REDUCED_AVAILABILITY``. Defaults to ``STANDARD``.
     */
    @Updatable
    @ValidStrings({"STANDARD", "NEARLINE", "COLDLINE", " MULTI-REGIONAL", "REGIONAL", "DURABLE_REDUCED_AVAILABILITY"})
    public String getStorageClass() {
        return storageClass;
    }

    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }

    /**
     * The bucket's versioning configuration.
     *
     * @subresource gyro.google.storage. BucketVersioning
     */
    @Updatable
    public BucketVersioning getVersioning() {
        return versioning;
    }

    public void setVersioning(BucketVersioning versioning) {
        this.versioning = versioning;
    }

    /**
     * The bucket's website configuration controlling how the service behaves when accessing bucket contents as a web site.
     *
     * @subresource gyro.google.storage.BucketWebsite
     */
    @Updatable
    public BucketWebsite getWebsite() {
        return website;
    }

    public void setWebsite(BucketWebsite website) {
        this.website = website;
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
        bucket.setAcl(getAcl() == null ? null : getAcl().stream().map(BucketAccessControlConfiguration::toBucketAccessControl).collect(Collectors.toList()));
        bucket.setName(getName());
        bucket.setLabels(getLabels());
        bucket.setLocation(getLocation());
        bucket.setDefaultEventBasedHold(getDefaultEventBasedHold());
        bucket.setCors(getCors().stream().map(BucketCors::toBucketCors).collect(Collectors.toList()));
        bucket.setBilling(getBilling() == null ? null : getBilling().toBucketBilling());
        bucket.setEncryption(getEncryption() == null ? null : getEncryption().toBucketEncryption());
        bucket.setIamConfiguration(getIamConfiguration() == null ? null : getIamConfiguration().toBucketIamConfiguration());
        bucket.setLifecycle(getLifecycle() == null ? null : getLifecycle().toLifecycle());
        bucket.setLogging(getLogging() == null ? null : getLogging().toBucketLogging());
        bucket.setRetentionPolicy(getRetentionPolicy() == null ? null : getRetentionPolicy().toBucketRententionPolicy());
        bucket.setStorageClass(getStorageClass());
        bucket.setVersioning(getVersioning() == null ? null : getVersioning().toBucketVersioning());
        bucket.setWebsite(getWebsite() == null ? null : getWebsite().toBucketWebsite());

        try {
            storage.buckets().insert(getProjectId(), bucket).execute();
        } catch (GoogleJsonResponseException e) {
            throw new GyroException(e.getDetails().getMessage());
        }
    }

    @Override
    public void update(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Storage storage = creatClient(Storage.class);

        try {
            Bucket bucket = storage.buckets().get(getName()).execute();
            bucket.setAcl(getAcl() == null ? null : getAcl().stream().map(BucketAccessControlConfiguration::toBucketAccessControl).collect(Collectors.toList()));
            bucket.setLabels(getLabels());
            bucket.setLocation(getLocation());
            bucket.setDefaultEventBasedHold(getDefaultEventBasedHold());
            bucket.setCors(getCors().stream().map(BucketCors::toBucketCors).collect(Collectors.toList()));
            bucket.setBilling(getBilling() == null ? null : getBilling().toBucketBilling());
            bucket.setEncryption(getEncryption() == null ? null : getEncryption().toBucketEncryption());
            bucket.setIamConfiguration(getIamConfiguration() == null ? null : getIamConfiguration().toBucketIamConfiguration());
            bucket.setLifecycle(getLifecycle() == null ? null : getLifecycle().toLifecycle());
            bucket.setLogging(getLogging() == null ? null : getLogging().toBucketLogging());
            bucket.setRetentionPolicy(getRetentionPolicy() == null ? null : getRetentionPolicy().toBucketRententionPolicy());
            bucket.setStorageClass(getStorageClass());
            bucket.setVersioning(getVersioning() == null ? null : getVersioning().toBucketVersioning());
            bucket.setWebsite(getWebsite() == null ? null : getWebsite().toBucketWebsite());

            // Lock retention policy. This can not be undone and ALL assets must reach policy time to delete bucket.
            if (changedFieldNames.contains("retention-policy")
                    && Boolean.TRUE.equals(bucket.getRetentionPolicy().getIsLocked())) {
                storage.buckets().lockRetentionPolicy(getName(), bucket.getMetageneration()).execute();
            }

            storage.buckets().update(getName(), bucket).execute();
        } catch (GoogleJsonResponseException e) {
            throw new GyroException(e.getDetails().getMessage());
        }
    }

    @Override
    public void delete(GyroUI ui, State state) throws Exception {
        try {
            Storage storage = creatClient(Storage.class);
            storage.buckets().delete(getName()).execute();
        } catch (IOException e) {
            throw new GyroException(String.format("Unable to delete Bucket: %s, Google error: %s", getName(), e.getMessage()));
        }
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

    @Override
    public void copyFrom(Bucket model) {
        setName(model.getName());
        setLabels(model.getLabels());
        setLocation(model.getLocation());
        setBilling(BucketBilling.fromBucketBilling(model.getBilling()));
        setEncryption(BucketEncryption.fromBucketEncryption(model.getEncryption()));
        setIamConfiguration(BucketIamConfiguration.fromBucketIamConfiguration(model.getIamConfiguration()));
        setLifecycle(BucketLifecycle.fromLifecycle(model.getLifecycle()));
        setLogging(BucketLogging.fromBucketLogging(model.getLogging()));
        setRetentionPolicy(BucketRetentionPolicy.fromBucketRententionPolicy(model.getRetentionPolicy()));
        setStorageClass(model.getStorageClass());
        setVersioning(BucketVersioning.fromBucketVersioning(model.getVersioning()));
        setWebsite(BucketWebsite.fromBucketWebsite(model.getWebsite()));

        if (model.getAcl() != null) {
            setAcl(model.getAcl().stream().map(acl -> BucketAccessControlConfiguration.fromBucketAccessControl(acl)).collect(Collectors.toList()));
        }

        if (model.getCors() != null) {
            setCors(model.getCors().stream().map(rule -> BucketCors.fromBucketCors(rule)).collect(Collectors.toList()));
        }
    }
}
