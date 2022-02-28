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
import java.util.Map;
import java.util.stream.Collectors;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.GetRegionSslCertificateRequest;
import com.google.cloud.compute.v1.ListRegionSslCertificatesRequest;
import com.google.cloud.compute.v1.ListRegionsRequest;
import com.google.cloud.compute.v1.Region;
import com.google.cloud.compute.v1.RegionSslCertificatesClient;
import com.google.cloud.compute.v1.RegionsClient;
import com.google.cloud.compute.v1.SslCertificate;
import com.psddev.dari.util.StringUtils;
import gyro.core.GyroException;
import gyro.core.Type;
import gyro.google.GoogleCredentials;
import gyro.google.GoogleFinder;
import gyro.google.util.Utils;

/**
 * Query for a regional SSL certificate.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    compute-region-ssl-certificate: $(external-query google::compute-region-ssl-certificate { name: 'region-ssl-certificate-example', region: 'us-east1' })
 */
@Type("compute-region-ssl-certificate")
public class RegionSslCertificateFinder
    extends GoogleFinder<RegionSslCertificatesClient, SslCertificate, SslCertificateResource> {

    private String name;
    private String region;

    /**
     * Name of the SSL certificate.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The region of the SSL certificate.
     */
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    protected List<SslCertificate> findAllGoogle(RegionSslCertificatesClient client) throws Exception {
        return getRegionSslCertificates(client, null);
    }

    @Override
    protected List<SslCertificate> findGoogle(RegionSslCertificatesClient client, Map<String, String> filters)
        throws Exception {
        List<SslCertificate> certificates = new ArrayList<>();
        String region = filters.remove("region");
        String name = filters.remove("name");
        String filter = Utils.convertToFilters(filters);

        try {
            if (filters.containsKey("zone")) {
                throw new GyroException("For zonal autoscaler, use 'compute-autoscaler' instead.");
            }

            if (region != null && name != null) {
                certificates.add(client.get(GetRegionSslCertificateRequest.newBuilder().setSslCertificate(name)
                    .setProject(getProjectId()).setRegion(region).build()));

            } else {
                if (region != null) {
                    certificates.addAll(getSslCertificates(client, filter, region));

                } else if (name != null) {
                    List<String> regions = getRegions();

                    for (String r : regions) {
                        try {
                            certificates.add(client.get(GetRegionSslCertificateRequest.newBuilder()
                                .setSslCertificate(name)
                                .setProject(getProjectId())
                                .setRegion(r)
                                .build()));

                        } catch (NotFoundException ex) {
                            // ignore
                        }
                    }
                } else {
                    certificates.addAll(getRegionSslCertificates(client, filter));
                }
            }
        } finally {
            client.close();
        }

        return certificates;
    }

    private List<SslCertificate> getRegionSslCertificates(RegionSslCertificatesClient client, String filter) {
        List<String> regionList = getRegions();

        List<SslCertificate> certificates = new ArrayList<>();

        try {
            for (String region : regionList) {
                certificates.addAll(getSslCertificates(client, filter, region));
            }

        } catch (NotFoundException ex) {
            // ignore

        } finally {
            client.close();
        }

        return certificates;
    }

    private List<SslCertificate> getSslCertificates(RegionSslCertificatesClient client, String filter, String region) {
        String pageToken = null;

        List<SslCertificate> certificates = new ArrayList<>();

        do {
            ListRegionSslCertificatesRequest.Builder builder = ListRegionSslCertificatesRequest.newBuilder()
                .setProject(getProjectId()).setRegion(region);

            if (pageToken != null) {
                builder.setPageToken(pageToken);
            }

            if (filter != null) {
                builder.setFilter(filter);
            }

            RegionSslCertificatesClient.ListPagedResponse response = client.list(builder.build());
            pageToken = response.getNextPageToken();

            if (response.getPage() != null && response.getPage().getResponse() != null) {
                certificates.addAll(response.getPage().getResponse().getItemsList());
            }

        } while (!StringUtils.isEmpty(pageToken));

        return certificates;
    }

    private List<String> getRegions() {
        String pageToken = null;
        List<String> regionList = new ArrayList<>();

        try (RegionsClient regionsClient = credentials(GoogleCredentials.class).createClient(RegionsClient.class)) {
            do {
                ListRegionsRequest.Builder builder = ListRegionsRequest.newBuilder()
                    .setProject(getProjectId());

                if (pageToken != null) {
                    builder.setPageToken(pageToken);
                }

                RegionsClient.ListPagedResponse list = regionsClient.list(builder.build());
                pageToken = list.getNextPageToken();
                regionList.addAll(list.getPage().getResponse().getItemsList()
                    .stream().map(Region::getName).collect(Collectors.toList()));

            } while (!StringUtils.isEmpty(pageToken));
        }

        return regionList;
    }
}
