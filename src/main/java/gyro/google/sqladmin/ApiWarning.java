/*
 * Copyright 2024, Brightspot.
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

package gyro.google.sqladmin;

import gyro.core.resource.Diffable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class ApiWarning extends Diffable implements Copyable<com.google.api.services.sqladmin.model.ApiWarning> {

    private String code;

    private String message;

    private String region;

    /**
     * Code to uniquely identify the warning type.
     */
    @ValidStrings({
        "SQL_API_WARNING_CODE_UNSPECIFIED",
        "REGION_UNREACHABLE",
        "MAX_RESULTS_EXCEEDS_LIMIT",
        "COMPROMISED_CREDENTIALS",
        "INTERNAL_STATE_FAILURE"
    })
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * The warning message.
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * The region name for REGION_UNREACHABLE warning.
     */
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.ApiWarning model) {
        setCode(model.getCode());
        setMessage(model.getMessage());
        setRegion(model.getRegion());
    }
}
