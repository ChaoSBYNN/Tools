# Controller 写法

> 说到 Controller，相信大家都不陌生，它可以很方便地对外提供数据接口。它的定位，我认为是「不可或缺的配角」，说它不可或缺是因为无论是传统的三层架构还是现在的COLA架构，Controller 层依旧有一席之地，说明他的必要性；说它是配角是因为 Controller 层的代码一般是不负责具体的逻辑业务逻辑实现，但是它负责接收和响应请求

## Controller层逻辑

Controller 主要的工作有以下几项

- 接收请求并解析参数
- 调用 Service 执行具体的业务代码（可能包含参数校验）
- 捕获业务逻辑异常做出反馈
- 业务逻辑执行成功做出响应

```java
//DTO
@Data
public class TestDTO {
    private Integer num;
    private String type;
}
//Service
@Service
public class TestService {

    public Double service(TestDTO testDTO) throws Exception {
        if (testDTO.getNum() <= 0) {
            throw new Exception("输入的数字需要大于0");
        }
        if (testDTO.getType().equals("square")) {
            return Math.pow(testDTO.getNum(), 2);
        }
        if (testDTO.getType().equals("factorial")) {
            double result = 1;
            int num = testDTO.getNum();
            while (num > 1) {
                result = result * num;
                num -= 1;
            }
            return result;
        }
        throw new Exception("未识别的算法");
    }
}
//Controller
@RestController
public class TestController {

    private TestService testService;

    @PostMapping("/test")
    public Double test(@RequestBody TestDTO testDTO) {
        try {
            Double result = this.testService.service(testDTO);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    public DTOid setTestService(TestService testService) {
        this.testService = testService;
    }
}

```

如果真的按照上面所列的工作项来开发 Controller 代码会有几个问题

1. 参数校验过多地耦合了业务代码，违背单一职责原则；
2. 可能在多个业务中都抛出同一个异常，导致代码重复；
3. 各种异常反馈和成功响应格式不统一，接口对接不友好；

## 改造 Controller 层逻辑

*统一返回结构*

统一返回值类型无论项目前后端是否分离都是非常必要的，方便对接接口的开发人员更加清晰地知道这个接口的调用是否成功（不能仅仅简单地看返回值是否为 `null` 就判断成功与否，因为有些接口的设计就是如此），使用一个状态码、状态信息就能清楚地了解接口调用情况
