/*
 * Copyright 2024, Brightspot.
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

package gyro.google.sqladmin;

import java.util.ArrayList;
import java.util.List;

import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class OperationErrors extends Diffable
    implements Copyable<com.google.api.services.sqladmin.model.OperationErrors> {

    private List<OperationError> error;

    /**
     * The list of errors encountered while processing this operation.
     *
     * @subresource gyro.google.sqladmin.base.OperationError
     */
    public List<OperationError> getError() {
        if (error == null) {
            error = new ArrayList<>();
        }

        return error;
    }

    public void setError(List<OperationError> error) {
        this.error = error;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.OperationErrors model) {
        setError(null);
        if (model.getErrors() != null) {
            model.getErrors().forEach(operationError -> {
                OperationError error = new OperationError();
                error.copyFrom(operationError);
                getError().add(error);
            });
        }
    }
}
