package gyro.provider.google.codegen;

import java.io.File;
import java.util.Set;
import javax.lang.model.element.Modifier;

import com.google.common.base.CaseFormat;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;

public class ResourceConcreteGenerator {

    protected TypeSpec typeSpec;
    protected String output;
    protected String schemaName;
    protected String basePackage;
    protected String packageName;

    public ResourceConcreteGenerator(
        TypeSpec typeSpec,
        String output,
        String schemaName,
        String basePackage,
        String packageName) {
        this.typeSpec = typeSpec;
        this.output = output;
        this.schemaName = schemaName;
        this.basePackage = basePackage;
        this.packageName = packageName;
    }

    public void generate() throws Exception {
        String resourcePackage = basePackage.replace(".base", "");
        String resourceName = schemaName + "Resource";
        String resourcePackagePath = resourcePackage.replaceAll("\\.", "/");
        String filePath = String.format("%s/%s/%s.java", output, resourcePackagePath, resourceName);
        File file = new File(filePath);

        if (!file.exists()) {
            TypeSpec.Builder resourceBuilder = TypeSpec.classBuilder(resourceName)
                .addModifiers(Modifier.PUBLIC)
                .superclass(ClassName.get(basePackage, typeSpec.name));

            String typeName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, schemaName);

            resourceBuilder.addAnnotation(
                AnnotationSpec.builder(Type.class)
                    .addMember("value", "$S", packageName + "-" + typeName)
                    .build()
            );

            generateMethods(resourceBuilder);

            TypeSpec resource = resourceBuilder.build();

            JavaFile javaFile = JavaFile.builder(resourcePackage, resource).indent("    ")
                .build();

            System.out.println("\nGenerating concrete class for " + schemaName);

            if (output != null) {
                javaFile.writeTo(new File(output));
            } else {
                javaFile.writeTo(System.out);
                System.out.println("----");
            }
        } else {
            System.out.println("\nSkipping concrete class for " + schemaName + ". Already present.");
        }
    }

    private void generateMethods(TypeSpec.Builder resourceBuilder) {
        resourceBuilder.addMethod(MethodSpec.methodBuilder("refresh")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(boolean.class)
            .addCode(CodeBlock.builder().addStatement("return false").build())
            .build());

        resourceBuilder.addMethod(MethodSpec.methodBuilder("create")
            .addAnnotation(Override.class)
            .addException(Exception.class)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ParameterSpec.builder(GyroUI.class, "ui").build())
            .addParameter(ParameterSpec.builder(State.class, "state").build())
            .build());

        resourceBuilder.addMethod(MethodSpec.methodBuilder("update")
            .addAnnotation(Override.class)
            .addException(Exception.class)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ParameterSpec.builder(GyroUI.class, "ui").build())
            .addParameter(ParameterSpec.builder(State.class, "state").build())
            .addParameter(ParameterSpec.builder(Resource.class, "current").build())
            .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(Set.class, String.class), "changedFieldNames")
                .build())
            .build());

        resourceBuilder.addMethod(MethodSpec.methodBuilder("delete")
            .addAnnotation(Override.class)
            .addException(Exception.class)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ParameterSpec.builder(GyroUI.class, "ui").build())
            .addParameter(ParameterSpec.builder(State.class, "state").build())
            .build());
    }
}
