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

package gyro.google.kms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.cloud.kms.v1.CryptoKey;
import com.google.cloud.kms.v1.CryptoKey.CryptoKeyPurpose;
import com.google.cloud.kms.v1.CryptoKeyPathName;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.KeyRingName;
import com.google.protobuf.Duration;
import com.google.protobuf.FieldMask;
import com.google.protobuf.Timestamp;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Min;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;
import gyro.google.GoogleResource;
import gyro.google.util.Utils;

/**
 * Add a cryto key to a king ring.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    google::crypto-key crypto-key-example
 *        key-ring: $(google::key-ring example-key-ring)
 *        name: "crypto-key-ring-example"
 *        purpose: ENCRYPT_DECRYPT
 *        rotation-period: 1
 *        next-rotation-date: "09/21/2020"
 *        primary-key-version-id: 1
 *
 *        crypto-key-version-template
 *            algorithm: GOOGLE_SYMMETRIC_ENCRYPTION
 *            protection-level: SOFTWARE
 *        end
 *    end
 */
@Type("crypto-key")
public class CryptoKeyResource extends GoogleResource implements Copyable<CryptoKey> {

    private KeyRingResource keyRing;
    private String name;
    private Long rotationPeriod;
    private CryptoKeyPurpose purpose;
    private String nextRotationDate;
    private CryptoKeyVersionTemplate cryptoKeyVersionTemplate;
    private Map<String, String> labels;
    private String primaryKeyVersionId;

    // Read-only
    private String id;
    private List<String> versions;

    /**
     * The key ring that holds the crypto key.
     */
    @Required
    public KeyRingResource getKeyRing() {
        return keyRing;
    }

    public void setKeyRing(KeyRingResource keyRing) {
        this.keyRing = keyRing;
    }

    /**
     * The name of the crypto key.
     */
    @Required
    @Regex(value = "^(\\w|-)+$", message = "")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The next date when the symmetric key should rotate.
     */
    @Regex(value = "^(1[0-2]|0[1-9])/(3[01]|[012][0-9]|)/[0-9]{4}$", message = "a string matching the @|bold mm/dd/yyyy|@ format")
    public String getNextRotationDate() {
        return nextRotationDate;
    }

    public void setNextRotationDate(String nextRotationDate) {
        this.nextRotationDate = nextRotationDate;
    }

    /**
     * The period after which the symmetric key should automatically rotate.
     */
    @Updatable
    @Min(1)
    public Long getRotationPeriod() {
        return rotationPeriod;
    }

    public void setRotationPeriod(Long rotationPeriod) {
        this.rotationPeriod = rotationPeriod;
    }

    /**
     * The immutable purpose of the key.
     */
    @Required
    @ValidStrings({"ENCRYPT_DECRYPT", "ASYMMETRIC_SIGN", "ASYMMETRIC_DECRYPT"})
    public CryptoKeyPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(CryptoKeyPurpose purpose) {
        this.purpose = purpose;
    }

    /**
     * The template describing settings for new crypto key versions.
     *
     * @subresource gyro.google.kms.CryptoKeyVersionTemplate
     */
    @Updatable
    @Required
    public CryptoKeyVersionTemplate getCryptoKeyVersionTemplate() {
        return cryptoKeyVersionTemplate;
    }

    public void setCryptoKeyVersionTemplate(CryptoKeyVersionTemplate cryptoKeyVersionTemplate) {
        this.cryptoKeyVersionTemplate = cryptoKeyVersionTemplate;
    }

    /**
     * The labels of the crypto key.
     */
    @Updatable
    public Map<String, String> getLabels() {
        if (labels == null) {
            labels = new HashMap<>();
        }

        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    /**
     * The ID of the primary key version.
     */
    @Updatable
    public String getPrimaryKeyVersionId() {
        return primaryKeyVersionId;
    }

    public void setPrimaryKeyVersionId(String primaryKeyVersionId) {
        this.primaryKeyVersionId = primaryKeyVersionId;
    }

    /**
     * The ID of the crypto key.
     */
    @Output
    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * The list of versions of the crypto key.
     */
    @Output
    public List<String> getVersions() {
        if (versions == null) {
            versions = new ArrayList<>();
        }
        return versions;
    }

    public void setVersions(List<String> versions) {
        this.versions = versions;
    }

    @Override
    public void copyFrom(CryptoKey model) throws Exception {
        setId(model.getName());
        setPurpose(model.getPurpose());
        setKeyRing(findById(KeyRingResource.class, Utils.getKmsKeyRingIdFromId(getId())));
        setName(Utils.getKmsKeyNameFromId(getId()));

        if (model.hasNextRotationTime()) {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date date = new Date(model.getNextRotationTime().getSeconds() * 1000L);
            setNextRotationDate(dateFormat.format(date));
        }

        if (model.hasRotationPeriod()) {
            setRotationPeriod(model.getRotationPeriod().getSeconds() / 86400);
        }

        if (model.getVersionTemplate() != null) {
            CryptoKeyVersionTemplate cryptoKeyVersionTemplate = newSubresource(CryptoKeyVersionTemplate.class);
            cryptoKeyVersionTemplate.copyFrom(model.getVersionTemplate());
            setCryptoKeyVersionTemplate(cryptoKeyVersionTemplate);
        }

        if (model.hasPrimary()) {
            setPrimaryKeyVersionId(Utils.getKmsPrimaryKeyVersionFromId(model.getPrimary().getName()));
        }

        refreshVersions();
    }

    @Override
    protected boolean doRefresh() throws Exception {
        KeyManagementServiceClient client = createClient(KeyManagementServiceClient.class);

        CryptoKey cryptoKey = client.getCryptoKey(getId());

        if (cryptoKey == null) {
            return false;
        }

        copyFrom(cryptoKey);

        client.shutdownNow();

        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        KeyManagementServiceClient client = createClient(KeyManagementServiceClient.class);

        CryptoKey.Builder builder = CryptoKey.newBuilder()
            .putAllLabels(getLabels())
            .setPurpose(getPurpose())
            .setVersionTemplate(getCryptoKeyVersionTemplate().toCryptoKeyVersionTemplate());

        if (getRotationPeriod() != null) {
            builder = builder.setRotationPeriod(Duration.newBuilder().setSeconds(getRotationPeriod() * 86400).build());
        }

        if (getNextRotationDate() != null) {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            builder = builder.setNextRotationTime(Timestamp.newBuilder()
                .setSeconds(format.parse(getNextRotationDate()).getTime() / 1000)
                .build());
        }

        String parent = KeyRingName.format(
            getProjectId(),
            Utils.getLocationFromId(getKeyRing().getId()),
            Utils.getKmsKeyRingNameFromId(getKeyRing().getId()));

        CryptoKey response = client.createCryptoKey(parent, getName(), builder.build());

        if (getPrimaryKeyVersionId() != null) {
            String cryptoKeyPathName = CryptoKeyPathName.format(
                getProjectId(),
                Utils.getLocationFromId(getKeyRing().getId()),
                Utils.getKmsKeyRingNameFromId(getKeyRing().getId()),
                getName());

            client.updateCryptoKeyPrimaryVersion(cryptoKeyPathName, Utils.getKmsPrimaryKeyVersionFromId(getPrimaryKeyVersionId()));
        }

        copyFrom(response);

        client.shutdownNow();
    }

    @Override
    protected void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        KeyManagementServiceClient client = createClient(KeyManagementServiceClient.class);

        FieldMask.Builder fieldMaskBuilder = FieldMask.newBuilder();
        CryptoKey.Builder cryptoKeyBuilder = CryptoKey.newBuilder().setName(getId());

        if (changedFieldNames.contains("rotation-period")) {
            cryptoKeyBuilder.setRotationPeriod(Duration.newBuilder().setSeconds(getRotationPeriod() * 86400).build());
            fieldMaskBuilder.addPaths("rotation_period");
        }

        if (changedFieldNames.contains("crypto-key-version-template")) {
            cryptoKeyBuilder.setVersionTemplate(getCryptoKeyVersionTemplate().updateCryptoKeyVersionAlgorithm());
            fieldMaskBuilder.addPaths("version_template.algorithm");
        }

        if (changedFieldNames.contains("labels")) {
            cryptoKeyBuilder.clearLabels();
            cryptoKeyBuilder.putAllLabels(getLabels());
            fieldMaskBuilder.addPaths("labels");
        }

        client.updateCryptoKey(cryptoKeyBuilder.build(), fieldMaskBuilder.build());

        String parent = CryptoKeyPathName.format(
            getProjectId(),
            Utils.getLocationFromId(getKeyRing().getId()),
            Utils.getKmsKeyRingNameFromId(getKeyRing().getId()),
            getName());

        if (changedFieldNames.contains("primary-key-version-id")) {
            client.updateCryptoKeyPrimaryVersion(parent, Utils.getKmsPrimaryKeyVersionFromId(getPrimaryKeyVersionId()));
        }

        client.shutdownNow();
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {

    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if (getPurpose().equals(CryptoKeyPurpose.ENCRYPT_DECRYPT)) {
            if (!configuredFields.contains("next-rotation-date") || !configuredFields.contains("rotation-period")) {
                errors.add(new ValidationError(
                    this,
                    null,
                    "Both 'next-rotation-date' and 'rotation-period' are required if 'purpose' is set to 'ENCRYPT_DECRYPT'"));
            }

        } else {
            if (configuredFields.contains("next-rotation-date") || configuredFields.contains("rotation-period")
                || configuredFields.contains("primary-key-version-id")) {
                errors.add(new ValidationError(
                    this,
                    null,
                    "The 'next-rotation-date', 'rotation-period' and 'primary-key-version-id' cannot be set if the 'purpose' is 'ASYMMETRIC_SIGN' or 'ASYMMETRIC_DECRYPT'"));
            }
        }

        return errors;
    }

    private void refreshVersions() {
        KeyManagementServiceClient client = createClient(KeyManagementServiceClient.class);
        getVersions().clear();
        client.listCryptoKeyVersions(getId())
            .iterateAll()
            .forEach(v -> getVersions().add(v.getName()));

        client.shutdownNow();
    }
}
