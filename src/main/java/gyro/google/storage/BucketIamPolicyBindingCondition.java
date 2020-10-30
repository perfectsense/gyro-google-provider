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

package gyro.google.storage;

import com.google.api.services.storage.model.Expr;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

/*
 *   The Bucket's IAM policy binding condition configuration.
 */
public class BucketIamPolicyBindingCondition extends Diffable implements Copyable<Expr> {

    private String description;
    private String expression;
    private String title;

    /**
     * The description of the condition.
     */
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The attribute-based logic expression. See also `Conditions Overview <https://cloud.google.com/iam/docs/conditions-overview#attributes>`_.
     */
    @Required
    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * The title of the condition.
     */
    @Required
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String primaryKey() {
        return String.format("with title '%s' and expression of '%s'", getTitle(), getExpression());
    }

    @Override
    public void copyFrom(Expr model) {
        setDescription(model.getDescription());
        setExpression(model.getExpression());
        setTitle(model.getTitle());
    }

    public Expr toCondition() {
        return new Expr().setDescription(getDescription()).setExpression(getExpression()).setTitle(getTitle());
    }
}
