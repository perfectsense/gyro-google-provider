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

import com.google.api.services.compute.model.ServiceAccount;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class ComputeServiceAccount extends Diffable implements Copyable<ServiceAccount> {

    private String email;

    private List<String> scopes;

    /**
     * Email address of the service account.
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * List of scopes to be made available for this service account.
     */
    public List<String> getScopes() {
        if (scopes == null) {
            scopes = new ArrayList();
        }
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    @Override
    public void copyFrom(ServiceAccount model) {
        setEmail(model.getEmail());
        setScopes(model.getScopes());
    }

    public ServiceAccount toServiceAccount() {
        ServiceAccount serviceAccount = new ServiceAccount();
        serviceAccount.setEmail(getEmail());
        serviceAccount.setScopes(getScopes());
        return serviceAccount;
    }

    @Override
    public String primaryKey() {
        String email = getEmail();

        if (email != null) {
            return email;
        }
        return "";
    }
}
