package com.example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2021/7/26 19:21
 */
public class TestNumber {

    public static void main(String[] args) {

        String url = "http://172.24.158.218:9001/test/1297801296027344905/jpeg/31c1d4c1c9af4471a9064c28563926ba.jpeg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20220325%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20220325T011150Z&X-Amz-Expires=36000&X-Amz-SignedHeaders=host&X-Amz-Signature=0a2ffda8960606f1809251b2c3e93e494a32a8ee50ac230578449be7ae235c11";

        if (url.contains("?")) {
            System.out.println(url.length());
            System.out.println(url.substring(0, url.indexOf("?")));
        }

        ;
//        System.out.println(BigDecimal.valueOf(36.555555).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
//        Long a = 1L;
//        Long b = 2L;
//        Long c = 3L;
//
//        HashMap<Long, Integer> map = new HashMap<>();
//        map.put(a, 1);
//        map.put(b, 2);
//        map.put(c, 3);
//
//        Integer i1 = 1;
//
//        System.out.println(map.get((long)i1));
//
//        ArrayList<Integer> list = new ArrayList<>();
//        list.add(11);
//        list.add(3);
//        list.add(30);
//        list.add(7);
////        list.add(-10);
//
//        Student s1 = new Student("a", 10, 5);
//        Student s2 = new Student("b", 20, 5);
//        Student s3 = new Student("c", 30, 7);
//
//        ArrayList<Student> students = new ArrayList<>();
//        students.add(s1);
//        students.add(s2);
//        students.add(s3);
//
//        Collections.sort(students, new Comparator<Student>() {
//            @Override
//            public int compare(Student o1, Student o2) {
//                if (o2.getCount() - o1.getCount() == 0) {
//                    return o2.getAge() - o1.getAge();
//                }
//                return o2.getCount() - o1.getCount();
//            }
//        });
//
//        System.out.println(students);

//        students.stream().sorted(Comparator.comparing(Student::getAge)).collect(Collectors.toList());

    }

    public boolean checkRecord(String s) {
        return s.indexOf("A") == s.lastIndexOf("A") && !s.contains("LLL");
    }

}
