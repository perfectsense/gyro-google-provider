package gyro.google.storage;

import com.google.api.services.storage.model.Bucket.Logging;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

/**
 * The bucket's logging configuration setting the destination bucket and optional name prefix for the current bucket's logs.
 */
public class BucketLogging extends Diffable implements Copyable<Logging> {

    private String logBucket;
    private String logObjectPrefix;

    /**
     * A prefix for log object names.
     */
    @Updatable
    public String getLogBucket() {
        return logBucket;
    }

    public void setLogBucket(String logBucket) {
        this.logBucket = logBucket;
    }

    /**
     * The destination bucket where the current bucket's logs should be placed.
     */
    @Updatable
    public String getLogObjectPrefix() {
        return logObjectPrefix;
    }

    public void setLogObjectPrefix(String logObjectPrefix) {
        this.logObjectPrefix = logObjectPrefix;
    }

    @Override
    public void copyFrom(Logging model) {
        if (model != null) {
            setLogBucket(model.getLogBucket());
            setLogObjectPrefix(model.getLogObjectPrefix());
        }
    }

    public Logging toBucketLogging() {
        return new Logging()
                .setLogBucket(getLogBucket())
                .setLogObjectPrefix(getLogObjectPrefix());
    }
}
