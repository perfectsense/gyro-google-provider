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

package gyro.google.compute;

import com.google.cloud.compute.v1.ProjectGlobalImageFamilyName;
import com.google.cloud.compute.v1.ProjectGlobalImageName;
import com.google.cloud.compute.v1.ProjectGlobalSnapshotName;
import com.google.cloud.compute.v1.ProjectRegionDiskTypeName;
import com.google.cloud.compute.v1.ProjectRegionResourcePolicyName;
import com.google.cloud.compute.v1.ProjectZoneDiskTypeName;
import com.google.cloud.compute.v1.ProjectZoneName;

public final class ComputeUtils {
    static String toSourceImageUrl(String projectId, String image) {
        String parseImage = formatResource(projectId, image);
        if (ProjectGlobalImageName.isParsableFrom(parseImage)) {
            return ProjectGlobalImageName.parse(parseImage).toString();
        }
        if (ProjectGlobalImageFamilyName.isParsableFrom(parseImage)) {
            return ProjectGlobalImageFamilyName.parse(parseImage).toString();
        }
        return ProjectGlobalImageName.format(image, projectId);
    }

    static String toSourceSnapshotUrl(String projectId, String snapshot) {
        String parseSnapshot = formatResource(projectId, snapshot);
        if (ProjectGlobalSnapshotName.isParsableFrom(parseSnapshot)) {
            return ProjectGlobalSnapshotName.parse(parseSnapshot).toString();
        }
        return ProjectGlobalSnapshotName.format(projectId, snapshot);
    }

    static String toResourcePolicyUrl(String projectId, String policy, String region) {
        String parsePolicy = formatResource(projectId, policy);
        if (ProjectRegionResourcePolicyName.isParsableFrom(parsePolicy)) {
            return ProjectRegionResourcePolicyName.parse(parsePolicy).toString();
        }
        return ProjectRegionResourcePolicyName.format(projectId, region, policy);
    }

    static String toZoneUrl(String projectId, String zone) {
        String parseZone = formatResource(projectId, zone);
        if (ProjectZoneName.isParsableFrom(parseZone)) {
            return ProjectZoneName.parse(parseZone).toString();
        }
        return ProjectZoneName.format(projectId, zone);
    }

    static String toRegionDiskTypeUrl(String projectId, String diskType, String region) {
        String parseDiskType = formatResource(projectId, diskType);
        if (ProjectRegionDiskTypeName.isParsableFrom(parseDiskType)) {
            return ProjectRegionDiskTypeName.parse(parseDiskType).toString();
        }
        return ProjectRegionDiskTypeName.format(diskType, projectId, region);
    }

    static String toZoneDiskTypeUrl(String projectId, String diskType, String zone) {
        String parseDiskType = formatResource(projectId, diskType);
        if (ProjectZoneDiskTypeName.isParsableFrom(parseDiskType)) {
            return ProjectZoneDiskTypeName.parse(parseDiskType).toString();
        }
        return ProjectZoneDiskTypeName.format(diskType, projectId, zone);
    }

    private static String formatResource(String projectId, String resource) {
        return resource.contains("projects/")
            ? resource.substring(resource.indexOf("projects/") + 9)
            : resource.startsWith("global/")
                ? projectId + "/" + resource
                : resource;
    }
}
