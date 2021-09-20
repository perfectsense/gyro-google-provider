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
import java.util.stream.Collectors;

import com.google.cloud.compute.v1.SslPolicy;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.google.Copyable;

public class SslPolicyWarning extends Diffable implements Copyable<SslPolicy.Warnings> {

    private String code;
    private List<SslPolicyWarningData> data;
    private String message;

    /**
     * A warning code, if applicable.
     */
    @Output
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Metadata about this warning in key : value format.
     *
     * @subresource gyro.google.compute.SslPolicyWarningData
     */
    @Output
    public List<SslPolicyWarningData> getData() {
        if (data == null) {
            data = new ArrayList<>();
        }

        return data;
    }

    public void setData(List<SslPolicyWarningData> data) {
        this.data = data;
    }

    /**
     * A human-readable description of the warning code.
     */
    @Output
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(SslPolicy.Warnings model) {
        setCode(model.getCode());
        setMessage(model.getMessage());

        List<SslPolicy.Warnings.Data> data = model.getData();
        getData().clear();
        if (data != null && !data.isEmpty()) {
            setData(data
                .stream()
                .map(d -> {
                    SslPolicyWarningData policyWarningData = newSubresource(SslPolicyWarningData.class);
                    policyWarningData.copyFrom(d);
                    return policyWarningData;
                })
                .collect(Collectors.toList()));
        }
    }
}
