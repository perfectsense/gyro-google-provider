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

public class AcquireSsrsLeaseContext extends Diffable
    implements Copyable<com.google.api.services.sqladmin.model.AcquireSsrsLeaseContext> {

    private String duration;

    private String reportDatabase;

    private String serviceLogin;

    private String setupLogin;

    /**
     * Lease duration needed for SSRS setup.
     */
    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     * The report database to be used for SSRS setup.
     */
    public String getReportDatabase() {
        return reportDatabase;
    }

    public void setReportDatabase(String reportDatabase) {
        this.reportDatabase = reportDatabase;
    }

    /**
     * The username to be used as the service login to connect to the report database for SSRS setup.
     */
    public String getServiceLogin() {
        return serviceLogin;
    }

    public void setServiceLogin(String serviceLogin) {
        this.serviceLogin = serviceLogin;
    }

    /**
     * The username to be used as the setup login to connect to the database server for SSRS setup.
     */
    public String getSetupLogin() {
        return setupLogin;
    }

    public void setSetupLogin(String setupLogin) {
        this.setupLogin = setupLogin;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.AcquireSsrsLeaseContext model) {
        setDuration(model.getDuration());
        setReportDatabase(model.getReportDatabase());
        setServiceLogin(model.getServiceLogin());
        setSetupLogin(model.getSetupLogin());
    }
}
