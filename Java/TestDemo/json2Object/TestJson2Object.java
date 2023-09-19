package com.example.testJson2Object;


import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2021/9/7 15:26
 */
public class TestJson2Object {

    public static void main(String[] args) {

        String json = " [{'name': 'classArea_001','points': [{'x':0.186,'y':0.051},{'x':0.792,'y':0.051}]}]";

//        RoiDTO roiDTO = JSONUtil.toBean(json,RoiDTO.class);
        List<RoiDTO> roiDTOS = JSONObject.parseArray(json, RoiDTO.class);

        System.out.println(roiDTOS);

    }

}
