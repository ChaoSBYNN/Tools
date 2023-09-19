package com.example.testJson2Object;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 点位坐标信息
 * @date 2021/9/6
 */
@Accessors(chain = true)
@Data
public class PointInfo implements Serializable {

    private static final long serialVersionUID = 2103104948026237858L;
    /**
     * x轴
     */
    private Float x;

    /**
     * y轴
     */
    private Float y;
}
