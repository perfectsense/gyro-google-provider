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

import com.google.api.services.compute.model.MetadataFilter;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class ComputeMetadataFilter extends Diffable implements Copyable<MetadataFilter> {

    /**
     * The list of label value pairs that must match labels in the provided metadata based on
     * filterMatchCriteria This list must not be empty and can have at the most 64 entries.
     */
    private java.util.List<ComputeMetadataFilterLabelMatch> filterLabels;

    /**
     * Specifies how individual filterLabel matches within the list of filterLabels contribute towards
     * the overall metadataFilter match. Supported values are:   - MATCH_ANY: At least one of the
     * filterLabels must have a matching label in the provided metadata.  - MATCH_ALL: All
     * filterLabels must have matching labels in the provided metadata.
     */
    private java.lang.String filterMatchCriteria;

    @Override
    public void copyFrom(MetadataFilter model) {

    }
}
