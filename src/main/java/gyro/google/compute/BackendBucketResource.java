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

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.BackendBucket;
import com.google.cloud.compute.v1.BackendBucketsClient;
import com.google.cloud.compute.v1.Operation;
import com.google.protobuf.InvalidProtocolBufferException;
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
     * The name of the backend bucket.
     */
    @Required
    @Regex(value = "(?:(?:[-a-z0-9]{1,63}\\.)*(?:[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?):)?(?:[0-9]{1,19}|(?:[a-z0-9](?:[-a-z0-9]{0,61}[a-z0-9])?))", message = "a string 1-63 characters long and the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash")
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

    static boolean isBackendBucket(String selfLink) {
        if (selfLink == null) {
            return false;
        }

        try {
            BackendBucket bucket = BackendBucket.parseFrom(formatResource(null, selfLink).getBytes());

            return bucket != null;

        } catch (InvalidProtocolBufferException ex) {
            return false;
        }
    }

    @Override
    protected boolean doRefresh() throws Exception {
        BackendBucketsClient client = createClient(BackendBucketsClient.class);

        BackendBucket backendBucket = getBackendBucket(client);

        if (backendBucket == null) {
            return false;
        }

        copyFrom(backendBucket);
        client.close();
        return true;

    }

    private BackendBucket getBackendBucket(BackendBucketsClient client) {
        BackendBucket bucket = null;

        try {
            bucket = client.get(getProjectId(), getName());

        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore
        }

        return bucket;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        try (BackendBucketsClient client = createClient(BackendBucketsClient.class)) {

            BackendBucket.Builder builder = BackendBucket.newBuilder().setBucketName(getBucket().getName())
                .setName(getName());

            if (getDescription() != null) {
                builder.setDescription(getDescription());
            }

            if (getEnableCdn() != null) {
                builder.setEnableCdn(getEnableCdn());
            }

            if (getCdnPolicy() != null) {
                builder.setCdnPolicy(getCdnPolicy().toBackendBucketCdnPolicy());
            }

            Operation operation = client.insert(getProjectId(), builder.build());
            waitForCompletion(operation);

            if (!getSignedUrlKey().isEmpty()) {
                state.save();

                for (BackendSignedUrlKey urlKey : getSignedUrlKey()) {
                    operation = client.addSignedUrlKey(getProjectId(), getName(), urlKey.toSignedUrlKey());
                    waitForCompletion(operation);
                }
            }
        }

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {

        try (BackendBucketsClient client = createClient(BackendBucketsClient.class)) {

            BackendBucket.Builder builder = BackendBucket.newBuilder();

            if (changedFieldNames.contains("bucket")) {
                builder.setBucketName(getBucket().getName());
            }

            if (changedFieldNames.contains("description")) {
                builder.setDescription(getDescription());
            }

            if (changedFieldNames.contains("enable-cdn")) {
                builder.setEnableCdn(getEnableCdn());
            }

            if (changedFieldNames.contains("cdn-policy")) {
                if (getCdnPolicy() == null) {
                    throw new GyroException("'cdn-policy' cannot be unset once set.");
                }

                builder.setCdnPolicy(getCdnPolicy().toBackendBucketCdnPolicy());
            }

            builder.setName(getName());

            Operation operation = client.patch(getProjectId(), getName(), builder.build());
            waitForCompletion(operation);

            if (changedFieldNames.contains("signed-url-key")) {
                // delete old keys
                List<String> deleteSignedUrlKeys = ((BackendBucketResource) current).getSignedUrlKey().stream().map(
                    BackendSignedUrlKey::getKey).collect(Collectors.toList());

                for (String urlKey : deleteSignedUrlKeys) {
                    waitForCompletion(client.deleteSignedUrlKey(getProjectId(), getName(), urlKey));
                }

                // add new keys
                for (BackendSignedUrlKey urlKey : getSignedUrlKey()) {
                    waitForCompletion(
                        client.addSignedUrlKey(getProjectId(), getName(), urlKey.toSignedUrlKey()));
                }
            }
        }
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (BackendBucketsClient client = createClient(BackendBucketsClient.class)) {
            Operation response = client.delete(getProjectId(), getName());

            waitForCompletion(response);
        }
    }
}
