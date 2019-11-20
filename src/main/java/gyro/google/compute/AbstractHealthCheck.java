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
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public abstract class AbstractHealthCheck extends Diffable implements Copyable<HealthCheck> {
    private String host;
    private Integer port;
    private String portSpecification;
    private String proxyHeader;
    private String requestPath;
    private String response;

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

    /**
     * The port for the domain name and/or ip address to monitor for the health check.
     */
    @Updatable
    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * Specifies how port is selected for health checking.
     */
    @Updatable
    @ValidStrings({"USE_FIXED_PORT", "USE_NAMED_PORT", "USE_SERVING_PORT"})
    public String getPortSpecification() {
        return portSpecification;
    }

    public void setPortSpecification(String portSpecification) {
        this.portSpecification = portSpecification;
    }

    /**
     * Specifies the type of proxy header to append before sending data to the backend, either NONE or PROXY_V1.
     * The default is NONE.
     */
    @Updatable
    @ValidStrings({"NONE", "PROXY_V1"})
    public String getProxyHeader() {
        return proxyHeader;
    }

    public void setProxyHeader(String proxyHeader) {
        this.proxyHeader = proxyHeader;
    }

    /**
     * The request path of the HTTPS health check request. The default value is /.
     */
    @Updatable
    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    /**
     * The string to match anywhere in the first 1024 bytes of the response body. If left empty (the default value),
     * the status code determines health. The response data can only be ASCII.
     */
    @Updatable
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}