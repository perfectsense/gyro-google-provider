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

package gyro.google;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.psddev.dari.util.ObjectUtils;
import gyro.core.FileBackend;
import gyro.core.GyroCore;
import gyro.core.GyroException;
import gyro.core.Type;
import gyro.core.auth.CredentialsSettings;

@Type("storage")
public class GoogleStorageFileBackend extends FileBackend {

    private String bucket;
    private String prefix;
    private String credentials;

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public String getCredentials() {
        if (ObjectUtils.isBlank(credentials)) {
            setCredentials("default");
        }

        return credentials;
    }

    @Override
    public Stream<String> list() throws Exception {
        if (this.equals(GyroCore.getStateBackend(getName()))) {
            return StreamSupport.stream(service().list(getBucket()).iterateAll().spliterator(), false)
                .map(Blob::getName)
                .filter(f -> f.endsWith(".gyro"))
                .map(this::removePrefix);
        }

        return Stream.empty();
    }

    @Override
    public InputStream openInput(String file) throws Exception {
        return Channels.newInputStream(service().reader(getBucket(), prefixed(file)));
    }

    @Override
    public OutputStream openOutput(String file) throws Exception {
        return Channels.newOutputStream(service().writer(BlobInfo.newBuilder(getBucket(), prefixed(file)).build()));
    }

    @Override
    public void delete(String file) throws Exception {
        service().delete(getBucket(), prefixed(file));
    }

    private Storage service() {
        return Optional.ofNullable(getRootScope())
            .map(e -> e.getSettings(CredentialsSettings.class))
            .map(CredentialsSettings::getCredentialsByName)
            .map(e -> e.get("google::" + getCredentials()))
            .filter(GoogleCredentials.class::isInstance)
            .map(GoogleCredentials.class::cast)
            .map(e -> StorageOptions.newBuilder()
                .setCredentials(e.getGoogleCredentials())
                .build()
                .getService())
            .orElseThrow(() -> new GyroException("No storage service available!"));
    }

    private String prefixed(String file) {
        return getPrefix() != null ? getPrefix() + '/' + file : file;
    }

    private String removePrefix(String file) {
        if (getPrefix() != null && file.startsWith(getPrefix() + "/")) {
            return file.substring(getPrefix().length() + 1);
        }

        return file;
    }
}
