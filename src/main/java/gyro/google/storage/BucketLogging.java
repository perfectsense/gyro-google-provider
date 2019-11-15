package gyro.google.storage;

import com.google.api.services.storage.model.Bucket;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

/**
 * The bucket's logging configuration setting the destination bucket and optional name prefix for the current bucket's logs.
 */
public class BucketLogging extends Diffable implements Copyable<Bucket.Logging> {

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
    public void copyFrom(Bucket.Logging model) {
        setLogBucket(model.getLogBucket());
        setLogObjectPrefix(model.getLogObjectPrefix());
    }

    public Bucket.Logging toBucketLogging() {
        return new Bucket.Logging()
                .setLogBucket(getLogBucket())
                .setLogObjectPrefix(getLogObjectPrefix());
    }

    public static BucketLogging fromBucketLogging(Bucket.Logging model) {
        if (model != null) {
            BucketLogging logging = new BucketLogging();
            logging.setLogBucket(model.getLogBucket());
            logging.setLogObjectPrefix(model.getLogObjectPrefix());
            return logging;
        }
        return null;
    }
}
