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

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.AddSignedUrlKeyBackendBucketRequest;
import com.google.cloud.compute.v1.BackendBucket;
import com.google.cloud.compute.v1.BackendBucketsClient;
import com.google.cloud.compute.v1.DeleteBackendBucketRequest;
import com.google.cloud.compute.v1.DeleteSignedUrlKeyBackendBucketRequest;
import com.google.cloud.compute.v1.InsertBackendBucketRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.PatchBackendBucketRequest;
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

    private SecurityPolicyResource securityPolicy;

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

    /**
     * The security policy associated with this backend bucket.
     */
    @Updatable
    public SecurityPolicyResource getSecurityPolicy() {
        return securityPolicy;
    }

    public void setSecurityPolicy(SecurityPolicyResource securityPolicy) {
        this.securityPolicy = securityPolicy;
    }

    @Override
    public void copyFrom(BackendBucket model) {
        BucketResource bucketResource = null;

        setName(model.getName());

        if (model.hasSelfLink()) {
            setSelfLink(model.getSelfLink());
        }

        if (model.hasDescription()) {
            setDescription(model.getDescription());
        }

        if (model.hasEnableCdn()) {
            setEnableCdn(model.getEnableCdn());
        }

        if (model.hasEdgeSecurityPolicy()) {
            setSecurityPolicy(findById(SecurityPolicyResource.class, model.getEdgeSecurityPolicy()));
        }

        setCdnPolicy(null);
        if (model.hasCdnPolicy()) {
            BackendBucketCdnPolicy cp = newSubresource(BackendBucketCdnPolicy.class);
            cp.copyFrom(model.getCdnPolicy());

            setCdnPolicy(cp);
        }

        setBucket(null);
        if (model.hasBucketName()) {
            bucketResource = findById(BucketResource.class, model.getBucketName());
            setBucket(bucketResource);
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
        return selfLink != null && selfLink.contains("backendBuckets");
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

        } catch (NotFoundException ex) {
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

            if (getSecurityPolicy() != null) {
                builder.setEdgeSecurityPolicy(getSecurityPolicy().getSelfLink());
            }

            Operation operation = client.insertCallable().call(InsertBackendBucketRequest.newBuilder()
                .setProject(getProjectId())
                .setBackendBucketResource(builder)
                .build());

            waitForCompletion(operation);

            if (!getSignedUrlKey().isEmpty()) {
                state.save();

                for (BackendSignedUrlKey urlKey : getSignedUrlKey()) {
                    operation = client.addSignedUrlKeyCallable().call(AddSignedUrlKeyBackendBucketRequest.newBuilder()
                        .setProject(getProjectId())
                        .setBackendBucket(getName())
                        .setSignedUrlKeyResource(urlKey.toSignedUrlKey())
                        .build());

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

            Operation operation = client.patchCallable().call(PatchBackendBucketRequest.newBuilder()
                .setProject(getProjectId())
                .setBackendBucket(getName())
                .setBackendBucketResource(builder)
                .build());

            waitForCompletion(operation);

            if (changedFieldNames.contains("signed-url-key")) {
                // delete old keys
                List<String> deleteSignedUrlKeys = ((BackendBucketResource) current).getSignedUrlKey().stream().map(
                    BackendSignedUrlKey::getKey).collect(Collectors.toList());

                for (String urlKey : deleteSignedUrlKeys) {
                    waitForCompletion(client.deleteSignedUrlKeyOperationCallable().call(
                        DeleteSignedUrlKeyBackendBucketRequest.newBuilder()
                            .setProject(getProjectId())
                            .setBackendBucket(getName())
                            .setKeyName(urlKey)
                            .build()));
                }

                // add new keys
                for (BackendSignedUrlKey urlKey : getSignedUrlKey()) {
                    waitForCompletion(client.addSignedUrlKeyOperationCallable()
                        .call(AddSignedUrlKeyBackendBucketRequest.newBuilder()
                            .setProject(getProjectId())
                            .setBackendBucket(getName())
                            .setSignedUrlKeyResource(urlKey.toSignedUrlKey())
                            .build()));
                }
            }
        }
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (BackendBucketsClient client = createClient(BackendBucketsClient.class)) {
            Operation response = client.deleteCallable().call(DeleteBackendBucketRequest.newBuilder()
                .setProject(getProjectId())
                .setBackendBucket(getName())
                .build());

            waitForCompletion(response);
        }
    }
}
