## 1.2.1 (July 31st, 2023)

* Updated Function call to getSelfLink() over getName() to fix issue with BackendBucketResource

## 1.1.2 (June 21st, 2023)

ENHANCEMENTS:

* [256](https://github.com/perfectsense/gyro-google-provider/pull/256): Add option to configure edge security policy options.

## 1.1.1 (June 8th, 2023)

ISSUES FIXED:

* [254](https://github.com/perfectsense/gyro-google-provider/pull/254): Removes the validation for disabling cdn when security policy is configured for a backend service resource.

## 1.1.0 (June 7th, 2023)

ENHANCEMENTS:

* [250](https://github.com/perfectsense/gyro-google-provider/pull/250): Add additional options for Security Policy Resource (Cloud Armor).
* [251](https://github.com/perfectsense/gyro-google-provider/pull/250): Allow modifying node size of a gke node pool.

## 1.0.1 (February 23rd, 2023)

ISSUES FIXED:

* [244](https://github.com/perfectsense/gyro-google-provider/pull/244): Fix Service account refresh issue.

## 1.0.0 (June 3rd, 2022)

ENHANCEMENTS:

* [231](https://github.com/perfectsense/gyro-google-provider/pull/231): Implement support for GKE v1beta1 resources.
* [232](https://github.com/perfectsense/gyro-google-provider/pull/232): Update google cloud libraries bom to 24.2.0
* [239](https://github.com/perfectsense/gyro-google-provider/pull/239): Supporting google client creation outside of the provider.

## 0.99.3 (January 28th, 2022)

ENHANCEMENTS:

* [205](https://github.com/perfectsense/gyro-google-provider/issues/205): Add support for Pubsub.
* [206](https://github.com/perfectsense/gyro-google-provider/issues/206): Add support for Google Kubernetes Engine.
* [213](https://github.com/perfectsense/gyro-google-provider/issues/213): Add support for Http redirect on Compute path rule.
* [216](https://github.com/perfectsense/gyro-google-provider/issues/216): Add support for Artifact registry.
* [220](https://github.com/perfectsense/gyro-google-provider/pull/220): Add support for secondary range in Subnetwork.

MISC:

* [200](https://github.com/perfectsense/gyro-google-provider/issues/200): Use @uses-credentials to set custom credentials for file backend.
* [202](https://github.com/perfectsense/gyro-google-provider/issues/202): Update doc generation.

ISSUES FIXED:

* [207](https://github.com/perfectsense/gyro-google-provider/issues/207): Fix service account role refresh error on custom role.
* [226](https://github.com/perfectsense/gyro-google-provider/pull/226): Allow uniform bucket level access to be updated.

## 0.99.2 (August 25th, 2020)

ENHANCEMENTS:

* [190](https://github.com/perfectsense/gyro-google-provider/issues/190): Allow GCP storage to work with remote state backends.

MISC:

* [193](https://github.com/perfectsense/gyro-google-provider/issues/193): Fix remote file backend to only delete a file if it exists.
* [195](https://github.com/perfectsense/gyro-google-provider/issues/195): Add `exists(String file)` and `copy(String source, String dest)` methods to FileBackend.

## 0.99.1 (June 10th, 2020)

NEW FEATURES:

* [11](https://github.com/perfectsense/gyro-google-provider/issues/11): Add support for Target Proxy.
* [12](https://github.com/perfectsense/gyro-google-provider/issues/12): Add support for Forwarding Rule.
* [13](https://github.com/perfectsense/gyro-google-provider/issues/13): Add support for URL Maps.
* [14](https://github.com/perfectsense/gyro-google-provider/issues/14): Add support for DNS Zones and Policies.
* [15](https://github.com/perfectsense/gyro-google-provider/issues/15): Add support for Network Endpoint Group.
* [16](https://github.com/perfectsense/gyro-google-provider/issues/16): Add support for Bucket.
* [17](https://github.com/perfectsense/gyro-google-provider/issues/17): Add support for Project Metadata.
* [18](https://github.com/perfectsense/gyro-google-provider/issues/18): Add support for Health Checks.
* [19](https://github.com/perfectsense/gyro-google-provider/issues/19): Add support for Instance Groups.
* [20](https://github.com/perfectsense/gyro-google-provider/issues/20): Add support for Instance Templates.
* [21](https://github.com/perfectsense/gyro-google-provider/issues/21): Add support for Disks.
* [22](https://github.com/perfectsense/gyro-google-provider/issues/22): Add support for Instances.
* [23](https://github.com/perfectsense/gyro-google-provider/issues/23): Add support for Routes.
* [24](https://github.com/perfectsense/gyro-google-provider/issues/24): Add support for Firewall Rule.
* [25](https://github.com/perfectsense/gyro-google-provider/issues/25): Add support for External/Static IP Address.
* [54](https://github.com/perfectsense/gyro-google-provider/issues/54): Add support for Snapshots.
* [55](https://github.com/perfectsense/gyro-google-provider/issues/55): Add support for Images.
* [59](https://github.com/perfectsense/gyro-google-provider/issues/59): Add support for Managed Instance Groups.
* [60](https://github.com/perfectsense/gyro-google-provider/issues/60): Add support for Resource Policies
* [63](https://github.com/perfectsense/gyro-google-provider/issues/63): Add support for Bucket Iam Policy.
* [67](https://github.com/perfectsense/gyro-google-provider/issues/67): Add support for Security Policy.
* [73](https://github.com/perfectsense/gyro-google-provider/issues/73): Add support for Backend.
* [79](https://github.com/perfectsense/gyro-google-provider/issues/79): Add support for Target Pools.
* [95](https://github.com/perfectsense/gyro-google-provider/issues/95): Add support for Autoscaler.
* [97](https://github.com/perfectsense/gyro-google-provider/issues/97): Add support for Storage Iam Role.
* [98](https://github.com/perfectsense/gyro-google-provider/issues/98): Add support for Legacy HTTP Health Check.
* [115](https://github.com/perfectsense/gyro-google-provider/issues/115): Add support for SSl Certificates.
* [116](https://github.com/perfectsense/gyro-google-provider/issues/116): Add support for KMS key Rings.
* [117](https://github.com/perfectsense/gyro-google-provider/issues/117): Add support for KMS Crypto Keys.
* [127](https://github.com/perfectsense/gyro-google-provider/issues/127): Add support for Service Account.
* [144](https://github.com/perfectsense/gyro-google-provider/issues/144): Add support for Compute Routers.

MISC:

* [125](https://github.com/perfectsense/gyro-google-provider/issues/125): Allow gyro ssh on instances.
* [147](https://github.com/perfectsense/gyro-google-provider/issues/147): Allow gyro ssh on autoscaled instances.
* [179](https://github.com/perfectsense/gyro-google-provider/issues/179): Report instance health per backend.
