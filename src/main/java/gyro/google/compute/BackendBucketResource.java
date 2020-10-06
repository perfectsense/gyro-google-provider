/*
 * Copyright 2019, Perfect Sense, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gyro.google.compute;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.client.util.Data;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.BackendBucket;
import com.google.api.services.compute.model.Operation;
import com.google.cloud.compute.v1.ProjectGlobalBackendBucketName;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.google.Copyable;
import gyro.google.storage.BucketResource;

/**
 * Creates a bucket backend.
 *
 * Examples
 * --------
 *
 * .. code-block:: gyro
 *
 *      google::compute-backend-bucket gyro-backend-bucket-example
 *         name: 'gyro-backend-bucket-example'
 *         description: 'gyro-backend-bucket-example-desc'
 *         bucket: $(google::bucket bucket-example-backend-bucket)
 *         enable-cdn: false
 *
 *         cdn-policy
 *             signed-url-max-age: 30000
 *         end
 *
 *         signed-url-key
 *             key: "xyz"
 *             value: "ZWVsbG8gZnJvbSBHb29nbA=="
 *         end
 *     end
 */
@Type("compute-backend-bucket")
public class BackendBucketResource extends ComputeResource implements Copyable<BackendBucket> {

    private BucketResource bucket;

    private String description;

    private Boolean enableCdn;

    private String name;

    private String selfLink;

    private BackendBucketCdnPolicy cdnPolicy;

    private List<BackendSignedUrlKey> signedUrlKey;

    /**
     * Cloud Storage bucket name.
     */
    @Required
    @Updatable
    public BucketResource getBucket() {
        return bucket;
    }

    public void setBucket(BucketResource bucket) {
        this.bucket = bucket;
    }

    /**
     * An optional textual description of the resource; provided by the client when the resource is
     * created.
     */
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * If true, enable Cloud CDN for this BackendBucket.
     */
    @Updatable
    public Boolean getEnableCdn() {
        return enableCdn;
    }

    public void setEnableCdn(Boolean enableCdn) {
        this.enableCdn = enableCdn;
    }

    /**
     * The name of the backend bucket. The name must be 1-63 characters long and the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash. (Required)
     */
    @Required
    @Regex("(?:(?:[-a-z0-9]{1,63}\\.)*(?:[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?):)?(?:[0-9]{1,19}|(?:[a-z0-9](?:[-a-z0-9]{0,61}[a-z0-9])?))")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Server-defined URL for the resource.
     */
    @Id
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    /**
     * CDN configuration for this BackendBucket.
     *
     * @subresource gyro.google.compute.BackendBucketCdnPolicy
     */
    @Updatable
    public BackendBucketCdnPolicy getCdnPolicy() {
        return cdnPolicy;
    }

    public void setCdnPolicy(BackendBucketCdnPolicy cdnPolicy) {
        this.cdnPolicy = cdnPolicy;
    }

    /**
     * Signed Url key configuration for the backend bucket.
     */
    @Updatable
    public List<BackendSignedUrlKey> getSignedUrlKey() {
        if (signedUrlKey == null) {
            signedUrlKey = new ArrayList<>();
        }

        return signedUrlKey;
    }

    public void setSignedUrlKey(List<BackendSignedUrlKey> signedUrlKey) {
        this.signedUrlKey = signedUrlKey;
    }

    @Override
    public void copyFrom(BackendBucket model) {
        BucketResource bucketResource = null;
        String bucketName = model.getBucketName();

        if (bucketName != null) {
            bucketResource = findById(BucketResource.class, bucketName);
        }
        setBucket(bucketResource);
        setDescription(model.getDescription());
        setEnableCdn(model.getEnableCdn());
        setName(model.getName());
        setSelfLink(model.getSelfLink());

        if (model.getCdnPolicy() != null) {
            BackendBucketCdnPolicy cdnPolicy = newSubresource(BackendBucketCdnPolicy.class);
            cdnPolicy.copyFrom(model.getCdnPolicy());
            setCdnPolicy(cdnPolicy);
        } else {
            setCdnPolicy(null);
        }

        if (getCdnPolicy() != null) {
            // add any new keys not configured through gyro
            Set<String> keys = getSignedUrlKey().stream()
                .map(BackendSignedUrlKey::getKey)
                .collect(Collectors.toSet());
            for (String key : getCdnPolicy().getSignedUrlKeyNames()) {
                if (!keys.contains(key)) {
                    BackendSignedUrlKey urlKey = newSubresource(BackendSignedUrlKey.class);
                    urlKey.setKey(key);
                    urlKey.setValue("hidden");
                    getSignedUrlKey().add(urlKey);
                }
            }

            // remove any keys configured through gyro but removed
            HashSet<String> keysStored = new HashSet<>(getCdnPolicy().getSignedUrlKeyNames());
            getSignedUrlKey().removeIf(o -> !keysStored.contains(o.getKey()));
        }
    }

    @Override
    protected boolean doRefresh() throws Exception {
        Compute client = createComputeClient();
        copyFrom(client.backendBuckets().get(getProjectId(), getName()).execute());

        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        BackendBucket backendBucket = new BackendBucket();
        backendBucket.setBucketName(getBucket().getName());
        backendBucket.setDescription(getDescription());
        backendBucket.setEnableCdn(getEnableCdn());
        backendBucket.setName(getName());
        backendBucket.setCdnPolicy(getCdnPolicy() != null
            ? getCdnPolicy().toBackendBucketCdnPolicy()
            : Data.nullOf(com.google.api.services.compute.model.BackendBucketCdnPolicy.class));

        Operation response = client.backendBuckets().insert(getProjectId(), backendBucket).execute();
        waitForCompletion(client, response);

        if (!getSignedUrlKey().isEmpty()) {
            state.save();

            for (BackendSignedUrlKey urlKey : getSignedUrlKey()) {
                waitForCompletion(
                    client,
                    client.backendBuckets()
                        .addSignedUrlKey(getProjectId(), getName(), urlKey.toSignedUrlKey())
                        .execute());
            }
        }

        refresh();
    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {

        Compute client = createComputeClient();

        BackendBucket backendBucket = new BackendBucket();

        if (changedFieldNames.contains("bucket")) {
            backendBucket.setBucketName(getBucket().getName());
        }

        if (changedFieldNames.contains("description")) {
            backendBucket.setDescription(getDescription());
        }

        if (changedFieldNames.contains("enable-cdn")) {
            backendBucket.setEnableCdn(getEnableCdn());
        }

        if (changedFieldNames.contains("cdn-policy")) {
            if (getCdnPolicy() == null) {
                throw new GyroException("'cdn-policy' cannot be unset once set.");
            }

            backendBucket.setCdnPolicy(getCdnPolicy().toBackendBucketCdnPolicy());
        }

        backendBucket.setName(getName());

        Operation operation = client.backendBuckets().patch(getProjectId(), getName(), backendBucket).execute();
        waitForCompletion(client, operation);

        if (changedFieldNames.contains("signed-url-key")) {
            // delete old keys
            List<String> deleteSignedUrlKeys = ((BackendBucketResource) current).getSignedUrlKey().stream().map(
                BackendSignedUrlKey::getKey).collect(
                Collectors.toList());

            for (String urlKey : deleteSignedUrlKeys) {
                waitForCompletion(
                    client,
                    client.backendBuckets().deleteSignedUrlKey(getProjectId(), getName(), urlKey).execute());
            }

            // add new keys
            for (BackendSignedUrlKey urlKey : getSignedUrlKey()) {
                waitForCompletion(
                    client,
                    client.backendBuckets()
                        .addSignedUrlKey(getProjectId(), getName(), urlKey.toSignedUrlKey())
                        .execute());
            }
        }
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Operation response = client.backendBuckets().delete(getProjectId(), getName()).execute();
        waitForCompletion(client, response);
    }

    static boolean isBackendBucket(String selfLink) {
        return selfLink != null && (ProjectGlobalBackendBucketName.isParsableFrom(formatResource(null, selfLink)));
    }
}
