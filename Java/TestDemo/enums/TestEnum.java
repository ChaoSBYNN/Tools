package com.example.testEnums;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2022/5/20 15:22
 */
public class TestEnum {
    public static void main(String[] args) {
        System.out.println(AccessRuleIssuedEnum.getDescByCode(0, 0));
        System.out.println(AccessRuleIssuedEnum.getDescByCode(1, 3));
        System.out.println(AccessRuleIssuedEnum.getDescByCode(1, 2));
    }
}
