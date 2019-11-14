package gyro.google.storage;

import com.google.api.client.util.DateTime;
import com.google.api.services.storage.model.Bucket.Lifecycle.Rule.Condition;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

import java.util.List;

/**
 * The condition(s) under which the action will be taken.
 *
 * Example
 * -------
 *
 * ..code-block:: gyro
 *
 *      condition
 *          age: 15
 *      end
 */
public class BucketLifecycleRuleCondition extends Diffable implements Copyable<Condition> {

    private Integer age;
    private String createdBefore;
    private Boolean isLive;
    private List<String> matchesStorageClass;
    private Integer numNewerVersions;

    /**
     * Age of an object in days.
     */
    @Updatable
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * A date in RFC-3339 format with only the date part. Ex ``2013-01-15``
     */
    @Updatable
    public String getCreatedBefore() {
        return createdBefore;
    }

    public void setCreatedBefore(String createdBefore) {
        this.createdBefore = createdBefore;
    }

    /**
     * Only for versioned objects. When ``true`` this condition matches live objects; When ``false`` it matches
     * archived objects.
     */
    @Updatable
    public Boolean getIsLive() {
        return isLive;
    }

    public void setIsLive(Boolean live) {
        isLive = live;
    }

    /**
     * Matches objects having any of the storage classes specified. Valid values are ``STANDARD``, ``NEARLINE``,
     * ``COLDLINE``, ``MULTI_REGIONAL``, ``REGIONAL``, and ``DURABLE_REDUCED_AVAILABILITY``.
     */
    @Updatable
    @ValidStrings({"STANDARD", "NEARLINE", "COLDLINE", "MULTI_REGIONAL", "REGIONAL", "DURABLE_REDUCED_AVAILABILITY"})
    public List<String> getMatchesStorageClass() {
        return matchesStorageClass;
    }

    public void setMatchesStorageClass(List<String> matchesStorageClass) {
        this.matchesStorageClass = matchesStorageClass;
    }

    /**
     * Only for versioned objects. If the value is ``N``, the condition is met when there are at least ``N`` versions,
     * including the live version, newer than this version of the object.
     */
    @Updatable
    public Integer getNumNewerVersions() {
        return numNewerVersions;
    }

    public void setNumNewerVersions(Integer numNewerVersions) {
        this.numNewerVersions = numNewerVersions;
    }

    @Override
    public void copyFrom(Condition model) {
        setAge(model.getAge());
        setCreatedBefore(model.getCreatedBefore() == null ? null : model.getCreatedBefore().toStringRfc3339());
        setIsLive(model.getIsLive());
        setMatchesStorageClass(model.getMatchesStorageClass());
        setNumNewerVersions(model.getNumNewerVersions());
    }

    public Condition toGcpLifecycleRuleCondition() {
        return new Condition()
                .setAge(getAge())
                .setCreatedBefore(getCreatedBefore() == null ? null : DateTime.parseRfc3339(getCreatedBefore()))
                .setIsLive(getIsLive())
                .setMatchesStorageClass(getMatchesStorageClass())
                .setNumNewerVersions(getNumNewerVersions());
    }

    public static BucketLifecycleRuleCondition fromGcpLifecycleRuleCondition(Condition model) {
        if (model != null) {
            BucketLifecycleRuleCondition condition = new BucketLifecycleRuleCondition();
            condition.setAge(model.getAge());
            condition.setCreatedBefore(model.getCreatedBefore() == null ? null : model.getCreatedBefore().toStringRfc3339());
            condition.setIsLive(model.getIsLive());
            condition.setMatchesStorageClass(model.getMatchesStorageClass());
            condition.setNumNewerVersions(model.getNumNewerVersions());
            return condition;
        }
        return null;
    }
}
