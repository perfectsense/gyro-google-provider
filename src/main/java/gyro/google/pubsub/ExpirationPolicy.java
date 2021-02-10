package gyro.google.pubsub;

import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class ExpirationPolicy extends Diffable implements Copyable<com.google.pubsub.v1.ExpirationPolicy> {

    private Duration ttl;

    /**
     * Specifies the "time-to-live" duration for an associated resource.
     */
    @Required
    public Duration getTtl() {
        return ttl;
    }

    public void setTtl(Duration ttl) {
        this.ttl = ttl;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.pubsub.v1.ExpirationPolicy model) throws Exception {
        Duration duration = newSubresource(Duration.class);
        duration.copyFrom(model.getTtl());
        setTtl(duration);
    }

    com.google.pubsub.v1.ExpirationPolicy toExpirationPolicy() {
        return com.google.pubsub.v1.ExpirationPolicy.newBuilder().setTtl(getTtl().toDuration()).build();
    }
}
