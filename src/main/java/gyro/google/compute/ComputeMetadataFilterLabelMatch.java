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

import com.google.api.services.compute.model.MetadataFilterLabelMatch;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class ComputeMetadataFilterLabelMatch extends Diffable implements Copyable<MetadataFilterLabelMatch> {

    /**
     * Name of metadata label. The name can have a maximum length of 1024 characters and must be at
     * least 1 character long.
     */
    private java.lang.String name;

    /**
     * The value of the label must match the specified value. value can have a maximum length of 1024
     * characters.
     */
    private java.lang.String value;

    @Override
    public void copyFrom(MetadataFilterLabelMatch model) {

    }
}
