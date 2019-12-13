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

import java.util.Optional;
import java.util.Set;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.BackendBucket;
import com.google.api.services.compute.model.Operation;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.google.Copyable;
import gyro.google.storage.BucketResource;

@Type("compute-backend-bucket")
public class BackendBucketResource extends ComputeResource implements Copyable<BackendBucket> {

    private BucketResource bucket;

    /**
     * Cloud CDN configuration for this BackendBucket.
     *
     private BackendBucketCdnPolicy cdnPolicy;
     */
    private String description;

    private Boolean enableCdn;

    private String name;

    private String selfLink;

    /**
     * Cloud Storage bucket name.
     */
    @Required
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
    public Boolean getEnableCdn() {
        return enableCdn;
    }

    public void setEnableCdn(Boolean enableCdn) {
        this.enableCdn = enableCdn;
    }

    /**
     * Name of the resource. Provided by the client when the resource is created. The name must be
     * 1-63 characters long, and comply with RFC1035. Specifically, the name must be 1-63 characters
     * long and match the regular expression `[a-z]([-a-z0-9]*[a-z0-9])?` which means the first
     * character must be a lowercase letter, and all following characters must be a dash, lowercase
     * letter, or digit, except the last character, which cannot be a dash.
     */
    @Required
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
    }

    @Override
    protected boolean doRefresh() throws Exception {
        Compute client = createComputeClient();
        copyFrom(client.backendBuckets().get(getProjectId(), getName()).execute());
        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        BackendBucket backendBucket = new BackendBucket();
        Optional.ofNullable(getBucket())
            .ifPresent(bucketResource -> backendBucket.setBucketName(bucketResource.getName()));
        backendBucket.setDescription(getDescription());
        backendBucket.setEnableCdn(getEnableCdn());
        backendBucket.setName(getName());

        Compute client = createComputeClient();
        Operation response = client.backendBuckets().insert(getProjectId(), backendBucket).execute();
        Operation.Error error = waitForCompletion(client, response);

        if (error != null) {
            throw new GyroException(error.toPrettyString());
        }
        refresh();
    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        // TODO:
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Operation response = client.backendBuckets().delete(getProjectId(), getName()).execute();
        Operation.Error error = waitForCompletion(client, response);

        if (error != null) {
            throw new GyroException(error.toPrettyString());
        }
    }
}
