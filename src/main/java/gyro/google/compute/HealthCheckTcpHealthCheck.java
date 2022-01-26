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

import com.google.cloud.compute.v1.HealthCheck;
import com.google.cloud.compute.v1.TCPHealthCheck;
import gyro.google.Copyable;

public class HealthCheckTcpHealthCheck extends AbstractHealthCheck implements Copyable<TCPHealthCheck> {

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    protected HealthCheck.Type getType() {
        return HealthCheck.Type.TCP;
    }

    @Override
    public void copyFrom(TCPHealthCheck model) {
        if (model != null) {
            setPort(model.getPort());
            setPortName(model.getPortName());
            setPortSpecification(
                model.getPortSpecification() != null ? model.getPortSpecification().toString().toUpperCase() : null);
            setProxyHeader(model.getProxyHeader() != null ? model.getProxyHeader().toString() : null);
            setResponse(model.getResponse());
            setRequestPath(model.getRequest());
        }
    }

    public TCPHealthCheck toTcpHealthCheck() {
        TCPHealthCheck.Builder builder = TCPHealthCheck.newBuilder();

        if (getPort() != null) {
            builder.setPort(getPort());
        }

        if (getPortName() != null) {
            builder.setPortName(getPortName());
        }

        if (getPortSpecification() != null) {
            builder.setPortSpecification(TCPHealthCheck.PortSpecification.valueOf(getPortSpecification()));
        }

        if (getProxyHeader() != null) {
            builder.setProxyHeader(TCPHealthCheck.ProxyHeader.valueOf(getProxyHeader()));
        }

        if (getResponse() != null) {
            builder.setResponse(getResponse());
        }

        if (getRequestPath() != null) {
            builder.setRequest(getRequestPath());
        }

        return builder.build();
    }
}
