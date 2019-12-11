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
import java.util.List;
import java.util.stream.Collectors;

import com.google.api.services.storage.model.Bucket.Lifecycle;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

/**
 * The Buckets lifecycle configuration.
 */
public class BucketLifecycle extends Diffable implements Copyable<Lifecycle> {

    private List<BucketLifecycleRule> rule;

    /**
     * A lifecycle management rule, which is made of an action to take and the condition(s) under which an action will be taken.
     *
     * @subresource gyro.google.storage.BucketLifecycleRule
     */
    @Required
    @Updatable
    public List<BucketLifecycleRule> getRule() {
        if (rule == null) {
            rule = new ArrayList<>();
        }

        return rule;
    }

    public void setRule(List<BucketLifecycleRule> rule) {
        this.rule = rule;
    }

    public void copyFrom(Lifecycle model) {
        getRule().clear();
        if (model.getRule() != null) {
            setRule(model.getRule().stream()
                .map(r -> {
                    BucketLifecycleRule bucketLifecycleRule = newSubresource(BucketLifecycleRule.class);
                    bucketLifecycleRule.copyFrom(r);
                    return bucketLifecycleRule;
                })
                .collect(Collectors.toList())
            );
        }
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public Lifecycle toLifecycle() {
        return new Lifecycle()
            .setRule(getRule().stream()
                .map(BucketLifecycleRule::toLifecycleRule)
                .collect(Collectors.toList()));
    }
}
