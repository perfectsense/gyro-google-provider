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

import java.util.List;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.discovery.Discovery;
import com.google.api.services.discovery.model.RestDescription;
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

    @Override
    public void execute() throws Exception {
        String service = arguments.get(0);
        version = version != null ? version : "v1";

        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Discovery discovery = new Discovery(httpTransport, JSON_FACTORY, null);

        RestDescription description = discovery.apis().getRest(service, version).execute();

        if (resource != null) {
            ResourceGenerator generator = new ResourceGenerator(description, resource, output);
            generator.generate();
        } else {
            for (String resource : description.getResources().keySet()) {
                System.out.println("Generating classes for " + resource);

                ResourceGenerator generator = new ResourceGenerator(description, resource, output);
                generator.generate();
            }
        }
    }
}
