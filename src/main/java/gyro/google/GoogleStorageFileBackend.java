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
import gyro.core.FileBackend;
import gyro.core.GyroCore;
import gyro.core.GyroException;
import gyro.core.Type;

@Type("storage")
public class GoogleStorageFileBackend extends FileBackend {

    private String bucket;
    private String prefix;

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
        return Channels.newOutputStream(service().writer(
            BlobInfo.newBuilder(getBucket(), prefixed(file)).build(),
            Storage.BlobWriteOption.predefinedAcl(Storage.PredefinedAcl.PRIVATE)));
    }

    @Override
    public void delete(String file) throws Exception {
        service().delete(getBucket(), prefixed(file));
    }

    @Override
    public boolean exists(String file) throws Exception {
        return service().get(getBucket(), prefixed(file)) != null;
    }

    @Override
    public void copy(String source, String destination) throws Exception {
        String bucket = getBucket();
        service().copy(Storage.CopyRequest.newBuilder()
            .setSource(bucket, prefixed(source))
            .setTarget(
                BlobInfo.newBuilder(bucket, prefixed(destination)).build(),

                Storage.BlobTargetOption.predefinedAcl(Storage.PredefinedAcl.PRIVATE))
            .build()).getResult();
    }

    private Storage service() {
        return Optional.ofNullable(getCredentials("google"))
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
