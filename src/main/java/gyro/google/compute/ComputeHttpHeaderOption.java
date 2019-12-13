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

import com.google.api.services.compute.model.HttpHeaderOption;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class ComputeHttpHeaderOption extends Diffable implements Copyable<HttpHeaderOption> {

    /**
     * The name of the header.
     */
    private String headerName;

    /**
     * The value of the header to add.
     */
    private String headerValue;

    /**
     * If false, headerValue is appended to any values that already exist for the header. If true,
     * headerValue is set for the header, discarding any values that were set for that header. The
     * default value is false.
     */
    private Boolean replace;

    @Override
    public void copyFrom(HttpHeaderOption model) {

    }
}
