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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.BucketAccessControl;
import com.google.api.services.storage.model.Buckets;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for Bucket Access Controls.
 *
 * Examples
 * --------
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
    protected List<BucketAccessControl> findAllGoogle(Storage client) throws Exception {
        List<BucketAccessControl> acls = new ArrayList<>();
        String pageToken;

        do {
            Buckets results = client.buckets().list(getProjectId()).execute();
            pageToken = results.getNextPageToken();

            if (results.getItems() != null) {
                for (Bucket b : results.getItems()) {
                    List<BucketAccessControl> items = client.bucketAccessControls()
                        .list(b.getName())
                        .execute()
                        .getItems();
                    if (items != null) {
                        acls.addAll(items);
                    }
                }
            }
        } while (pageToken != null);

        return acls;
    }

    @Override
    protected List<BucketAccessControl> findGoogle(Storage client, Map<String, String> filters) throws Exception {

        if (filters.containsKey("bucket") && filters.containsKey("entity")) {
            BucketAccessControl acl = client.bucketAccessControls()
                .get(filters.get("bucket"), filters.get("entity"))
                .execute();

            if (acl != null) {
                return Collections.singletonList(acl);
            }
        }

        return Collections.emptyList();
    }
}
