package gyro.google.compute;

import com.google.api.services.compute.model.Instance;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.google.Copyable;

import java.util.Set;

@Type("instance")
public class InstanceResource extends ComputeResource implements Copyable<Instance> {
    private String name;

    // Read-only
    private String selfLink;

    @Id
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    @Override
    public void copyFrom(Instance instance) {
        setName(instance.getName());
        setSelfLink(instance.getSelfLink());
    }

    @Override
    public boolean refresh() {
        return false;
    }

    @Override
    public void create(GyroUI ui, State state) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {

    }

    @Override
    public void delete(GyroUI ui, State state) throws Exception {

    }
}
