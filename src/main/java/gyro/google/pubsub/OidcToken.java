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

package gyro.google.pubsub;

import com.google.pubsub.v1.PushConfig;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class OidcToken extends Diffable implements Copyable<PushConfig.OidcToken> {

    private String audience;
    private String serviceAccountEmail;

    /**
     * The audience to be used when generating OIDC token. The audience claim identifies the recipients that the JWT is intended for.
     */
    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    /**
     * The email of the service account to be used for generating the OIDC token.
     */
    public String getServiceAccountEmail() {
        return serviceAccountEmail;
    }

    public void setServiceAccountEmail(String serviceAccountEmail) {
        this.serviceAccountEmail = serviceAccountEmail;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(PushConfig.OidcToken model) throws Exception {
        setAudience(model.getAudience());
        setServiceAccountEmail(model.getServiceAccountEmail());
    }

    PushConfig.OidcToken toOidcToken() {
        return PushConfig.OidcToken.newBuilder()
            .setAudience(getAudience())
            .setServiceAccountEmail(getServiceAccountEmail())
            .build();
    }
}
