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

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;

import com.google.api.services.discovery.model.JsonSchema;
import com.google.api.services.discovery.model.RestDescription;
import com.psddev.dari.util.ObjectUtils;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
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
            for (String propertyName : getSortedPropertyNames()) {
                JsonSchema property = diffableSchema.getProperties().get(propertyName);

                if (!isDeprecated(property)) {
                    generateField(propertyName, property);
                }
            }
        }

        TypeSpec typeSpec = resourceBuilder.build();
        JavaFile javaFile = JavaFile.builder(PROVIDER_PACKAGE + "." + description.getName(), typeSpec).indent("    ")
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

        name = isReservedName(name) ? handleReservedName(name) : name;

        if ("kind".equals(name) || "etag".equals(name)
            || "timeCreated".equals(name) || "updated".equals(name)) {
            return;
        }

        if ("string".equals(type)) {
            resourceBuilder.addField(FieldSpec.builder(String.class, name, Modifier.PRIVATE).build());

            generateGetterSetter(name, TypeVariableName.get(String.class), false, property);
        } else if ("integer".equals(type)) {
            resourceBuilder.addField(FieldSpec.builder(Integer.class, name, Modifier.PRIVATE).build());

            generateGetterSetter(name, TypeVariableName.get(Integer.class), false, property);
        } else if ("number".equals(type)) {
            resourceBuilder.addField(FieldSpec.builder(Double.class, name, Modifier.PRIVATE).build());

            generateGetterSetter(name, TypeVariableName.get(Double.class), false, property);
        } else if ("boolean".equals(type)) {
            resourceBuilder.addField(FieldSpec.builder(Boolean.class, name, Modifier.PRIVATE).build());

            generateGetterSetter(name, TypeVariableName.get(Boolean.class), false, property);
        } else if ("object".equals(type)) {
            String typeName = schemaName + StringUtils.capitalize(name);
            TypeSpec complexType = generateComplexType(typeName, property);

            resourceBuilder.addField(
                FieldSpec.builder(TypeVariableName.get(complexType.name), name, Modifier.PRIVATE)
                    .build());

            generateGetterSetter(name, TypeVariableName.get(complexType.name), false, property);
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

                generateGetterSetter(removePlural(name), listOf, true, property);
            } else if ("string".equals(schemaType)) {
                ClassName list = ClassName.get("java.util", "List");
                TypeName listOf = ParameterizedTypeName.get(list, TypeVariableName.get(String.class));

                resourceBuilder.addField(
                    FieldSpec.builder(listOf, name, Modifier.PRIVATE)
                        .build());

                generateGetterSetter(name, listOf, true, property);
            } else if (schemaType == null) {
                TypeSpec complexType = generateComplexType(schema.get$ref());
                ClassName list = ClassName.get("java.util", "List");
                TypeName listOf = ParameterizedTypeName.get(list, TypeVariableName.get(complexType.name));

                resourceBuilder.addField(
                    FieldSpec.builder(listOf, removePlural(name), Modifier.PRIVATE)
                        .build());

                generateGetterSetter(removePlural(name), listOf, true, property);

            } else {
                throw new NotImplementedException("Unhandled schema: " + name + ": " + type + "(" + schemaType + ")");
            }

        } else if (type == null) {
            String typeName = StringUtils.uncapitalize(name);

            TypeSpec complexType = generateComplexType(property.get$ref());

            resourceBuilder.addField(
                FieldSpec.builder(TypeVariableName.get(complexType.name), typeName, Modifier.PRIVATE)
                    .build());

            generateGetterSetter(typeName, TypeVariableName.get(complexType.name), false, property);
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
            word = StringUtils.chop(word);
            return isReservedName(word) ? handleReservedName(word) : word;
        }

        return word;
    }

    private void generateGetterSetter(String name, TypeName type, boolean isList, JsonSchema property) {
        // getter start
        MethodSpec.Builder builder = MethodSpec.methodBuilder(String.format("get%s", StringUtils.capitalize(name)))
            .returns(type)
            .addModifiers(Modifier.PUBLIC);

        // Add java doc
        if (property.getDescription() != null) {
            builder.addJavadoc(property.getDescription()
                .replaceFirst(Pattern.quote("[Output Only] "), "")
                .replaceAll("\n", "") + "\n");
        }

        // Add code to initialize list type variable if null
        if (isList) {
            builder.beginControlFlow("if ($L == null)", name)
                .addStatement("$L = new $T<>()", name, ClassName.get("java.util", "ArrayList"))
                .endControlFlow()
                .addCode("\n");
        }

        // Add @Required annotation
        if (isRequired(property)) {
            builder.addAnnotation(Required.class);
        }

        // Add @Output annotation
        if (isOutput(property)) {
            builder.addAnnotation(Output.class);
        }

        // Add @Regex annotation
        // Ignore if output type attribute
        if (!isOutput(property) && !ObjectUtils.isBlank(property.getPattern())) {
            builder.addAnnotation(AnnotationSpec.builder(Regex.class)
                .addMember("value", "$S", property.getPattern())
                .build());
        }

        resourceBuilder.addMethod(builder
            .addCode(CodeBlock.builder().addStatement("return $L", name).build())
            .build()
        );
        // getter end

        //setter
        resourceBuilder.addMethod(MethodSpec.methodBuilder(String.format("set%s", StringUtils.capitalize(name)))
            .returns(TypeName.VOID)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(type, name)
            .addCode(CodeBlock.builder().addStatement("this.$L = $L", name, name).build())
            .build()
        );
    }

    // Sorts the properties based on name, with required fields on top and output fields at the bottom
    private List<String> getSortedPropertyNames() {
        Comparator<Map.Entry<String, JsonSchema>> alphabeticComparator = Comparator.comparing(entry -> entry.getKey()
            .toLowerCase());
        Comparator<Map.Entry<String, JsonSchema>> requiredComparator = Comparator.comparing(a -> !isRequired(a.getValue()));
        Comparator<Map.Entry<String, JsonSchema>> outputComparator = Comparator.comparing(a -> isOutput(a.getValue()));

        return diffableSchema.getProperties().entrySet().stream()
            .sorted(alphabeticComparator)
            .sorted(requiredComparator)
            .sorted(outputComparator)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    private boolean isRequired(JsonSchema property) {
        return property.getAnnotations() != null && !property.getAnnotations().getRequired().isEmpty();
    }

    private boolean isOutput(JsonSchema property) {
        return property.getDescription() != null
            && property.getDescription().startsWith("[Output Only]")
            && !property.getDescription().contains("You must specify this field as part of the HTTP request URL");
    }

    private boolean isDeprecated(JsonSchema property) {
        return property.getDescription() != null && property.getDescription().startsWith("Deprecated");
    }

    private boolean isReservedName(String name) {
        if (name.equals("interface")) {
            return true;
        }

        return false;
    }

    private String handleReservedName(String name) {
        if (name.equals("interface")) {
            return "interfaceValue";
        }

        return name;
    }
}
