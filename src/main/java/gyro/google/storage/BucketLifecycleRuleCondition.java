/*
 * Copyright 2019, Perfect Sense, Inc.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.api.client.util.DateTime;
import com.google.cloud.storage.BucketInfo.LifecycleRule.LifecycleCondition;
import com.google.cloud.storage.StorageClass;
import gyro.core.resource.Diffable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

/**
 * The condition(s) under which the action will be taken.
 */
public class BucketLifecycleRuleCondition extends Diffable implements Copyable<LifecycleCondition> {

    private Integer age;
    private String createdBefore;
    private Boolean isLive;
    private List<String> matchesStorageClass;
    private List<String> matchesPrefix;
    private List<String> matchesSuffix;
    private Integer numNewerVersions;

    /**
     * Age of an object in days.
     */
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * A date in RFC-3339 format with only the date part. Ex ``2013-01-15``.
     */
    public String getCreatedBefore() {
        return createdBefore;
    }

    public void setCreatedBefore(String createdBefore) {
        this.createdBefore = createdBefore;
    }

    /**
     * Only for versioned objects. When ``true`` this condition matches live objects; When ``false`` it matches archived objects.
     */
    public Boolean getIsLive() {
        return isLive;
    }

    public void setIsLive(Boolean live) {
        isLive = live;
    }

    /**
     * Matches objects having any of the storage classes specified.
     */
    @ValidStrings({ "STANDARD", "NEARLINE", "COLDLINE", "MULTI_REGIONAL", "REGIONAL", "DURABLE_REDUCED_AVAILABILITY" })
    public List<String> getMatchesStorageClass() {
        return matchesStorageClass;
    }

    public void setMatchesStorageClass(List<String> matchesStorageClass) {
        this.matchesStorageClass = matchesStorageClass;
    }

    /**
     * Only for versioned objects. If the value is ``N``, the condition is met when there are at least ``N`` versions, including the live version, newer than this version of the object.
     */
    public Integer getNumNewerVersions() {
        return numNewerVersions;
    }

    public void setNumNewerVersions(Integer numNewerVersions) {
        this.numNewerVersions = numNewerVersions;
    }

    public List<String> getMatchesPrefix() {
        return matchesPrefix;
    }

    public void setMatchesPrefix(List<String> matchesPrefix) {
        this.matchesPrefix = matchesPrefix;
    }

    public List<String> getMatchesSuffix() {
        return matchesSuffix;
    }

    public void setMatchesSuffix(List<String> matchesSuffix) {
        this.matchesSuffix = matchesSuffix;
    }

    @Override
    public String primaryKey() {
        ArrayList<String> values = new ArrayList<>();
        values.add(String.format("is-live = %s", "TRUE".equals(getIsLive())));

        if (getAge() != null) {
            values.add(String.format("age = %d", getAge()));
        }

        if (getCreatedBefore() != null) {
            values.add(String.format("created-before = %s", getCreatedBefore()));
        }

        if (getNumNewerVersions() != null) {
            values.add(String.format("num-new-versions = %d", getNumNewerVersions()));
        }

        if (getMatchesStorageClass() != null) {
            values.add(String.format("matches-storage-class = [%s]", String.join(", ", getMatchesStorageClass())));
        }

        return String.join("; ", values);
    }

    @Override
    public void copyFrom(LifecycleCondition model) {
        setAge(model.getAge());
        setCreatedBefore(model.getCreatedBefore() == null ? null : model.getCreatedBefore().toStringRfc3339());
        setIsLive(model.getIsLive());
        setNumNewerVersions(model.getNumberOfNewerVersions());
    }

    public LifecycleCondition toLifecycleRuleCondition() {
        List<StorageClass> storageClasses = getMatchesStorageClass() == null
            ? Collections.emptyList()
            : getMatchesStorageClass().stream()
                .map(StorageClass::valueOf)
                .collect(Collectors.toList());

        return LifecycleCondition.newBuilder()
            .setAge(getAge())
            .setCreatedBefore(getCreatedBefore() == null ? null : DateTime.parseRfc3339(getCreatedBefore()))
            .setIsLive(getIsLive())
            .setMatchesStorageClass(storageClasses)
            .setNumberOfNewerVersions(getNumNewerVersions())
            .setMatchesPrefix(getMatchesPrefix())
            .setMatchesSuffix(getMatchesSuffix())
            .build();
    }
}
