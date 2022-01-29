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
import com.google.cloud.compute.v1.SSLHealthCheck;
import gyro.google.Copyable;

public class HealthCheckSslHealthCheck extends AbstractHealthCheck implements Copyable<SSLHealthCheck> {

    private String request;

    /**
     * The data to send once the connection has been established.
     */
    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    protected HealthCheck.Type getType() {
        return HealthCheck.Type.SSL;
    }

    @Override
    public void copyFrom(SSLHealthCheck model) {
        if (model.hasPort()) {
            setPort(model.getPort());
        }

        if (model.hasPortName()) {
            setPortName(model.getPortName());
        }

        if (model.hasPortSpecification()) {
            setPortSpecification(model.getPortSpecification());
        }

        if (model.hasProxyHeader()) {
            setProxyHeader(model.getProxyHeader());
        }

        if (model.hasResponse()) {
            setResponse(model.getResponse());
        }

        if (model.hasRequest()) {
            setRequest(getRequest());
        }
    }

    public SSLHealthCheck toSslHealthCheck() {
        SSLHealthCheck.Builder builder = SSLHealthCheck.newBuilder();

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

        if (getRequest() != null) {
            builder.setRequest(getRequest());
        }

        return builder.build();
    }
}
