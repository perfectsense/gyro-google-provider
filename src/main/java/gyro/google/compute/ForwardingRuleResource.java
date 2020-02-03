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

package gyro.google.compute;

import java.util.Set;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.ForwardingRule;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.TargetReference;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Required;

/**
 * Creates a regional forwarding rule.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-forwarding-rule forwarding-rule-example
 *         name: "forwarding-rule-example"
 *         description: "Regional forwarding rule description."
 *         region: "us-central1"
 *         ip-protocol: "TCP"
 *         load-balancing-scheme: "EXTERNAL"
 *         port-range: "80-80"
 *         target-pool: $(google::compute-target-pool target-pool-example-forwarding-rule)
 *     end
 */
@Type("compute-forwarding-rule")
public class ForwardingRuleResource extends AbstractForwardingRuleResource {

    private String region;
    private TargetPoolResource targetPool;

    /**
     * The region this forwarding rule should live in. (Required)
     */
    @Required
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region != null ? region.substring(region.lastIndexOf("/") + 1) : null;
    }

    /**
     * The target pool resource to receive the matched traffic. This target pool must live in the same region as the forwarding rule. (Required)
     */
    @Required
    @Updatable
    public TargetPoolResource getTargetPool() {
        return targetPool;
    }

    public void setTargetPool(TargetPoolResource targetPool) {
        this.targetPool = targetPool;
    }

    @Override
    public void copyFrom(ForwardingRule forwardingRule) {
        super.copyFrom(forwardingRule);
        setRegion(forwardingRule.getRegion());
        setTargetPool(findById(TargetPoolResource.class, forwardingRule.getTarget()));
    }

    @Override
    protected boolean doRefresh() throws Exception {
        Compute client = createComputeClient();
        copyFrom(client.forwardingRules().get(getProjectId(), getRegion(), getName()).execute());
        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        ForwardingRule forwardingRule = toForwardingRule();
        forwardingRule.setTarget(getTargetPool().getSelfLink());

        Operation response = client.forwardingRules().insert(getProjectId(), getRegion(), forwardingRule).execute();
        waitForCompletion(client, response);
        refresh();
    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Compute client = createComputeClient();

        TargetReference targetReference = new TargetReference();
        targetReference.setTarget(getTargetPool().getSelfLink());
        Operation response =
            client.forwardingRules().setTarget(getProjectId(), getRegion(), getName(), targetReference).execute();
        waitForCompletion(client, response);

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Operation response = client.forwardingRules().delete(getProjectId(), getRegion(), getName()).execute();
        waitForCompletion(client, response);
    }
}
