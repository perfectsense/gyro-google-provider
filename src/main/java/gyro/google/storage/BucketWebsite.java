/*
 * Copyright 2019, Perfect Sense, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gyro.google.storage;

import com.google.api.services.storage.model.Bucket.Website;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

/**
 * Configuration controlling how the service behaves when accessing bucket contents as a web site.
 */
public class BucketWebsite extends Diffable implements Copyable<Website> {

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
    public String primaryKey() {
        return "website-config";
    }

    @Override
    public void copyFrom(Website model) {
        setMainPageSuffix(model.getMainPageSuffix());
        setNotFoundPage(model.getNotFoundPage());
    }

    public Website toBucketWebsite() {
        return new Website().setMainPageSuffix(getMainPageSuffix()).setNotFoundPage(getNotFoundPage());
    }
}
