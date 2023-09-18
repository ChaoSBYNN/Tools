package com.example.teseList;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2022/8/31 13:53
 */
public class TestListCopy {

    public static void main(String[] args) {
        System.out.println((int) (Math.random() * 10000));


        List<String> str1 = new ArrayList<>();
        str1.add("a");
        str1.add("b");
        str1.add(null);
        str1.add(null);
        str1.add("c");

        System.out.println(str1);
        str1 = str1.stream().filter(e -> {return !StringUtils.isEmpty(e);
        }).collect(Collectors.toList());
        System.out.println(str1);
    }
}
