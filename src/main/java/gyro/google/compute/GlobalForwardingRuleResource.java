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
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.ForwardingRule;
import com.google.api.services.compute.model.Operation;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.ValidationError;

@Type("compute-global-forwarding-rule")
public class GlobalForwardingRuleResource extends ForwardingRuleResource {

    // TODO: this can be any target resources.
    private GlobalTargetHttpProxyResource targetHttpProxyResource;

    /**
     * The target resource to receive the matched traffic. For regional forwarding rules,
     * this target must live in the same region as the forwarding rule. For global forwarding rules,
     * this target must be a global load balancing resource. The forwarded traffic must be of a type
     * appropriate to the target object. For INTERNAL_SELF_MANAGED load balancing, only HTTP and HTTPS
     * targets are valid.
     */
    public GlobalTargetHttpProxyResource getTargetHttpProxyResource() {
        return targetHttpProxyResource;
    }

    public void setTargetHttpProxyResource(GlobalTargetHttpProxyResource targetHttpProxyResource) {
        this.targetHttpProxyResource = targetHttpProxyResource;
    }

    @Override
    protected boolean doRefresh() throws Exception {
        Compute client = createComputeClient();
        copyFrom(client.globalForwardingRules().get(getProjectId(), getName()).execute());
        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        ForwardingRule forwardingRule = new ForwardingRule();
        forwardingRule.setIPAddress(getIpAddress());
        forwardingRule.setIPProtocol(getIpProtocol());
        forwardingRule.setAllPorts(getAllPorts());
        forwardingRule.setDescription(getDescription());
        forwardingRule.setIPProtocol(getIpProtocol());
        forwardingRule.setLoadBalancingScheme(getLoadBalancingScheme());
        forwardingRule.setName(getName());
        forwardingRule.setNetworkTier(getNetworkTier());
        // This should be used even though the docs says it's deprecated as setting port is not working.
        forwardingRule.setPortRange(getPortRange());
        List<String> ports = getPorts();

        if (!ports.isEmpty()) {
            forwardingRule.setPorts(ports);
        }
        forwardingRule.setServiceLabel(getServiceLabel());

        Optional.ofNullable(getTargetHttpProxyResource())
            .ifPresent(targetHttpProxyResource -> forwardingRule.setTarget(targetHttpProxyResource.getSelfLink()));

        Compute client = createComputeClient();
        Operation response = client.globalForwardingRules().insert(getProjectId(), forwardingRule).execute();
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
        Operation response = client.globalForwardingRules().delete(getProjectId(), getName()).execute();
        Operation.Error error = waitForCompletion(client, response);

        if (error != null) {
            throw new GyroException(error.toPrettyString());
        }
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        // make 'target-http-proxy-resource' effectively required.
        if (getTargetHttpProxyResource() == null) {
            errors.add(new ValidationError(
                this,
                "target-http-proxy-resource",
                "target-http-proxy-resource is required!"));
        }

        //        if (getTargetHttpProxyResource() != null) {
        //            List<String> ports = getPorts();
        //
        //            if (ports.size() != 1 || !ports.contains("80")) {
        //                errors.add(new ValidationError(this, "ports", "Must be '80'"));
        //            }
        //        }
        return errors;
    }

    @Override
    protected void doCopyFrom(ForwardingRule model) {
        GlobalTargetHttpProxyResource globalTargetHttpProxyResource = null;
        String target = model.getTarget();

        if (target != null) {
            globalTargetHttpProxyResource = findById(GlobalTargetHttpProxyResource.class, target);
        }
        setTargetHttpProxyResource(globalTargetHttpProxyResource);
    }
}
