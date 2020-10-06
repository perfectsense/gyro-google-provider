/*
 * Copyright 2020, Perfect Sense, Inc.
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
import java.util.List;

import com.google.api.services.compute.model.SchedulingNodeAffinity;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class ComputeSchedulingNodeAffinity extends Diffable implements Copyable<SchedulingNodeAffinity> {

    private String key;

    private String operator;

    private List<String> values;

    /**
     * Corresponds to the label key of Node resource.
     */
    @Required
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Defines the operation of node selection.
     */
    @ValidStrings({"IN", "NOT_IN"})
    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * Corresponds to the label values of Node resource.
     */
    public List<String> getValues() {
        if (values == null) {
            values = new ArrayList<>();
        }
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    public void copyFrom(SchedulingNodeAffinity model) {
        setKey(model.getKey());
        setOperator(model.getOperator());
        setValues(model.getValues());
    }

    public SchedulingNodeAffinity toSchedulingNodeAffinity() {
        SchedulingNodeAffinity schedulingNodeAffinity = new SchedulingNodeAffinity();
        schedulingNodeAffinity.setKey(getKey());
        schedulingNodeAffinity.setOperator(getOperator());
        schedulingNodeAffinity.setValues(getValues());
        return schedulingNodeAffinity;
    }

    @Override
    public String primaryKey() {
        return getKey();
    }
}
