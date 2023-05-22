package gyro.google.compute;

import com.google.cloud.compute.v1.SecurityPolicyAdvancedOptionsConfig;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class SecurityPolicyAdvancedOptions extends Diffable implements Copyable<SecurityPolicyAdvancedOptionsConfig> {

    private String logLevel;
    private String jsonParsing;

    /**
     * The log level.
     */
    @Required
    @Updatable
    @ValidStrings({"NORMAL", "VERBOSE"})
    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * The JSON parsing. Defaults to ``DISABLED``.
     */
    @Updatable
    @ValidStrings({"STANDARD", "DISABLED"})
    public String getJsonParsing() {
        if (jsonParsing == null) {
            jsonParsing = "DISABLED";
        }

        return jsonParsing;
    }

    public void setJsonParsing(String jsonParsing) {
        this.jsonParsing = jsonParsing;
    }

    @Override
    public void copyFrom(SecurityPolicyAdvancedOptionsConfig model) {

    }

    @Override
    public String primaryKey() {
        return "";
    }

    public SecurityPolicyAdvancedOptionsConfig toAdvancedOptions() {
        return SecurityPolicyAdvancedOptionsConfig.newBuilder()
            .setLogLevel(getLogLevel())
            .setJsonParsing(getJsonParsing())
            .build();
    }
}
