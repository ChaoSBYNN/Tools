
> `BaseMapper` 默认不提供批量插入

## BaseMapper

实体类对应的mapper在继承BaseMapper后，就可以使用以下Mybatis-plus提供的方法进行数据操作。
BaseMapper中默认提供一个insert()方法，仅支持数据的单条插入。如果有上万甚至数十万数据需要插入时，耗时过久。

```java
public interface BaseMapper<T> extends Mapper<T> {

    /**
     * 插入一条记录
     *
     * @param entity 实体对象
     */
    int insert(T entity);

    ...
}
```

## IService

与BaseMapper相同，实体类对应接口在继承了IService后，可以调用其中提供的save(),saveBatch()进行插入操作。其中saveBatch()入参为list，可以实现批量插入。

```java
public interface IService<T> {

    /**
     * 默认批次提交数量
     */
    int DEFAULT_BATCH_SIZE = 1000;

    /**
     * 插入一条记录（选择字段，策略插入）
     *
     * @param entity 实体对象
     */
    default boolean save(T entity) {
        return SqlHelper.retBool(getBaseMapper().insert(entity));
    }

    /**
     * 插入（批量）
     *
     * @param entityList 实体对象集合
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean saveBatch(Collection<T> entityList) {
        return saveBatch(entityList, DEFAULT_BATCH_SIZE);
    }

    /**
     * 插入（批量）
     *
     * @param entityList 实体对象集合
     * @param batchSize  插入批次数量
     */
    boolean saveBatch(Collection<T> entityList, int batchSize);

    /**
     * 批量修改插入
     *
     * @param entityList 实体对象集合
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean saveOrUpdateBatch(Collection<T> entityList) {
        return saveOrUpdateBatch(entityList, DEFAULT_BATCH_SIZE);
    }

    /**
     * 批量修改插入
     *
     * @param entityList 实体对象集合
     * @param batchSize  每次的数量
     */
    boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize);

    ...
}
```

但是，saveBatch()方法实现的批量插入其实是伪批量，其底层实现仍然是一条条数据进行插入的。源码的解析就不贴了，有兴趣的小伙伴可以看一下这篇文章

[为什么说saveBatch是伪批量插入?](https://www.cnblogs.com/chcha1/p/16340254.html)

## 通过添加mapper层选装插件实现真正的批量插入

Mybatis-plus其实是有真正实现了批量插入的方法的，方法名是insertBatchSomeColumn()需要我们配合SQL注入器来开启。(可能是因为仅支持MySQL，所以作者没有将其设置为默认方法？)

开启insertBatchSomeColumn()可分为3个步骤:

### 创建插件类

新建一个名为InsertBatchSqlInjector 的类，继承DefaultSqlInjector。（当然，类名可以根据自己的喜好来）

```java
@Component
public class InsertBatchSqlInjector extends DefaultSqlInjector {
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        // 添加InsertBatchSomeColumn方法
        methodList.add(new InsertBatchSomeColumn());
        return methodList;
    }
}
```

### 将SQL注入器交给Spring容器

```java
@Configuration
public class MybatisPlusConfiguration {

    @Resource
    private InsertBatchSqlInjector insertBatchSqlInjector;


    /**
     * 添加批量插入插件
     */
    @Bean
    public GlobalConfig globalConfig(){
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setSqlInjector(insertBatchSqlInjector);
        return globalConfig;
    }
}
```

> 💡 这一步中，有一点需要注意。如果你的MybatisPlusConfig类中自定义了sqlSessionFactory，上面的配置不会被加载到，需要在sqlSessionFactory中进行设置。

```java
@Bean("sqlSessionFactory")
public SqlSessionFactory sqlSessionFactory(@Qualifier("globalConfig") GlobalConfig globalConfig ) throws Exception {
    MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        
	// 其他设置，与本话题无关

    //添加自定义sql注入接口
    sqlSessionFactory.setGlobalConfig(globalConfig);//添加自定义sql注入接口
    return sqlSessionFactory.getObject();
}
```

### 配置自定义Mapper继承BaseMapper

新建BaseBatchMapper类，继承BaseMapper，并在此类中配置insertBatchSomeColumn()方法。

```java
public interface BaseBatchMapper<T> extends BaseMapper<T> {

    /**
     * 批量插入 仅适用于mysql
     * 如果要自动填充，@Param(xx) xx参数名必须是 list/collection/array 3个的其中之一
     */
    Integer insertBatchSomeColumn(@Param("list") Collection<T> list);
}
```

然后，用业务Mapper继承BaseBatchMapper就可以调用insertBatchSomeColumn()方法了。

> 当然，如果在你的项目中，仅仅有一两个类需要用到批量插入，那完全没必要抽取一个BaseBatchMapper。直接用你的业务Mapper继承BaseMapper，并在对应业务Mapper中配置insertBatchSomeColumn()方法即可，代码同上。

## 参考

[Mybatis-plus实现【真·批量插入】](https://blog.csdn.net/knock_me/article/details/132165909)
