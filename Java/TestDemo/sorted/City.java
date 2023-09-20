package com.example.testSorted;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2023/3/7 16:01
 */
@Getter
@Setter
@ToString
public class City {

    private String name;

    private Integer person;

    public City(String name, Integer person) {
        this.name = name;
        this.person = person;
    }
}
