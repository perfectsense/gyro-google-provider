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

package gyro.provider.google.codegen;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.discovery.Discovery;
import com.google.api.services.discovery.model.RestDescription;
import com.google.api.services.discovery.model.RestResource;
import com.google.common.base.CaseFormat;
import com.squareup.javapoet.TypeSpec;
import gyro.core.command.GyroCommand;
import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.Option;

@Command(name = "generate", description = "Generate Java Classes for GCP Provider")
public class GenerateCommand implements GyroCommand {

    private static final String PROVIDER_PACKAGE = "gyro.provider.google";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @Arguments(description = "", required = true)
    private List<String> arguments;

    @Option(name = { "--version" })
    private String version;

    @Option(name = { "--resource" })
    private String resource;

    @Option(name = { "--output" })
    private String output;

    @Option(name = {"--generate-concrete"})
    private boolean generateConcrete;

    @Override
    public void execute() throws Exception {
        String service = arguments.get(0);
        version = version != null ? version : "v1";

        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Discovery discovery = new Discovery(httpTransport, JSON_FACTORY, null);

        RestDescription description = discovery.apis().getRest(service, version).execute();

        Map<String, TypeSpec> typeSpecMap = new HashMap<>();
        Map<String, ResourceGenerator> resourceGeneratorMap = new HashMap<>();
        Map<String, RestResource> restResourceMap = getResourceMap(description.getResources());

        for (String resourceKey : restResourceMap.keySet()) {
            ResourceGenerator generator = new ResourceGenerator(description, output, restResourceMap.get(resourceKey), generateConcrete);
            String type = CaseFormat.UPPER_CAMEL.to(
                CaseFormat.LOWER_CAMEL,
                restResourceMap.get(resourceKey).getMethods().get("get").getResponse().get$ref());
            typeSpecMap.put(type, generator.resourceBuilder.build());
            resourceGeneratorMap.put(type, generator);
        }

        if (resource != null) {
            System.out.println("\nGenerating classes for " + resource);
            ResourceGenerator generator = new ResourceGenerator(description, output, restResourceMap.get(resource), generateConcrete);
            generator.generate(typeSpecMap);
            Set<String> createdResources = new HashSet<>();
            createdResources.add(resource);
            generateDependentResource(generator, typeSpecMap, resourceGeneratorMap, createdResources);
        } else {
            for (String resourceKey : restResourceMap.keySet()) {
                ResourceGenerator generator = new ResourceGenerator(description, output, restResourceMap.get(resourceKey), generateConcrete);

                System.out.println("\nGenerating classes for " + resourceKey);
                generator.generate(typeSpecMap);
            }
        }
    }

    private void generateDependentResource(
        ResourceGenerator resourceGenerator,
        Map<String, TypeSpec> resourceMap,
        Map<String, ResourceGenerator> generatorMap,
        Set<String> createdResources) throws Exception {
        for (String resource : resourceGenerator.dependentResources) {
            if (!createdResources.contains(resource)) {
                System.out.println("\nGenerating classes for " + resource);
                ResourceGenerator generator = generatorMap.get(resource);
                generator.generate(resourceMap);
                createdResources.add(resource);

                if (!generator.dependentResources.isEmpty()) {
                    generateDependentResource(generator, resourceMap, generatorMap, createdResources);
                }
            }
        }
    }

    private boolean isNotResource(String resource) {
        return Arrays.asList("regions", "zones").contains(resource);
    }

    private Map<String, RestResource> getResourceMap(Map<String, RestResource> restResourceMap) {
        Map<String, RestResource> resourceMap = new HashMap<>();
        for (String resourceKey : restResourceMap.keySet()) {
            if (isNotResource(resourceKey)) {
                continue;
            }

            RestResource restResource = restResourceMap.get(resourceKey);
            if (restResource.getMethods() != null && restResource.getMethods().containsKey("get")) {
                resourceMap.put(resourceKey, restResource);
            }

            if (restResource.getResources() != null && !restResource.getResources().isEmpty()) {
                resourceMap.putAll(getResourceMap(restResource.getResources()));
            }
        }

        return resourceMap;
    }
}
