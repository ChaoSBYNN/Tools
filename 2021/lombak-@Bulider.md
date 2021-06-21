lombok问题

@Builder和@NoArgsConstructor同时使用时会报错
两个同时使用时需要加上@AllArgsConstructor全参构造函数

数据库对应实体的BO使用了@Builder必须要需要加上@NoArgsConstructor和@AllArgsConstructor
因为mybatis必须要有@NoArgsConstructor创建无参构造函数才能进行操作

实体类上如果有属性有初始化值需要在属性上加@Builder.Default
@Accessors(chain = true)使用注意：使用了这个注解，clone对象不能被赋值
序列化问题
枚举加了@JsonSerialize(using = JsonEnumSerializer.class)注解，序列化时用jackson会报错，需要用fastJson

使用select... for update加悲观锁的sql语句必须加事务
spring中update操作会自动commit,select不会自动commit

https://juejin.im/post/5ce6092ce51d4510b71da563

mysql 字段越多查询越慢，尽量需要哪些字段就查哪些字段
不加索引，条件查询很慢，45000条带不加索引的条件，查询出一条要2.5秒，添加索引后只要0.02秒

组合索引，是最左前缀原则
alter table recharge_card_information add index index_card (card_no,card_money,CARD_PASSWORD) ;

最左前缀原则 :必须要带上card_no条件，card_money,CARD_PASSWORD的索引才会生效
组合索引生效的条件 :
where 后带card_no、where 后带card_no和card_money、where 后带card_no和CARD_PASSWORD
或者 where后带card_no,card_money,CARD_PASSWORD。
总之必须带最左的条件
