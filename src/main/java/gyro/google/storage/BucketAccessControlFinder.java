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
import com.google.api.services.storage.model.BucketAccessControl;
import com.google.api.services.storage.model.Buckets;
import gyro.core.GyroException;
import gyro.core.Type;
import gyro.google.GoogleFinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Query for Bucket Access Controls.
 *
 * ========
 * Examples
 * ========
 *
 * **Find all Bucket Access Controls**
 *
 * .. code-block:: gyro
 *
 *    aclAll: $(external-query google::acl)
 *
 *
 * **A single Bucket Access Controls for the bucket named "acl-example" and the entity value "domain-sample.com"**
 *
 * .. code-block:: gyro
 *
 *    acl: $(external-query google::acl {bucket: 'acl-example', entity: 'domain-sample.com'})
 */
@Type("acl")
public class BucketAccessControlFinder extends GoogleFinder<Storage, BucketAccessControl, BucketAccessControlResource> {

    private BucketResource bucket;
    private String entity;

    /**
     * Name of a bucket.
     */
    public BucketResource getBucket() {
        return bucket;
    }

    public void setBucket(BucketResource bucket) {
        this.bucket = bucket;
    }

    /**
     * Entity value holding the permission.
     */
    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    @Override
    protected List<BucketAccessControl> findAllGoogle(Storage client) {
        List<BucketAccessControl> acls = new ArrayList<>();
        String pageToken;

        try {
            do {
                Buckets results = client.buckets().list(getProjectId()).execute();
                pageToken = results.getNextPageToken();

                if (results.getItems() != null) {
                    Iterator<Bucket> it = results.getItems().iterator();
                    while (it.hasNext()) {
                        Bucket b = it.next();
                        List<BucketAccessControl> items = client.bucketAccessControls().list(b.getName()).execute().getItems();
                        if (items != null) {
                            acls.addAll(items);
                        }
                    }
                }
            } while (pageToken != null);
        } catch (IOException e) {
            throw new GyroException(e);
        }

        return acls;
    }

    @Override
    protected List<BucketAccessControl> findGoogle(Storage client, Map<String, String> filters) {

        if (filters.containsKey("bucket") && filters.containsKey("entity")) {
            try {
                BucketAccessControl acl = client.bucketAccessControls().get(filters.get("bucket"), filters.get("entity")).execute();

                if (acl != null) {
                    return Collections.singletonList(acl);
                }
            } catch (IOException e) {
                throw new GyroException(e);
            }
        }

        return Collections.emptyList();
    }
}
