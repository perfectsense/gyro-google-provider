package gyro.google.iam;

import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class Expr extends Diffable implements Copyable<com.google.api.services.cloudresourcemanager.model.Expr> {

    private String title;
    private String description;
    private String expression;
    private String location;

    /**
     * The title of the expression.
     */
    @Required
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * The description of the expression.
     */
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Textual representation of an expression in Common Expression Language syntax. See `Cloud IAM Conditions <https://cloud.google.com/iam/docs/conditions-overview/>`_.
     */
    @Updatable
    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * The location of the expression for error reporting.
     */
    @Updatable
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String primaryKey() {
        return String.format("Title: %s, Description: %s, Expression: %s, Location: %s",
            getTitle(), getDescription(), getExpression(), getLocation());
    }

    @Override
    public void copyFrom(com.google.api.services.cloudresourcemanager.model.Expr model) throws Exception {
        setTitle(model.getTitle());
        setDescription(model.getDescription());
        setExpression(model.getExpression());
        setLocation(model.getLocation());
    }

    com.google.api.services.cloudresourcemanager.model.Expr toExpr() {
        com.google.api.services.cloudresourcemanager.model.Expr expr = new com.google.api.services.cloudresourcemanager.model.Expr();
        expr.setDescription(getDescription());
        expr.setExpression(getExpression());
        expr.setTitle(getTitle());
        expr.setLocation(getLocation());

        return expr;
    }
}
