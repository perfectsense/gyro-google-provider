/*
 * Copyright 2023, Brightspot.
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

import java.util.HashMap;
import java.util.Map;

import com.google.cloud.compute.v1.SecurityPolicyRuleHttpHeaderAction;
import com.google.cloud.compute.v1.SecurityPolicyRuleHttpHeaderActionHttpHeaderOption;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class SecurityPolicyRuleHttpHeaderActionConfig extends Diffable
    implements Copyable<SecurityPolicyRuleHttpHeaderAction> {

    private Map<String, String> headers;

    /**
     * The list of header names and values that will be added.
     */
    @Required
    @Updatable
    public Map<String, String> getHeaders() {
        if (headers == null) {
            headers = new HashMap<>();
        }

        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public void copyFrom(SecurityPolicyRuleHttpHeaderAction model) {
        getHeaders().clear();
        model.getRequestHeadersToAddsList().forEach(o -> getHeaders().put(o.getHeaderName(), o.getHeaderValue()));
    }

    @Override
    public String primaryKey() {
        return "";
    }

    SecurityPolicyRuleHttpHeaderAction toSecurityPolicyRuleHttpHeaderAction() {
        return SecurityPolicyRuleHttpHeaderAction.newBuilder()
            .addAllRequestHeadersToAdds(getHeaders().entrySet().stream()
                .map(o -> SecurityPolicyRuleHttpHeaderActionHttpHeaderOption.newBuilder()
                    .setHeaderName(o.getKey())
                    .setHeaderValue(o.getValue())
                    .build())
                .collect(java.util.stream.Collectors.toList()))
            .build();
    }
}
