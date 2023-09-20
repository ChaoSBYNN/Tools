package com.example.testString;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2022/6/29 10:00
 */
public class TestString {

    public static void main(String[] args) {

//        System.out.println("https://xxx.com/visitor-appointment/apply/index?source=3&hash=e6217a42".length());
//        String str = "yyyy-MM-dd";
//        System.out.println(str.substring(5, str.length()));
//        GregorianCalendar now = new GregorianCalendar();
//        int hour = now.get(Calendar.HOUR_OF_DAY);
//        String time1 = "00:00";
//        String time2 = "15:00";
//        String time3 = "20:00";
//        System.out.println(Integer.parseInt(time1.substring(0, 2)));
//        System.out.println(Integer.parseInt(time2.substring(0, 2)));
//        System.out.println(Integer.parseInt(time3.substring(0, 2)));
//        System.out.println(Integer.parseInt(time1.substring(0, 2)) < hour);
//        System.out.println(Integer.parseInt(time2.substring(0, 2)) < hour);
//        System.out.println(Integer.parseInt(time3.substring(0, 2)) < hour);
//        System.out.println();
//        System.out.println();
//        System.out.println(hour);

//        String str = "\"source\":{\"devId\":\"ee2af3f3a5ab0eed02685b1b79efebe9\",\"devModel\":\"DragonInfo.VehicleLocate.iHK53\",\"isModuleType\":true,\"method\":\"propsNotify\",\"payload\":[{\"propertyCode\":\"latitude\",\"propertyValue\":\"0.008729\",\"propertyOldValue\":\"0.008729\",\"isChanged\":false},{\"propertyCode\":\"longitude\",\"propertyValue\":\"0.022001\",\"propertyOldValue\":\"0.022001\",\"isChanged\":false}],\"seq\":\"123456789\",\"timestamp\":1668145018539,\"topic\":\"iot/v1/s/dc96db5f9a394b8db8c3ee8824fcf2dc/subdevice/props_notify\",\"type\":\"notify\"},\"timestamp\":1668145018539}]";

//        System.out.println(str.contains("DragonInfo.VehicleLocate.iHK53"));

//        String text = "push:studentSign:abnormal:in:ruleId:%s:gradeId:%s";
//
//        String str = String.format(text, 1585509645131317250L, 1585509645131317250L);
//
//        System.out.println(str);

//        String a = null;
//        String b = "123";
//        System.out.println(patch(a, b));

    }

    public static String patch (String a, String b) {
        return (a + b);
    }
}
