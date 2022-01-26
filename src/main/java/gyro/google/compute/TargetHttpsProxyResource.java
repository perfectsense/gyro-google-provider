/*
 * Copyright 2020, Perfect Sense, Inc.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.GetTargetHttpsProxyRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.SslPolicyReference;
import com.google.cloud.compute.v1.TargetHttpsProxiesClient;
import com.google.cloud.compute.v1.TargetHttpsProxiesSetQuicOverrideRequest;
import com.google.cloud.compute.v1.TargetHttpsProxiesSetSslCertificatesRequest;
import com.google.cloud.compute.v1.TargetHttpsProxy;
import com.google.cloud.compute.v1.UrlMapReference;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.CollectionMax;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;

/**
 * Creates a target https proxy.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-target-https-proxy target-https-proxy-example
 *         name: "target-https-proxy-example"
 *         description: "Target https proxy description."
 *         quic-override: "ENABLE"
 *         url-map: $(google::compute-url-map url-map-example-target-https-proxy)
 *         ssl-policy: $(google::compute-ssl-policy ssl-policy-example-target-https-proxy)
 *         ssl-certificates: [ $(google::compute-ssl-certificate ssl-certificate-example-target-https-proxy) ]
 *     end
 */
@Type("compute-target-https-proxy")
public class TargetHttpsProxyResource extends AbstractTargetHttpsProxyResource {

    private UrlMapResource urlMap;
    private List<SslCertificateResource> sslCertificates;
    private String quicOverride;
    private SslPolicyResource sslPolicy;

    /**
     * The URL map that defines the mapping from URL to a backend service or bucket.
     */
    @Required
    @Updatable
    public UrlMapResource getUrlMap() {
        return urlMap;
    }

    public void setUrlMap(UrlMapResource urlMap) {
        this.urlMap = urlMap;
    }

    /**
     * List of SSL certificates that are used to authenticate connections between users and the load balancer.
     */
    @Required
    @Updatable
    @CollectionMax(15)
    public List<SslCertificateResource> getSslCertificates() {
        if (sslCertificates == null) {
            sslCertificates = new ArrayList<>();
        }

        return sslCertificates;
    }

    public void setSslCertificates(List<SslCertificateResource> sslCertificates) {
        this.sslCertificates = sslCertificates;
    }

    /**
     * Specifies the QUIC override policy for this target proxy. Default value is ``NONE``.
     */
    @Updatable
    @ValidStrings({ "NONE", "ENABLE", "DISABLE" })
    public String getQuicOverride() {
        return quicOverride;
    }

    public void setQuicOverride(String quicOverride) {
        this.quicOverride = quicOverride;
    }

    /**
     * SslPolicy that will be associated with this target proxy. If not set, the target proxy has no SSL policy configured.
     */
    @Updatable
    public SslPolicyResource getSslPolicy() {
        return sslPolicy;
    }

    public void setSslPolicy(SslPolicyResource sslPolicy) {
        this.sslPolicy = sslPolicy;
    }

    @Override
    public void copyFrom(TargetHttpsProxy model) {
        super.copyFrom(model);

        setQuicOverride(model.getQuicOverride().toString());

        setUrlMap(null);
        if (model.getUrlMap() != null) {
            setUrlMap(findById(UrlMapResource.class, model.getUrlMap()));
        }

        getSslCertificates().clear();
        if (model.getSslCertificatesList() != null) {
            setSslCertificates(model.getSslCertificatesList().stream()
                .map(cert -> findById(SslCertificateResource.class, cert))
                .collect(Collectors.toList()));
        }

        setSslPolicy(null);
        if (model.getSslPolicy() != null) {
            setSslPolicy(findById(SslPolicyResource.class, model.getSslPolicy()));
        }
    }

    @Override
    protected boolean doRefresh() throws Exception {
        try (TargetHttpsProxiesClient client = createClient(TargetHttpsProxiesClient.class)) {
            TargetHttpsProxy targetHttpProxy = getTargetHttpsProxy(client);

            if (targetHttpProxy == null) {
                return false;
            }

            copyFrom(targetHttpProxy);

            return true;
        }
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        try (TargetHttpsProxiesClient client = createClient(TargetHttpsProxiesClient.class)) {
            TargetHttpsProxy.Builder targetHttpsProxy = toTargetHttpsProxy().toBuilder();
            targetHttpsProxy.setUrlMap(getUrlMap().getSelfLink());

            if (getQuicOverride() != null) {
                targetHttpsProxy.setQuicOverride(TargetHttpsProxy.QuicOverride.valueOf(getQuicOverride()));
            }

            if (getSslCertificates() != null) {
                targetHttpsProxy.setSslPolicy(getSslPolicy().getSelfLink());
            }

            targetHttpsProxy.addAllSslCertificates(getSslCertificates().stream()
                .map(AbstractSslCertificateResource::getSelfLink).collect(Collectors.toList()));

            Operation operation = client.insert(getProjectId(), targetHttpsProxy.build());
            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    protected void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        try (TargetHttpsProxiesClient client = createClient(TargetHttpsProxiesClient.class)) {

            if (changedFieldNames.contains("quic-override")) {
                TargetHttpsProxiesSetQuicOverrideRequest.Builder builder = TargetHttpsProxiesSetQuicOverrideRequest.newBuilder();
                builder.setQuicOverride(getQuicOverride() != null
                    ? TargetHttpsProxiesSetQuicOverrideRequest.QuicOverride.valueOf(getQuicOverride())
                    : TargetHttpsProxiesSetQuicOverrideRequest.QuicOverride.NONE);

                Operation response = client.setQuicOverride(getProjectId(), getName(), builder.build());
                waitForCompletion(response);
            }

            if (changedFieldNames.contains("ssl-certificates")) {
                TargetHttpsProxiesSetSslCertificatesRequest.Builder sslCertificates = TargetHttpsProxiesSetSslCertificatesRequest
                    .newBuilder();
                sslCertificates.addAllSslCertificates(getSslCertificates().stream()
                    .map(AbstractSslCertificateResource::getSelfLink)
                    .collect(Collectors.toList()));
                Operation response = client.setSslCertificates(getProjectId(), getName(), sslCertificates.build());
                waitForCompletion(response);
            }

            if (changedFieldNames.contains("ssl-policy")) {
                SslPolicyReference.Builder sslPolicy = SslPolicyReference.newBuilder();
                sslPolicy.setSslPolicy(getSslPolicy().getSelfLink());
                Operation response = client.setSslPolicy(getProjectId(), getName(), sslPolicy.build());
                waitForCompletion(response);
            }

            if (changedFieldNames.contains("url-map")) {
                UrlMapReference.Builder urlMapReference = UrlMapReference.newBuilder();
                urlMapReference.setUrlMap(getUrlMap().getSelfLink());
                Operation operation = client.setUrlMap(getProjectId(), getName(), urlMapReference.build());
                waitForCompletion(operation);
            }
        }

        refresh();
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        try (TargetHttpsProxiesClient client = createClient(TargetHttpsProxiesClient.class)) {
            Operation response = client.delete(getProjectId(), getName());
            waitForCompletion(response);
        }
    }

    static boolean isTargetHttpsProxy(String selfLink) {
        return selfLink != null && selfLink.contains("targetHttpsProxies");
    }

    private TargetHttpsProxy getTargetHttpsProxy(TargetHttpsProxiesClient client) {
        TargetHttpsProxy targetHttpsProxy = null;

        try {
            targetHttpsProxy = client.get(GetTargetHttpsProxyRequest.newBuilder()
                .setProject(getProjectId())
                .setTargetHttpsProxy(getName())
                .build());

        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore
        }

        return targetHttpsProxy;
    }
}
