package gyro.google.gke;

import java.util.List;

import com.google.container.v1beta1.LoggingComponentConfig;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

public class GkeLoggingComponentConfig extends Diffable implements Copyable<LoggingComponentConfig> {

    private List<LoggingComponentConfig.Component> component;

    @Updatable
    public List<LoggingComponentConfig.Component> getComponent() {
        return component;
    }

    public void setComponent(List<LoggingComponentConfig.Component> component) {
        this.component = component;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(LoggingComponentConfig model) throws Exception {
        setComponent(model.getEnableComponentsList());
    }

    public LoggingComponentConfig toLoggingComponentConfig() {
        return LoggingComponentConfig.newBuilder()
            .addAllEnableComponents(getComponent())
            .build();
    }
}
