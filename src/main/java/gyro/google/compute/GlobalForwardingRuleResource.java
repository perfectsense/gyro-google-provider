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
 * Creates a global forwarding rule.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-global-forwarding-rule global-forwarding-rule-example
 *         name: "global-forwarding-rule-example"
 *         description: "Global forwarding rule description."
 *         ip-protocol: "TCP"
 *         load-balancing-scheme: "EXTERNAL"
 *         port-range: "80-80"
 *         target-http-proxy: $(google::compute-target-http-proxy target-http-proxy-example-forwarding-rule)
 *     end
 */
@Type("compute-global-forwarding-rule")
public class GlobalForwardingRuleResource extends AbstractForwardingRuleResource {

    // TODO: this can be any target resources.
    private TargetHttpProxyResource targetHttpProxy;

    /**
     * The target http proxy to receive the matched traffic.
     */
    @Updatable
    @Required
    public TargetHttpProxyResource getTargetHttpProxy() {
        return targetHttpProxy;
    }

    public void setTargetHttpProxy(TargetHttpProxyResource targetHttpProxy) {
        this.targetHttpProxy = targetHttpProxy;
    }

    @Override
    public void copyFrom(ForwardingRule forwardingRule) {
        super.copyFrom(forwardingRule);

        // Multiple type of targets needs to checked when multiple targets are defined.

        setTargetHttpProxy(findById(TargetHttpProxyResource.class, forwardingRule.getTarget()));
    }

    @Override
    protected boolean doRefresh() throws Exception {
        Compute client = createComputeClient();
        copyFrom(client.globalForwardingRules().get(getProjectId(), getName()).execute());
        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        ForwardingRule forwardingRule = toForwardingRule();
        if (getTargetHttpProxy() != null) {
            forwardingRule.setTarget(getTargetHttpProxy().getSelfLink());
        }

        Operation response = client.globalForwardingRules().insert(getProjectId(), forwardingRule).execute();
        waitForCompletion(client, response);
        refresh();
    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Compute client = createComputeClient();

        if (changedFieldNames.contains("target-http-proxy")) {
            TargetReference targetReference = new TargetReference();
            targetReference.setTarget(getTargetHttpProxy().getSelfLink());
            Operation response =
                client.globalForwardingRules().setTarget(getProjectId(), getName(), targetReference).execute();
            waitForCompletion(client, response);
        }

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Operation response = client.globalForwardingRules().delete(getProjectId(), getName()).execute();
        waitForCompletion(client, response);
    }
}