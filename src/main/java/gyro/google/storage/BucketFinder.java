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

package gyro.google.storage;

import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.Bucket;
import gyro.core.GyroException;
import gyro.core.Type;
import gyro.google.GoogleFinder;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Type("bucket")
public class BucketFinder extends GoogleFinder<Storage, Bucket, BucketResource> {
    private String name;

    /**
     * The name of the bucket.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<Bucket> findAllGoogle(Storage client) {
        try {
            return client.buckets().list(getProjectId()).execute().getItems();
        } catch (IOException e) {
            throw new GyroException(e);
        }
    }

    @Override
    protected List<Bucket> findGoogle(Storage client, Map<String, String> filters) {
        if (filters.containsKey("name")) {
            try {
                Bucket bucket = client.buckets().get(filters.get("name")).execute();

                if (bucket != null) {
                    return Collections.singletonList(bucket);
                }

            } catch (IOException e) {
                throw new GyroException(e);
            }
        }

        return Collections.emptyList();
    }
}
