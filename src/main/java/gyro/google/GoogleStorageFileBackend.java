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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.stream.Stream;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.StorageObject;

import com.psddev.dari.util.ObjectUtils;

import gyro.core.FileBackend;
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
        return null;
    }

    @Override
    public InputStream openInput(String file) throws Exception {
        return client().objects().get(getBucket(), prefixed(file)).executeMediaAsInputStream();
    }

    @Override
    public OutputStream openOutput(String file) throws Exception {
        return new ByteArrayOutputStream() {
            public void close() {
                try {
                    StorageObject upload = new StorageObject();
                    upload.setName(prefixed(file));
                    client().objects().insert(getBucket(), upload, new ByteArrayContent(null, toByteArray())).execute();
                } catch (Exception e) {
                    throw new GyroException(String.format("Could not upload container config file %s.", prefixed(file)));
                }
            }
        };
    }

    @Override
    public void delete(String file) throws Exception {
        client().objects().delete(getBucket(), prefixed(file)).execute();
    }

    private Storage client() {
        GoogleCredentials credentials = (GoogleCredentials) getRootScope().getSettings(CredentialsSettings.class)
                .getCredentialsByName()
                .get("google::" + getCredentials());

        return credentials.createClient(Storage.class);
    }

    private String prefixed(String file) {
        return getPrefix() != null ? getPrefix() + '/' + file : file;
    }

}
