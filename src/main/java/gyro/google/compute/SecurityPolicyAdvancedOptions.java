/*
 * Copyright 2023, Brightspot.
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

import com.google.cloud.compute.v1.SecurityPolicyAdvancedOptionsConfig;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class SecurityPolicyAdvancedOptions extends Diffable implements Copyable<SecurityPolicyAdvancedOptionsConfig> {

    private String logLevel;
    private String jsonParsing;

    /**
     * The log level.
     */
    @Required
    @Updatable
    @ValidStrings({"NORMAL", "VERBOSE"})
    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * The JSON parsing. Defaults to ``DISABLED``.
     */
    @Updatable
    @ValidStrings({"STANDARD", "DISABLED"})
    public String getJsonParsing() {
        if (jsonParsing == null) {
            jsonParsing = "DISABLED";
        }

        return jsonParsing;
    }

    public void setJsonParsing(String jsonParsing) {
        this.jsonParsing = jsonParsing;
    }

    @Override
    public void copyFrom(SecurityPolicyAdvancedOptionsConfig model) {
        setJsonParsing(model.getJsonParsing());
        setLogLevel(model.getLogLevel());
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public SecurityPolicyAdvancedOptionsConfig toAdvancedOptions() {
        return SecurityPolicyAdvancedOptionsConfig.newBuilder()
            .setLogLevel(getLogLevel())
            .setJsonParsing(getJsonParsing())
            .build();
    }
}
