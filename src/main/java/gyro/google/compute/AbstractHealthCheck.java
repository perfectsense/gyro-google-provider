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

import com.psddev.dari.util.StringUtils;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.ValidStrings;

public abstract class AbstractHealthCheck extends Diffable {

    private Integer port;
    private String portName;
    private String portSpecification;
    private String proxyHeader;
    private String response;
    private String requestPath;

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
     * The port name. If both port and portName are defined, port takes precedence.
     */
    @Updatable
    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    /**
     * The port specification determines how the port is selected for health checking.
     */
    @Updatable
    @ValidStrings({ "USE_FIXED_PORT", "USE_NAMED_PORT", "USE_SERVING_PORT" })
    public String getPortSpecification() {
        return portSpecification;
    }

    public void setPortSpecification(String portSpecification) {
        this.portSpecification = portSpecification;
    }

    /**
     * The proxy header to append before sending data to the backend.
     */
    @Updatable
    @ValidStrings({ "NONE", "PROXY_V1" })
    public String getProxyHeader() {
        return proxyHeader;
    }

    public void setProxyHeader(String proxyHeader) {
        this.proxyHeader = proxyHeader;
    }

    /**
     * The string to match in the response body. If left empty the status code determines health.
     */
    @Updatable
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    /**
     * The request path of the health check request. Prefixes the path with a ``/`` if missing.
     */
    @Updatable
    public String getRequestPath() {
        if (requestPath != null) {
            requestPath = StringUtils.ensureStart(requestPath, "/");
        }

        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    protected abstract String getType();
}
