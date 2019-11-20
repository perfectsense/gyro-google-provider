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
import com.google.api.services.compute.model.SSLHealthCheck;

public class HealthCheckSslHealthCheck extends AbstractHealthCheck {

    @Override
    public void copyFrom(HealthCheck model) {
        if (model != null) {
            setPort(model.getHttpsHealthCheck().getPort());
            setPortSpecification(model.getHttpsHealthCheck().getPortSpecification());
            setHost(model.getHttpsHealthCheck().getHost());
            setResponse(model.getHttpsHealthCheck().getResponse());
            setRequestPath(model.getHttpsHealthCheck().getRequestPath());
            setProxyHeader(model.getHttpsHealthCheck().getProxyHeader());
        }
    }

    public SSLHealthCheck toSslHealthCheck() {
        return new SSLHealthCheck()
                .setPort(getPort())
                .setPortSpecification(getPortSpecification())
                .setResponse(getResponse())
                .setProxyHeader(getProxyHeader());
    }
}
