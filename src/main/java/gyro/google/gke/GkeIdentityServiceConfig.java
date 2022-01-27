package gyro.google.gke;

import com.google.container.v1beta1.IdentityServiceConfig;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class GkeIdentityServiceConfig extends Diffable implements Copyable<IdentityServiceConfig> {

    private Boolean enabled;

    /**
     * When set to ``true``, the identity service will be enabled.
     */
    @Required
    @Updatable
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(IdentityServiceConfig model) throws Exception {
        setEnabled(model.getEnabled());
    }

    public IdentityServiceConfig toIdentityServiceConfig() {
        return IdentityServiceConfig.newBuilder()
            .setEnabled(getEnabled())
            .build();
    }
}
