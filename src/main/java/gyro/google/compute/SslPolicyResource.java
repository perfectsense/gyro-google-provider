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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.SslPolicy;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

/**
 * Creates an SSL policy.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-ssl-policy ssl-policy-example
 *         name: "ssl-policy-example"
 *         profile: "MODERN"
 *         min-tls-version: "TLS_1_0"
 *     end
 *
 * .. code-block:: gyro
 *
 *     google::compute-ssl-policy custom-ssl-policy-example
 *         name: "custom-ssl-policy-example"
 *         profile: "CUSTOM"
 *         min-tls-version: "TLS_1_2"
 *         custom-features: [ "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA", "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256" ]
 *     end
 */
@Type("compute-ssl-policy")
public class SslPolicyResource extends ComputeResource implements Copyable<SslPolicy> {

    private List<String> customFeatures;
    private String description;
    private String minTlsVersion;
    private String name;
    private String profile;

    // Read-only
    private String fingerprint;
    private List<String> enabledFeatures;
    private String selfLink;
    private List<SslPolicyWarning> warning;

    /**
     * A list of features enabled when the selected profile is ``CUSTOM``.
     */
    @Updatable
    public List<String> getCustomFeatures() {
        if (customFeatures == null) {
            customFeatures = new ArrayList<>();
        }

        return customFeatures;
    }

    public void setCustomFeatures(List<String> customFeatures) {
        this.customFeatures = customFeatures;
    }

    /**
     * An optional description of this SSL policy.
     */
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The minimum version of SSL protocol that can be used by the clients to establish a connection with the load balancer. Valid values are ``TLS_1_0``, ``TLS_1_1``, or ``TLS_1_2``.
     */
    @Required
    @Updatable
    @ValidStrings({ "TLS_1_0", "TLS_1_1", "TLS_1_2" })
    public String getMinTlsVersion() {
        return minTlsVersion;
    }

    public void setMinTlsVersion(String minTlsVersion) {
        this.minTlsVersion = minTlsVersion;
    }

    /**
     * The name of the SSL policy. Must be 1-63 characters long, and the first character must be a lowercase letter. All other characters must be a lowercase letter, digit, or ``-``, except the last character, which cannot be a ``-``.
     */
    @Required
    @Regex("[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Specifies the set of SSL features that can be used by the load balancer when negotiating SSL with clients. Valid values are ``COMPATIBLE``, ``MODERN``, ``RESTRICTED``, or ``CUSTOM``. If using ``CUSTOM``, the set of SSL features to enable must be specified in the ``custom-features`` field.
     */
    @Required
    @Updatable
    @ValidStrings({ "COMPATIBLE", "MODERN", "RESTRICTED", "CUSTOM" })
    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    /**
     * Fingerprint of this SSL policy.
     */
    @Output
    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    /**
     * The list of features enabled in the SSL policy.
     */
    @Output
    public List<String> getEnabledFeatures() {
        if (enabledFeatures == null) {
            enabledFeatures = new ArrayList<>();
        }

        return enabledFeatures;
    }

    public void setEnabledFeatures(List<String> enabledFeatures) {
        this.enabledFeatures = enabledFeatures;
    }

    /**
     * Server-defined URL for the SSL policy.
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
     * If potential misconfigurations are detected for this SSL policy, this field will be populated with warning messages.
     *
     * @subresource gyro.google.compute.SslPolicyWarning
     */
    @Output
    public List<SslPolicyWarning> getWarning() {
        if (warning == null) {
            warning = new ArrayList<>();
        }

        return warning;
    }

    public void setWarning(List<SslPolicyWarning> warning) {
        this.warning = warning;
    }

    @Override
    public void copyFrom(SslPolicy model) {
        setCustomFeatures(model.getCustomFeatures());
        setDescription(model.getDescription());
        setFingerprint(model.getFingerprint());
        setMinTlsVersion(model.getMinTlsVersion());
        setName(model.getName());
        setProfile(model.getProfile());
        setEnabledFeatures(model.getEnabledFeatures());
        setSelfLink(model.getSelfLink());

        List<SslPolicy.Warnings> warnings = model.getWarnings();
        getWarning().clear();
        if (warnings != null && !warnings.isEmpty()) {
            setWarning(warnings
                .stream()
                .map(warning -> {
                    SslPolicyWarning policyWarning = newSubresource(SslPolicyWarning.class);
                    policyWarning.copyFrom(warning);
                    return policyWarning;
                })
                .collect(Collectors.toList()));
        }
    }

    @Override
    protected boolean doRefresh() throws Exception {
        Compute client = createComputeClient();

        SslPolicy policy = client.sslPolicies().get(getProjectId(), getName()).execute();
        copyFrom(policy);

        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        Operation operation = client.sslPolicies().insert(getProjectId(), toSslPolicy()).execute();
        waitForCompletion(client, operation);

        refresh();
    }

    @Override
    protected void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Compute client = createComputeClient();

        Operation operation = client.sslPolicies().patch(getProjectId(), getName(), toSslPolicy()).execute();
        waitForCompletion(client, operation);

        refresh();
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Operation response = client.sslPolicies().delete(getProjectId(), getName()).execute();
        waitForCompletion(client, response);
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if (!getCustomFeatures().isEmpty() && !"CUSTOM".equals(getProfile())) {
            errors.add(new ValidationError(
                this,
                "profile",
                "When 'custom-features' is set, 'profile' must be 'CUSTOM'!"));
        }
        return errors;
    }

    private SslPolicy toSslPolicy() {
        SslPolicy policy = new SslPolicy();

        policy.setCustomFeatures(getCustomFeatures());
        policy.setDescription(getDescription());
        policy.setFingerprint(getFingerprint());
        policy.setMinTlsVersion(getMinTlsVersion());
        policy.setName(getName());
        policy.setProfile(getProfile());
        policy.setEnabledFeatures(getEnabledFeatures());

        return policy;
    }
}
