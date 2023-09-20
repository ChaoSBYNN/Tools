package com.example.testRandom;

import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @date 2023/1/9 12:57
 */
public class TestRandom {
    public static List<Integer> red1 = Arrays.asList(1, 2, 3 ,4 , 6, 5, 7 , 8, 9, 10, 11);
    public static List<Integer> red2 = Arrays.asList(12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22);
    public static List<Integer> red3 = Arrays.asList(23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33);

    public static List<Integer> red =  Arrays.asList(1, 2, 3 ,4 , 5, 6, 7 , 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33);
    public static List<Integer> blue = Arrays.asList(1, 2, 3 ,4 ,5, 6, 7 , 8, 9, 10, 11, 12, 13, 14, 15, 16);

    public static void main(String[] args) {
        Random rand = new Random();
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i<7; i++) {
                System.out.print(red.get(rand.nextInt(32)) + " ");
            }
            System.out.println("- " + blue.get(rand.nextInt(15)));

        }
    }

}
