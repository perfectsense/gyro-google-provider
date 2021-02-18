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

import java.util.HashMap;
import java.util.Map;

import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class PushConfig extends Diffable implements Copyable<com.google.pubsub.v1.PushConfig> {

    private Map<String, String> attributes;
    private OidcToken oidcToken;
    private String pushEndpoint;

    /**
     * The endpoint configuration attributes that can be used to control different aspects of the message delivery.
     */
    public Map<String, String> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<>();
        }

        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    /**
     * The OIDC JWT token created by Pub/Sub as an authorization header in the HTTP request for every pushed message.
     *
     * @subresource gyro.google.pubsub.OidcToken
     */
    public OidcToken getOidcToken() {
        return oidcToken;
    }

    public void setOidcToken(OidcToken oidcToken) {
        this.oidcToken = oidcToken;
    }

    /**
     * The URL locating the endpoint to which messages should be pushed.
     */
    @Required
    public String getPushEndpoint() {
        return pushEndpoint;
    }

    public void setPushEndpoint(String pushEndpoint) {
        this.pushEndpoint = pushEndpoint;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.pubsub.v1.PushConfig model) throws Exception {
        setAttributes(model.getAttributesMap());
        setPushEndpoint(model.getPushEndpoint());

        setOidcToken(null);
        if (model.hasOidcToken()) {
            OidcToken oidcToken = newSubresource(OidcToken.class);
            oidcToken.copyFrom(model.getOidcToken());
            setOidcToken(oidcToken);
        }
    }

    com.google.pubsub.v1.PushConfig toPushConfig() {
        com.google.pubsub.v1.PushConfig.Builder builder = com.google.pubsub.v1.PushConfig.newBuilder()
            .setPushEndpoint(getPushEndpoint());

        if (getOidcToken() != null) {
            builder.setOidcToken(getOidcToken().toOidcToken());
        }

        if (!getAttributes().isEmpty()) {
            builder.putAllAttributes(getAttributes()).build();
        }

        return builder.build();
    }
}
