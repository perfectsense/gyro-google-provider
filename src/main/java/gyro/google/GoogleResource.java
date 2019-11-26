/*
 * Copyright 2019, Perfect Sense, Inc.
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

package gyro.google;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import gyro.core.resource.Resource;

public abstract class GoogleResource extends Resource {

    protected static <T extends AbstractGoogleJsonClient> T createClient(
        Class<T> clientClass,
        GoogleCredentials credentials) {
        return credentials.createClient(clientClass);
    }

    protected <T extends AbstractGoogleJsonClient> T createClient(Class<T> clientClass) {
        return createClient(clientClass, credentials(GoogleCredentials.class));
    }

    protected String getProjectId() {
        return credentials(GoogleCredentials.class).getProjectId();
    }

}
