package com.example.testJson2Object;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 区域坐标实体
 * @date 2021/9/6
 */
@Accessors(chain = true)
@Data
public class RoiDTO implements Serializable {
    private static final long serialVersionUID = 4677171088829505132L;

    /**
     * 名称
     */
    private String name;

    /**
     * 点位信息
     */
    private List<PointInfo> points;
}
