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

package gyro.google.dns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.api.services.dns.Dns;
import com.google.api.services.dns.model.Change;
import com.google.api.services.dns.model.ResourceRecordSet;
import com.google.api.services.dns.model.ResourceRecordSetsListResponse;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.Wait;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.google.Copyable;
import gyro.google.GoogleResource;

/**
 * Creates a Resource Record Set.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::dns-resource-record-set public-managed-zone-www-cname-example
 *         managed-zone: $(google::dns-managed-zone public-managed-zone-example)
 *         name: "www.p.example.com."
 *         type: "CNAME"
 *         rrdatas: [ "fe.p.example.com." ]
 *     end
 *
 *     google::dns-resource-record-set public-managed-zone-fe-a-example
 *         managed-zone: $(google::dns-managed-zone public-managed-zone-example)
 *         name: "fe.p.example.com."
 *         type: "A"
 *         rrdatas: [ "192.168.1.156", "192.168.1.158" ]
 *     end
 */
@Type("dns-resource-record-set")
public class ResourceRecordSetResource extends GoogleResource implements Copyable<ResourceRecordSet> {

    private ManagedZoneResource managedZone;

    private String name;

    private List<String> rrdatas;

    private List<String> signatureRrdatas;

    private Integer ttl;

    private String type;

    /**
     * The managed zone to create this record set in.
     */
    @Required
    public ManagedZoneResource getManagedZone() {
        return managedZone;
    }

    public void setManagedZone(ManagedZoneResource managedZone) {
        this.managedZone = managedZone;
    }

    /**
     * The name of the resource record set. For example, www.example.com.
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * As defined in RFC 1035 (section 5) and RFC 1034 (section 3.6.1) -- see examples.
     */
    @Updatable
    public List<String> getRrdatas() {
        if (rrdatas == null) {
            rrdatas = new ArrayList<>();
        }
        return rrdatas;
    }

    public void setRrdatas(List<String> rrdatas) {
        this.rrdatas = rrdatas;
    }

    /**
     * As defined in RFC 4034 (section 3.2).
     */
    @Updatable
    public List<String> getSignatureRrdatas() {
        if (signatureRrdatas == null) {
            signatureRrdatas = new ArrayList<>();
        }
        return signatureRrdatas;
    }

    public void setSignatureRrdatas(List<String> signatureRrdatas) {
        this.signatureRrdatas = signatureRrdatas;
    }

    /**
     * Number of seconds that this resource record set can be cached by resolvers.
     */
    @Updatable
    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    /**
     * The identifier of a supported record type. See the list of Supported DNS record types.
     */
    @Required
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean doRefresh() throws Exception {
        Dns client = createClient(Dns.class);
        String managedZoneName = getManagedZone().getName();
        Dns.ResourceRecordSets.List request = client.resourceRecordSets().list(getProjectId(), managedZoneName);
        request.setName(getName());
        request.setType(getType());
        ResourceRecordSetsListResponse response = request.execute();
        List<ResourceRecordSet> rrsets = response.getRrsets();

        if (rrsets.isEmpty()) {
            return false;
        } else if (rrsets.size() > 1) {
            throw new GyroException(
                String.format("Multiple records found! [%s] [%s] [%s]", managedZoneName, getName(), getType()));
        }
        copyFrom(rrsets.get(0));

        return true;
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        Change change = new Change();
        change.setAdditions(Collections.singletonList(copyTo()));
        process(change);

        refresh();
    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {

        Change change = new Change();
        change.setDeletions(Collections.singletonList(((ResourceRecordSetResource) current).copyTo()));
        change.setAdditions(Collections.singletonList(copyTo()));
        process(change);

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Change change = new Change();
        change.setDeletions(Collections.singletonList(copyTo()));
        process(change);
    }

    @Override
    public void copyFrom(ResourceRecordSet model) {
        setName(model.getName());
        List<String> rrdatas = model.getRrdatas();

        if (rrdatas != null && !rrdatas.isEmpty()) {
            setRrdatas(rrdatas);
        }
        List<String> signatureRrdatas = model.getSignatureRrdatas();

        if (signatureRrdatas != null && !signatureRrdatas.isEmpty()) {
            setSignatureRrdatas(signatureRrdatas);
        }
        setTtl(model.getTtl());
        setType(model.getType());
    }

    public ResourceRecordSet copyTo() {
        ResourceRecordSet resourceRecordSet = new ResourceRecordSet();
        resourceRecordSet.setName(getName());
        List<String> rrdatas = getRrdatas();

        if (!rrdatas.isEmpty()) {
            resourceRecordSet.setRrdatas(rrdatas);
        }
        List<String> signatureRrdatas = getSignatureRrdatas();

        if (!signatureRrdatas.isEmpty()) {
            resourceRecordSet.setSignatureRrdatas(signatureRrdatas);
        }
        resourceRecordSet.setTtl(getTtl());
        resourceRecordSet.setType(getType());
        return resourceRecordSet;
    }

    private void process(Change change) throws Exception {
        Dns client = createClient(Dns.class);
        Change operation = client.changes().create(getProjectId(), getManagedZone().getName(), change).execute();

        boolean success = Wait.atMost(1, TimeUnit.MINUTES)
            .checkEvery(20, TimeUnit.SECONDS)
            .prompt(false)
            .until(() -> isChangeDone(client, operation));

        if (!success) {
            throw new InterruptedException("Timed out waiting for operation to complete");
        }
    }

    private boolean isChangeDone(Dns client, Change operation) throws Exception {
        Change change = client.changes().get(getProjectId(), getManagedZone().getName(), operation.getId()).execute();

        return change.getStatus().equals("done");
    }
}
