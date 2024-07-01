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

import com.google.api.services.sqladmin.model.ExportContext;
import gyro.core.resource.Diffable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class ExportContextBakExportOptions extends Diffable implements Copyable<ExportContext.BakExportOptions> {

    private String bakType;

    private Boolean differentialBase;

    private Integer stripeCount;

    private Boolean striped;

    /**
     * Type of this bak file will be export, FULL or DIFF, SQL Server only
     */
    @ValidStrings({
        "BAK_TYPE_UNSPECIFIED",
        "FULL",
        "DIFF",
        "TLOG"
    })
    public String getBakType() {
        return bakType;
    }

    public void setBakType(String bakType) {
        this.bakType = bakType;
    }

    /**
     * Whether or not the backup can be used as a differential base copy_only backup can not be served as differential base
     */
    public Boolean getDifferentialBase() {
        return differentialBase;
    }

    public void setDifferentialBase(Boolean differentialBase) {
        this.differentialBase = differentialBase;
    }

    /**
     * Option for specifying how many stripes to use for the export. If blank, and the value of the striped field is true, the number of stripes is automatically chosen.
     */
    public Integer getStripeCount() {
        return stripeCount;
    }

    public void setStripeCount(Integer stripeCount) {
        this.stripeCount = stripeCount;
    }

    /**
     * Whether or not the export should be striped.
     */
    public Boolean getStriped() {
        return striped;
    }

    public void setStriped(Boolean striped) {
        this.striped = striped;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(ExportContext.BakExportOptions model) {
        setBakType(model.getBakType());
        setDifferentialBase(model.getDifferentialBase());
        setStripeCount(model.getStripeCount());
        setStriped(model.getStriped());
    }
}
