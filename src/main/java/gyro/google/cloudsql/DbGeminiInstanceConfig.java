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

package gyro.google.cloudsql;

import com.google.api.services.sqladmin.model.GeminiInstanceConfig;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.google.Copyable;

public class DbGeminiInstanceConfig extends Diffable implements Copyable<GeminiInstanceConfig> {

    private Boolean activeQueryEnabled;
    private Boolean entitled;
    private Boolean flagRecommenderEnabled;
    private Boolean googleVacuumMgmtEnabled;
    private Boolean indexAdvisorEnabled;
    private Boolean oomSessionCancelEnabled;

    /**
     * When set to ``true``, active query is enabled.
     */
    @Output
    public Boolean getActiveQueryEnabled() {
        return activeQueryEnabled;
    }

    public void setActiveQueryEnabled(Boolean activeQueryEnabled) {
        this.activeQueryEnabled = activeQueryEnabled;
    }

    /**
     * When set to ``true``, gemini is enabled.
     */
    @Output
    public Boolean getEntitled() {
        return entitled;
    }

    public void setEntitled(Boolean entitled) {
        this.entitled = entitled;
    }

    /**
     * When set to ``true``, flag recommender is enabled.
     */
    @Output
    public Boolean getFlagRecommenderEnabled() {
        return flagRecommenderEnabled;
    }

    public void setFlagRecommenderEnabled(Boolean flagRecommenderEnabled) {
        this.flagRecommenderEnabled = flagRecommenderEnabled;
    }

    /**
     * When set to ``true``, google vacuum mgmt is enabled.
     */
    @Output
    public Boolean getGoogleVacuumMgmtEnabled() {
        return googleVacuumMgmtEnabled;
    }

    public void setGoogleVacuumMgmtEnabled(Boolean googleVacuumMgmtEnabled) {
        this.googleVacuumMgmtEnabled = googleVacuumMgmtEnabled;
    }

    /**
     * When set to ``true``, index advisor is enabled.
     */
    @Output
    public Boolean getIndexAdvisorEnabled() {
        return indexAdvisorEnabled;
    }

    public void setIndexAdvisorEnabled(Boolean indexAdvisorEnabled) {
        this.indexAdvisorEnabled = indexAdvisorEnabled;
    }

    /**
     * When set to ``true``, oom session cancel is enabled.
     */
    @Output
    public Boolean getOomSessionCancelEnabled() {
        return oomSessionCancelEnabled;
    }

    public void setOomSessionCancelEnabled(Boolean oomSessionCancelEnabled) {
        this.oomSessionCancelEnabled = oomSessionCancelEnabled;
    }

    @Override
    public void copyFrom(GeminiInstanceConfig model) throws Exception {
        setActiveQueryEnabled(model.getActiveQueryEnabled());
        setEntitled(model.getEntitled());
        setFlagRecommenderEnabled(model.getFlagRecommenderEnabled());
        setGoogleVacuumMgmtEnabled(model.getGoogleVacuumMgmtEnabled());
        setIndexAdvisorEnabled(model.getIndexAdvisorEnabled());
        setOomSessionCancelEnabled(model.getOomSessionCancelEnabled());
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public GeminiInstanceConfig toGeminiInstanceConfig() {
        GeminiInstanceConfig geminiInstanceConfig = new GeminiInstanceConfig();
        geminiInstanceConfig.setActiveQueryEnabled(getActiveQueryEnabled());
        geminiInstanceConfig.setEntitled(getEntitled());
        geminiInstanceConfig.setFlagRecommenderEnabled(getFlagRecommenderEnabled());
        geminiInstanceConfig.setGoogleVacuumMgmtEnabled(getGoogleVacuumMgmtEnabled());
        geminiInstanceConfig.setIndexAdvisorEnabled(getIndexAdvisorEnabled());
        geminiInstanceConfig.setOomSessionCancelEnabled(getOomSessionCancelEnabled());

        return geminiInstanceConfig;
    }
}
