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

import java.util.Optional;

import com.google.api.services.dns.model.DnsKeySpec;
import gyro.core.resource.Diffable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class KeySpec extends Diffable implements Copyable<DnsKeySpec> {

    private String algorithm;

    private Long keyLength;

    private String keyType;

    /**
     * String mnemonic specifying the DNSSEC algorithm of this key.
     */
    @ValidStrings({ "ecdsap256sha256", "ecdsap384sha384", "rsasha1", "rsasha256", "rsasha512" })
    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * Length of the keys in bits.
     */
    public Long getKeyLength() {
        return keyLength;
    }

    public void setKeyLength(Long keyLength) {
        this.keyLength = keyLength;
    }

    /**
     * Specifies whether this is a key signing key (KSK) or a zone signing key (ZSK). Key signing keys have the Secure Entry Point flag set and, when active, will only be used to sign resource record sets of type DNSKEY. Zone signing keys do not have the Secure Entry Point flag set and will be used to sign all other types of resource record sets.
     */
    @ValidStrings({ "keySigning", "zoneSigning" })
    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    @Override
    public void copyFrom(DnsKeySpec model) {
        setAlgorithm(model.getAlgorithm());
        setKeyLength(model.getKeyLength());
        setKeyType(model.getKeyType());
    }

    @Override
    public String primaryKey() {
        return getKeyType();
    }

    public DnsKeySpec copyTo() {
        DnsKeySpec dnsKeySpec = new DnsKeySpec();
        dnsKeySpec.setAlgorithm(getAlgorithm());
        dnsKeySpec.setKeyLength(getKeyLength());
        dnsKeySpec.setKeyType(getKeyType());
        return dnsKeySpec;
    }

    public boolean isEqualTo(DnsKeySpec dnsKeySpec) {
        return Optional.ofNullable(dnsKeySpec)
            .map(DnsKeySpec::getKeyType)
            .filter(keyType -> keyType.equals(getKeyType()))
            .isPresent();
    }
}
