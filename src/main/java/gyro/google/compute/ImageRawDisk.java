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

import com.google.cloud.compute.v1.RawDisk;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

/**
 * A raw disk file used to create an image.
 */
public class ImageRawDisk extends Diffable implements Copyable<RawDisk> {

    private String source;

    /**
     *  The full Google Cloud Storage URL of the storage file that should be the source of the image. File must use ``.tar.gz`` extension and the file inside the archive must be named ``disk.raw``. See `Manually importing virtual disks  <https://cloud.google.com/compute/docs/import/import-existing-image>`_ for instructions on how to create the file.
     */
    @Required
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(RawDisk model) {
        if (model.hasSource()) {
            setSource(model.getSource());
        }
    }

    RawDisk toRawDisk() {
        return RawDisk.newBuilder()
            .setSource(getSource())
            .build();
    }
}
