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

import com.google.api.services.discovery.model.JsonSchema;
import com.google.api.services.discovery.model.RestDescription;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import gyro.core.resource.Diffable;
import java.io.File;
import javax.lang.model.element.Modifier;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;

public class DiffableGenerator {
    private static final String PROVIDER_PACKAGE = "gyro.provider.google";

    protected String schemaName;
    protected String output;
    protected RestDescription description;
    protected JsonSchema diffableSchema;
    protected TypeSpec.Builder resourceBuilder;

    public DiffableGenerator() {
    }

    public DiffableGenerator(RestDescription description, String schemaName, String output) {
        this.schemaName = schemaName;
        this.description = description;
        this.diffableSchema = description.getSchemas().get(schemaName);
        this.output = output;
        this.resourceBuilder = TypeSpec.classBuilder(schemaName)
            .addModifiers(Modifier.PUBLIC)
            .superclass(Diffable.class);
    }

    public DiffableGenerator(RestDescription description, String schemaName, JsonSchema diffableSchema, String output) {
        this.schemaName = schemaName;
        this.description = description;
        this.diffableSchema = diffableSchema;
        this.output = output;
        this.resourceBuilder = TypeSpec.classBuilder(schemaName)
            .addModifiers(Modifier.PUBLIC)
            .superclass(Diffable.class);
    }

    public TypeSpec generate() throws Exception {
        // Generate fields, getters, and setters
        if (diffableSchema.getProperties() != null) {
            for (String propertyName : diffableSchema.getProperties().keySet()) {
                JsonSchema property = diffableSchema.getProperties().get(propertyName);

                generateField(propertyName, property);
            }
        }

        TypeSpec typeSpec = resourceBuilder.build();
        JavaFile javaFile = JavaFile.builder(PROVIDER_PACKAGE + "." + description.getName(), typeSpec)
            .build();

        if (output != null) {
            javaFile.writeTo(new File(output));
        } else {
            javaFile.writeTo(System.out);
            System.out.println("----");
        }

        return typeSpec;
    }

    private void generateField(String name, JsonSchema property) throws Exception {
        String type = property.getType();

        if ("selfLink".equals(name) || "kind".equals(name) || "etag".equals(name)
            || "timeCreated".equals(name) || "updated".equals(name)) {
            return;
        }

        if ("string".equals(type)) {
            resourceBuilder.addField(FieldSpec.builder(String.class, name, Modifier.PRIVATE).build());
        } else if ("integer".equals(type)) {
            resourceBuilder.addField(FieldSpec.builder(Integer.class, name, Modifier.PRIVATE).build());
        } else if ("number".equals(type)) {
            resourceBuilder.addField(FieldSpec.builder(Double.class, name, Modifier.PRIVATE).build());
        } else if ("boolean".equals(type)) {
            resourceBuilder.addField(FieldSpec.builder(Boolean.class, name, Modifier.PRIVATE).build());
        } else if ("object".equals(type)) {
            String typeName = schemaName + StringUtils.capitalize(name);
            TypeSpec complexType = generateComplexType(typeName, property);

            resourceBuilder.addField(
                FieldSpec.builder(TypeVariableName.get(complexType.name), name, Modifier.PRIVATE)
                    .build());
        } else if ("array".equals(type)) {
            JsonSchema schema = property.getItems();
            String schemaType = schema.getType();

            if ("object".equals(schemaType)) {
                String typeName = removePlural(schemaName + StringUtils.capitalize(name));
                TypeSpec complexType = generateComplexType(typeName, property.getItems());
                ClassName list = ClassName.get("java.util", "List");
                TypeName listOf = ParameterizedTypeName.get(list, TypeVariableName.get(complexType.name));

                resourceBuilder.addField(
                    FieldSpec.builder(listOf, removePlural(name), Modifier.PRIVATE)
                        .build());
            } else if ("string".equals(schemaType)) {
                ClassName list = ClassName.get("java.util", "List");
                TypeName listOf = ParameterizedTypeName.get(list, TypeVariableName.get(String.class));

                resourceBuilder.addField(
                    FieldSpec.builder(listOf, name, Modifier.PRIVATE)
                        .build());
            }

        } else if (type == null) {
            String typeName = StringUtils.uncapitalize(name);

            TypeSpec complexType = generateComplexType(property.get$ref());

            resourceBuilder.addField(
                FieldSpec.builder(TypeVariableName.get(complexType.name), typeName, Modifier.PRIVATE)
                    .build());
        } else {
            throw new NotImplementedException("Unhandled type: " + name + ": " + type);
        }

    }

    private MethodSpec generateMethods(String name, JsonSchema property) {
        return null;
    }

    private TypeSpec generateComplexType(String name, JsonSchema schema) throws Exception {
        DiffableGenerator diffableGenerator = new DiffableGenerator(description, name, schema, output);
        return diffableGenerator.generate();
    }

    private TypeSpec generateComplexType(String name) throws Exception {
        DiffableGenerator diffableGenerator = new DiffableGenerator(description, name, output);
        return diffableGenerator.generate();
    }

    private String removePlural(String word) {
        if (word.endsWith("s")) {
            return StringUtils.chop(word);
        }

        return word;
    }
}
