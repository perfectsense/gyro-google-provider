package gyro.google.pubsub;

import com.google.pubsub.v1.PushConfig;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class OidcToken extends Diffable implements Copyable<PushConfig.OidcToken> {

    private String audience;

    private String serviceAccountEmail;

    /**
     * Audience to be used when generating OIDC token. The audience claim identifies the recipients that the JWT is intended for. The audience value is a single case-sensitive string. Having multiple values (array) for the audience field is not supported. More info about the OIDC JWT token audience here: https://tools.ietf.org/html/rfc7519#section-4.1.3 Note: if not specified, the Push endpoint URL will be used.
     */
    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    /**
     * [Service account email](https://cloud.google.com/iam/docs/service-accounts) to be used for generating the OIDC token. The caller (for CreateSubscription, UpdateSubscription, and ModifyPushConfig RPCs) must have the iam.serviceAccounts.actAs permission for the service account.
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
