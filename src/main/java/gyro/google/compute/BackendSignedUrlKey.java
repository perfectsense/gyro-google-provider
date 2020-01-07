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

import com.google.api.services.compute.model.SignedUrlKey;
import gyro.core.resource.Diffable;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;

public class BackendSignedUrlKey extends Diffable {

    private String key;
    private String value;

    /**
     * The name of the key. The name must be 1-63 characters long and the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash. (Required)
     */
    @Required
    @Regex("(?:(?:[-a-z0-9]{1,63}\\.)*(?:[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?):)?(?:[0-9]{1,19}|(?:[a-z0-9](?:[-a-z0-9]{0,61}[a-z0-9])?))")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 128-bit key value used for signing the URL. The key value must be a valid RFC 4648 Section 5 base64url encoded string. (Required)
     */
    @Required
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String primaryKey() {
        return getKey();
    }

    SignedUrlKey toSignedUrlKey() {
        SignedUrlKey key = new SignedUrlKey();
        key.setKeyName(getKey());
        key.setKeyValue(getValue());

        return key;
    }
}
