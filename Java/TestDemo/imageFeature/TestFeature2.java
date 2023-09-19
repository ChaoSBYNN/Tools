package com.example.testFeature;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2023/3/29 9:57
 */
public class TestFeature2 {
    public final static int ARRAY_SIZE = 512;
    public static void main(String[] args) {
        try {


//            String feature1 = "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";
//            String feature2 = "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";


            String feature1 = "TUZYMQABBQAAAAAAAAAAAAYiEBAAAAAAiXohxbwwIRLNIqQv2/1+TWEFbZYm6Z+Vjhr0r85XRKGQM9jRWo41+UGMHzBaR0pmvm3KBz3pl1R/Tbhw6EASpvnt6/tOXNDJEqETZonCXHAQLz/6NxgZTObiyspXQIAUfVKAThgQ3C7zexFXVizkFPujkyKZMXvMSewbW9F+VDPqe1XEWXBvEuA4k1CuyHKJwk8b1EGaXLXhtT6PP1bWG6fkR6PaOeYhn8RoSY2LHNg7lXjaFM/c2TdaqVwWYIu3aK5avO2z/Oh6uZtm3y1iY1e62GePa0zreZZygqr957xgBLlYO+HM21zJ71FHnvlca5b3HVTuT9I0yi0UrfXdKQ==";
            String feature2 = "TUZYMQABBQAAAAAAAAAAAAYiEBAAAAAArEIOmqEoaBFx/d3xCbmdskc/a3UxuJxMmD4G/DBwRYToYtQ8dXEsIn5SLOFUnjCDT3wj7uYU3buZLoKo5r3tUfhd1AdBeiAqJr7/bP3UUnwoyQX1FiQiqsvoydOyTG07i3yupDzgHjvoTOd2TzrCE9N7Y8WlhHPiaQXEjeZhowEZbbMhVXi7Gu01pben7pvX527iyESFYaPMYCeLPmPYG6AsYpk4K+g/jxBTR5Z87MU3hHDd48XgCxGDu7TkbYtEdrxUq+qwCud5rahq4SEdcEaH31ymU1/weVJloHIY/oqNBrigyurMyKscHWtMgvBEmE3MAl0qc8zI83TnVvYyLQ==";
            String feature3 = "TUZYMQABBQAAAAAAAAAAAAYiEBAAAAAAjbE0hqkfZwuI3MP2/fGYQ0BFkXnEpWxIh84WuDNkofuDeObmHW/F1H5vADZYfz6opGjdEfjx/1KPKaeC91YRQZEf5wFOXDspPIXyb4AOaW4CNAfcFBwYqcza0dquR2IjgE+PRxgTBjHWchdbXj7PDeqaaceleqTEReMbmDVmVxUXZkAwVHVZ7+gflkVPz5S7xVMbKW+MRF0wSj+NX33bCVbHdJTCRc4kkwljdph4HdTGl4st29jtIDK+qo31eIm3T7VUn9S/AOBut5+C4N9nc0COymOvWlzycG5ovULu57ObNLGFIBo+3IcfGGSokMdTmmb06Kwjg/Iy9QUDQ88wMQ==";
            String feature4 = "TUZYMQABBQAAAAAAAAAAAAYiEBAAAAAAjWculL0bVgiaMaPu3DF5qlBGtk7L0rlqkyILuyxLVfy1SOTTa3E40lWkHSK0czKio3HZ8Qzk+a13GEBA1FMfr/gYCfpwRg/Nx4v2dpDaSYodKV7+KQj4rQr57e1BsoUXkwqkWC77D9jgf/JlRgS0KgOibTGbN5TaY+HnkO2IVDT7laA4S4VCAssLe71E5GqI3n3HzmOCVITBVzyAEmMj8a7AdZ/aPBUIcxpiUoJ+GsDTnYzD6933LTG2uK7Ocp2par9ZTNu8BuR1t41++S8WdUZw8Fyxblj4d2GJvUjh6laRPEKFJDw7yazl5nCeoNhJRZPzD6HFbd09wR8QtPwwPA==";

            System.out.println(Arrays.toString(getBytes(feature1)));
            System.out.println(Arrays.toString(getBytes(feature2)));
            System.out.println(Arrays.toString(getBytes(feature3)));
            System.out.println(Arrays.toString(getBytes(feature4)));

//            System.out.println(distanceCalc(getBytes(feature1), getBytes(feature2)));
//            System.out.println(distanceCalc(getBytes(feature1), getBytes(feature3)));
//            System.out.println(distanceCalc(getBytes(feature1), getBytes(feature4)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] split(byte[] base) {
        byte[] target = new byte[base.length-24];
        System.arraycopy(base, 24, target, 0, base.length-24);
        return target;
    }

    public static byte[] getBytes(String base64Code) {
//        return Base64.getDecoder().decode(base64Code);
        return Base64.getDecoder().decode(base64Code);
    }


    public static float distanceCalc(byte[] features0, byte[] features1) {
        int dimension = features0.length / 4;
        return distanceCalc(features0, 0, features1, 0, dimension);
    }

    public static float distanceCalc(byte[] features0, int startPos0, byte[] features1, int startPos1, int featureLength) {
        List<MapperConvert.Node> mapperList = MapperConvert.getInstance().getMapperList(100500512);
        int mapperLength = mapperList.size();
        int dimension = featureLength;
        float distance = 0.0F;

        for(int i = 0; i < dimension; ++i) {
            distance += FloatUtil.byteToFloatEx(features0, i * 4 + startPos0) * FloatUtil.byteToFloatEx(features1, i * 4 + startPos1);
        }

        return MapperConvert.getInstance().scoreConvert(distance, mapperList, mapperLength);
    }

}
