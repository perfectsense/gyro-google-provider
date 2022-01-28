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

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.DeleteForwardingRuleRequest;
import com.google.cloud.compute.v1.ForwardingRule;
import com.google.cloud.compute.v1.ForwardingRulesClient;
import com.google.cloud.compute.v1.InsertForwardingRuleRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.SetTargetForwardingRuleRequest;
import com.google.cloud.compute.v1.TargetReference;
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
     * The region this forwarding rule should live in.
     */
    @Required
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region != null ? region.substring(region.lastIndexOf("/") + 1) : null;
    }

    /**
     * The target pool resource to receive the matched traffic. This target pool must live in the same region as the forwarding rule.
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
        try (ForwardingRulesClient client = createClient(ForwardingRulesClient.class)) {
            ForwardingRule forwardingRule = getForwardingRule(client);

            if (forwardingRule == null) {
                return false;
            }

            copyFrom(forwardingRule);

            return true;
        }
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        try (ForwardingRulesClient client = createClient(ForwardingRulesClient.class)) {
            ForwardingRule.Builder builder = toForwardingRule().toBuilder();
            builder.setTarget(getTargetPool().getSelfLink());

            Operation operation = client.insertCallable().call(InsertForwardingRuleRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setForwardingRuleResource(builder)
                .build());

            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        try (ForwardingRulesClient client = createClient(ForwardingRulesClient.class)) {
            TargetReference.Builder builder = TargetReference.newBuilder();
            builder.setTarget(getTargetPool().getSelfLink());

            Operation operation = client.setTargetCallable()
                .call(SetTargetForwardingRuleRequest.newBuilder()
                    .setProject(getProjectId())
                    .setRegion(getRegion())
                    .setForwardingRule(getName())
                    .setTargetReferenceResource(builder)
                    .build());

            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (ForwardingRulesClient client = createClient(ForwardingRulesClient.class)) {
            Operation operation = client.deleteCallable().call(DeleteForwardingRuleRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setForwardingRule(getName())
                .build());

            waitForCompletion(operation);
        }
    }

    private ForwardingRule getForwardingRule(ForwardingRulesClient client) {
        ForwardingRule forwardingRule = null;

        try {
            forwardingRule = client.get(getProjectId(), getRegion(), getName());
        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore
        }

        return forwardingRule;
    }
}
