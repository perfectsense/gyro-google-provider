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

import com.google.cloud.compute.v1.HTTP2HealthCheck;
import com.google.cloud.compute.v1.HealthCheck;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

public class HealthCheckHttp2HealthCheck extends AbstractHealthCheck implements Copyable<HTTP2HealthCheck> {

    private String host;

    /**
     * The value of the host header in the health check request.
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
        return "";
    }

    @Override
    protected HealthCheck.Type getType() {
        return HealthCheck.Type.HTTP2;
    }

    @Override
    public void copyFrom(HTTP2HealthCheck model) {
        if (model != null) {
            setHost(model.getHost());
            setPort(model.getPort());
            setPortName(model.getPortName());
            setPortSpecification(model.getPortSpecification() != null ? model.getPortSpecification().toString() : null);
            setProxyHeader(model.getProxyHeader() != null ? model.getProxyHeader().toString() : null);
            setResponse(model.getResponse());
            setRequestPath(model.getRequestPath());
        }
    }

    public HTTP2HealthCheck toHttp2HealthCheck() {
        HTTP2HealthCheck.Builder builder = HTTP2HealthCheck.newBuilder();

        if (getHost() != null) {
            builder.setHost(getHost());
        }

        if (getPort() != null) {
            builder.setPort(getPort());
        }

        if (getPortName() != null) {
            builder.setPortName(getPortName());
        }

        if (getPortSpecification() != null) {
            builder.setPortSpecification(getPortSpecification());
        }

        if (getProxyHeader() != null) {
            builder.setProxyHeader(getProxyHeader());
        }

        if (getResponse() != null) {
            builder.setResponse(getResponse());
        }

        if (getRequestPath() != null) {
            builder.setRequestPath(getRequestPath());
        }

        return builder.build();
    }
}
