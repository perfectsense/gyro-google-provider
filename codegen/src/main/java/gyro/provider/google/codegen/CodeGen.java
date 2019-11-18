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

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import gyro.core.command.GyroCommand;
import io.airlift.airline.Cli;
import io.airlift.airline.Help;

public class CodeGen {

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public static void main(String[] arguments) throws Exception {
        Cli.CliBuilder<Object> builder = Cli.<Object>builder("codegen");

        builder.withDefaultCommand(Help.class);
        builder.withCommand(GenerateCommand.class);
        Cli<Object> cli = builder.build();

        Object command = cli.parse(arguments);
        if (command instanceof GyroCommand) {
            ((GyroCommand) command).execute();
        }
    }

}
