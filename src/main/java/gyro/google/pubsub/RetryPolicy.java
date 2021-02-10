package gyro.google.pubsub;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import gyro.core.resource.Diffable;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public class RetryPolicy extends Diffable implements Copyable<com.google.pubsub.v1.RetryPolicy> {

    private Duration maximumBackoff;

    private Duration minimumBackoff;

    /**
     * The maximum delay between consecutive deliveries of a given message. Value should be between 0 and 600 seconds. Defaults to 600 seconds.
     *
     * @subresource gyro.google.pubsub.Duration
     */
    public Duration getMaximumBackoff() {
        return maximumBackoff;
    }

    public void setMaximumBackoff(Duration maximumBackoff) {
        this.maximumBackoff = maximumBackoff;
    }

    /**
     * The minimum delay between consecutive deliveries of a given message. Value should be between 0 and 600 seconds. Defaults to 10 seconds.
     *
     * @subresource gyro.google.pubsub.Duration
     */
    public Duration getMinimumBackoff() {
        return minimumBackoff;
    }

    public void setMinimumBackoff(Duration minimumBackoff) {
        this.minimumBackoff = minimumBackoff;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.pubsub.v1.RetryPolicy model) throws Exception {
        if (model.hasMaximumBackoff()) {
            Duration maxBackOff = newSubresource(Duration.class);
            maxBackOff.copyFrom(model.getMaximumBackoff());
            setMaximumBackoff(maxBackOff);
        }

        if (model.hasMinimumBackoff()) {
            Duration minBackOff = newSubresource(Duration.class);
            minBackOff.copyFrom(model.getMaximumBackoff());
            setMinimumBackoff(minBackOff);
        }
    }

    com.google.pubsub.v1.RetryPolicy toRetryPolicy() {
        com.google.pubsub.v1.RetryPolicy.Builder builder = com.google.pubsub.v1.RetryPolicy.newBuilder();

        if (getMaximumBackoff() != null) {
            builder.setMaximumBackoff(getMaximumBackoff().toDuration());
        }

        if (getMinimumBackoff() != null) {
            builder.setMinimumBackoff(getMinimumBackoff().toDuration());
        }

        return builder.build();
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if (getMinimumBackoff() == null && getMaximumBackoff() == null) {
            errors.add(new ValidationError(this, null, "At least one of 'minimum-back-off' or 'maximum-back-off' is required."));
        }

        return errors;
    }
}
