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


## 时间差

```java
  LocalDateTime now = LocalDateTime.now();
  LocalDateTime nowB = LocalDateTime.now();
  
  // 获取两个日期相差毫秒
  long betweenMs =  ChronoUnit.MILLIS.between(now , nowB );
  
  // 获取两个日期相差秒
  long betweenSs =  ChronoUnit.SECONDS.between(now , nowB );
  
  // 获取两个日期相差分钟
  long betweenMinutes =  ChronoUnit.MINUTES.between(now , nowB );
  
  // 获取两个日期相差小时
  long betweenHours =  ChronoUnit.HOURS.between(now , nowB );
  
  // 获取两个日期相差天数
  long betweenDay =  ChronoUnit.DAYS.between(now , nowB );
  
  // 获取两个日期相差多少周
  long betweenWeeks =  ChronoUnit.WEEKS.between(now , nowB );
  
  // 获取两个日期相差月
  long betweenMonths =  ChronoUnit.MONTHS.between(now , nowB );
  
  // 获取两个日期相差年
  long betweenYear =  ChronoUnit.YEARS.between(now , nowB );

```
