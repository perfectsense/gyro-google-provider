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

package gyro.google.compute;

import com.google.api.services.compute.model.Image;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;
import gyro.google.storage.BucketResource;
import org.apache.commons.lang3.StringUtils;

/**
 * A raw disk file used to create an image.
 */
public class ImageRawDisk extends Diffable implements Copyable<Image.RawDisk> {

    private BucketResource source;

    /**
     * The storage file that should be the source of the image. File must use ``.tar.gz`` extension and the file inside the archive must be named ``disk.raw``. See `Manually importing virtual disks  <https://cloud.google.com/compute/docs/import/import-existing-image>`_ for instructions on how to create the file. (Required)
     */
    @Required
    public BucketResource getSource() {
        return source;
    }

    public void setSource(BucketResource source) {
        this.source = source;
    }

    @Override
    public void copyFrom(Image.RawDisk model) {
        if (StringUtils.isNotBlank(model.getSource())) {
            setSource(findById(BucketResource.class, model.getSource()));
        }
    }

    Image.RawDisk toRawDisk() {
        return new Image.RawDisk()
            .setSource(getSource().getSelfLink());
    }
}
