## Readme

The codegen module allows you to automatically generate resource files based on the [GCP Discovery API](https://developers.google.com/discovery).

The codegen generates java files for for GCP resources. Under a base package abstract java classes are created for each resource, having all the fields, their getters and setters with annotations.
Fields that are other resources are created as references as well as fields which are complex in nature are created as separate java files and referenced where needed.

The generated fields have the following annotations if applicable
 - `@Required` - if the field is required.
 - `@Output` - if the field is output only.
 - `@ValidStrings` - If the filed can only be set to certain values.
 - `@Regex` - If the field value needs to match a particular regex.

Every time the codegen is run, it overwrites the classes it generates if it exists already under the base package.

Optionally the codegen can also generate concrete implementation of the abstract resource, setting the `@Type` on the resource file and having blank implementation of the CRUD methods. If the concrete implementations exists, they are not overwritten. 

### Usage
Run the project jar file with appropriate param to generate resource files.

java -jar build/libs/gyro-google-provider-codegen-0.99.1-SNAPSHOT.jar generate <package_name> --output <java_file_output_path>

package_name - The name of the package to generate resource files for like ``compute``, ``storage``, ``dns``, ``iam`` etc.

Options
 - `output` - The location where the resource files are going to be generated. If you are in the project directory this is typically `../src/main/java`.
 - `resource` - When you specify a resource, the codegen only generates resource files for that particular resource and any dependent resources.
 - `version` - Specify the version of the GCP API that the codegen will generate the resource class for. Defaults to ``v1`` when not specified.
 - `generate-concrete` - Adding this option generates concrete classes that implement the abstract resource classes. If concrete classes already exist, it does not touch that file.  

#### Example

Generate all resources for the compute package along with their concrete implementations

`java -jar build/libs/gyro-google-provider-codegen-0.99.1-SNAPSHOT.jar generate compute --output ../src/main/java --generate-concrete`

Generate network resource for the compute package without the concrete implementations

`java -jar build/libs/gyro-google-provider-codegen-0.99.1-SNAPSHOT.jar generate compute --output ../src/main/java --resource networks`

## License

[Apache License 2.0](https://github.com/perfectsense/gyro-google-provider/blob/master/LICENSE) 
