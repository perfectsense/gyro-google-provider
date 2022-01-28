<img src="https://github.com/perfectsense/gyro/blob/master/etc/gyro.png" height="200"/>

[![Gitter](https://img.shields.io/gitter/room/perfectsense/gyro)](https://gitter.im/perfectsense/gyro)
[![TravisCI](https://api.travis-ci.com/perfectsense/gyro-google-provider.svg?branch=master)](https://travis-ci.com/perfectsense/gyro-google-provider)
[![Apache License 2.0](https://img.shields.io/github/license/perfectsense/gyro-google-provider)](https://github.com/perfectsense/gyro-google-provider/blob/master/LICENSE)


The **Google Provider for Gyro** enables users to easily work with GCP Services. The Google provider extends Gyro allowing you to manage your Google infrastructure.

To learn more about Gyro see [getgyro.io](https://getgyro.io) and [gyro](https://github.com/perfectsense/gyro). 

* [Resource Documentation](https://gyro.dev/providers/google/index.html)
* [Submit an Issue](https://github.com/perfectsense/gyro-google-provider/issues)
* [Getting Help](#getting-help)

## Using the Google Provider

### Google Account ###

Before you can use the Google provider, you will need a Google account. Please see [Sign Up for Google](https://cloud.google.com/gcp/) to create an Google Account.

Once your account is set up and ready to be used, you need to set up a [Google service account](https://cloud.google.com/docs/authentication/getting-started) and save the credentials of that account in a json file in the following format

```
"type": "service_account",
"project_id": "#########",
"private_key_id": "#########",
"private_key": ""#########","
"client_email": "#########.iam.gserviceaccount.com",
"client_id": "###########",
"auth_uri": "https://accounts.google.com/o/oauth2/auth",
"token_uri": "https://oauth2.googleapis.com/token",
"auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
"client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/#############.iam.gserviceaccount.com"
``` 

### Using The Provider ###

#### Import ####

Load the Google provider in your project by consuming it as a `plugin` directive in your init file. It uses the format `@plugin: gyro:gyro-google-provider:<version>`.

```shell
@repository: 'https://artifactory.psdops.com/gyro-releases'
@plugin: 'gyro:gyro-google-provider:0.99.3'
```

#### Authentication ####

Provide the Google provider with the path of the credentials file by defining the following in your `.gyro/init.gyro` file:

```
@credentials 'google::credentials'
    credential-file-path: '<google_credentials_file_path>'
    project-id: '<project-id>'
@end
```

See [Google authentication for Gyro](https://gyro.dev/providers/google/index.html#authentication) for more details.

## Supported Services

* [Artifact Registry](https://gyro.dev/providers/google/artifactregistry/index.html)
* [Compute](https://gyro.dev/providers/google/compute/index.html)
* [DNS](https://gyro.dev/providers/google/dns/index.html)
* [GKE](https://gyro.dev/providers/google/gke/index.html)
* [IAM](https://gyro.dev/providers/google/iam/index.html)
* [KMS](https://gyro.dev/providers/google/kms/index.html)
* [PubSub](https://gyro.dev/providers/google/pubsub/index.html)
* [Storage](https://gyro.dev/providers/google/bucket/index.html)

## Developing the Google Provider

The provider is written in Java using Gradle as the build tool.

We recommend installing [AdoptOpenJDK](https://adoptopenjdk.net/) 11 or higher if you're going to contribute to this provider. 

Gyro uses the Gradle build tool. Once you have a JDK installed building is easy, just run ./gradlew at the root of the Gyro project. This wrapper script will automatically download and install Gradle for you, then build the provider:
```shell
$ ./gradlew
Downloading https://services.gradle.org/distributions/gradle-5.2.1-all.zip
..............................................................................................................................

Welcome to Gradle 5.2.1!

Here are the highlights of this release:
 - Define sets of dependencies that work together with Java Platform plugin
 - New C++ plugins with dependency management built-in
 - New C++ project types for gradle init
 - Service injection into plugins and project extensions

For more details see https://docs.gradle.org/5.2.1/release-notes.html

Starting a Gradle Daemon, 1 stopped Daemon could not be reused, use --status for details

.
.
.

BUILD SUCCESSFUL in 17s
38 actionable tasks: 28 executed, 10 from cache
$
```

## Getting Help

* Join the Gyro community chat on [Gitter](https://gitter.im/perfectsense/gyro).
* Take a look at the [documentation](https://gyro.dev/providers/google/index.html) for tutorial and examples.

## License

This software is open source under the [Apache License 2.0](https://github.com/perfectsense/gyro-google-provider/blob/master/LICENSE).
