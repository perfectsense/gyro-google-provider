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
import gyro.google.Copyable;

public class GeminiInstanceConfig extends Diffable
    implements Copyable<com.google.api.services.sqladmin.model.GeminiInstanceConfig> {

    private Boolean activeQueryEnabled;

    private Boolean entitled;

    private Boolean flagRecommenderEnabled;

    private Boolean googleVacuumMgmtEnabled;

    private Boolean indexAdvisorEnabled;

    private Boolean oomSessionCancelEnabled;

    /**
     * Output only. Whether the active query is enabled.
     */
    public Boolean getActiveQueryEnabled() {
        return activeQueryEnabled;
    }

    public void setActiveQueryEnabled(Boolean activeQueryEnabled) {
        this.activeQueryEnabled = activeQueryEnabled;
    }

    /**
     * Output only. Whether Gemini is enabled.
     */
    public Boolean getEntitled() {
        return entitled;
    }

    public void setEntitled(Boolean entitled) {
        this.entitled = entitled;
    }

    /**
     * Output only. Whether the flag recommender is enabled.
     */
    public Boolean getFlagRecommenderEnabled() {
        return flagRecommenderEnabled;
    }

    public void setFlagRecommenderEnabled(Boolean flagRecommenderEnabled) {
        this.flagRecommenderEnabled = flagRecommenderEnabled;
    }

    /**
     * Output only. Whether the vacuum management is enabled.
     */
    public Boolean getGoogleVacuumMgmtEnabled() {
        return googleVacuumMgmtEnabled;
    }

    public void setGoogleVacuumMgmtEnabled(Boolean googleVacuumMgmtEnabled) {
        this.googleVacuumMgmtEnabled = googleVacuumMgmtEnabled;
    }

    /**
     * Output only. Whether the index advisor is enabled.
     */
    public Boolean getIndexAdvisorEnabled() {
        return indexAdvisorEnabled;
    }

    public void setIndexAdvisorEnabled(Boolean indexAdvisorEnabled) {
        this.indexAdvisorEnabled = indexAdvisorEnabled;
    }

    /**
     * Output only. Whether canceling the out-of-memory (OOM) session is enabled.
     */
    public Boolean getOomSessionCancelEnabled() {
        return oomSessionCancelEnabled;
    }

    public void setOomSessionCancelEnabled(Boolean oomSessionCancelEnabled) {
        this.oomSessionCancelEnabled = oomSessionCancelEnabled;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.GeminiInstanceConfig model) {
        setActiveQueryEnabled(model.getActiveQueryEnabled());
        setEntitled(model.getEntitled());
        setFlagRecommenderEnabled(model.getFlagRecommenderEnabled());
        setGoogleVacuumMgmtEnabled(model.getGoogleVacuumMgmtEnabled());
        setIndexAdvisorEnabled(model.getIndexAdvisorEnabled());
        setOomSessionCancelEnabled(model.getOomSessionCancelEnabled());
    }

    com.google.api.services.sqladmin.model.GeminiInstanceConfig toGeminiInstanceConfig() {
        com.google.api.services.sqladmin.model.GeminiInstanceConfig geminiInstanceConfig = new com.google.api.services.sqladmin.model.GeminiInstanceConfig();
        geminiInstanceConfig.setActiveQueryEnabled(getActiveQueryEnabled());
        geminiInstanceConfig.setEntitled(getEntitled());
        geminiInstanceConfig.setFlagRecommenderEnabled(getFlagRecommenderEnabled());
        geminiInstanceConfig.setGoogleVacuumMgmtEnabled(getGoogleVacuumMgmtEnabled());
        geminiInstanceConfig.setIndexAdvisorEnabled(getIndexAdvisorEnabled());
        geminiInstanceConfig.setOomSessionCancelEnabled(getOomSessionCancelEnabled());

        return geminiInstanceConfig;
    }
}
