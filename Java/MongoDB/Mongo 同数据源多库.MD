
```java

import com.mongodb.client.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(
        basePackages = "com.xxx.*",
        mongoTemplateRef = "logMongoTemplate"
)
@Configuration
public class MongoConfiguration {

    public static final String LOG_DB_TEMPLATE = "logMongoTemplate";

    public static final String XXX_DB_TEMPLATE = "xxxMongoTemplate";

    private static final String LOG_DB = "DB_LOGS";

    private static final String XXX_DB = "DB_XXX";

    @Primary
    @Bean(name = LOG_DB_TEMPLATE)
    public MongoTemplate logMongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, LOG_DB);
    }

    @Bean(name = XXX_DB_TEMPLATE)
    public MongoTemplate xxxMongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, XXX_DB);
    }
```
