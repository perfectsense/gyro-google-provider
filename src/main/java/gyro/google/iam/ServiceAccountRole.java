package gyro.google.iam;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.ValidationError;

public class ServiceAccountRole extends Diffable {

    public RolePredefinedRoleResource predefinedRole;
    public RoleCustomProjectRoleResource customRole;
    public Expr condition;

    /**
     * The predefined role to attach to the service account.
     */
    @ConflictsWith("custom-role")
    public RolePredefinedRoleResource getPredefinedRole() {
        return predefinedRole;
    }

    public void setPredefinedRole(RolePredefinedRoleResource predefinedRole) {
        this.predefinedRole = predefinedRole;
    }

    /**
     * The custom role to attach to the service account.
     */
    @ConflictsWith("predefined-role")
    public RoleCustomProjectRoleResource getCustomRole() {
        return customRole;
    }

    public void setCustomRole(RoleCustomProjectRoleResource customRole) {
        this.customRole = customRole;
    }

    /**
     * The conditions upon which to attach the role to the service account.
     */
    @Updatable
    public Expr getCondition() {
        return condition;
    }

    public void setCondition(Expr condition) {
        this.condition = condition;
    }

    @Override
    public String primaryKey() {
        if (getCustomRole() != null) {
            return String.format(
                "Role: %s",
                getCustomRole().getRoleId() == null ? getCustomRole().getName() : getCustomRole().getRoleId());

        } else if (getPredefinedRole() != null) {
            return String.format("Role: %s", getPredefinedRole().getName());

        } else {
            return null;
        }
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if (!configuredFields.contains("predefined-role") && !configuredFields.contains("custom-role")) {
            errors.add(new ValidationError(this, null, "Exactly one of 'predefined-role' or 'custom-role' is required."));
        }

        return errors;
    }

    public String getRoleName() {
        if (getCustomRole() != null) {
            return getCustomRole().getName();

        } else {
            return getPredefinedRole().getName();
        }
    }
}
