package gyro.google.gke;

import com.google.container.v1beta1.LoggingConfig;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class GkeLoggingConfig extends Diffable implements Copyable<LoggingConfig> {

    private GkeLoggingComponentConfig loggingComponentConfig;

    @Required
    @Updatable
    public GkeLoggingComponentConfig getLoggingComponentConfig() {
        return loggingComponentConfig;
    }

    public void setLoggingComponentConfig(GkeLoggingComponentConfig loggingComponentConfig) {
        this.loggingComponentConfig = loggingComponentConfig;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(LoggingConfig model) throws Exception {
        GkeLoggingComponentConfig config = newSubresource(GkeLoggingComponentConfig.class);
        config.copyFrom(model.getComponentConfig());
        setLoggingComponentConfig(config);
    }

    public LoggingConfig toLoggingConfig() {
        return LoggingConfig.newBuilder()
            .setComponentConfig(getLoggingComponentConfig().toLoggingComponentConfig())
            .build();
    }
}
