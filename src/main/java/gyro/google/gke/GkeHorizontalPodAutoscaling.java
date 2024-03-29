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

import com.google.container.v1beta1.HorizontalPodAutoscaling;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class GkeHorizontalPodAutoscaling extends Diffable implements Copyable<HorizontalPodAutoscaling> {

    private Boolean disabled;

    /**
     * When set to ``true``, the Horizontal Pod Autoscaling feature is disabled in the cluster.
     */
    @Required
    @Updatable
    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(HorizontalPodAutoscaling model) throws Exception {
        setDisabled(model.getDisabled());
    }

    HorizontalPodAutoscaling toHorizontalPodAutoscaling() {
        return HorizontalPodAutoscaling.newBuilder().setDisabled(getDisabled()).build();
    }
}
