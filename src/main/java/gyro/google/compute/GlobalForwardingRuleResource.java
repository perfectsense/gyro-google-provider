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
import gyro.core.validation.ValidationError;

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
    private TargetHttpsProxyResource targetHttpsProxy;

    /**
     * The target http proxy to receive the matched traffic.
     */
    @Updatable
    public TargetHttpProxyResource getTargetHttpProxy() {
        return targetHttpProxy;
    }

    public void setTargetHttpProxy(TargetHttpProxyResource targetHttpProxy) {
        this.targetHttpProxy = targetHttpProxy;
    }

    /**
     * The target https proxy to receive the matched traffic.
     */
    @Updatable
    public TargetHttpsProxyResource getTargetHttpsProxy() {
        return targetHttpsProxy;
    }

    public void setTargetHttpsProxy(TargetHttpsProxyResource targetHttpsProxy) {
        this.targetHttpsProxy = targetHttpsProxy;
    }

    @Override
    public void copyFrom(ForwardingRule forwardingRule) {
        super.copyFrom(forwardingRule);

        String target = forwardingRule.getTarget();

        setTargetHttpProxy(null);
        if (TargetHttpProxyResource.isTargetHttpProxy(target)) {
            setTargetHttpProxy(findById(TargetHttpProxyResource.class, target));
        }

        setTargetHttpsProxy(null);
        if (TargetHttpsProxyResource.isTargetHttpsProxy(target)) {
            setTargetHttpsProxy(findById(TargetHttpsProxyResource.class, target));
        }
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
        forwardingRule.setTarget(getTarget());

        Operation response = client.globalForwardingRules().insert(getProjectId(), forwardingRule).execute();
        waitForCompletion(client, response);
        refresh();
    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Compute client = createComputeClient();

        if (changedFieldNames.contains("target-http-proxy") || changedFieldNames.contains("target-https-proxy")) {
            TargetReference targetReference = new TargetReference();
            targetReference.setTarget(getTarget());
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

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if (getTargetHttpProxy() == null && getTargetHttpsProxy() == null) {
            errors.add(new ValidationError(
                this,
                null,
                "Either 'target-http-proxy' or 'target-https-proxy' is required!"));
        }
        return errors;
    }

    private String getTarget() {
        if (getTargetHttpProxy() != null) {
            return getTargetHttpProxy().getSelfLink();
        }

        if (getTargetHttpsProxy() != null) {
            return getTargetHttpsProxy().getSelfLink();
        }

        return "";
    }
}