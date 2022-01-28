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

import com.google.container.v1beta1.NetworkPolicy;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class GkeNetworkPolicy extends Diffable implements Copyable<NetworkPolicy> {

    private Boolean enabled;
    private NetworkPolicy.Provider provider;

    /**
     * When set to ``true``, the network policy is enabled on the cluster.
     */
    @Required
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * The selected network policy provider.
     */
    @ValidStrings("CALICO")
    public NetworkPolicy.Provider getProvider() {
        return provider;
    }

    public void setProvider(NetworkPolicy.Provider provider) {
        this.provider = provider;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(NetworkPolicy model) throws Exception {
        setEnabled(model.getEnabled());
        setProvider(model.getProvider());
    }

    NetworkPolicy toNetworkPolicy() {
        return NetworkPolicy.newBuilder().setEnabled(getEnabled()).setProvider(getProvider()).build();
    }
}
