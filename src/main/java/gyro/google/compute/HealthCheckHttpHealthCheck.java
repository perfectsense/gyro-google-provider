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
import gyro.core.resource.Updatable;

public class HealthCheckHttpHealthCheck extends AbstractHealthCheck {
    private String host;

    /**
     * The value of the host header in the HTTPS health check request. If left empty (default value),
     * the IP on behalf of which this health check is performed will be used.
     */
    @Updatable
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }


    @Override
    public String primaryKey() {
        return "HttpHealthCheck";
    }

    @Override
    public void copyFrom(HealthCheck model) {
        if (model != null) {
            setHost(model.getHttpHealthCheck().getHost());
            setPort(model.getHttpHealthCheck().getPort());
            setPortName(model.getHttpHealthCheck().getPortName());
            setPortSpecification(model.getHttpHealthCheck().getPortSpecification());
            setProxyHeader(model.getHttpHealthCheck().getProxyHeader());
            setResponse(model.getHttpHealthCheck().getResponse());
            setRequestPath(model.getHttpHealthCheck().getRequestPath());
        }
    }

    public HTTPHealthCheck toHttpHealthCheck() {
        return new HTTPHealthCheck()
                .setHost(getHost())
                .setPort(getPort())
                .setPortName(getPortName())
                .setPortSpecification(getPortSpecification())
                .setProxyHeader(getProxyHeader())
                .setResponse(getResponse())
                .setRequestPath(getRequestPath());
    }
}
