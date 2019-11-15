package gyro.google.storage;

import com.google.api.services.storage.model.Bucket;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

/**
 * Configuration controlling how the service behaves when accessing bucket contents as a web site.
 */
public class BucketWebsite extends Diffable implements Copyable<Bucket.Website> {

    private String mainPageSuffix;
    private String notFoundPage;

    /**
     * If the requested object path is missing the service will ensure the path has a trailing '/', append the suffix, and attempt to retrieve the resulting object.
     */
    @Updatable
    public String getMainPageSuffix() {
        return mainPageSuffix;
    }

    public void setMainPageSuffix(String mainPageSuffix) {
        this.mainPageSuffix = mainPageSuffix;
    }

    /**
     * If the requested object path is missing, and any mainPageSuffix object is missing, if applicable, the service will return the named object from this bucket as the content
     */
    @Updatable
    public String getNotFoundPage() {
        return notFoundPage;
    }

    public void setNotFoundPage(String notFoundPage) {
        this.notFoundPage = notFoundPage;
    }

    @Override
    public void copyFrom(Bucket.Website model) {
        if (model != null) {
            setMainPageSuffix(model.getMainPageSuffix());
            setNotFoundPage(model.getNotFoundPage());
        }
    }

    public Bucket.Website toBucketWebsite() {
        return new Bucket.Website().setMainPageSuffix(getMainPageSuffix()).setNotFoundPage(getNotFoundPage());
    }
}
