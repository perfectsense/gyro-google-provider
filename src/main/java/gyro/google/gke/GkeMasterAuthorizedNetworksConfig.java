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

package gyro.google.gke;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.container.v1.MasterAuthorizedNetworksConfig;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class GkeMasterAuthorizedNetworksConfig extends Diffable implements Copyable<MasterAuthorizedNetworksConfig> {

    private Boolean enabled;
    private List<GkeCidrBlock> cidrBlock;

    @Required
    @Updatable
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Updatable
    public List<GkeCidrBlock> getCidrBlock() {
        if (cidrBlock == null) {
            cidrBlock = new ArrayList<>();
        }

        return cidrBlock;
    }

    public void setCidrBlock(List<GkeCidrBlock> cidrBlock) {
        this.cidrBlock = cidrBlock;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(MasterAuthorizedNetworksConfig model) {
        setEnabled(model.getEnabled());

        setCidrBlock(null);
        if (model.getCidrBlocksCount() > 0) {
            setCidrBlock(model.getCidrBlocksList().stream().map(c -> {
                GkeCidrBlock block = newSubresource(GkeCidrBlock.class);
                block.copyFrom(c);

                return block;
            }).collect(Collectors.toList()));
        }
    }

    MasterAuthorizedNetworksConfig toMasterAuthorizedNetworksConfig() {
        MasterAuthorizedNetworksConfig.Builder builder = MasterAuthorizedNetworksConfig.newBuilder()
            .setEnabled(getEnabled());

        for (GkeCidrBlock b : getCidrBlock()) {
            builder.addCidrBlocks(b.toCidrBlock());
        }

        return builder.build();
    }
}
