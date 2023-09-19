package com.example.testOrder;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2021/8/30 14:01
 */
@Setter
@Getter
@ToString
public class User {

    public User(String name, String birthday) {
        this.name = name;
        this.birthday = birthday;
    }

    int id;

    String name;

    String birthday;

}
