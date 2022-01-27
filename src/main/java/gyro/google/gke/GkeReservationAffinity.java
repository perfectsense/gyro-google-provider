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

import java.util.List;

import com.google.container.v1beta1.ReservationAffinity;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class GkeReservationAffinity extends Diffable implements Copyable<ReservationAffinity> {

    private ReservationAffinity.Type consumeReservationType;
    private String key;
    private List<String> values;

    /**
     * The type of reservation consumption.
     */
    @Required
    @ValidStrings({ "NO_RESERVATION", "ANY_RESERVATION", "SPECIFIC_RESERVATION" })
    public ReservationAffinity.Type getConsumeReservationType() {
        return consumeReservationType;
    }

    public void setConsumeReservationType(ReservationAffinity.Type consumeReservationType) {
        this.consumeReservationType = consumeReservationType;
    }

    /**
     * The label key of a reservation resource. To target a ``SPECIFIC_RESERVATION`` by name, specify ``googleapis.com/reservation-name`` as the key and specify the name of your reservation as its value.
     */
    @Required
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * The label value(s) of reservation resource(s).
     */
    @Required
    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(ReservationAffinity model) {
        setConsumeReservationType(model.getConsumeReservationType());
        setKey(model.getKey());
        setValues(model.getValuesList());
    }

    ReservationAffinity toReservationAffinity() {
        return ReservationAffinity.newBuilder().setConsumeReservationType(getConsumeReservationType())
            .setKey(getKey()).addAllValues(getValues()).build();
    }
}
