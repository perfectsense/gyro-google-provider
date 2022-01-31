/*
 * Copyright 2021, Brightspot.
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

import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.DependsOn;
import gyro.core.validation.Max;
import gyro.core.validation.Min;
import gyro.core.validation.ValidStrings;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public class SubnetworkLogConfig extends Diffable implements Copyable<com.google.cloud.compute.v1.SubnetworkLogConfig> {

    private String aggregationInterval;
    private Float flowSampling;
    private List<String> metadataFields;
    private String metadata;

    /**
     * The aggregation interval for collecting flow logs.
     */
    @Updatable
    @ValidStrings({
        "INTERVAL_10_MIN", "INTERVAL_15_MIN", "INTERVAL_1_MIN", "INTERVAL_30_SEC",
        "INTERVAL_5_MIN", "INTERVAL_5_SEC" })
    public String getAggregationInterval() {
        return aggregationInterval;
    }

    public void setAggregationInterval(String aggregationInterval) {
        this.aggregationInterval = aggregationInterval;
    }

    /**
     * The sampling rate of VPC flow logs within the subnetwork.
     */
    @Max(1)
    @Min(0)
    @Updatable
    public Float getFlowSampling() {
        return flowSampling;
    }

    public void setFlowSampling(Float flowSampling) {
        this.flowSampling = flowSampling;
    }

    /**
     * The list of metadata fields.
     */
    @Updatable
    @DependsOn("metadata")
    public List<String> getMetadataFields() {
        if (metadataFields == null) {
            metadataFields = new ArrayList<>();
        }

        return metadataFields;
    }

    public void setMetadataFields(List<String> metadataFields) {
        this.metadataFields = metadataFields;
    }

    /**
     * The setting for which metadata fields should be added to the reported VPC flow logs.
     */
    @Updatable
    @ValidStrings({ "CUSTOM_METADATA", "EXCLUDE_ALL_METADATA", "INCLUDE_ALL_METADATA" })
    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.cloud.compute.v1.SubnetworkLogConfig model) {
        setAggregationInterval(model.getAggregationInterval());
        setFlowSampling(model.getFlowSampling());
        setMetadata(model.getMetadata());

        setMetadataFields(null);
        if (model.hasMetadata()) {
            setMetadataFields(new ArrayList<>(model.getMetadataFieldsList()));
        }
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        ArrayList<ValidationError> errors = new ArrayList<>();

        if (configuredFields.contains("metadata-fields") && !"CUSTOM_METADATA".equals(getMetadata())) {
            errors.add(new ValidationError(
                this, null, "'metadata-fields' cannot be set unless 'metadata' is set to 'CUSTOM_METADATA'"));
        }

        return errors;
    }

    com.google.cloud.compute.v1.SubnetworkLogConfig toSubnetworkLogConfig() {
        SubnetworkResource parent = (SubnetworkResource) parentResource();

        com.google.cloud.compute.v1.SubnetworkLogConfig.Builder builder = com.google.cloud.compute.v1.SubnetworkLogConfig
            .newBuilder().setEnable(parent.getEnableFlowLogs());

        if (getAggregationInterval() != null) {
            builder.setAggregationInterval(getAggregationInterval());
        }

        if (getFlowSampling() != null) {
            builder.setFlowSampling(getFlowSampling());
        }

        if (getMetadata() != null) {
            builder.setMetadata(getMetadata());
        }

        if (getMetadataFields() != null) {
            builder.addAllMetadataFields(getMetadataFields());
        }

        return builder.build();
    }
}
