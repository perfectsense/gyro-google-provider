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
import com.google.cloud.compute.v1.DeleteRegionTargetHttpsProxyRequest;
import com.google.cloud.compute.v1.GetRegionTargetHttpsProxyRequest;
import com.google.cloud.compute.v1.InsertRegionTargetHttpsProxyRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.RegionTargetHttpsProxiesClient;
import com.google.cloud.compute.v1.TargetHttpsProxy;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.CollectionMax;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.util.Utils;

/**
 * Creates a regional target https proxy.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-region-target-https-proxy region-target-https-proxy-example
 *         name: "region-target-https-proxy-example"
 *         description: "Region target https proxy description."
 *         region: "us-central1"
 *         region-url-map: $(google::compute-region-url-map region-url-map-example-region-target-https-proxy)
 *         region-ssl-certificates: [ $(google::compute-region-ssl-certificate region-ssl-certificate-example-region-target-https-proxy) ]
 *     end
 */
@Type("compute-region-target-https-proxy")
public class RegionTargetHttpsProxyResource extends AbstractTargetHttpsProxyResource {

    private String region;
    private RegionUrlMapResource regionUrlMap;
    private List<RegionSslCertificateResource> regionSslCertificates;
    private String quicOverride;
    private SslPolicyResource sslPolicy;

    /**
     * Region where the target proxy resides.
     */
    @Required
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = Utils.extractName(region);
    }

    /**
     * The region URL map that defines the mapping from URL to a backend service or bucket.
     */
    @Required
    public RegionUrlMapResource getRegionUrlMap() {
        return regionUrlMap;
    }

    public void setRegionUrlMap(RegionUrlMapResource regionUrlMap) {
        this.regionUrlMap = regionUrlMap;
    }

    /**
     * List of region SSL certificates that are used to authenticate connections between users and the load balancer.
     */
    @Required
    @CollectionMax(15)
    public List<RegionSslCertificateResource> getRegionSslCertificates() {
        if (regionSslCertificates == null) {
            regionSslCertificates = new ArrayList<>();
        }
        return regionSslCertificates;
    }

    public void setRegionSslCertificates(List<RegionSslCertificateResource> regionSslCertificates) {
        this.regionSslCertificates = regionSslCertificates;
    }

    /**
     * Specifies the QUIC override policy for this target proxy. Default value is ``NONE``.
     */
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
    public SslPolicyResource getSslPolicy() {
        return sslPolicy;
    }

    public void setSslPolicy(SslPolicyResource sslPolicy) {
        this.sslPolicy = sslPolicy;
    }

    @Override
    public void copyFrom(TargetHttpsProxy model) {
        super.copyFrom(model);

        setRegion(model.getRegion());
        setQuicOverride(model.getQuicOverride().toString());

        setRegionUrlMap(null);
        if (model.getUrlMap() != null) {
            setRegionUrlMap(findById(RegionUrlMapResource.class, model.getUrlMap()));
        }

        getRegionSslCertificates().clear();
        if (model.getSslCertificatesList() != null) {
            setRegionSslCertificates(model.getSslCertificatesList().stream()
                .map(cert -> findById(RegionSslCertificateResource.class, cert))
                .collect(Collectors.toList()));
        }

        setSslPolicy(null);
        if (model.getSslPolicy() != null) {
            setSslPolicy(findById(SslPolicyResource.class, model.getSslPolicy()));
        }
    }

    @Override
    protected boolean doRefresh() throws Exception {
        try (RegionTargetHttpsProxiesClient client = createClient(RegionTargetHttpsProxiesClient.class)) {
            TargetHttpsProxy proxies = getRegionTargetHttpsProxy(client);

            if (proxies == null) {
                return false;
            }

            copyFrom(proxies);

            return true;
        }
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        try (RegionTargetHttpsProxiesClient client = createClient(RegionTargetHttpsProxiesClient.class)) {
            TargetHttpsProxy.Builder builder = toTargetHttpsProxy().toBuilder();
            builder.setRegion(getRegion());
            builder.setUrlMap(getRegionUrlMap().getSelfLink());
            if (getQuicOverride() != null) {
                builder.setQuicOverride(getQuicOverride());
            }
            if (getSslPolicy() != null) {
                builder.setSslPolicy(getSslPolicy().getSelfLink());
            }
            if (getRegionSslCertificates() != null) {
                builder.addAllSslCertificates(getRegionSslCertificates().stream()
                    .map(AbstractSslCertificateResource::getSelfLink)
                    .collect(Collectors.toList()));
            }

            Operation operation = client.insertCallable().call(InsertRegionTargetHttpsProxyRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setTargetHttpsProxyResource(builder)
                .build());

            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {

    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (RegionTargetHttpsProxiesClient client = createClient(RegionTargetHttpsProxiesClient.class)) {
            Operation response = client.deleteCallable().call(DeleteRegionTargetHttpsProxyRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setTargetHttpsProxy(getName())
                .build());

            waitForCompletion(response);
        }
    }

    private TargetHttpsProxy getRegionTargetHttpsProxy(RegionTargetHttpsProxiesClient client) {
        TargetHttpsProxy proxies = null;

        try {
            proxies = client.get(GetRegionTargetHttpsProxyRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setTargetHttpsProxy(getName())
                .build());

        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore
        }

        return proxies;
    }
}
