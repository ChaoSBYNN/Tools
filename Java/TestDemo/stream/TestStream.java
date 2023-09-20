package com.example.testStream;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestStream {

    public static void main(String[] args) {

//        List<String> list = new ArrayList<>();
//        list.add("aaa1");
//        list.add("bbb1");
//        list.add("ccc1111");
//        list.add("ddd2222");
//
//        long count = list.stream().distinct().count();
//        System.out.println(count);
//
//        list.stream().filter(element -> element.length() > 1).forEach(e -> {
//            System.out.println(e.length());
//        });

        Stu stu1 = new Stu("a1" , 10, 1);
        Stu stu2 = new Stu("a2" , 13, 1);
        Stu stu3 = new Stu("a3" , 87, 1);
        Stu stu4 = new Stu("b1" , 56, 0);
        Stu stu5 = new Stu("b2" , 6, 0);
        Stu stu6 = new Stu("c1" , 31, 2);
        Stu stu7 = new Stu("c1" , 10, 2);

        List<Stu> stus = new ArrayList<>();
        stus.add(stu1);
        stus.add(stu2);
        stus.add(stu3);
        stus.add(stu4);
        stus.add(stu5);
        stus.add(stu6);
        stus.add(stu7);

        Stu result = stus.stream().filter(s -> {
            return s.getAge() == 10;
        }).findAny().orElse(null);

        System.out.println(result);
//
//        Map<Integer, List<Stu>> map = stus.stream()
//                .collect(
//                        Collectors.groupingBy(Stu::getSex,
//                                Collectors.mapping(s ->s, Collectors.toList())
//                        )
//                );
//
//        System.out.println(map);
    }


}
