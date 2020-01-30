/*
 * Copyright 2020, Perfect Sense, Inc.
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

package gyro.google.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.api.services.storage.model.Policy;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

/*
  The Bucket's IAM policy configuration.
 */
public class BucketIamPolicy extends Diffable implements Copyable<Policy> {

    private List<BucketIamPolicyBinding> bindings;

    // Read-only
    private Integer version;
    private String resourceId;

    /**
     * The association between the policies' role and members who may assume that role.
     *
     * @subresource gyro.google.storage.BucketIamPolicyBinding
     */
    @Updatable
    public List<BucketIamPolicyBinding> getBindings() {
        if (bindings == null) {
            bindings = new ArrayList<>();
        }
        return bindings;
    }

    public void setBindings(List<BucketIamPolicyBinding> bindings) {
        this.bindings = bindings;
    }

    /**
     * The ID of the resource to which this policy belongs.
     */
    @Output
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    /**
     * The version of this policy. This is always set to ``3``. See also `Understanding Policies <https://cloud.google.com/iam/docs/policies#versions>`_.
     */
    @Output
    public Integer getVersion() {
        return 3;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(Policy model) {
        setVersion(3);
        setResourceId(model.getResourceId());
        getBindings().clear();
        if (model.getBindings() != null) {
            setBindings(model.getBindings().stream().map(binding -> {
                    BucketIamPolicyBinding iamBinding = newSubresource(BucketIamPolicyBinding.class);
                    iamBinding.copyFrom(binding);
                    return iamBinding;
                }).collect(Collectors.toList())
            );
        }
    }

    public Policy toPolicy() {
        return new Policy().setVersion(getVersion()).setResourceId(getResourceId())
            .setBindings(getBindings().stream().map(BucketIamPolicyBinding::toBinding).collect(Collectors.toList()));
    }
}
