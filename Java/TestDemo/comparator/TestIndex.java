package com.example.testOrder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2021/8/30 14:01
 */
public class TestIndex {

    public static void main(String[] args) {

        List<User> userList = new ArrayList<>();
        userList.add(new User("张三","05-07"));
        userList.add(new User("李四","05-07"));
        userList.add(new User("张三","05-06"));
        userList.add(new User("李四","05-06"));
        System.out.println(userList);

        //排序，把姓名相同的放在一起，再根据时间进行排序
        userList = userList.stream().sorted(
                Comparator.comparing(User::getName)
                        .thenComparing(User::getBirthday)).collect(Collectors.toList());
        System.out.println(userList);


        //给list每个元素添加序号，可以试下这里如果换成 Integer i = 1; 下面set的时候传 i++ 试下看会发生什么？
        Integer[] arr = {1};
        userList = userList.stream().peek(e->e.setId(arr[0] ++)).collect(Collectors.toList());

        System.out.println(userList);
    }

}
