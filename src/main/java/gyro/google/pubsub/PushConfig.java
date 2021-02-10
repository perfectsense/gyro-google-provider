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
     * Endpoint configuration attributes that can be used to control different aspects of the message delivery. The only currently supported attribute is `x-goog-version`, which you can use to change the format of the pushed message. This attribute indicates the version of the data expected by the endpoint. This controls the shape of the pushed message (i.e., its fields and metadata). If not present during the `CreateSubscription` call, it will default to the version of the Pub/Sub API used to make such call. If not present in a `ModifyPushConfig` call, its value will not be changed. `GetSubscription` calls will always return a valid version, even if the subscription was created without this attribute. The only supported values for the `x-goog-version` attribute are: * `v1beta1`: uses the push format defined in the v1beta1 Pub/Sub API. * `v1` or `v1beta2`: uses the push format defined in the v1 Pub/Sub API. For example: attributes { "x-goog-version": "v1" }
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
     * If specified, Pub/Sub will generate and attach an OIDC JWT token as an `Authorization` header in the HTTP request for every pushed message.
     */
    public OidcToken getOidcToken() {
        return oidcToken;
    }

    public void setOidcToken(OidcToken oidcToken) {
        this.oidcToken = oidcToken;
    }

    /**
     * A URL locating the endpoint to which messages should be pushed. For example, a Webhook endpoint might use `https://example.com/push`.
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
        com.google.pubsub.v1.PushConfig.Builder builder = com.google.pubsub.v1.PushConfig.newBuilder();

        if (getOidcToken() != null) {
            builder.setOidcToken(getOidcToken().toOidcToken());
        }

        if (!getAttributes().isEmpty()) {
            builder.putAllAttributes(getAttributes()).build();
        }

        return builder.build();
    }
}
