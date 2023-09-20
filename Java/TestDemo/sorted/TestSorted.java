package com.example.testSorted;

import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2023/3/7 16:01
 */
public class TestSorted {

    public static void main(String[] args) {

        List<City> country = new ArrayList<>();
        List<City> result = new ArrayList<>();
        country.add(new City(null, 10));
        country.add(new City("哈尔滨", 7));
        country.add(new City("上海", 4));
        country.add(new City("深圳", 9));

//        result = country.stream().filter(x -> null!= x.getName()).sorted(Comparator.comparing(City::getPerson, Comparator.naturalOrder())).collect(Collectors.toList());
//        City other = country.stream().filter(x -> null == x.getName()).findAny().orElse(null);
//        if (ObjectUtils.isNotEmpty(other)) {
//            result.add(other);
//        }
//
//        System.out.println(country);
//        System.out.println(result);

        System.out.println(country);
        country = country.stream().sorted(Comparator.comparing(City::getPerson).reversed()).collect(Collectors.toList());
        System.out.println(country);
        City ruleOther = new City ("其他", 0);
        country.forEach(r -> {

            if (result.size() > 1) {
                ruleOther.setPerson(ruleOther.getPerson() + r.getPerson());
            } else {
                result.add(r);
            }
        });
        result.add(ruleOther);
        System.out.println(result);
    }


}
