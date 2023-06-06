/*
 * Copyright 2023, Brightspot.
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

import com.google.cloud.compute.v1.Expr;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class SecurityMatcherRuleExpressionConfig extends Diffable implements Copyable<Expr> {

    private String expression;
    private String location;
    private String description;

    /**
     * The expression that is evaluated to determine if the request should be matched.
     */
    @Required
    @Updatable
    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * The optional location of the expression for error reporting, e.g. line number or column number.
     */
    @Updatable
    public String getLocation() {
        if (location == null) {
            location = "";
        }

        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * An optional description of the expression.
     */
    @Updatable
    public String getDescription() {
        if (description == null) {
            description = "";
        }

        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void copyFrom(Expr model) {
        setExpression(model.getExpression());
        setLocation(model.getLocation());
        setDescription(model.getDescription());
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public Expr toExpr() {
        return Expr.newBuilder()
            .setExpression(getExpression())
            .setLocation(getLocation())
            .setDescription(getDescription())
            .build();
    }
}
