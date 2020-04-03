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

package gyro.google.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.psddev.dari.util.StringUtils;

public final class Utils {

    /**
     * Extract zone or region name from url.
     * If non-url is provided, it will be returned.
     */
    public static String extractName(String url) {
        return Optional.ofNullable(url)
            .map(e -> e.substring(e.lastIndexOf("/") + 1))
            .orElse("");
    }

    /**
     * Convert Gyro filter map to filter string that Google uses.
     * This can be used in APIs that accept `filter` in a `list` request.
     *
     * <p>
     * e.g.
     * <pre>
     * { "name": "foo", "enable-schedule": true }
     * ->
     * "(name = \"foo\") (enableSchedule = true)"
     * </pre>
     * </p>
     */
    public static String convertToFilters(Map<String, String> filterMap) {
        if (filterMap == null) {
            return "";
        }
        return filterMap.entrySet()
            .stream()
            .map(e -> String.format(
                "(%s = \"%s\")",
                Stream.of(e.getKey().split("\\."))
                    .map(StringUtils::toCamelCase)
                    .collect(Collectors.joining(".")),
                e.getValue())
            )
            .collect(Collectors.joining(" "));
    }

    public static String getServiceAccountIdFromName(String name, String projectId) {
        return String.format("projects/%1$s/serviceAccounts/%2$s@%1$s.iam.gserviceaccount.com", projectId, name);
    }

    public static String getServiceAccountNameFromId(String id) {
        List<String> list = Arrays.asList(id.split("/"));
        int index = list.indexOf("serviceAccounts");
        return list.get(index + 1).split("@")[0];
    }

    public static String getServiceAccountEmailFromId(String id) {
        List<String> list = Arrays.asList(id.split("/"));
        int index = list.indexOf("serviceAccounts");
        return list.get(index + 1);
    }

    public static boolean isRoleIdForCustomRole(String roleId) {
        return Arrays.asList(roleId.split("/")).size() > 2;

    }

    public static String getKmsLocationFromId(String id) {
        List<String> list = Arrays.asList(id.split("/"));
        int index = list.indexOf("locations");
        return list.get(index + 1);
    }

    public static String getKmsKeyRingNameFromId(String id) {
        List<String> list = Arrays.asList(id.split("/"));
        int index = list.indexOf("keyRings");
        return list.get(index + 1);
    }

    public static String getKmsKeyNameFromId(String id) {
        List<String> list = Arrays.asList(id.split("/"));
        int index = list.indexOf("cryptoKeys");
        return list.get(index + 1);
    }

    public static String getKmsKeyRingIdFromId(String id) {
        List<String> list = Arrays.asList(id.split("/"));
        int indexStart = list.indexOf("projects");
        int indexEnd = list.indexOf("keyRings") + 1;
        return String.join("/", Arrays.copyOfRange(id.split("/"), indexStart, indexEnd + 1));
    }

    public static String getKmsKeyIdFromId(String id) {
        List<String> list = Arrays.asList(id.split("/"));
        int indexStart = list.indexOf("projects");
        int indexEnd = list.indexOf("cryptoKeys") + 1;
        return String.join("/", Arrays.copyOfRange(id.split("/"), indexStart, indexEnd + 1));
    }

    public static String getKmsPrimaryKeyVersionFromId(String id) {
        List<String> list = Arrays.asList(id.split("/"));
        int index = list.indexOf("cryptoKeyVersions");
        return list.get(index + 1);
    }
}
