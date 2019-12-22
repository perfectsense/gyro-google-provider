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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.BackendBucket;
import com.google.api.services.compute.model.BackendBucketList;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query backend bucket.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    compute-backend-bucket: $(external-query google::compute-backend-bucket { name: 'compute-backend-bucket-example'})
 */
@Type("compute-backend-bucket")
public class BackendBucketFinder extends GoogleFinder<Compute, BackendBucket, BackendBucketResource> {

    private String name;

    /**
     * The name of the backend bucket.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<BackendBucket> findAllGoogle(Compute client) throws Exception {
        List<BackendBucket> backendBuckets = new ArrayList<>();
        BackendBucketList backendBucketList;
        String nextPageToken = null;

        do {
            backendBucketList = client.backendBuckets().list(getProjectId()).setPageToken(nextPageToken).execute();
            if (backendBucketList.getItems() != null) {
                backendBuckets.addAll(backendBucketList.getItems());
            }
            nextPageToken = backendBucketList.getNextPageToken();
        } while (nextPageToken != null);

        return backendBuckets;
    }

    @Override
    protected List<BackendBucket> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        return Collections.singletonList(client.backendBuckets().get(getProjectId(), filters.get("name")).execute());
    }
}
