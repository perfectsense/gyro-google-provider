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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.SslCertificate;
import com.google.api.services.compute.model.SslCertificateAggregatedList;
import com.google.api.services.compute.model.SslCertificateList;
import com.google.api.services.compute.model.SslCertificatesScopedList;
import gyro.core.Type;
import gyro.google.GoogleFinder;

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
public class RegionSslCertificateFinder extends GoogleFinder<Compute, SslCertificate, SslCertificateResource> {

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
    protected List<SslCertificate> findAllGoogle(Compute client) throws Exception {
        List<SslCertificate> sslCertificates = new ArrayList<>();
        String nextPageToken = null;
        SslCertificateAggregatedList sslCertificateList;

        do {
            sslCertificateList = client.sslCertificates()
                .aggregatedList(getProjectId())
                .setPageToken(nextPageToken)
                .execute();
            if (sslCertificateList.getItems() != null) {
                sslCertificates.addAll(sslCertificateList.getItems().values().stream()
                    .map(SslCertificatesScopedList::getSslCertificates)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .filter(urlMap -> urlMap.getRegion() != null)
                    .collect(Collectors.toList()));
            }
            nextPageToken = sslCertificateList.getNextPageToken();
        } while (nextPageToken != null);

        return sslCertificates;
    }

    @Override
    protected List<SslCertificate> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        List<SslCertificate> sslCertificates;

        if (filters.containsKey("name")) {
            sslCertificates = Collections.singletonList(client.regionSslCertificates()
                .get(getProjectId(), filters.get("region"), filters.get("name"))
                .execute());
        } else {
            sslCertificates = new ArrayList<>();
            SslCertificateList sslCertificateList;
            String nextPageToken = null;

            do {
                sslCertificateList =
                    client.regionSslCertificates()
                        .list(getProjectId(), filters.get("region"))
                        .setPageToken(nextPageToken)
                        .execute();
                nextPageToken = sslCertificateList.getNextPageToken();

                if (sslCertificateList.getItems() != null) {
                    sslCertificates.addAll(sslCertificateList.getItems());
                }
            } while (nextPageToken != null);
        }

        return sslCertificates;
    }
}
