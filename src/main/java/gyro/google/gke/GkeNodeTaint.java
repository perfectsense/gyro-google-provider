/*
 * Copyright 2021, Brightspot.
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

package gyro.google.gke;

import com.google.container.v1.NodeTaint;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class GkeNodeTaint extends Diffable implements Copyable<NodeTaint> {

    private String key;
    private String value;
    private NodeTaint.Effect effect;

    /**
     * The key for taint.
     */
    @Required
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * The value for taint.
     */
    @Required
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * The effect for taint.
     */
    @Required
    @ValidStrings({ "NO_SCHEDULE", "PREFER_NO_SCHEDULE", "NO_EXECUTE" })
    public NodeTaint.Effect getEffect() {
        return effect;
    }

    public void setEffect(NodeTaint.Effect effect) {
        this.effect = effect;
    }

    @Override
    public String primaryKey() {
        return String.format("key: %s, value %s, effect: %s", getKey(), getValue(), getEffect().toString());
    }

    @Override
    public void copyFrom(NodeTaint model) {
        setKey(model.getKey());
        setValue(getValue());
        setEffect(model.getEffect());
    }

    NodeTaint toNodeTaint() {
        return NodeTaint.newBuilder().setKey(getKey()).setValue(getValue()).setEffect(getEffect()).build();
    }
}
