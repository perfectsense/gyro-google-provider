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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.client.util.Data;
import com.google.api.services.compute.model.Autoscaler;
import com.google.api.services.compute.model.AutoscalerStatusDetails;
import com.google.api.services.compute.model.AutoscalingPolicy;
import gyro.core.GyroUI;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public abstract class AbstractAutoscalerResource extends ComputeResource implements Copyable<Autoscaler> {

    private String name;

    private ComputeAutoscalingPolicy autoscalingPolicy;

    private String description;

    private Integer recommendedSize;

    private String selfLink;

    private String status;

    private List<ComputeAutoscalerStatusDetails> statusDetail;

    abstract void insert(Autoscaler autoscaler) throws Exception;

    abstract void patch(Autoscaler autoscaler) throws Exception;

    /**
     * Name of the resource.
     * The name must be 1-63 characters long, and comply with RFC1035. Specifically, the name must be 1-63 characters long and match the regular expression `[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?` which means the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash.
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
     * The configuration parameters for the autoscaling algorithm.
     * You can define one or more of the policies for an autoscaler:
     *
     * - ``cpu-utilization``
     * - ``custom-metric-utilizations``
     * - ``load-balancing-utilization``
     *
     * If none of these are specified, the default will be to autoscale based on ``cpu-utilization`` to ``0.6`` or 60%.
     *
     * @subresource gyro.google.compute.ComputeAutoscalingPolicy
     */
    @Required
    @Updatable
    public ComputeAutoscalingPolicy getAutoscalingPolicy() {
        return autoscalingPolicy;
    }

    public void setAutoscalingPolicy(ComputeAutoscalingPolicy autoscalingPolicy) {
        this.autoscalingPolicy = autoscalingPolicy;
    }

    /**
     * A description of this resource.
     */
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Target recommended MIG size (number of instances) computed by autoscaler.
     * Autoscaler calculates recommended MIG size even when autoscaling policy mode is different from ON. This field is empty when autoscaler is not connected to the existing managed instance group or autoscaler did not generate its prediction.
     */
    @Output
    public Integer getRecommendedSize() {
        return recommendedSize;
    }

    public void setRecommendedSize(Integer recommendedSize) {
        this.recommendedSize = recommendedSize;
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

    /**
     * The status of the autoscaler configuration.
     * Current set of possible values:
     *
     * - ``PENDING``: Autoscaler backend hasn't read new/updated configuration.
     * - ``DELETING``: Configuration is being deleted.
     * - ``ACTIVE``: Configuration is acknowledged to be effective. Some warnings might be present in the ``status-detail`` field.
     * - ``ERROR``: Configuration has errors. Actionable for users. Details are present in the ``status-detail`` field.
     */
    @Output
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Human-readable details about the current state of the autoscaler.
     *
     * @subresource gyro.google.compute.ComputeAutoscalerStatusDetails
     */
    @Output
    public List<ComputeAutoscalerStatusDetails> getStatusDetail() {
        if (statusDetail == null) {
            statusDetail = new ArrayList<>();
        }

        return statusDetail;
    }

    public void setStatusDetail(List<ComputeAutoscalerStatusDetails> statusDetail) {
        this.statusDetail = statusDetail;
    }

    @Override
    public void copyFrom(Autoscaler model) {
        setName(model.getName());
        setAutoscalingPolicy(Optional.ofNullable(model.getAutoscalingPolicy())
            .map(e -> {
                ComputeAutoscalingPolicy computeAutoscalingPolicy = newSubresource(ComputeAutoscalingPolicy.class);
                computeAutoscalingPolicy.copyFrom(e);
                return computeAutoscalingPolicy;
            })
            .orElse(null));
        setDescription(model.getDescription());
        // `recommendedSize` is not available from Google SDK yet.
        setRecommendedSize(Optional.ofNullable(model.get("recommendedSize"))
            .filter(Integer.class::isInstance)
            .map(Integer.class::cast)
            .orElse(null));
        setSelfLink(model.getSelfLink());
        setStatus(model.getStatus());

        List<ComputeAutoscalerStatusDetails> diffableAutoscalerStatusDetails = null;
        List<AutoscalerStatusDetails> statusDetails = model.getStatusDetails();

        if (statusDetails != null && !statusDetails.isEmpty()) {
            diffableAutoscalerStatusDetails = statusDetails
                .stream()
                .map(e -> {
                    ComputeAutoscalerStatusDetails computeVersion = newSubresource(
                        ComputeAutoscalerStatusDetails.class);
                    computeVersion.copyFrom(e);
                    return computeVersion;
                })
                .collect(Collectors.toList());
        }
        setStatusDetail(diffableAutoscalerStatusDetails);
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        Autoscaler autoscaler = new Autoscaler();
        autoscaler.setName(getName());
        Optional.ofNullable(getAutoscalingPolicy())
            .map(ComputeAutoscalingPolicy::copyTo)
            .ifPresent(autoscaler::setAutoscalingPolicy);
        autoscaler.setDescription(getDescription());

        insert(autoscaler);
        refresh();
    }

    @Override
    protected void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames)
        throws Exception {
        Autoscaler autoscaler = constructPatchRequest(changedFieldNames);

        if (autoscaler != null) {
            patch(autoscaler);
        }
        refresh();
    }

    private Autoscaler constructPatchRequest(Set<String> changedFieldNames) {
        Autoscaler autoscaler = new Autoscaler();
        Set<String> changedFields = new HashSet<>(changedFieldNames);
        int count = changedFields.size();
        boolean shouldPatch = false;

        if (changedFields.remove("description")) {
            autoscaler.setDescription(getDescription());

            if (--count == 0) {
                return autoscaler;
            }
            shouldPatch = true;
        }

        if (changedFields.remove("autoscaling-policy")) {
            ComputeAutoscalingPolicy diffableAutoscalingPolicy = getAutoscalingPolicy();
            autoscaler.setAutoscalingPolicy(diffableAutoscalingPolicy == null
                ? Data.nullOf(AutoscalingPolicy.class)
                : diffableAutoscalingPolicy.copyTo());

            if (--count == 0) {
                return autoscaler;
            }
            shouldPatch = true;
        }
        return shouldPatch ? autoscaler : null;
    }
}
