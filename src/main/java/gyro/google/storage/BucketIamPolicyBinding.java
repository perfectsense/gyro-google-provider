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

import com.google.api.services.storage.model.Expr;
import com.google.api.services.storage.model.Policy;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

/*
 *   The Bucket's IAM policy binding configuration.
 */
public class BucketIamPolicyBinding extends Diffable implements Copyable<Policy.Bindings> {

    private String role;
    private List<String> members;
    private BucketIamPolicyBindingCondition condition;

    /**
     * The role associated with this binding.
     */
    @Required
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /**
     * A list of identifiers for members who may assume the provided role.
     */
    @Required
    public List<String> getMembers() {
        if (members == null) {
            members = new ArrayList<>();
        }
        return members.stream().sorted().collect(Collectors.toList());
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    /**
     * The condition object associated with this binding.
     *
     * @subresource gyro.google.storage.BucketIamPolicyBindingConfiguration
     */
    @Updatable
    public BucketIamPolicyBindingCondition getCondition() {
        return condition;
    }

    public void setCondition(BucketIamPolicyBindingCondition condition) {
        this.condition = condition;
    }

    @Override
    public String primaryKey() {
        return String.format(
            "with role  '%s', members [ '%s' ] and condition '%s'",
            getRole(),
            String.join("','", getMembers()),
            (getCondition() == null) ? "" : getCondition().primaryKey());
    }

    @Override
    public void copyFrom(Policy.Bindings model) {
        setRole(model.getRole());
        setCondition(null);
        if (model.getCondition() != null) {
            Expr condition = model.getCondition();
            BucketIamPolicyBindingCondition iamCondition = newSubresource(BucketIamPolicyBindingCondition.class);
            iamCondition.copyFrom(condition);
            setCondition(iamCondition);
        }

        getMembers().clear();
        if (model.getMembers() != null) {
            setMembers(model.getMembers());
        }
    }

    public Policy.Bindings toBinding() {
        Policy.Bindings policyBinding = new Policy.Bindings();
        policyBinding.setMembers(getMembers());
        policyBinding.setRole(getRole());
        if (getCondition() != null) {
            policyBinding.setCondition(getCondition().toCondition());
        }

        return policyBinding;
    }
}
