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

import com.google.api.services.compute.model.HealthCheck;
import com.google.api.services.compute.model.HTTPHealthCheck;

public class HealthCheckHttpHealthCheck extends AbstractHealthCheck {

    @Override
    public String primaryKey() {
        return "HttpHealthCheck";
    }

    @Override
    public void copyFrom(HealthCheck model) {
        if (model != null) {
            setPort(model.getHttpHealthCheck().getPort());
            setPortSpecification(model.getHttpHealthCheck().getPortSpecification());
            setHost(model.getHttpHealthCheck().getHost());
            setResponse(model.getHttpHealthCheck().getResponse());
            setRequestPath(model.getHttpHealthCheck().getRequestPath());
            setProxyHeader(model.getHttpHealthCheck().getProxyHeader());
        }
    }

    public HTTPHealthCheck toHttpHealthCheck() {
        return new HTTPHealthCheck()
                .setPort(getPort())
                .setPortSpecification(getPortSpecification())
                .setHost(getHost())
                .setRequestPath(getRequestPath())
                .setProxyHeader(getProxyHeader())
                .setResponse(getResponse());
    }
}
