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

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Subnetwork;
import com.google.api.services.compute.model.SubnetworksScopedList;
import gyro.core.GyroException;
import gyro.core.Type;
import gyro.google.GoogleFinder;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Query subnet.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    subnet: $(external-query google::subnet { name: 'subnet-example', region: 'us-east1'})
 */
@Type("subnet")
public class SubnetworkFinder extends GoogleFinder<Compute, Subnetwork, SubnetworkResource> {
    private String name;
    private String region;

    /**
     * The name of the subnet.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The region of the subnet.
     */
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    protected List<Subnetwork> findAllGoogle(Compute client) {
        try {
            return client.subnetworks()
                .aggregatedList(getProjectId()).execute()
                .getItems().values().stream()
                .map(SubnetworksScopedList::getSubnetworks)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(je.getDetails().getMessage());
        } catch (IOException ex) {
            throw new GyroException(ex);
        }
    }

    @Override
    protected List<Subnetwork> findGoogle(Compute client, Map<String, String> filters) {
        Subnetwork subnetwork = null;

        try {
            subnetwork = client.subnetworks().get(getProjectId(), filters.get("region"), filters.get("name")).execute();
        } catch (GoogleJsonResponseException je) {
            if (!je.getDetails().getMessage().matches("The resource (.*) was not found")) {
                throw new GyroException(je.getDetails().getMessage());
            }
        } catch (IOException ex) {
            throw new GyroException(ex);
        }

        if (subnetwork != null) {
            return Collections.singletonList(subnetwork);
        } else {
            return Collections.emptyList();
        }
    }
}