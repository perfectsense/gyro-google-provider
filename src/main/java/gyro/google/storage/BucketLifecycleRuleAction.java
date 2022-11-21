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

import com.google.cloud.storage.BucketInfo.LifecycleRule.LifecycleAction;
import com.google.cloud.storage.BucketInfo.LifecycleRule.SetStorageClassLifecycleAction;
import com.google.cloud.storage.StorageClass;
import gyro.core.resource.Diffable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

/**
 * The action to take.
 */
public class BucketLifecycleRuleAction extends Diffable implements Copyable<LifecycleAction> {

    private String storageClass;
    private String type;

    /**
     * Target storage class. Required when ``action`` is set to ``SetStorageClass``.
     */
    public String getStorageClass() {
        return storageClass;
    }

    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }

    /**
     * Type of the action to take on condition.
     */
    @ValidStrings({ "Delete", "SetStorageClass" })
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String primaryKey() {
        ArrayList<String> values = new ArrayList<>();

        if (getStorageClass() != null) {
            values.add(String.format("storage-class = %s", getStorageClass()));
        }

        if (getType() != null) {
            values.add(String.format("type = %s", getType()));
        }

        return String.join("; ", values);
    }

    @Override
    public void copyFrom(LifecycleAction model) {
        setType(model.getActionType());

        if (model instanceof SetStorageClassLifecycleAction) {
            SetStorageClassLifecycleAction action = (SetStorageClassLifecycleAction) model;
            setStorageClass(action.getStorageClass().name());
        }
    }

    public LifecycleAction toLifecycleRuleAction() {
        if (getType().equals("Delete")) {
            return LifecycleAction.newDeleteAction();
        } else {
            return LifecycleAction.newSetStorageClassAction(StorageClass.valueOf(getStorageClass()));
        }
    }
}
