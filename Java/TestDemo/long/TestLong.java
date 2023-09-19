package com.example.testLong;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2022/7/20 11:18
 */
public class TestLong {

    //1547831045476761602
    //1547831173197512705
    //1547845681177608194
    //1547845933494353921
    //1547860762705182722
    //1547860762705182723
    //1549286126136463361
    //1549286561245171713
    //1549586945675292673
    public static void main(String[] args) {
        List<Long> origin = new ArrayList<>();
        origin.add(1547831045476761602L);
        origin.add(1547831173197512705L);
        origin.add(1547845681177608194L);
        List<Long> target = new ArrayList<>();
        target.add(1547845933494353921L);
        target.add(1547831045476761602L);

        origin.forEach(e -> {
            System.out.print(e + ":");
            if (target.contains(e)) {
                System.out.print(1);
            } else {
                System.out.print(0);
            }
            System.out.println();
        });
    }
}
