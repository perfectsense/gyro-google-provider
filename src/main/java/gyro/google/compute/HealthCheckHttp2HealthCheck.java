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

import com.google.api.services.compute.model.HTTP2HealthCheck;
import com.google.api.services.compute.model.HealthCheck;

public class HealthCheckHttp2HealthCheck extends AbstractHealthCheck {

    @Override
    public String primaryKey() {
        return "Http2HealthCheck";
    }

    @Override
    public void copyFrom(HealthCheck model) {
        if (model != null) {
            setPort(model.getHttp2HealthCheck().getPort());
            setPortSpecification(model.getHttp2HealthCheck().getPortSpecification());
            setHost(model.getHttp2HealthCheck().getHost());
            setResponse(model.getHttp2HealthCheck().getResponse());
            setRequestPath(model.getHttp2HealthCheck().getRequestPath());
            setProxyHeader(model.getHttp2HealthCheck().getProxyHeader());
        }
    }

    public HTTP2HealthCheck toHttp2HealthCheck() {
        return new HTTP2HealthCheck()
                .setPort(getPort())
                .setPortSpecification(getPortSpecification())
                .setHost(getHost())
                .setResponse(getResponse())
                .setRequestPath(getRequestPath())
                .setProxyHeader(getProxyHeader());
    }
}
