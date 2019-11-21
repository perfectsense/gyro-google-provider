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

import com.google.api.services.storage.model.Bucket.Lifecycle.Rule.Action;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

/**
 * The action to take.
 */
public class BucketLifecycleRuleAction extends Diffable implements Copyable<Action> {

    private String storageClass;
    private String type;

    /**
     * Target storage class. If the ``action`` is ``SetStorageClass`` it is required.
     */
    @Updatable
    public String getStorageClass() {
        return storageClass;
    }

    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }

    /**
     * Type of the action to take on condition. Valid types are ``Delete`` or ``SetStorageClass``.
     */
    @Updatable
    @ValidStrings({"Delete", "SetStorageClass"})
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void copyFrom(Action model) {
        setStorageClass(model.getStorageClass());
        setType(model.getType());
    }

    public Action toLifecycleRuleAction() {
        return new Action().setStorageClass(getStorageClass()).setType(getType());
    }
}
