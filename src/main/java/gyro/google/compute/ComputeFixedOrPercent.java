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
import java.util.Set;

import com.google.api.services.compute.model.FixedOrPercent;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.Min;
import gyro.core.validation.Range;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public class ComputeFixedOrPercent extends Diffable implements Copyable<FixedOrPercent> {

    private Integer fixed;

    private Integer percent;

    private Integer calculated;

    /**
     * Specifies a fixed number of VM instances.
     */
    @ConflictsWith("percent")
    @Min(0)
    public Integer getFixed() {
        return fixed;
    }

    public void setFixed(Integer fixed) {
        this.fixed = fixed;
    }

    /**
     * Specifies a percentage of instances between 0 to 100%, inclusive.
     * For example, specify 80 for 80%.
     */
    @ConflictsWith("fixed")
    @Range(min = 0, max = 100)
    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }

    /**
     * Absolute value of VM instances calculated based on the specific mode.
     * - If the value is fixed, then the calculated value is equal to the fixed value.
     * - If the value is a percent, then the calculated value is percent/100 * targetSize.
     *
     * For example, the calculated value of a 80% of a managed instance group with 150 instances would be (80/100 * 150) = 120 VM instances. If there is a remainder, the number is rounded up.
     */
    @Output
    public Integer getCalculated() {
        return calculated;
    }

    public void setCalculated(Integer calculated) {
        this.calculated = calculated;
    }

    public FixedOrPercent copyTo() {
        FixedOrPercent fixedOrPercent = new FixedOrPercent();
        fixedOrPercent.setFixed(getFixed());
        fixedOrPercent.setPercent(getPercent());
        fixedOrPercent.setCalculated(getCalculated());
        return fixedOrPercent;
    }

    @Override
    public void copyFrom(FixedOrPercent model) {
        setFixed(model.getFixed());
        setPercent(model.getPercent());
        setCalculated(model.getCalculated());
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if ((!configuredFields.contains("fixed") && !configuredFields.contains("percent"))
            || (getFixed() == null && getPercent() == null)) {
            errors.add(new ValidationError(
                this,
                "fixed",
                "Either 'fixed' or 'percent' is required!"));
            errors.add(new ValidationError(
                this,
                "percent",
                "Either 'fixed' or 'percent' is required!"));
        }
        return errors;
    }
}
