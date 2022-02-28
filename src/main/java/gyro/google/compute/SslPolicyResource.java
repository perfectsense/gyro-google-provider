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

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.DeleteSslPolicyRequest;
import com.google.cloud.compute.v1.GetSslPolicyRequest;
import com.google.cloud.compute.v1.InsertSslPolicyRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.PatchSslPolicyRequest;
import com.google.cloud.compute.v1.SslPoliciesClient;
import com.google.cloud.compute.v1.SslPolicy;
import com.google.cloud.compute.v1.Warnings;
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
     * The minimum version of SSL protocol that can be used by the clients to establish a connection with the load balancer.
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
     * The name of the SSL policy.
     */
    @Required
    @Regex(value = "[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?", message = "a string 1-63 characters long and the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Specifies the set of SSL features that can be used by the load balancer when negotiating SSL with clients. If using ``CUSTOM``, the set of SSL features to enable must be specified in the ``custom-features`` field.
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
        setName(model.getName());
        setEnabledFeatures(model.getEnabledFeaturesList());

        if (model.hasSelfLink()) {
            setSelfLink(model.getSelfLink());
        }

        if (model.hasDescription()) {
            setDescription(model.getDescription());
        }

        setCustomFeatures(null);
        if (!model.getCustomFeaturesList().isEmpty()) {
            setCustomFeatures(model.getCustomFeaturesList());
        }

        if (model.hasFingerprint()) {
            setFingerprint(model.getFingerprint());
        }

        if (model.hasMinTlsVersion()) {
            setMinTlsVersion(model.getMinTlsVersion());
        }

        if (model.hasProfile()) {
            setProfile(model.getProfile());
        }

        List<Warnings> warnings = model.getWarningsList();
        getWarning().clear();
        if (!warnings.isEmpty()) {
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
        try (SslPoliciesClient client = createClient(SslPoliciesClient.class)) {
            SslPolicy sslPolicy = getSslPolicy(client);

            if (sslPolicy == null) {
                return false;
            }

            copyFrom(sslPolicy);

            return true;
        }
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        try (SslPoliciesClient client = createClient(SslPoliciesClient.class)) {
            Operation operation = client.insertCallable().call(InsertSslPolicyRequest.newBuilder()
                .setProject(getProjectId())
                .setSslPolicyResource(toSslPolicy())
                .build());

            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    protected void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        try (SslPoliciesClient client = createClient(SslPoliciesClient.class)) {
            Operation operation = client.patchCallable().call(PatchSslPolicyRequest.newBuilder()
                .setProject(getProjectId())
                .setSslPolicy(getName())
                .setSslPolicyResource(toSslPolicy())
                .build());

            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        try (SslPoliciesClient client = createClient(SslPoliciesClient.class)) {
            Operation operation = client.deleteCallable().call(DeleteSslPolicyRequest.newBuilder()
                .setProject(getProjectId())
                .setSslPolicy(getName())
                .build());

            waitForCompletion(operation);
        }
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
        SslPolicy.Builder builder = SslPolicy.newBuilder().addAllEnabledFeatures(getEnabledFeatures())
            .setMinTlsVersion(getMinTlsVersion())
            .setName(getName()).addAllCustomFeatures(getCustomFeatures())
            .setProfile(getProfile());

        if (getDescription() != null) {
            builder.setDescription(getDescription());
        }

        if (getFingerprint() != null) {
            builder.setFingerprint(getFingerprint());
        }

        return builder.build();
    }

    private SslPolicy getSslPolicy(SslPoliciesClient client) {
        SslPolicy sslPolicy = null;

        try {
            sslPolicy = client.get(GetSslPolicyRequest.newBuilder()
                .setProject(getProjectId())
                .setSslPolicy(getName())
                .build());

        } catch (NotFoundException ex) {
            // ignore
        }

        return sslPolicy;
    }
}
