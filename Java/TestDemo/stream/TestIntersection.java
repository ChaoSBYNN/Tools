package com.example.testStream;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: 交集
 * @date 2022/6/13 14:04
 */
public class TestIntersection {

    public static void main(String[] args) {
        List<Entity> l1 = new ArrayList<>();
        l1.add(new Entity("1" ,1));
        l1.add(new Entity("4" ,2));
        l1.add(new Entity("3" ,3));

        List<String> l2 = new ArrayList<>();
        l2.add("2");
        l2.add("3");
        l2.add("4");


        List<String> intersection = l1.stream().filter(e -> l2.contains(e.getS())).map(Entity::getS).collect(Collectors.toList());

        System.out.println(intersection);
    }

    @Getter
    @Setter
    static
    class Entity{

        public Entity(String s, Integer i) {
            this.s = s;
            this.i = i;
        }

        String s;
        Integer i;

    }

}
