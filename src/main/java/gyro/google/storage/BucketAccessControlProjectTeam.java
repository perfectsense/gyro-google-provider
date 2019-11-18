package gyro.google.storage;

import com.google.api.services.storage.model.BucketAccessControl.ProjectTeam;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

/**
 * The project team associated with the entity.
 */
public class BucketAccessControlProjectTeam extends Diffable implements Copyable<ProjectTeam> {

    private String projectNumber;
    private String team;

    /**
     * The project number.
     */
    @Updatable
    public String getProjectNumber() {
        return projectNumber;
    }

    public void setProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
    }

    /**
     * The team. Valid values are ``editors``, ``owners`` or ``viewers``
     */
    @Updatable
    @ValidStrings({"editors", "owners", "viewers"})
    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    @Override
    public void copyFrom(ProjectTeam model) {
        if (model != null) {
            setProjectNumber(model.getProjectNumber());
            setTeam(model.getTeam());
        }
    }

    public ProjectTeam toBucketAccessControlProjectTeam() {
        return new ProjectTeam().setProjectNumber(getProjectNumber()).setTeam(getTeam());
    }
}
