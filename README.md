# ice2

Tool for extracting business rules and integrity constraints (PoC for diploma thesis). This repo is a split from the private repo where all of the implementation has been done.

## Installation

### The easy way

```shell
git clone 'git@github.com:apolicky/ice2.git'
cd ice2
# check that both shell scripts (*.sh) in the `scripts` folder are checked out with `LF` as the EoLine sequence
docker compose up setup-inputs setup-libs
docker compose up
```

- just open the swagger UI in your browser `http://localhost:8080/swagger-ui/index.html`
- and in another tab, open the neo4j instance `http://localhost:7474/browser/`
  - you should find the password for neo4j in `docker-compose.yml`

## Installation - the hard way

- we rely on FD and IND implementations from the Metanome Project
- you need to include it somehow
- your options:
  1. download built jars directly from Metanome project: <https://hpi.de/naumann/projects/data-profiling-and-analytics/metanome-data-profiling/algorithms.html>. With this approach, you'll need to do some java magic i was not able to do. Packages for fasterxml json parsing are clashing with Spark dependencies. We solved it by "shading" metanome jars.
  2. download built jars from <https://github.com/apolicky/ice2/releases/tag/v0.0.0>
  3. build on your own:
     - `git clone git@github.com:HPI-Information-Systems/metanome-algorithms.git`
     - install `metanome-algorithms` based on provided instructions
     - point your JAVA_HOME to Java 1.8
     - HyFD:
       - to HyFD maven manifest - HyFD/pom.xml
       - comment out `org.apache.maven.plugins___maven-assembly-plugin` from their `build/plugins` section
       - append the code block shown below, 2 new plugin sections
         - this needs to be done so that the included jar doesn't collide with packages required by Spark
       - run `mvn clean package shade:shade` in the HyFD folder
     - SPIDER:
       - to all 3 `SPIDER/(SPIDERAlgorithm|SPIDERDatabase|SPIDERFile)/pom.xml`, do the same step as you did for HyFD
       - run `mvn clean package shade:shade` in the SPIDER folder
       - you'll use the `SPIDER-1.2-SNAPSHOT-clean.jar` from SPIDERFile
- add the 2 .jars to the `libs` folder. The folder might not exist, so create it
  - `HyFD-1.2-SNAPSHOT-clean.jar`
  - `SPIDER-1.2-SNAPSHOT-clean.jar`
- open the solution in IDEA
- sync the pom project
- build it, run it
- you might need to set this VM option: `--add-exports java.base/sun.nio.ch=ALL-UNNAMED` to your profile

```xml
<plugins>
    <!-- keep this plugin -->
     <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <!-- configuration, ...s -->
     </plugin>
    <!-- comment out their use of plugin org.apache.maven.plugins___maven-assembly-plugin -->
    <!-- add the following 2 plugins -->
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-shade-plugin</artifactId>
        <executions>
            <execution>
                <phase>package</phase>
                <goals>
                    <goal>shade</goal>
                </goals>
                <configuration>
                    <relocations>
                        <relocation>
                            <pattern>com.fasterxml.jackson</pattern>
                            <shadedPattern>shaded.fasterxml.jackson</shadedPattern>
                        </relocation>
                    </relocations>
                </configuration>
            </execution>
        </executions>
    </plugin>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-antrun-plugin</artifactId>
        <version>1.8</version>
        <executions>
            <execution>
                <phase>package</phase>
                <goals>
                    <goal>run</goal>
                </goals>
                <configuration>
                    <tasks>
                        <unzip src="${project.build.directory}/${project.build.finalName}.jar"
                               dest="${project.build.directory}/cleaned-jar"/>
                        <delete includeemptydirs="true">
                            <fileset dir="${project.build.directory}/cleaned-jar" 
                                     includes="**/com/fasterxml/jackson/**"/>
                        </delete>
                        <jar destfile="${project.build.directory}/${project.build.finalName}-clean.jar"
                             basedir="${project.build.directory}/cleaned-jar"/>
                    </tasks>
                </configuration>
            </execution>
        </executions>
    </plugin>
</plugins>
```

## Licensing

The main code in this repository is licensed under the MIT License.

Some files in `src/main/java/de/metanome/backend/input/file` are unmodified and come from a project
licensed under the Apache License 2.0. These files retain their original
license notices and are distributed accordingly.

See `LICENSE`, `LICENSE-APACHE`, and `NOTICE` for details.
