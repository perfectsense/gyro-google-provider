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

/**
 * Google
 * ======
 *
 * The Google provider implements support for Google Cloud Platform cloud provider.
 *
 * Usage
 * +++++
 *
 * The Google provider is implemented as a plugin. To use it add the plugin to your init file.
 *
 * .. code:: shell
 *
 *     {@literal @}repository: 'https://artifactory.psdops.com/gyro-releases'
 *     {@literal @}plugin: 'gyro:gyro-google-provider:0.99.3'
 *
 * This lets Gyro load the Google provider plugin and lets you start managing Google Cloud Platform resources using Gyro.
 *
 * Authentication
 * ++++++++++++++
 *
 * This provider expects credentials to be provided using a json file containing the
 * credentials of a service account. For more info refer `Service account creation <https://cloud.google.com/docs/authentication/getting-started>`_
 *
 * Credentials should be defined in ``.gyro/init.gyro``. You must provide the path to your json credentials file along with
 * the project-ID that these credentials are for.
 *
 * .. code:: shell
 *
 *     {@literal @}credentials 'google::credentials'
 *         credential-file-path: '<path_to_credentials_file>'
 *         project-id: 'Project-1-ID'
 *     {@literal @}end
 *
 * To use more than one project, provide a name for your credentials. When a name is not provided
 * then the credentials becomes the ``default``.
 *
 * .. code:: shell
 *
 *     {@literal @}credentials 'google::credentials' project-2
 *         credential-file-path: '<path_to_credentials_file>'
 *         project-id: 'Project-2-ID'
 *     {@literal @}end
 *
 * To use a non-default set of credentials you must explicitly use them in your resource definitions:
 *
 * .. code:: shell
 *
 *     google::compute-network backend
 *         name: 'backend'
 *
 *         {@literal @}uses-credentials: 'project-2'
 *     end
 *
 */
@DocNamespace("google")
@Namespace("google")
package gyro.google;

import gyro.core.Namespace;
import gyro.core.resource.DocNamespace;
