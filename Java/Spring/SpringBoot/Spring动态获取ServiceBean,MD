# Spring 动态获取 Service Bean 

```java
@Component
public class DeviceCommandServiceFactory implements ApplicationContextAware {
    private Map<String, IAlarmDeviceCommandHandler> map;
    public IAlarmDeviceCommandHandler getHandler(String typeCode) {
        return map.values().stream().filter(e -> e.isUse(typeCode)).findFirst().orElse(null);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        map = applicationContext.getBeansOfType(IAlarmDeviceCommandHandler.class);
    }
}
```

```java
public interface IAlarmDeviceCommandHandler {

    /**
     * 通过typeCode 判断是否使用该Handler
     * @param typeCode
     * @return
     */
    boolean isUse(String typeCode);

    void command(AlarmRecordVO record, List<Integer> command);
}
```
