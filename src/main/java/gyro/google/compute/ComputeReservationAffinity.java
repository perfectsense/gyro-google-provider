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

import com.google.cloud.compute.v1.ReservationAffinity;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class ComputeReservationAffinity extends Diffable implements Copyable<ReservationAffinity> {

    private String consumeReservationType;

    private String key;

    private List<String> values;

    /**
     * Specifies the type of reservation from which this instance can consume resources.
     * Valid values are:
     *
     * - ``ANY_RESERVATION`` (default)
     * - ``SPECIFIC_RESERVATION``
     * - ``NO_RESERVATION``
     */
    @ValidStrings({ "ANY_RESERVATION", "SPECIFIC_RESERVATION", "NO_RESERVATION" })
    public String getConsumeReservationType() {
        return consumeReservationType;
    }

    public void setConsumeReservationType(String consumeReservationType) {
        this.consumeReservationType = consumeReservationType;
    }

    /**
     * Corresponds to the label key of a reservation resource.
     * To target a ``SPECIFIC_RESERVATION`` by name, specify ``googleapis.com/reservation-name`` as the key and specify the name of your
     * reservation as its value.
     */
    @Required
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Corresponds to the label values of a reservation resource.
     */
    @Required
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
    public void copyFrom(ReservationAffinity model) {
        if (model.hasConsumeReservationType()) {
            setConsumeReservationType(model.getConsumeReservationType());
        }

        if (model.hasKey()) {
            setKey(model.getKey());
        }

        setValues(model.getValuesList());
    }

    public ReservationAffinity toReservationAffinity() {
        ReservationAffinity.Builder builder = ReservationAffinity.newBuilder();
        builder.addAllValues(getValues());

        if (getKey() != null) {
            builder.setKey(getKey());
        }

        if (getConsumeReservationType() != null) {
            builder.setConsumeReservationType(getConsumeReservationType());
        }

        return builder.build();
    }

    @Override
    public String primaryKey() {
        return "";
    }
}
