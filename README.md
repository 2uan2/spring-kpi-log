## Quick Start

- Just add the dependency to an existing Spring Boot project

```xml
<dependency>
    <groupId>com.atviettelsolutions</groupId>
    <artifactId>spring-kpi-log</artifactId>
    <version>1.0.0</version>
</dependency>
```

- Then, add the following properties to your `application.properties` file.

```properties
kpi.grpc.enable=true #if you wanna kpi log for grpc request
app.application.code=app_code #code of your application
app.service.code=service_code #code of your service

# Determine database to save log, for now, this library supports mongodb, mariadb, mysql, postgresql, elasticsearch
kpi.database=mongodb

# With MongoDB
kpi.datasource.url=mongodb://localhost:27017/my_db 

# With SQL Database
kpi.datasource.url=your_database_uri #example: jdbc:postgresql://localhost:5432/my_db for postgresql
kpi.datasource.username=your_database_username
kpi.datasource.password=your_database_password

# With Elasticsearch
kpi.datasource.url=your_database_uri #example: localhost:9200
kpi.datasource.username=your_database_username
kpi.datasource.password=your_database_password

# Add ignored routes
kpi.ignore-rest-routes[0].path=/api/authenticate
kpi.ignore-rest-routes[0].method=POST

# Add ignored gRPC methods
kpi.ignore-grpc-methods[0]=AuthenticateService/Authorize
```
- Please ensure that when you use SQL Databases, you must include the driver dependencies:
```xml
# For PostgreSQL
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
# For MariaDB
<dependency>
    <groupId>org.mariadb.jdbc</groupId>
    <artifactId>mariadb-java-client</artifactId>
</dependency>
# For MySQL
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```
## Usage

- Enable KPI Log by adding `@EnableKpiLog` to your main application class:

```java
@SpringBootApplication
@EnableKpiLog // adding this line
public JavaMainApplication {
    public static void main(String[] args) {
        StringApplication.run(JavaMainApplication.class, args);
    }
}
```

- Get authentication information by using `@Bean` `GrpcContext` of our library:

```java
import com.atviettelsolutions.config.GrpcContext;
import org.springframework.security.oauth2.jwt.Jwt;

@AllArgsConstructor
public class YourService {
    private final GrpcContext grpcContext;

    public void getAuthentication() {
        Jwt jwt = grpcContext.getJwt();
    }

    public void getCurrentUserLogin() {
        String userLogin = grpcContext.getCurrentUserLogin();
    }
}
```
