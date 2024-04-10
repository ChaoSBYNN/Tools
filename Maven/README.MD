# Maven

* [IDEA构建maven项目生成的文件详解](https://blog.csdn.net/vhfdff/article/details/89048334)

```xml
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>11</source>
                    <target>11</target>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>${spring-boot.version}</version>
                <configuration>
                    <mainClass>com.kaadas.open.OpenClientServerApplication</mainClass>
                    <executable>true</executable>
                    <!-- 配置在使用第三方的jar包作为依赖，打包时，指定maven把scope为system的依赖同样打到jar包中去，
                    需要配合dependency的<scope>system</scope>和<systemPath>${project.basedir}/libs/xxx-api-sdk-1.1.0.jar</systemPath>使用-->
                    <includeSystemScope>true</includeSystemScope>
                </configuration>
                <executions>
                    <execution>
                        <id>repackage</id>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
<!--            <plugin>-->
<!--                <groupId>org.apache.maven.plugins</groupId>-->
<!--                <artifactId>maven-assembly-plugin</artifactId>-->
<!--                <configuration>-->
<!--                    <descriptorRefs>-->
<!--                        &lt;!&ndash;给jar包起的别名&ndash;&gt;-->
<!--                        <descriptorRef>jar-with-dependencies</descriptorRef>-->
<!--                    </descriptorRefs>-->
<!--                    <archive>-->
<!--                        <manifest>-->
<!--                            <addClasspath>true</addClasspath>-->
<!--                            <classpathPrefix>lib/</classpathPrefix>-->
<!--                            &lt;!&ndash;添加项目中主类&ndash;&gt;-->
<!--                            <mainClass>com.kaadas.open.OpenClientServerApplication</mainClass>-->
<!--                        </manifest>-->
<!--                    </archive>-->
<!--                </configuration>-->
<!--                <executions>-->
<!--                    <execution>-->
<!--                        <id>make-assembly</id>-->
<!--                        <phase>package</phase>-->
<!--                        <goals>-->
<!--                            <goal>single</goal>-->
<!--                        </goals>-->
<!--                    </execution>-->
<!--                </executions>-->
<!--            </plugin>-->
        </plugins>
```
