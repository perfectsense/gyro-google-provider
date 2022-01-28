package gyro.google.gke;

import com.google.container.v1beta1.Autopilot;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class GkeAutopilot extends Diffable implements Copyable<Autopilot> {

    private Boolean enabled;

    /**
     * When set to ``true``, the cluster will be created as an autopilot cluster.
     */
    @Required
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void copyFrom(Autopilot model) throws Exception {
        setEnabled(model.getEnabled());
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public Autopilot toAutopilot() {
        return Autopilot.newBuilder()
            .setEnabled(getEnabled())
            .build();
    }
}
