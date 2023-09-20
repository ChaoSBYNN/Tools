package com.example.testRunTimeUse;

public class TestRuntimeUse {

//    static int aa = 0;

    public void test(){
        long start = System.currentTimeMillis();
        int aa = 0;
        for (int i=0;i<1000000000;i++){
            aa++;
        }
        long useTime = System.currentTimeMillis()-start;
        System.out.println("useTime:"+useTime);
    }

    public static void main(String[] args) {
        TestRuntimeUse testRuntimeUse = new TestRuntimeUse();
        testRuntimeUse.test();
    }

}
