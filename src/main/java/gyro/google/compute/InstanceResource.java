package gyro.google.compute;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Instance;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.google.Copyable;

import java.util.Set;

@Type("instance")
public class InstanceResource extends ComputeResource implements Copyable<Instance> {
    private String name;

    @Id
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void copyFrom(Instance instance) {
        setName(instance.getName());
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
