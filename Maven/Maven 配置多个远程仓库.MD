# Maven 配置多个远程仓库

配置Maven Settings.xml文件

1. `mirrorOf` 将 `*` 改为 `central`

```xml
     <!-- 原来的 http://repo1.maven.org  -->
    <mirror>
      <id>mavenRepository3</id>
      <mirrorOf>central</mirrorOf>
      <name>mavenRepository3</name>
      <url>https://repo.maven.apache.org/maven2/</url>
    </mirror>
```

2. 调整 `profiles` `activeProfiles`

```xml
  <profiles>
    <profile>
      <id>private-repository</id> 
      <repositories>
        <repository>
          <id>kaadas</id> 
          <url>http://private-repository/repository/maven-snapshots/</url> 
          <releases>
            <enabled>false</enabled>
          </releases> 
          <snapshots>
            <enabled>true</enabled> 
            <!-- <updatePolicy>always</updatePolicy> -->
          </snapshots>
        </repository>
      </repositories>
    </profile>
    <profile>
      <id>maven-central</id> 
      <repositories>
        <repository>
          <id>maven-central</id> 
          <url>https://repo.maven.apache.org/maven2/</url> 
          <releases>
            <enabled>true</enabled>
          </releases> 
          <snapshots>
            <enabled>false</enabled> 
          </snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>

  <activeProfiles>
    <activeProfile>private-repository</activeProfile>
    <activeProfile>maven-central</activeProfile>
  </activeProfiles>
```
