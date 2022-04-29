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

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.DeleteRouterRequest;
import com.google.cloud.compute.v1.GetRouterRequest;
import com.google.cloud.compute.v1.InsertRouterRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.PatchRouterRequest;
import com.google.cloud.compute.v1.Router;
import com.google.cloud.compute.v1.RoutersClient;
import com.google.cloud.compute.v1.UpdateRouterRequest;
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
     * The name of the router.
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
     * The region where the router resides.
     */
    @Required
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * The network to which this router belongs.
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
     *
     * @subresource gyro.google.compute.RouterBgp
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
     * @subresource gyro.google.compute.RouterNat
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
        setName(model.getName());
        setSelfLink(model.getSelfLink());
        setDescription(model.getDescription());

        if (model.hasRegion()) {
            setRegion(Utils.extractName(model.getRegion()));
        }

        if (model.hasNetwork()) {
            setNetwork(findById(NetworkResource.class, model.getNetwork()));
        }

        setRouterBgp(null);
        if (model.hasBgp()) {
            RouterBgp bgp = newSubresource(RouterBgp.class);
            bgp.copyFrom(model.getBgp());
            setRouterBgp(bgp);
        }

        getRouterNat().clear();
        for (com.google.cloud.compute.v1.RouterNat n : model.getNatsList()) {
            RouterNat nat = newSubresource(RouterNat.class);
            nat.copyFrom(n);
            getRouterNat().add(nat);
        }

        getRouterInterface().clear();
        for (com.google.cloud.compute.v1.RouterInterface i : model.getInterfacesList()) {
            RouterInterface routerInterface1 = newSubresource(RouterInterface.class);
            routerInterface1.copyFrom(i);
            getRouterInterface().add(routerInterface1);
        }

        getRouterBgpPeer().clear();
        for (com.google.cloud.compute.v1.RouterBgpPeer p : model.getBgpPeersList()) {
            RouterBgpPeer bgpPeer = newSubresource(RouterBgpPeer.class);
            bgpPeer.copyFrom(p);
            getRouterBgpPeer().add(bgpPeer);
        }
    }

    @Override
    protected boolean doRefresh() throws Exception {
        try (RoutersClient client = createClient(RoutersClient.class)) {
            Router router = getRouter(client);

            if (router == null) {
                return false;
            }

            copyFrom(router);

            return true;
        }
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        try (RoutersClient client = createClient(RoutersClient.class)) {

            Router.Builder builder = getRouterBuilder();

            Operation operation = client.insertCallable().call(InsertRouterRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setRouterResource(builder)
                .build());

            waitForCompletion(operation);

            setSelfLink(operation.getTargetLink());

            state.save();

            if (!getRouterNat().isEmpty()) {
                Router.Builder newRouter = Router.newBuilder();
                newRouter.addAllNats(getRouterNat().stream().map(RouterNat::toRouterNat).collect(Collectors.toList()));

                operation = client.patchCallable().call(PatchRouterRequest.newBuilder()
                    .setProject(getProjectId())
                    .setRegion(getRegion())
                    .setRouter(getName())
                    .setRouterResource(newRouter.build())
                    .build());

                waitForCompletion(operation);
            }
        }
    }

    @Override
    protected void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        try (RoutersClient client = createClient(RoutersClient.class)) {

            Router.Builder builder = Router.newBuilder();

            if (changedFieldNames.contains("description")) {
                builder.setDescription(getDescription());
            }

            //            if (changedFieldNames.contains("router-bgp")) {
            //                builder.setBgp(getRouterBgp() == null
            //                    ? com.google.cloud.compute.v1.RouterBgp.newBuilder().build()
            //                    : getRouterBgp().toRouterBgp());
            //            }

            if (changedFieldNames.contains("router-nat")) {
                builder.addAllNats(getRouterNat().stream().map(RouterNat::toRouterNat).collect(Collectors.toList()));
            }

            if (changedFieldNames.contains("interfaces")) {
                builder.addAllInterfaces(getRouterInterface().stream()
                    .map(RouterInterface::toRouterInterface)
                    .collect(Collectors.toList()));
            }

            if (changedFieldNames.contains("router-bgp-peers")) {
                builder.addAllBgpPeers(getRouterBgpPeer().stream()
                    .map(RouterBgpPeer::toRouterBgpPeer)
                    .collect(Collectors.toList()));
            }

            Operation operation = client.patchCallable().call(PatchRouterRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setRouter(getName())
                .setRouterResource(builder)
                .build());

            waitForCompletion(operation);

            builder = ((RouterResource) current).getRouterBuilder();

            if (changedFieldNames.contains("router-bgp")) {
                builder.setBgp(getRouterBgp() == null
                    ? com.google.cloud.compute.v1.RouterBgp.newBuilder().build()
                    : getRouterBgp().toRouterBgp());

                operation = client.updateCallable().call(UpdateRouterRequest.newBuilder()
                    .setProject(getProjectId())
                    .setRegion(getRegion())
                    .setRouter(getName())
                    .setRouterResource(builder.build())
                    .build());

                waitForCompletion(operation);
            }
        }
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        try (RoutersClient client = createClient(RoutersClient.class)) {
            Operation operation = client.deleteCallable().call(DeleteRouterRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setRouter(getName())
                .build());

            waitForCompletion(operation);
        }
    }

    private Router.Builder getRouterBuilder() {
        Router.Builder builder = Router.newBuilder();
        builder.setName(getName());

        if (getRegion() != null) {
            builder.setRegion(getRegion());
        }

        if (getDescription() != null) {
            builder.setDescription(getDescription());
        }

        if (getRouterInterface() != null) {
            builder.addAllInterfaces(getRouterInterface().stream()
                .map(RouterInterface::toRouterInterface)
                .collect(Collectors.toList()));
        }

        if (getRouterBgpPeer() != null) {
            builder.addAllBgpPeers(getRouterBgpPeer().stream()
                .map(RouterBgpPeer::toRouterBgpPeer)
                .collect(Collectors.toList()));
        }

        if (getRouterBgp() != null) {
            builder.setBgp(getRouterBgp().toRouterBgp());
        }

        if (getNetwork() != null) {
            builder.setNetwork(getNetwork().getSelfLink());
        }

        return builder;
    }

    private Router getRouter(RoutersClient client) {
        Router router = null;

        try {
            router = client.get(GetRouterRequest.newBuilder().setProject(getProjectId())
                .setRouter(getName()).setRegion(getRegion()).build());

        } catch (NotFoundException ex) {
            // ignore
        }

        return router;
    }
}
