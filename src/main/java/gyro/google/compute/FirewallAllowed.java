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

import com.google.api.services.compute.model.Firewall.Allowed;
import gyro.google.Copyable;

import java.util.ArrayList;
import java.util.HashSet;

public class FirewallAllowed extends FirewallRule implements Copyable<Allowed> {
    @Override
    public void copyFrom(Allowed allowed) {
        setProtocol(allowed.getIPProtocol());
        setPorts(allowed.getPorts() != null ? new HashSet<>(allowed.getPorts()) : null);
    }

    Allowed toAllowed() {
        Allowed allowed = new Allowed();
        allowed.setIPProtocol(getProtocol());
        if (!getPorts().isEmpty()) {
            allowed.setPorts(new ArrayList<>(getPorts()));
        }
        return allowed;
    }
}
