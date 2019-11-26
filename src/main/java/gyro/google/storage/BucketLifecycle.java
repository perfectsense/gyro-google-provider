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

import com.google.api.services.storage.model.Bucket.Lifecycle;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public void copyFrom(Lifecycle model) {
        if (model != null && model.getRule() != null) {
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
        if (getRule() == null) {
            return "0";
        }

        return String.format("lifecycle with %s rules", getRule().size());
    }

    public Lifecycle toLifecycle() {
        return new Lifecycle()
                .setRule(getRule() == null ? null : getRule().stream().map(BucketLifecycleRule::toLifecycleRule)
                        .collect(Collectors.toList()));
    }
}
