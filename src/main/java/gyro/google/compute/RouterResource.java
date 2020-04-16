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

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.Router;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.google.Copyable;
import gyro.google.util.Utils;

/**
 * Creates a router.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *      google::router router-example
 *          name: "router-example"
 *          description: "example description"
 *          network: $(google::compute-network network-example-subnet)
 *          region: "us-east1"
 *
 *          router-bgp
 *              asn: 64512
 *              advertise-mode: "CUSTOM"
 *
 *              advertised-groups: [
 *                  "ALL_SUBNETS"
 *              ]
 *
 *              ip-range
 *                  range: "192.168.1.0/24"
 *                  description: "example ip range updated"
 *              end
 *          end
 *
 *          router-bgp-peer
 *              name: "ex-2"
 *              interface-name: "if-ex-2"
 *              peer-ip-address: "169.254.0.2"
 *              peer-asn: 64513
 *              advertise-mode: "DEFAULT"
 *              advertised-route-priority: 1
 *          end
 *
 *          router-interface
 *              name: "if-ex-2"
 *              ip-range: "169.254.0.1/30"
 *          end
 *
 *          router-nat
 *              icmp-idle-timeout-sec: 35
 *
 *              log-config
 *                  enable: true
 *                  filter: "ALL"
 *              end
 *
 *              min-ports-per-vm: 32
 *              name: "nats-example"
 *              ip-allocation-option: "AUTO_ONLY"
 *
 *              source-subnetwork-ip-ranges-to-nat: [
 *                  "LIST_OF_SUBNETWORKS"
 *              ]
 *
 *              subnet
 *                  subnet: $(google::compute-subnet subnet-example)
 *
 *                  source-ip-ranges-to-nat: [
 *                      "ALL_IP_RANGES"
 *                  ]
 *              end
 *          end
 *      end
 */
@Type("router")
public class RouterResource extends ComputeResource implements Copyable<Router> {

    private String name;
    private String description;
    private String region;
    private NetworkResource network;
    private RouterBgp routerBgp;
    private List<RouterNat> routerNat;
    private List<RouterInterface> routerInterface;
    private List<RouterBgpPeer> routerBgpPeer;

    // Read-only
    private String selfLink;

    /**
     * The name of the router. Must be a string starting with a lowercase letter, followed by hyphens, lowercase letters, or digits, except the last character, which cannot be a hyphen. (Required)
     */
    @Required
    @Regex(value = "^[a-z]([-a-z0-9]*[a-z0-9])?$", message = "a string starting with a lowercase letter, followed by hyphens, lowercase letters, or digits, except the last character, which cannot be a hyphen.")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The description of the router.
     */
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The region where the router resides. (Required)
     */
    @Required
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * The network to which this router belongs. (Required)
     */
    @Required
    public NetworkResource getNetwork() {
        return network;
    }

    public void setNetwork(NetworkResource network) {
        this.network = network;
    }

    /**
     * The Border Gateway Protocol (BGP) information specific to the router.
     */
    @Updatable
    public RouterBgp getRouterBgp() {
        return routerBgp;
    }

    public void setRouterBgp(RouterBgp routerBgp) {
        this.routerBgp = routerBgp;
    }

    /**
     * The list of Network Address Translation (NAT) gateway configuration to be created in this router.
     *
     * @subresource gyro.google.compute.RouterBgp
     */
    @Updatable
    public List<RouterNat> getRouterNat() {
        if (routerNat == null) {
            routerNat = new ArrayList<>();
        }

        return routerNat;
    }

    public void setRouterNat(List<RouterNat> routerNat) {
        this.routerNat = routerNat;
    }

    /**
     * The list of router interfaces.
     *
     * @subresource gyro.google.compute.RouterInterface
     */
    @Updatable
    public List<RouterInterface> getRouterInterface() {
        if (routerInterface == null) {
            routerInterface = new ArrayList<>();
        }

        return routerInterface;
    }

    public void setRouterInterface(List<RouterInterface> routerInterface) {
        this.routerInterface = routerInterface;
    }

    /**
     * The BGP information that must be configured to establish BGP peering.
     *
     * @subresource gyro.google.compute.RouterBgpPeer
     */
    @Updatable
    public List<RouterBgpPeer> getRouterBgpPeer() {
        if (routerBgpPeer == null) {
            routerBgpPeer = new ArrayList<>();
        }

        return routerBgpPeer;
    }

    public void setRouterBgpPeer(List<RouterBgpPeer> routerBgpPeer) {
        this.routerBgpPeer = routerBgpPeer;
    }

    /**
     * The URL of the router.
     */
    @Id
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    @Override
    public void copyFrom(Router model) throws Exception {
        setSelfLink(model.getSelfLink());
        setName(model.getName());
        setDescription(model.getDescription());
        setRegion(Utils.extractName(model.getRegion()));
        setNetwork(findById(NetworkResource.class, model.getNetwork()));

        if (model.getBgp() != null) {
            RouterBgp bgp = newSubresource(RouterBgp.class);
            bgp.copyFrom(model.getBgp());
            setRouterBgp(bgp);
        }

        if (model.getNats() != null) {
            getRouterNat().clear();
            for (com.google.api.services.compute.model.RouterNat n : model.getNats()) {
                RouterNat nat = newSubresource(RouterNat.class);
                nat.copyFrom(n);
                getRouterNat().add(nat);
            }
        }

        if (model.getInterfaces() != null) {
            getRouterInterface().clear();
            for (com.google.api.services.compute.model.RouterInterface i : model.getInterfaces()) {
                RouterInterface routerInterface = newSubresource(RouterInterface.class);
                routerInterface.copyFrom(i);
                getRouterInterface().add(routerInterface);
            }
        }

        if (model.getBgpPeers() != null) {
            getRouterBgpPeer().clear();
            for (com.google.api.services.compute.model.RouterBgpPeer p : model.getBgpPeers()) {
                RouterBgpPeer bgpPeer = newSubresource(RouterBgpPeer.class);
                bgpPeer.copyFrom(p);
                getRouterBgpPeer().add(bgpPeer);
            }
        }
    }

    @Override
    protected boolean doRefresh() throws Exception {
        Compute client = createComputeClient();

        Router router = getRouter(client);

        if (router == null) {
            return false;
        }

        copyFrom(router);

        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        Router router = new Router();
        router.setName(getName());
        router.setDescription(getDescription());
        router.setRegion(getRegion());
        router.setInterfaces(getRouterInterface().stream()
            .map(RouterInterface::toRouterInterface)
            .collect(Collectors.toList()));
        router.setBgpPeers(getRouterBgpPeer().stream()
            .map(RouterBgpPeer::toRouterBgpPeer)
            .collect(Collectors.toList()));

        if (getRouterBgp() != null) {
            router.setBgp(getRouterBgp().toRouterBgp());
        }

        if (getNetwork() != null) {
            router.setNetwork(getNetwork().getSelfLink());
        }

        Operation operation = client.routers().insert(getProjectId(), getRegion(), router).execute();
        waitForCompletion(client, operation);

        setSelfLink(operation.getTargetLink());

        state.save();

        if (!getRouterNat().isEmpty()) {
            Router newRouter = new Router();
            newRouter.setNats(getRouterNat().stream().map(RouterNat::toRouterNat).collect(Collectors.toList()));

            operation = client.routers().patch(getProjectId(), getRegion(), getName(), newRouter).execute();
            waitForCompletion(client, operation);
        }
    }

    @Override
    protected void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Compute client = createComputeClient();

        Router router = new Router();

        if (changedFieldNames.contains("description")) {
            router.setDescription(getDescription());
        }

        if (changedFieldNames.contains("router-bgp")) {
            router.setBgp(getRouterBgp() == null
                ? new com.google.api.services.compute.model.RouterBgp()
                : getRouterBgp().toRouterBgp());
        }

        if (changedFieldNames.contains("router-nats")) {
            router.setNats(getRouterNat().stream().map(RouterNat::toRouterNat).collect(Collectors.toList()));
        }

        if (changedFieldNames.contains("interfaces")) {
            router.setInterfaces(getRouterInterface().stream()
                .map(RouterInterface::toRouterInterface)
                .collect(Collectors.toList()));
        }

        if (changedFieldNames.contains("router-bgp-peers")) {
            router.setBgpPeers(getRouterBgpPeer().stream()
                .map(RouterBgpPeer::toRouterBgpPeer)
                .collect(Collectors.toList()));
        }

        Operation operation = client.routers().patch(getProjectId(), getRegion(), getName(), router).execute();
        waitForCompletion(client, operation);
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        Operation operation = client.routers().delete(getProjectId(), getRegion(), getName()).execute();
        waitForCompletion(client, operation);
    }

    private Router getRouter(Compute client) throws java.io.IOException {
        return client.routers()
            .list(getProjectId(), getRegion())
            .setFilter(String.format("selfLink = \"%s\"", getSelfLink()))
            .execute()
            .getItems()
            .stream()
            .findFirst()
            .orElse(null);
    }
}
