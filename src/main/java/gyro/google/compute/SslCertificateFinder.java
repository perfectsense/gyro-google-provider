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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.SslCertificate;
import com.google.api.services.compute.model.SslCertificateList;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for an SSL certificate.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    compute-ssl-certificate: $(external-query google::compute-ssl-certificate { name: 'ssl-certificate-example' })
 */
@Type("compute-ssl-certificate")
public class SslCertificateFinder extends GoogleFinder<Compute, SslCertificate, SslCertificateResource> {

    private String name;

    /**
     * Name of the SSL certificate.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<SslCertificate> findAllGoogle(Compute client) throws Exception {
        List<SslCertificate> sslCertificates = new ArrayList<>();
        String nextPageToken = null;
        SslCertificateList sslCertificateList;

        do {
            sslCertificateList = client.sslCertificates().list(getProjectId()).setPageToken(nextPageToken).execute();
            if (sslCertificateList.getItems() != null) {
                sslCertificates.addAll(sslCertificateList.getItems());
            }
            nextPageToken = sslCertificateList.getNextPageToken();
        } while (nextPageToken != null);

        return sslCertificates;
    }

    @Override
    protected List<SslCertificate> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        return Collections.singletonList(client.sslCertificates().get(getProjectId(), filters.get("name")).execute());
    }
}
