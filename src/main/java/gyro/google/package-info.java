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
 *     {@literal @}plugin: 'gyro:gyro-google-provider:0.99.0'
 *
 * This lets Gyro load the Google provider plugin and lets you start managing Google Cloud Platform resources using Gyro.
 *
 * Authentication
 * ++++++++++++++
 *
 * This provider expects credentials to be provided using a json file containing the
 * credentials of a service account. For more info refer `Service account creation <https://cloud.google.com/docs/authentication/getting-started>`_
 *
 * Credentials can be defined in ``.gyro/init.gyro``. You will need to provide the path to your json credentials file along with
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
 *     google::network backend
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
