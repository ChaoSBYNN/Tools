# 

## 时间转换

```java
  LocalDate localDate = LocalDate.now();
  LocalDateTime localDateTime1 = localDate.atStartOfDay();
  //23时1分2秒
  LocalDateTime localDateTime2 = localDate.atTime(23, 1, 2);
  //localTime ：时、分、秒、毫秒 ： 10:18:43.639
  LocalTime localTime = LocalTime.now();
  //拼接当前时间
  LocalDateTime localDateTime3 = localDate.atTime(localTime);
  System.out.println("atStartOfDay:日期后面拼接 00点00分 ： " + localDateTime1);
  System.out.println("atTime：参数分别代表 时、分、秒： " + localDateTime2);
  System.out.println("atTime:拼接当前localTime : " + localDateTime3);
```
