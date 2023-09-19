package com.example.testFeature;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Base64;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2023/3/29 9:57
 */
public class TestFeature {
    public final static int ARRAY_SIZE = 512;
    public static void main(String[] args) {
        try {
            String feature1 = "TUZYMQABBQAAAAAAAAAAAAYiEBAAAAAAiXohxbwwIRLNIqQv2/1+TWEFbZYm6Z+Vjhr0r85XRKGQM9jRWo41+UGMHzBaR0pmvm3KBz3pl1R/Tbhw6EASpvnt6/tOXNDJEqETZonCXHAQLz/6NxgZTObiyspXQIAUfVKAThgQ3C7zexFXVizkFPujkyKZMXvMSewbW9F+VDPqe1XEWXBvEuA4k1CuyHKJwk8b1EGaXLXhtT6PP1bWG6fkR6PaOeYhn8RoSY2LHNg7lXjaFM/c2TdaqVwWYIu3aK5avO2z/Oh6uZtm3y1iY1e62GePa0zreZZygqr957xgBLlYO+HM21zJ71FHnvlca5b3HVTuT9I0yi0UrfXdKQ==";
            String feature2 = "TUZYMQABBQAAAAAAAAAAAAYiEBAAAAAArEIOmqEoaBFx/d3xCbmdskc/a3UxuJxMmD4G/DBwRYToYtQ8dXEsIn5SLOFUnjCDT3wj7uYU3buZLoKo5r3tUfhd1AdBeiAqJr7/bP3UUnwoyQX1FiQiqsvoydOyTG07i3yupDzgHjvoTOd2TzrCE9N7Y8WlhHPiaQXEjeZhowEZbbMhVXi7Gu01pben7pvX527iyESFYaPMYCeLPmPYG6AsYpk4K+g/jxBTR5Z87MU3hHDd48XgCxGDu7TkbYtEdrxUq+qwCud5rahq4SEdcEaH31ymU1/weVJloHIY/oqNBrigyurMyKscHWtMgvBEmE3MAl0qc8zI83TnVvYyLQ==";
            String feature3 = "TUZYMQABBQAAAAAAAAAAAAYiEBAAAAAAjbE0hqkfZwuI3MP2/fGYQ0BFkXnEpWxIh84WuDNkofuDeObmHW/F1H5vADZYfz6opGjdEfjx/1KPKaeC91YRQZEf5wFOXDspPIXyb4AOaW4CNAfcFBwYqcza0dquR2IjgE+PRxgTBjHWchdbXj7PDeqaaceleqTEReMbmDVmVxUXZkAwVHVZ7+gflkVPz5S7xVMbKW+MRF0wSj+NX33bCVbHdJTCRc4kkwljdph4HdTGl4st29jtIDK+qo31eIm3T7VUn9S/AOBut5+C4N9nc0COymOvWlzycG5ovULu57ObNLGFIBo+3IcfGGSokMdTmmb06Kwjg/Iy9QUDQ88wMQ==";
            String feature4 = "TUZYMQABBQAAAAAAAAAAAAYiEBAAAAAAjWculL0bVgiaMaPu3DF5qlBGtk7L0rlqkyILuyxLVfy1SOTTa3E40lWkHSK0czKio3HZ8Qzk+a13GEBA1FMfr/gYCfpwRg/Nx4v2dpDaSYodKV7+KQj4rQr57e1BsoUXkwqkWC77D9jgf/JlRgS0KgOibTGbN5TaY+HnkO2IVDT7laA4S4VCAssLe71E5GqI3n3HzmOCVITBVzyAEmMj8a7AdZ/aPBUIcxpiUoJ+GsDTnYzD6933LTG2uK7Ocp2par9ZTNu8BuR1t41++S8WdUZw8Fyxblj4d2GJvUjh6laRPEKFJDw7yazl5nCeoNhJRZPzD6HFbd09wR8QtPwwPA==";

//            System.out.println(Arrays.toString(getBytes(feature1)));
//            System.out.println(Arrays.toString(getBytes(feature2)));
//            System.out.println(Arrays.toString(getBytes(feature3)));
//            System.out.println(Arrays.toString(getBytes(feature4)));

            System.out.println(calcSimilarity(getBytes(feature1), getBytes(feature2)));
            System.out.println(calcSimilarity(getBytes(feature1), getBytes(feature3)));
            System.out.println(calcSimilarity(getBytes(feature1), getBytes(feature4)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] getBytes(String base64Code) {
        return Base64.getDecoder().decode(base64Code);
    }

    /**
     * 将byte数组数据转换成float
     * @param arr
     * @return
     */
    public static float bytes2Float(byte[] arr) {
        int number = 0;
        number = number|(arr[0] & 0xff) << 0;
        number = number|(arr[1] & 0xff) << 8;
        number = number|(arr[2] & 0xff) << 16;
        number = number|(arr[3] & 0xff) << 24;
        return Float.intBitsToFloat(number);
    }

    /**
     * 将byte数组转换成float数组
     * @param bytes
     * @return
     */
    public static float[] byteArrayToFloatArray(byte[] bytes){
        float[] target = new float[ARRAY_SIZE];
        byte[] doubleBuffer = new byte[4];
        for(int j = 0; j < bytes.length; j += 4) {
            System.arraycopy(bytes, j, doubleBuffer, 0, doubleBuffer.length);
            target[j/4] = (bytes2Float(doubleBuffer));
        }
        return target;
    }

    /**
     *  对比两个特征值
     * @param feat1 特征值1
     * @param feat2 特征值2
     * @return
     */
    public static float calcSimilarity(byte[] feat1, byte[] feat2) {
        float[] featArray1 = byteArrayToFloatArray(feat1);
        float[] featArray2 = byteArrayToFloatArray(feat2);
        if(featArray1 == null || featArray2 == null){
            return 0.0f;
        }
        return calcSimilarity(featArray1, featArray2);
    }

    public static float calcSimilarity(float[] featArray1, float[] featArray2){
        float result = 0.0f;
        for(int i=0;i<ARRAY_SIZE; i++){
            result += featArray1[i] * featArray2[i];
        }
        return (result+1)/2;
    }
}
