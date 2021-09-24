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

import com.google.container.v1.StatusCondition;
import gyro.core.resource.Diffable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class GkeStatusCondition extends Diffable implements Copyable<StatusCondition> {

    private String message;
    private StatusCondition.Code canonicalCode;

    /**
     * The human-friendly representation of the condition
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * The canonical code of the condition.
     */
    @ValidStrings({
        "GCE_STOCKOUT", "GKE_SERVICE_ACCOUNT_DELETED", "GCE_QUOTA_EXCEEDED", "SET_BY_OPERATOR", "CLOUD_KMS_KEY_ERROR" })
    public StatusCondition.Code getCanonicalCode() {
        return canonicalCode;
    }

    public void setCanonicalCode(StatusCondition.Code canonicalCode) {
        this.canonicalCode = canonicalCode;
    }

    @Override
    public String primaryKey() {
        return String.format("Code: %s, Message: %s", getCanonicalCode(), getMessage());
    }

    @Override
    public void copyFrom(StatusCondition model) {
        setCanonicalCode(getCanonicalCode());
        setMessage(getMessage());
    }

    StatusCondition toStatusCondition() {
        return StatusCondition.newBuilder().setCode(getCanonicalCode()).setMessage(getMessage()).build();
    }
}
