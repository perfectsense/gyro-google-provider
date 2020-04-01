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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.Route;
import gyro.core.GyroCore;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Range;
import gyro.core.validation.Required;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

/**
 * Creates a route.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-route route-example
 *         name: "route-example"
 *         description: "route-example-description"
 *         network: $(google::compute-network network-example-route)
 *         next-hop-ip: "10.0.0.5"
 *         dest-range: "0.0.0.0/0"
 *     end
 */
@Type("compute-route")
public class RouteResource extends ComputeResource implements Copyable<Route> {

    private String name;
    private String description;
    private String destRange;
    private NetworkResource network;
    private Long priority;
    private Set<String> tags;
    private String nextHopGateway;
    private String nextHopVpnTunnel;
    private String nextHopIp;

    // Read-only
    private String id;
    private String selfLink;

    /**
     * The name of the route. (Required)
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The description of the route.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The destination range of outgoing packets that this route applies to. (Required)
     */
    @Required
    public String getDestRange() {
        return destRange;
    }

    public void setDestRange(String destRange) {
        this.destRange = destRange;
    }

    /**
     * The network to associate this route with. (Required)
     */
    @Required
    public NetworkResource getNetwork() {
        return network;
    }

    public void setNetwork(NetworkResource network) {
        this.network = network;
    }

    /**
     * The priority of the route. Defaults to ``1000``.
     */
    @Range(min = 0, max = 65535)
    public Long getPriority() {
        if (priority == null) {
            priority = 1000L;
        }

        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    /**
     * A list of instance tags that this route will match to.
     */
    public Set<String> getTags() {
        if (tags == null) {
            tags = new HashSet<>();
        }

        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    /**
     * The fully qualified url of a gateway that handles matching routed packets. Currently only internet gateway is supported.
     */
    public String getNextHopGateway() {
        return nextHopGateway;
    }

    public void setNextHopGateway(String nextHopGateway) {
        this.nextHopGateway = nextHopGateway;
    }

    /**
     * The fully qualified url of a vpn tunnel that handles matching routed packets.
     */
    public String getNextHopVpnTunnel() {
        return nextHopVpnTunnel;
    }

    public void setNextHopVpnTunnel(String nextHopVpnTunnel) {
        this.nextHopVpnTunnel = nextHopVpnTunnel;
    }

    /**
     * The network ip address of an instance that handles matching routed packets.
     */
    public String getNextHopIp() {
        return nextHopIp;
    }

    public void setNextHopIp(String nextHopIp) {
        this.nextHopIp = nextHopIp;
    }

    /**
     * The Id of the route.
     */
    @Output
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * The fully qualified url of the route.
     */
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    @Override
    public void copyFrom(Route route) {
        setName(route.getName());
        setId(route.getId().toString());
        setSelfLink(route.getSelfLink());
        setDescription(route.getDescription());
        setDestRange(route.getDestRange());
        setNetwork(findById(NetworkResource.class, route.getNetwork()));
        setPriority(route.getPriority());
        setTags(route.getTags() != null ? new HashSet<>(route.getTags()) : null);
        setNextHopGateway(route.getNextHopGateway());
        setNextHopVpnTunnel(route.getNextHopVpnTunnel());
        setNextHopIp(route.getNextHopIp());
    }

    @Override
    public boolean doRefresh() throws Exception {
        Compute client = createComputeClient();

        Route route = client.routes().get(getProjectId(), getName()).execute();
        copyFrom(route);

        return true;
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        Route route = new Route();
        route.setName(getName());
        route.setDescription(getDescription());
        route.setNetwork(getNetwork().getSelfLink());
        route.setDestRange(getDestRange());
        route.setNextHopGateway(getNextHopGateway());
        route.setNextHopVpnTunnel(getNextHopVpnTunnel());
        route.setNextHopIp(getNextHopIp());
        route.setPriority(getPriority());
        route.setTags(new ArrayList<>(getTags()));

        Operation operation = client.routes().insert(getProjectId(), route).execute();
        waitForCompletion(client, operation);

        route = client.routes().get(getProjectId(), getName()).execute();
        copyFrom(route);
        if (route.getWarnings() != null && !route.getWarnings().isEmpty()) {
            GyroCore.ui().write(
                "\n@|cyan Route created with warnings:|@ %s\n",
                route.getWarnings().stream().map(Route.Warnings::getMessage).collect(Collectors.joining("\n")));
        }
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) {

    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        Operation operation = client.routes().delete(getProjectId(), getName()).execute();
        waitForCompletion(client, operation);
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        long count = Stream.of(getNextHopGateway(), getNextHopIp(), getNextHopVpnTunnel())
            .filter(Objects::nonNull)
            .count();

        if (count == 0) {
            errors.add(new ValidationError(
                this,
                null,
                "One of 'next-hop-gateway', 'next-hop-ip', or 'next-hop-vpn-tunnel' is required."));
        } else if (count > 1) {
            errors.add(new ValidationError(
                this,
                null,
                "Only one of 'next-hop-gateway', 'next-hop-ip', or 'next-hop-vpn-tunnel' can be set."));
        }

        return errors;
    }
}
