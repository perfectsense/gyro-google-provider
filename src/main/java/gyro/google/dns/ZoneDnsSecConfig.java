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

package gyro.google.dns;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.api.services.dns.model.DnsKeySpec;
import com.google.api.services.dns.model.ManagedZoneDnsSecConfig;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class ZoneDnsSecConfig extends Diffable implements Copyable<ManagedZoneDnsSecConfig> {

    private List<KeySpec> defaultKeySpec;

    private String nonExistence;

    private String state;

    /**
     * Specifies parameters for generating initial DnsKeys for this managed zone.
     *
     * @subresource gyro.google.dns.KeySpec
     */
    @Updatable
    public List<KeySpec> getDefaultKeySpec() {
        if (defaultKeySpec == null) {
            defaultKeySpec = new ArrayList<>();
        }
        return defaultKeySpec;
    }

    public void setDefaultKeySpec(List<KeySpec> defaultKeySpec) {
        this.defaultKeySpec = defaultKeySpec;
    }

    /**
     * Specifies the mechanism for authenticated denial-of-existence responses.
     */
    @Updatable
    @ValidStrings({ "nsec", "nsec3" })
    public String getNonExistence() {
        return nonExistence;
    }

    public void setNonExistence(String nonExistence) {
        this.nonExistence = nonExistence;
    }

    /**
     * Specifies whether DNSSEC is enabled, and what mode it is in.
     */
    @Updatable
    @ValidStrings({ "on", "off", "transfer" })
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(ManagedZoneDnsSecConfig model) {
        List<KeySpec> diffableKeySpecs = null;
        List<DnsKeySpec> defaultKeySpecs = model.getDefaultKeySpecs();

        if (defaultKeySpecs != null && !defaultKeySpecs.isEmpty()) {
            diffableKeySpecs = defaultKeySpecs
                .stream()
                .map(defaultKeySpec -> {
                    KeySpec diffableKeySpec = newSubresource(KeySpec.class);
                    diffableKeySpec.copyFrom(defaultKeySpec);
                    return diffableKeySpec;
                })
                .collect(Collectors.toList());
        }
        setDefaultKeySpec(diffableKeySpecs);
        setNonExistence(model.getNonExistence());
        setState(model.getState());
    }

    public ManagedZoneDnsSecConfig toManagedZoneDnsSecConfig() {
        ManagedZoneDnsSecConfig managedZoneDnsSecConfig = new ManagedZoneDnsSecConfig();
        List<KeySpec> defaultKeySpec = getDefaultKeySpec();

        if (!defaultKeySpec.isEmpty()) {
            managedZoneDnsSecConfig.setDefaultKeySpecs(defaultKeySpec
                .stream()
                .map(KeySpec::toKeySpec)
                .collect(Collectors.toList()));
        }
        managedZoneDnsSecConfig.setNonExistence(getNonExistence());
        managedZoneDnsSecConfig.setState(getState());
        return managedZoneDnsSecConfig;
    }
}
