package com.example.testSaveListIndex;

import java.util.ArrayList;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2021/10/14 10:02
 */
public class TestSaveListIndex {

    public static void main(String[] args) {
        int j = 0;
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);
        list.add(8);
        list.add(9);
        list.add(0);

        System.out.println(list);
        for (int i = 5; i < list.size() + j; i++) {
            list.remove(5);
            j++;
        }

        System.out.println(list);
    }

}
