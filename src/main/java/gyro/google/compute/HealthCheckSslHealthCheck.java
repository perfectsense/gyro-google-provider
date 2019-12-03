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

import com.google.api.services.compute.model.SSLHealthCheck;
import gyro.google.Copyable;

public class HealthCheckSslHealthCheck extends AbstractHealthCheck implements Copyable<SSLHealthCheck> {

    @Override
    protected String getType() {
        return "SSL";
    }

    @Override
    public void copyFrom(SSLHealthCheck model) {
        if (model != null) {
            setPort(model.getPort());
            setPortName(model.getPortName());
            setPortSpecification(model.getPortSpecification());
            setProxyHeader(model.getProxyHeader());
            setResponse(model.getResponse());
            setRequestPath(model.getRequest());
        }
    }

    public SSLHealthCheck toSslHealthCheck() {
        return new SSLHealthCheck()
            .setPort(getPort())
            .setPortName(getPortName())
            .setPortSpecification(getPortSpecification())
            .setProxyHeader(getProxyHeader())
            .setResponse(getResponse())
            .setRequest(getRequestPath());
    }
}
