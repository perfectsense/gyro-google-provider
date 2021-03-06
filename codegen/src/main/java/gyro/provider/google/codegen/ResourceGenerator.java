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

import javax.lang.model.element.Modifier;

import com.google.api.services.discovery.model.RestDescription;
import com.google.api.services.discovery.model.RestMethod;
import com.google.api.services.discovery.model.RestResource;
import com.squareup.javapoet.TypeSpec;
import gyro.core.resource.Resource;

public class ResourceGenerator extends DiffableGenerator {

    public ResourceGenerator(RestDescription description, String output, RestResource restResource, boolean generateConcrete) {
        this.description = description;
        this.output = output;

        RestMethod getMethod = restResource.getMethods().get("get");
        RestMethod.Response getResponse = getMethod.getResponse();

        this.schemaName = getResponse.get$ref();
        this.diffableSchema = description.getSchemas().get(schemaName);
        this.resourceBuilder = TypeSpec.classBuilder("Abstract" + schemaName + "Resource")
            .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
            .superclass(Resource.class);
        this.generateConcrete = generateConcrete;
    }
}
