package com.example.testSum;

import com.example.testOrder.User;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2021/8/30 14:20
 */
public class TestSum {

    public static void main(String[] args) {

        DoubleSum d1 = new DoubleSum("1", 0.1);
        DoubleSum d2 = new DoubleSum("1", 0.2);
        DoubleSum d3 = new DoubleSum("1", 0.3);
        DoubleSum d4 = new DoubleSum("1", null);

        List<DoubleSum> list = new ArrayList<>();
        list.add(d1);
        list.add(d2);
        list.add(d3);
        list.add(d4);
        Double count = list.stream().mapToDouble(e -> e.getValue()).sum();
        System.out.println(count);

//        DecimalFormat df = new DecimalFormat("0.000");
//        Double sum = 1.310 + 0.013;
//        Double avg = 0.0000;
//        Double out;
//
//        avg = sum / 2;
//
//        out = BigDecimal.valueOf(avg).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
//        System.out.println(out);

////        Double sum = 814.748;
////        Double avg = sum / 3;
//        Double sum = 814.747;
////        Double avg = sum / 2;
//        Double avg = 407.37355;
//
//        System.out.println(avg);
//        System.out.println(Double.parseDouble(df.format(avg)));

//        List<DoubleSum> list = new ArrayList<>();
//
//        list.add(DoubleSum.builder().type("a").value(17.9).build());
//        list.add(DoubleSum.builder().type("b").value(7.312312).build());
//        list.add(DoubleSum.builder().type("c").value(4.7).build());
//        list.add(DoubleSum.builder().type("d").value(0.9).build());
//        list.add(DoubleSum.builder().type("e").value(1.0).build());
//
//
//        list = list.stream().sorted(
//                Comparator.comparing(DoubleSum::getValue).reversed()).collect(Collectors.toList());
//        System.out.println(list);

//        System.out.println(UUID.randomUUID().toString().replaceAll("-","").length());

    }

}
