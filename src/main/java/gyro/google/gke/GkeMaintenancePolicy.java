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

import com.google.container.v1.MaintenancePolicy;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class GkeMaintenancePolicy extends Diffable implements Copyable<MaintenancePolicy> {

    private String version;
    private GkeMaintenanceWindow maintenanceWindow;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public GkeMaintenanceWindow getMaintenanceWindow() {
        return maintenanceWindow;
    }

    public void setMaintenanceWindow(GkeMaintenanceWindow maintenanceWindow) {
        this.maintenanceWindow = maintenanceWindow;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(MaintenancePolicy model) throws Exception {
        setVersion(model.getResourceVersion());

        setMaintenanceWindow(null);
        if (model.hasWindow()) {
            GkeMaintenanceWindow window = newSubresource(GkeMaintenanceWindow.class);
            window.copyFrom(model.getWindow());
            setMaintenanceWindow(window);
        }
    }

    MaintenancePolicy toMaintenancePolicy() {
        return MaintenancePolicy.newBuilder().setResourceVersion(getVersion())
            .setWindow(getMaintenanceWindow().toMaintenanceWindow()).build();
    }
}
