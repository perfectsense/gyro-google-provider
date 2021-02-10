package gyro.google.pubsub;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import gyro.core.resource.Diffable;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public class Duration extends Diffable implements Copyable<com.google.protobuf.Duration> {

    private Integer nanos;
    private Long seconds;

    /**
     * The nanosecond value of the duration.
     */
    public Integer getNanos() {
        return nanos;
    }

    public void setNanos(Integer nanos) {
        this.nanos = nanos;
    }

    /**
     * The second value for of the duration.
     */
    public Long getSeconds() {
        return seconds;
    }

    public void setSeconds(Long seconds) {
        this.seconds = seconds;
    }

    @Override
    public String primaryKey() {
        return String.format(
            "Duration: %s seconds, %s nano seconds",
            getSeconds() != null ? getSeconds().toString() : "0",
            getNanos() != null ? getNanos().toString() : "0");
    }

    @Override
    public void copyFrom(com.google.protobuf.Duration model) throws Exception {
        setNanos(model.getNanos());
        setSeconds(model.getSeconds());
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if (getSeconds() == null && getNanos() == null) {
            errors.add(new ValidationError(this, null, "At least one of 'seconds' or 'nanos' is required"));
        }

        return errors;
    }

    com.google.protobuf.Duration toDuration() {
        com.google.protobuf.Duration.Builder builder = com.google.protobuf.Duration.newBuilder();

        if (getSeconds() != null) {
            builder.setSeconds(getSeconds());
        }

        if (getNanos() != null) {
            builder.setNanos(getNanos());
        }

        return builder.build();
    }
}
