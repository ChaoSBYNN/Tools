package com.example.teseList;

import java.math.BigDecimal;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2022/5/20 15:59
 */
public class BigDecimalSum  {
    public static BigDecimal ifNull(BigDecimal value) {
        if (value != null) {
            return value;
        } else {
            return BigDecimal.ZERO;
        }
    }
    public static BigDecimal sum(BigDecimal ...value){
        BigDecimal result = BigDecimal.ZERO;
        for (int i = 0; i < value.length; i++){
            result = result.add(ifNull(value[i]));
        }
        return result;
    }
}
