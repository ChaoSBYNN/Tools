package com.example.testOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2021/8/12 14:28
 */
public class TestOrder {

    public static void main(String[] args) {

        SpaceCount count1 = new SpaceCount("40楼-告警专用", 1418475043381342209L, 276);
        SpaceCount count2 = new SpaceCount("测试空间，半球", 1422461757825294337L, 11);
//        SpaceCount count3 = new SpaceCount("王木木测试", 9L, 4);
//        SpaceCount count4 = new SpaceCount("201", 11L, 4);
        SpaceCount count3 = new SpaceCount("王木木测试", 1424650510991888389L, 4);
        SpaceCount count4 = new SpaceCount("201", 1437313613382709241L, 4);

        ArrayList<SpaceCount> list = new ArrayList<>();
        list.add(count3);
        list.add(count4);
        list.add(count2);
        list.add(count1);

        System.out.println(list);
        System.out.println("-------------------------------");

//        Collections.sort(list, new TestComparator());
        System.out.println(list);
        list = (ArrayList<SpaceCount>) list.stream().sorted(Comparator.comparing(SpaceCount::getCount).reversed()).collect(Collectors.toList());
//        list.stream().sorted(Comparator.comparing(SpaceCount::getCount ,Comparator.reverseOrder()));
        System.out.println("-------------------------------");
        System.out.println(list);

    }
}
