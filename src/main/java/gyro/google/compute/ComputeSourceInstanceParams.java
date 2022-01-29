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
import java.util.stream.Collectors;

import com.google.cloud.compute.v1.SourceInstanceParams;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class ComputeSourceInstanceParams extends Diffable implements Copyable<SourceInstanceParams> {

    private List<ComputeDiskInstantiationConfig> diskConfig;

    /**
     * List of attached disks configuration.
     * For boot disk and any other R/W disks, new custom images will be created from each disk.
     * For read-only disks, they will be attached in read-only mode.
     * Local SSD disks will be created as blank volumes.
     *
     * @subresource gyro.google.compute.ComputeDiskInstantiationConfig
     */
    @Required
    public List<ComputeDiskInstantiationConfig> getDiskConfig() {
        if (diskConfig == null) {
            diskConfig = new ArrayList<>();
        }
        return diskConfig;
    }

    public void setDiskConfig(List<ComputeDiskInstantiationConfig> diskConfig) {
        this.diskConfig = diskConfig;
    }

    @Override
    public void copyFrom(SourceInstanceParams model) {
        setDiskConfig(null);
        if (!model.getDiskConfigsList().isEmpty()) {
            List<ComputeDiskInstantiationConfig> diffableDiskConfigs = model.getDiskConfigsList()
                .stream()
                .map(attachedDisk -> {
                    ComputeDiskInstantiationConfig diffableDiskConfig =
                        newSubresource(ComputeDiskInstantiationConfig.class);
                    diffableDiskConfig.copyFrom(attachedDisk);

                    return diffableDiskConfig;
                })
                .collect(Collectors.toList());

            setDiskConfig(diffableDiskConfigs);
        }
    }

    public SourceInstanceParams toSourceInstanceParams() {
        SourceInstanceParams.Builder builder = SourceInstanceParams.newBuilder();
        List<ComputeDiskInstantiationConfig> diskConfig = getDiskConfig();

        if (!diskConfig.isEmpty()) {
            builder.addAllDiskConfigs(diskConfig.stream().map(ComputeDiskInstantiationConfig::toDiskInstantiationConfig)
                .collect(Collectors.toList()));
        }
        return builder.build();
    }

    @Override
    public String primaryKey() {
        return "";
    }
}
