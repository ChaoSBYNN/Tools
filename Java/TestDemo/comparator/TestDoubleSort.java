package com.example.testOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2021/9/14 15:36
 */
public class TestDoubleSort {

    public static void main(String[] args) {

        List<Double> doubles  = new ArrayList<Double>() ;
        for (int i = 1; i < 100; i++)
            doubles.add(i/7d) ;
        Collections.sort(doubles,new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                // TODO Auto-generated method stub
                int i = 0 ;
                double d = o2 -o1 ;
                if(d>0)
                    i = 1 ;
                if(d<0)
                    i = -1 ;
                return i;
            }
        }) ;
        for (Double double1 : doubles) {
            System.out.println(double1);
        }
    }
}
