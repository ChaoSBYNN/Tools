# Cache

```java
// 开启缓存
@EnableCaching
```

```java
// 新增&修改
@CachePut(cacheNames = "prefix", key = "#key")

// 查询
@Cacheable(cacheNames = "prefix", key = "#key")

// 删除
@CacheEvict(cacheNames = "prefix", key = "#key")

// 资源统一前缀
@CacheConfig(cacheNames = "prefix")

```

## RedisCache

> 基于redis存储

```java
@EnableCaching
@Configuration
public class SpringCacheConfiguration extends CachingConfigurerSupport {
 
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {
 
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
 
        return config.serializeValuesWith(RedisSerializationContext.SerializationPair
                // 序列化值 TODO这里需要解决LocalDateTime转换问题
                .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                // 设置过期时间
                .entryTtl(Duration.ofMinutes(120))
                // 解决双冒号问题
                .computePrefixWith(name -> name + ":");
    }
}
```


* (@Cacheable与Redis缓存)[https://blog.csdn.net/m0_37589586/article/details/87782001]
