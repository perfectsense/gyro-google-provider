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

import java.util.ArrayList;
import java.util.HashSet;

import com.google.cloud.compute.v1.Allowed;
import gyro.google.Copyable;

public class FirewallAllowed extends FirewallRule implements Copyable<Allowed> {

    @Override
    public void copyFrom(Allowed allowed) {
        setProtocol(allowed.getIPProtocol());
        setPorts(new HashSet<>(allowed.getPortsList()));
    }

    Allowed toAllowed() {
        Allowed.Builder builder = Allowed.newBuilder();

        if (getProtocol() != null) {
            builder.setIPProtocol(getProtocol());
        }

        if (!getPorts().isEmpty()) {
            builder.addAllPorts(new ArrayList<>(getPorts()));
        }

        return builder.build();
    }
}
